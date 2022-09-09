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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(MEAL_MATCHER.contentJson(meal1));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
//        assertThrows(org.springframework.web.util.NestedServletException.class, this::get);
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MEAL_TO_MATCHER.contentJson(MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay())));
    }

    @Test
    void createWithLocation() throws Exception {
        Meal newMeal = MealTestData.getNew();
        ResultActions action = perform(post(REST_URL)
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
        ResultActions resultActions = perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent())
                .andDo(print());
        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, USER_ID), updated);
    }

// Resolved Exception:
//          Type = org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException
// when use #getBetweenUsingConverters simultaneously
//    @Test
//    void getBetween() throws Exception {
//        List<MealTo> toList = List.of(
//                MealsUtil.createTo(meal5, true),
//                MealsUtil.createTo(meal4, true),
//                MealsUtil.createTo(meal1, false)
//        );
//
//        perform(MockMvcRequestBuilders.get(REST_URL + "between")
//                .param("start", "2020-01-30T00:00:00")
//                .param("end", "2020-01-31T10:01:00"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(MEAL_TO_MATCHER.contentJson(toList));
//    }

    @Test
    void getBetweenUsingConverters() throws Exception {
        List<MealTo> toList = List.of(
                MealsUtil.createTo(meal5, true),
                MealsUtil.createTo(meal4, true),
                MealsUtil.createTo(meal1, false)
        );

        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2020-01-30")
                .param("startTime", "00:00:00")
                .param("endDate", "2020-01-31")
                .param("endTime", "10:01:00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MEAL_TO_MATCHER.contentJson(toList));
    }
}