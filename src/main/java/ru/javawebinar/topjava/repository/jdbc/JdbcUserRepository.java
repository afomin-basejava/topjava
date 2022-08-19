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
import java.util.stream.Collectors;

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
        class UserRoles {
            static int id;
            static Role role;

            private UserRoles(int id, String role) {
                UserRoles.id = id;
                UserRoles.role = Role.valueOf(role);
            }
        }

        Map<Integer, Set<Role>> map = jdbcTemplate
                .queryForStream("SELECT * FROM user_roles", (rs, rowNum) -> new UserRoles(rs.getInt(1), rs.getString(2)))
                .collect(Collectors.groupingBy(userId->UserRoles.id,
                        Collectors.mapping(role->UserRoles.role,
                                Collectors.toSet())));

        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        users.forEach(user -> user.setRoles(map.get(user.id())));
        return users;
    }

    private List<User> getAllResultSetOnePassRSE() {
        final String query = "SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id ORDER BY name, email";
        return jdbcTemplate.query(query, new ResultSetExtractor<>() {
            private final List<User> users = new ArrayList<>();
            private int currentId;
            private boolean isContinue = true;

            @Override
            public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (!rs.next()) {
                    return users;
                }
                currentId = rs.getInt("id");
                while (isContinue) {
                    users.add(getUser(currentId, rs));
                }
                return users;
            }

            private User getUser(int userId, ResultSet rs) throws SQLException {
                Collection<Role> roles = new HashSet<>();
                User user = ROW_MAPPER.mapRow(rs, 7);
                if (user != null) {
                    user.setRoles(roles);
                }
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

    private List<User> getAllResultSetOnePassRSEByRecursion() {
        final var query = "SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id ORDER BY name, email";
        List<User> users = new ArrayList<>();
        ResultSetExtractor<List<User>> rse = new ResultSetExtractor<>() {
            int current;

            @Override
            public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (!rs.next()) return users;
                current = rs.getInt("id");
                return getUsers(current, rs);
            }

            private List<User> getUsers(int userId, ResultSet rs) throws SQLException {
                User user = ROW_MAPPER.mapRow(rs, 7);
                user.setRoles(Collections.emptySet());
                Collection<Role> roles = new HashSet<>(Collections.emptySet());
                while (userId == rs.getInt("id")) {
                    if (rs.getString("role") != null)
                        roles.add(Role.valueOf(rs.getString("role")));
                    if (!rs.next()) {
                        user.setRoles(roles);
                        users.add(user);
                        return users;
                    }
                }
                user.setRoles(roles);
                users.add(user);
                current = rs.getInt("id");
                return getUsers(current, rs);
            }
        };
        return jdbcTemplate.query(query, rse);
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
