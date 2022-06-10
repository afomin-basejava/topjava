package ru.javawebinar.topjava.model.excesswrapper;

import ru.javawebinar.topjava.util.excesswrapper.Excess;

import java.time.LocalDateTime;

public class UserMealWithExcessWrapper {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    private final Excess excess;

    public UserMealWithExcessWrapper(LocalDateTime dateTime, String description, int calories, Excess excess) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
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
