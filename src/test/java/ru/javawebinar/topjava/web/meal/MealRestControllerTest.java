package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.web.meal.MealRestController.REST_URL;

class MealRestControllerTest extends AbstractControllerTest {

    @Autowired
    private MealService mealService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1))
        ;
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpectAll(status().is2xxSuccessful(), status().isNoContent())
        ;
//        assertThrows(org.springframework.web.util.NestedServletException.class, this::get);
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MEALTO_MATCHER.contentJson(MealsUtil.getTos(meals, MealsUtil.DEFAULT_CALORIES_PER_DAY)))
        ;
    }

    @Test
    void createWithLocation() throws Exception {
        Meal newMeal = MealTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMeal)))
                .andExpect(status().isCreated());

        Meal created = MEAL_MATCHER.readFromJson(action);
        int newId = created.id();
        newMeal.setId(newId);
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(mealService.get(newId, SecurityUtil.authUserId()), newMeal);
    }

    @Test
    void update() throws Exception {
        Meal updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpectAll(status().is2xxSuccessful())
                .andDo(print());
        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, USER_ID), updated);
    }

    @Test
    void getBetween() throws Exception {
        LocalDateTime ldtStart = LocalDateTime.of(2020, Month.JANUARY, 30, 0, 0);
        LocalDateTime ldtEnd = LocalDateTime.of(2020, Month.JANUARY, 31, 10, 1);
        List<MealTo> toList = List.of(
                new MealTo(meal5.getId(), meal5.getDateTime(), meal5.getDescription(), meal5.getCalories(), true),
                new MealTo(meal4.getId(), meal4.getDateTime(), meal4.getDescription(), meal4.getCalories(), true),
                new MealTo(meal1.getId(), meal1.getDateTime(), meal1.getDescription(), meal1.getCalories(), false)
        );

        perform(MockMvcRequestBuilders.get(REST_URL + "between?start=" + ldtStart + "&end=" + ldtEnd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MEALTO_MATCHER.contentJson(toList))
                .andReturn();
    }
}