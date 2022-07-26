package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/meals")
public class JspMealController extends MealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    MealService mealService;

    public JspMealController(MealService service) {
        super(service);
    }

    @GetMapping("/delete")
    public String delete(HttpServletRequest request) {
        log.debug("JspMealController: delete");
        int id = Integer.parseInt(request.getParameter("id"));
        super.delete(id);
        return "redirect:/meals";
    }
}
