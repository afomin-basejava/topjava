package ru.javawebinar.topjava.web;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {
    private static int userId = 1;  /*default user*/

    public static int authUserId() {
        return userId;
    }

    public static void setAuthUserId(int userType) {
        userId = userType;
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }

    public String getAuthUserId() {
        return String.valueOf(userId);
    }
}