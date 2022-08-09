package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        ValidationUtil.validate(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            return addRoles(user);
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password,
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }
        return resetRoles(user);
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        return setRoles(users);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        return setRoles(users);
    }

    @Override
    public List<User> getAll() {
        final String query = "SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id ORDER BY name, email";
        return jdbcTemplate.query(query, new ResultSetExtractor<>() {
            private final List<User> users = new ArrayList<>();
            private int currentId = 100000;
            private boolean isContinue = true;

            @Override
            public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                currentId = rs.getInt("id");
                while (isContinue) {
                    users.add(getUser(currentId, rs));
                }
                return users;
            }

            private User getUser(int userId, ResultSet rs) throws SQLException {
                Collection<Role> roles = new HashSet<>(Collections.emptySet());
                User user = new User(
                        userId,
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("calories_per_day"),
                        rs.getBoolean("enabled"),
                        rs.getTimestamp("registered"),
                        roles);
                do {
                    if (rs.getString("role") != null) {
                        roles.add(Role.valueOf(rs.getString("role")));
                    }
                    if (!(isContinue = rs.next())) {
                        break;
                    } else {
                        currentId = rs.getInt("id");
                    }
                } while (userId == currentId);
                user.setRoles(roles);
                return user;
            }
        });
    }

    private User resetRoles(User user) {// User -> DB
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", user.id());
        return addRoles(user);
    }

    private User addRoles(User user) {
        Set<Role> roles = user.getRoles();
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", roles, roles.size(),
                (pss, role) -> {
                    pss.setInt(1, user.id());
                    pss.setString(2, role.name());
                });
        return user;
    }

    private User setRoles(List<User> users) {
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            user.setRoles(jdbcTemplate.queryForList("SELECT role FROM user_roles  WHERE user_id=?", Role.class, user.getId()));
        }
        return user;
    }
}
