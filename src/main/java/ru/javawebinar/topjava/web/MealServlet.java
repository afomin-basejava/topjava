package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.InMemoryMealStorage;
import ru.javawebinar.topjava.repository.MealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.CALORIES_PER_DAY;
import static ru.javawebinar.topjava.util.MealsUtil.meals;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private MealStorage storage;

    @Override
    public void init() {
        storage = new InMemoryMealStorage();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        log.debug("doGet action - {}", action);
        switch (action == null ? "default" : action) {
            case "create": {
                log.debug("doGet meals create");
                final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 999);
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealEditForm.jsp").forward(request, response);
            }
            break;
            case "update":
                String id = request.getParameter("id");
                log.debug("doGet meals update {}", id);
                final Meal meal = storage.get(Integer.parseInt(id));
                request.setAttribute("meal", meal);
                request.setAttribute("action", action);
                request.getRequestDispatcher("/mealEditForm.jsp").forward(request, response);
                break;
            case "delete":
                storage.delete(Integer.parseInt(request.getParameter("id")));
                log.debug("doGet meals delete {}", request.getParameter("id"));
                response.sendRedirect("meals");
                break;
            case "default":
            default:
                List<MealTo> mealsTo = MealsUtil.filteredByStreams(storage.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
                request.setAttribute("meals", mealsTo);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        LocalDateTime ldt = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        if (id.isEmpty()) {
            storage.create(new Meal(null, ldt, description, calories));
        } else {
            storage.update(new Meal(Integer.valueOf(id), ldt, description, calories));
        }
        response.sendRedirect("meals");
    }
}
