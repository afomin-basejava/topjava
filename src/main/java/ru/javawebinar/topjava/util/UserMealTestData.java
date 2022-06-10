package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

public class UserMealTestData {
    public static final int CALORIES_PER_DAY = 2000;
    public static final int HOUR_FROM = 0;
    public static final int MINUTE_FROM = 0;
    public static final int HOUR_TO = 23;
    public static final int MINUTE_TO = 59;
    public static final LocalTime START_TIME = LocalTime.of(HOUR_FROM, MINUTE_FROM);
    public static final LocalTime END_TIME = LocalTime.of(HOUR_TO, MINUTE_TO);
    public static List<UserMeal> meals = Arrays.asList(
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
            );
}
