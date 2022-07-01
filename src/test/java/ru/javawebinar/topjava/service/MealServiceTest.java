package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.assertMatch;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
@RunWith(SpringRunner.class)
public class MealServiceTest {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int GUEST_ID = START_SEQ + 2;
    public static final int NOT_EXIST_ID = 10;

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    MealService service;

    @Test
    public void get() {
        Meal test = service.get(100003, USER_ID);
        assertThat(test).usingRecursiveComparison().isEqualTo(meal100003);
    }

    @Test
    public void delete() {
        service.delete(100011, ADMIN_ID);
        assertThrows(NotFoundException.class, () -> service.get(100011, ADMIN_ID));
    }
//
    @Test
    public void getBetweenInclusive() {
        final LocalDateTime from = LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0);
        final LocalDateTime to = LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0);
        final List<Meal> betweenInclusive = service.getBetweenInclusive(from.toLocalDate(), to.toLocalDate(), USER_ID);
        assertThat(betweenInclusive)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(MealTestData.getFilteredMealsInclusive(from.toLocalDate(), to.toLocalDate()));
    }

    @Test
    public void getAll() {
        List<Meal> userMeals = service.getAll(USER_ID);
        assertThat(userMeals).usingRecursiveFieldByFieldElementComparator().isEqualTo(MealTestData.getMeals());
    }

    @Test
    public void update() {
        Meal updated = service.get(100004, USER_ID);
        updated.setDescription("updated description");
        updated.setCalories(999);
        service.update(updated, USER_ID);
        Meal actual = service.get(100004, USER_ID);
        assertThat(actual)
                .matches(meal -> updated.getDescription().equals(meal.getDescription()) &&
                                 updated.getCalories() == meal.getCalories() &&
                                 updated.getDateTime().equals(meal.getDateTime()) &&
                                 Objects.equals(updated.getId(), meal.getId()),
                        "comparasion by all fields"
                );
//        assertThat(service.get(100004, USER_ID)).usingRecursiveComparison().isEqualTo(updated);
    }

    @Test
    public void create() {
        Meal newMeal = service.create(new Meal(LocalDateTime.now(), "newMeal", 999), USER_ID);
        assertThat(service.get(newMeal.getId(), USER_ID)).usingRecursiveComparison().isEqualTo(newMeal);
    }

    /*5.2 Сделать тесты на чужую еду (delete, get, update) с тем,
    чтобы получить NotFoundException и duplicateDateTimeCreate, аналогичный duplicateMailCreate.*/
    @Test
    public void deleteNotFound() {
        assertThrows("NOT_EXIST_ID", NotFoundException.class, () -> service.delete(NOT_EXIST_ID, USER_ID));
    }

    @Test
    public void getNotFound() {
        assertThrows("NOT_EXIST_ID", NotFoundException.class, () -> service.get(NOT_EXIST_ID, USER_ID));
    }

    @Test
    public void updateNotFound() {
        assertThrows("NOT_EXIST_ID",
                NotFoundException.class, () -> service.get(NOT_EXIST_ID, USER_ID));
    }

    @Test
    public void updateNotOwnMeal() {
        assertThrows(NotFoundException.class, () -> service.update(meal100011Admin, USER_ID));
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new Meal(meal100003), USER_ID));
    }
}