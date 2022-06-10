package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.UserMealTestData.*;

public class UserMealsUtil {

    public static void main(String[] args) {

        System.out.println("filteredByCycles:");
        List<UserMealWithExcess> mealsTo = sortList(filteredByCycles(meals, START_TIME, END_TIME, CALORIES_PER_DAY));
        mealsTo.forEach(System.out::println);
        System.out.println("filteredByCyclesMerge:");
        sortList(filteredByCyclesMerge(meals, START_TIME, END_TIME, CALORIES_PER_DAY)).forEach(System.out::println);
        System.out.println("filteredByStreams:");
        sortList(filteredByStreams(meals, START_TIME, END_TIME, CALORIES_PER_DAY)).forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayCaloriesTotal = new HashMap<>();
        meals.forEach(userMeal -> dayCaloriesTotal.put(userMeal.getDate(), userMeal.getCalories() + dayCaloriesTotal.getOrDefault(userMeal.getDate(), 0)));
        return getUserMealWithExcesses(meals, startTime, endTime, caloriesPerDay, dayCaloriesTotal);
    }

    public static List<UserMealWithExcess> filteredByCyclesMerge(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayCaloriesTotal = new HashMap<>();
        meals.forEach(userMeal -> dayCaloriesTotal.merge(userMeal.getDate(), userMeal.getCalories(), Integer::sum));
        return getUserMealWithExcesses(meals, startTime, endTime, caloriesPerDay, dayCaloriesTotal);
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayCaloriesTotal = meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate,
                        Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime))
                .map(userMeal -> newUserMealWithExcess(userMeal, dayCaloriesTotal.get(userMeal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static List<UserMealWithExcess> getUserMealWithExcesses(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay, Map<LocalDate, Integer> dayCaloriesTotal) {
        List<UserMealWithExcess> mealsTo = new ArrayList<>();
        meals.forEach(userMeal -> {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime)) {
                mealsTo.add(newUserMealWithExcess(userMeal, dayCaloriesTotal.get(userMeal.getDate()) > caloriesPerDay));
            }
        });
        return mealsTo;
    }

    private static UserMealWithExcess newUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }

    private static List<UserMealWithExcess> sortList(List<UserMealWithExcess> mealTo) {
        return mealTo.stream()
                .sorted((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()))
                .collect(Collectors.toList());
    }

}
