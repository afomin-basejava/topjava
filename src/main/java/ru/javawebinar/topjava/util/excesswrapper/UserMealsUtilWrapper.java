package ru.javawebinar.topjava.util.excesswrapper;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.excesswrapper.UserMealWithExcessWrapper;
import ru.javawebinar.topjava.util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.UserMealTestData.*;

public class UserMealsUtilWrapper {

    public static void main(String[] args) {

        System.out.println("filteredByCyclesWithWrapper:");
        sortList(filteredByCyclesWithWrapper(meals, START_TIME, END_TIME, CALORIES_PER_DAY))
                .forEach(System.out::println);

    }

    public static List<UserMealWithExcessWrapper> filteredByCyclesWithWrapper(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, ExcessWrapper> excessWrapperMap = new HashMap<>();
        List<UserMealWithExcessWrapper> mealsTo = new ArrayList<>();
        meals.forEach(userMeal -> {
            if (!excessWrapperMap.containsKey(userMeal.getDate())) {
                excessWrapperMap.put(userMeal.getDate(), new ExcessWrapper(userMeal.getCalories(), caloriesPerDay));
            } else {
                excessWrapperMap.get(userMeal.getDate()).setExcess(userMeal.getCalories());
            }
            if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime)) {
                mealsTo.add(newUserMealWithExcessWrapper(userMeal, excessWrapperMap.get(userMeal.getDate()).getExcess()));
            }
        });
        return mealsTo;
    }

    private static UserMealWithExcessWrapper newUserMealWithExcessWrapper(UserMeal userMeal, Excess excess) {
        return new UserMealWithExcessWrapper(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }

    private static List<UserMealWithExcessWrapper> sortList(List<UserMealWithExcessWrapper> mealTo) {
        return mealTo.stream()
                .sorted((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()))
                .collect(Collectors.toList());
    }


}

