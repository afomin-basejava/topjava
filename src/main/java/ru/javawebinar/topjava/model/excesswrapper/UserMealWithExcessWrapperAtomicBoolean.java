package ru.javawebinar.topjava.model.excesswrapper;

import ru.javawebinar.topjava.model.UserMeal;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserMealWithExcessWrapperAtomicBoolean {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    private final AtomicBoolean excess;

    public UserMealWithExcessWrapperAtomicBoolean(LocalDateTime dateTime, String description, int calories, AtomicBoolean excess) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
    }

    public UserMealWithExcessWrapperAtomicBoolean(UserMeal userMeal, AtomicBoolean excess) {
        this(userMeal.getDateTime(),userMeal.getDescription(),userMeal.getCalories(), excess);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "UserMealWithExcessWrapper{" +
               "dateTime=" + dateTime +
               ", description='" + description + '\'' +
               ", calories=" + calories +
               ", excess=" + excess.get() +
               '}';
    }

}
