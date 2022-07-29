package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class MealController extends AbstractMealController{
    protected static final Logger log = LoggerFactory.getLogger(MealController.class);
}