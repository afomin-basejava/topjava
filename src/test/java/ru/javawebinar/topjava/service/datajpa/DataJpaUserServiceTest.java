package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;

import java.util.List;

import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {
    @Test
    public void getUserMealsTest() {
        User user = service.getUserMeals(USER_ID);
        List<Meal> meals = user.getMeals();
        UserTestData.USER_MATCHER.assertMatch(user, UserTestData.user);
        MealTestData.MEAL_MATCHER.assertMatch(meals, MealTestData.meals);
    }
}
