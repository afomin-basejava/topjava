package ru.javawebinar.topjava.util.excesswrapper;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.excesswrapper.UserMealWithExcessWrapperAtomicBoolean;
import ru.javawebinar.topjava.util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.UserMealTestData.*;

public class UserMealsUtilAtomicBoolean {

    public static void main(String[] args) {

        System.out.println("filteredByCycle:");
        sortList(filteredByCycle(meals, START_TIME, END_TIME, CALORIES_PER_DAY)).forEach(System.out::println);
    }

    public static List<UserMealWithExcessWrapperAtomicBoolean> filteredByCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcessWrapperAtomicBoolean> mealsTo = new ArrayList<>();
        Map<LocalDate, Integer> dayCaloriesTotal = new HashMap<>();
        Map<LocalDate, AtomicBoolean> excessWrapperMap = new HashMap<>();
        meals.forEach(userMeal -> {
            AtomicBoolean excess = excessWrapperMap.computeIfAbsent(userMeal.getDate(), ld -> new AtomicBoolean());
            if (dayCaloriesTotal.merge(userMeal.getDate(), userMeal.getCalories(), Integer::sum) > caloriesPerDay) {
                excess.set(true);
            }
            if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime)) {
                mealsTo.add(new UserMealWithExcessWrapperAtomicBoolean(userMeal, excess));
            }
        });
        return mealsTo;
    }

    private static List<UserMealWithExcessWrapperAtomicBoolean> sortList(List<UserMealWithExcessWrapperAtomicBoolean> mealTo) {
        return mealTo.stream()
                .sorted((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()))
                .collect(Collectors.toList());
    }

}
