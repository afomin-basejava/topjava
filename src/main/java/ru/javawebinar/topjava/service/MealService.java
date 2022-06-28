package ru.javawebinar.topjava.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.AbstractBaseEntity;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {
    public static final Logger log = LoggerFactory.getLogger(MealService.class);

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal save(Integer userId, Meal meal) {
        log.info("save");
        return checkNotFoundWithId(repository.save(userId, meal), meal.getId());
    }

    public boolean delete(Integer userId, int id) {
        log.info("delete");
        boolean check;
        check = repository.delete(userId, id);
        checkNotFoundWithId(check, id);
        return check;
    }

    public Meal get(Integer userId, int id) {
        log.info("get");
        return checkNotFoundWithId(repository.get(userId, id), id);
    }

    public List<Meal> getAll(int userId) {
        log.info("getAll");
        return repository.getAll(userId);
    }


}