package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final Meal meal100003 = new Meal(100003, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
    public static final Meal meal100004 = new Meal(100004, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
    public static final Meal meal100005 = new Meal(100005, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    public static final Meal meal100006 = new Meal(100006, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal meal100007Admin = new Meal(100007, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Admin Завтрак", 1000);
    public static final Meal meal100008 = new Meal(100008, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal meal100009 = new Meal(100009, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);
    public static final Meal meal100010Admin = new Meal(100010, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Admin Обед", 500);
    public static final Meal meal100011Admin = new Meal(100011, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Admin Ужин", 410);

    private static List<Meal> meals = Stream.of(meal100003, meal100004, meal100005, meal100006, meal100008, meal100009)
            .sorted((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime())).collect(Collectors.toList());;

    public static final List<Meal> getMeals() {
        return meals;
    }

    public static final List<Meal> getFilteredMealsInclusive(final LocalDate from, final LocalDate to) {
        return meals.stream()
                .filter(meal -> meal.getDate().compareTo(from) >= 0 && meal.getDate().compareTo(to) <= 0)
                .collect(Collectors.toList());
    }
    public static final List<Meal> getBetweenHalfOpen(final LocalDateTime from, final LocalDateTime to) {
        return meals.stream()
                .filter(meal -> meal.getDateTime().compareTo(from) >= 0 && meal.getDateTime().compareTo(to) < 0)
                .collect(Collectors.toList());
    }
}
