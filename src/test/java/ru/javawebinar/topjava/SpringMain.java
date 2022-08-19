package ru.javawebinar.topjava;

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.RootController;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)
        try (GenericXmlApplicationContext appCtx = new GenericXmlApplicationContext()) {
            appCtx.load("classpath:spring/inmemory.xml");
            appCtx.refresh();

            System.out.println("Bean definition names: ");
            Arrays.stream(appCtx.getBeanDefinitionNames()).forEach(System.out::println);
            System.out.println();

            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));
            System.out.println();

            MealRestController mealController = appCtx.getBean(MealRestController.class);
            List<MealTo> filteredMealsWithExcess =
                    mealController.getBetween(
                            LocalDate.of(2020, Month.JANUARY, 30), LocalTime.of(7, 0),
                            LocalDate.of(2020, Month.JANUARY, 31), LocalTime.of(11, 0));
            filteredMealsWithExcess.forEach(System.out::println);
            System.out.println();
            System.out.println(mealController.getBetween(null, null, null, null));
            System.out.println();

            RootController rootController = appCtx.getBean(RootController.class);
            System.out.println(rootController.root());

            Model model = new ConcurrentModel();
            System.out.println(rootController.getAll(model));
            System.out.println(model.asMap().get("meals"));
            System.out.println(rootController.getUsers(model));
            System.out.println(model.asMap().get("users"));
        }
    }
}
