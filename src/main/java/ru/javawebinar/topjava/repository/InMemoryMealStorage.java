package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.MealsUtil.meals;

public class InMemoryMealStorage implements MealStorage {
    private final Map<Integer, Meal> mealStorage = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger();

    public InMemoryMealStorage() {
        meals.forEach(this::create);
    }

    @Override
    public Meal create(Meal meal) {
        return mealStorage.computeIfAbsent(counter.incrementAndGet(), id ->
                new Meal(counter.get(), meal.getDateTime(), meal.getDescription(), meal.getCalories())
        );
    }

    @Override
    public boolean delete(int id) {
        return mealStorage.remove(id) != null;
    }

    @Override
    public Meal update(Meal meal) {
        return mealStorage.computeIfPresent(meal.getId(), (id, preMeal) -> meal);
    }

    @Override
    public Meal get(int id) {
        return mealStorage.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(mealStorage.values());
    }
}
