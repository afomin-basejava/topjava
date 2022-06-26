package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.MealsUtil.meals;

@Repository
public class InMemoryMealRepository implements MealRepository {
    public static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    //    каждый конкретный ==аутентифицированный== пользователь
    //    реализовать хранение еды для каждого пользователя можно с добавлением поля userId в Meal ИЛИ без него
    private final Map<Integer, Set<Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger();
    private final Set<Meal> userMeals = new TreeSet<>(Comparator.comparing(Meal::getId));

    {
        meals.forEach(meal -> save(SecurityUtil.authUserId(), meal));
    }

    @Override
    public Meal save(Integer userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
        } else {
            userMeals.remove(meal);
        }
        userMeals.add(meal);
        repository.put(userId, userMeals);
        return meal;
    }

    @Override
    public boolean delete(Integer userId, int id) {
        boolean removed = userMeals.remove(findMealById(userMeals, id));
        repository.put(userId, userMeals);
        return removed;
    }

    @Override
    public Meal get(Integer userId, int id) {
        return findMealById(repository.get(userId), id);
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        log.debug("getAll for user {}", userId);
        return repository.get(userId).stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private Meal findMealById(Set<Meal> meals, int id) {
        return meals.stream()
                .filter(meal -> meal.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

