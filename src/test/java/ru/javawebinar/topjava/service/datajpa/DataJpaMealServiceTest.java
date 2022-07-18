package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealServiceTest;

import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaMealServiceTest extends MealServiceTest {
    @Test
    public void getMealWithUserTest() {
        Meal meal = service.getWithUser(MealTestData.MEAL1_ID, USER_ID);
        User user = meal.getUser();
        MealTestData.MEAL_MATCHER.assertMatch(meal, MealTestData.meal1);
        UserTestData.USER_MATCHER.assertMatch(user, UserTestData.user);
    }
}
