package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import java.util.List;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    public static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal save(Integer userId, Meal meal) {
        log.info("save user's meal: userId - {}, {}", userId, meal);
        return service.save(authUserId(), meal);
    }

    public boolean delete(Integer userId, int id) {
        log.info("delete: user - {}, meal {}", userId, get(userId, id));
        return service.delete(authUserId(), id);
    }

    public Meal get(int userId, int id) {
        log.info("get: user - {}, meal {}", userId, id);
        return service.get(authUserId(), id);
    }

    public List<Meal> getAll(int userId) {
        log.info("getAll");
        return service.getAll(userId);
    }

}