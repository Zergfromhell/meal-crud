package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;

@Controller
public class RootController {

    private final UserService service;
    private final MealService mealService;

    @Autowired
    public RootController(UserService service, MealService mealService) {
        this.service = service;
        this.mealService = mealService;
    }

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @PostMapping("/index.jsp")
    public String home() {
        return "index";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", service.getAll());
        return "users";
    }

    @PostMapping("/users")
    public String setUser(HttpServletRequest request) {
        int userId = Integer.parseInt(request.getParameter("userId"));
        SecurityUtil.setAuthUserId(userId);
        return "redirect:meals";
    }

    @GetMapping("/meals")
    public String getMeals(Model model) {
        model.addAttribute("meals", mealService.getAllMealTo(SecurityUtil.authUserId()));
        return "meals";
    }

    @GetMapping("/meals/create")
    public String createMeal(Model model) {
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }

    @PostMapping("/meals")
    public String saveMeal(HttpServletRequest request) {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (request.getParameter("id").isEmpty()) {
            mealService.create(meal, SecurityUtil.authUserId());
        } else {
            assureIdConsistent(meal, Integer.parseInt(request.getParameter("id")));
            mealService.update(meal, SecurityUtil.authUserId());
        }
        return "redirect:meals";
    }

    @GetMapping("/meals/filter")
    public String filterMeals(HttpServletRequest request, Model model) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        model.addAttribute("meals", mealService.getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    @GetMapping("/meals/delete")
    public String deleteMeal(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        mealService.delete(id);
        return "redirect:/meals";
    }

    @GetMapping("/meals/update")
    public String updateMeal(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        model.addAttribute("meal", mealService.get(id, SecurityUtil.authUserId()));
        return "mealForm";
    }
}
