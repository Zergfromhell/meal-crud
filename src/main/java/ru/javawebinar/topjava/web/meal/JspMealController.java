package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//@Controller
public class JspMealController {
    private final MealService service;

    //@Autowired
    public JspMealController(MealService service) {
        this.service = service;
    }

    //@GetMapping("/")
    public String root() {
        return "index";
    }

    //@GetMapping("/meals")
    public String getMeals(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("meals", service.getAll(SecurityUtil.authUserId()));
        /*request.getRequestDispatcher("/meals.jsp").forward(request, response);*/
        return "meals";
    }

}
