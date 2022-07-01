package ru.javawebinar.topjava.repository.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.meal100003;
import static ru.javawebinar.topjava.MealTestData.meal100005;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
//
@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
@RunWith(SpringRunner.class)
public class JdbcMealRepositoryTest {

    @Autowired
    JdbcMealRepository jdbcMealRepository;

    @Test
    public void save() {
        Meal saved = jdbcMealRepository.save(new Meal(LocalDateTime.now(), "NEW MEAL", 999), USER_ID);
        assertThat(jdbcMealRepository.get(saved.getId(), USER_ID)).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    public void delete() {
        assertThat(jdbcMealRepository.delete(100003, USER_ID)).isTrue();
    }

    @Test
    public void get() {
        final Meal meal = jdbcMealRepository.get(100003, USER_ID);
        assertThat(meal).usingRecursiveComparison().isEqualTo(meal100003);
    }

    @Test
    public void getAll() {
        List<Meal> userMeals = jdbcMealRepository.getAll(USER_ID);
        assertThat(userMeals).usingRecursiveFieldByFieldElementComparator().isEqualTo(MealTestData.getMeals());
    }

    @Test
    public void getBetweenHalfOpen() {
        final LocalDateTime from = LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0);
        LocalDateTime to = LocalDateTime.of(2020, Month.JANUARY, 30, 0, 0);
        List<Meal> mealsBetweenHalfOpen = jdbcMealRepository
                .getBetweenHalfOpen(from, to, USER_ID);
        assertThat(mealsBetweenHalfOpen).usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(MealTestData.getBetweenHalfOpen(from, to));
    }
}