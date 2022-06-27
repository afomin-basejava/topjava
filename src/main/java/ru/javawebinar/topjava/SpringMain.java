package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static ru.javawebinar.topjava.util.MealsUtil.meals;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: ");
            Arrays.asList(appCtx.getBeanDefinitionNames()).forEach(System.out::println);
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));

            int userId = SecurityUtil.authUserId();
            MealRestController mealRestController = appCtx.getBean(MealRestController.class);
            meals.forEach(meal -> mealRestController.save(userId, meal));
            System.out.println("Before deletion =============================");
            mealRestController.getAll(userId).forEach(System.out::println);
            mealRestController.delete(userId, 2);
            System.out.println("After deletion =============================");
            mealRestController.getAll(userId).forEach(System.out::println);
            System.out.println("Add test meal  =============================");
            mealRestController.save(userId,
                    new Meal(LocalDateTime.of(2022, Month.JANUARY, 31, 0, 0), "Test meal", 100));
            mealRestController.getAll(userId).forEach(System.out::println);
            System.out.println("checkNotFoundWithId========================");
            mealRestController.get(userId, 2);
//            mealRestController.get(userId, 2000);
//            mealRestController.delete(userId, 2);
// update: userId is hardcoded in controller now
        }
    }
}
