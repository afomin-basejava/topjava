package ru.javawebinar.topjava.util.excesswrapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ExcessWrapper {
    static Map<LocalDate, ExcessWrapper> excessWrapperMap = new HashMap<>();
    Excess excess;
    int mealCalories;
    int caloriesPerDay;

    public ExcessWrapper(int mealCalories, int caloriesPerDay) {
        this.caloriesPerDay = caloriesPerDay;
        this.mealCalories += mealCalories;
        excess = new Excess(this.mealCalories > caloriesPerDay);
    }

    public void setExcess(int mealCalories) {
        this.mealCalories += mealCalories;
        excess.set(this.mealCalories > caloriesPerDay);
    }

    public Excess getExcess() {
        return excess;
    }
}
