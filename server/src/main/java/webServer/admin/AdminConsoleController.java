package webServer.admin;

import model.MealCategory;
import model.MealOption;
import model.StatusEnum;
import model.user.Roles;
import model.user.SimpleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import webServer.WhatIsForDinnerService;
import webServer.userManagement.UserDetailServiceImpl;
import webServer.userManagement.UserService;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminConsoleController {

    @Autowired
    WhatIsForDinnerService whatIsForDinnerService;

    @Autowired
    UserService userService;

    @GetMapping(value = "")
    public String showAdminHome(Model model) {
        List<MealCategory> mealCategories = whatIsForDinnerService.getMealCategories(false);
        List<MealOption> mealOptions =  whatIsForDinnerService.GetMealOptionsWithStatus(StatusEnum.SUGGESTED);

        List<SimpleUser> users = userService.GetUserList(); //aint enough users for this to be a problem

        model.addAttribute("unconfirmed", mealCategories.stream().filter(mealCategory -> StatusEnum.SUGGESTED.equals(mealCategory.getStatus())).collect(Collectors.toList()));
        model.addAttribute("unconfirmedOptions", mealOptions);
        model.addAttribute("users", users);
        model.addAttribute("roles", Arrays.asList(Roles.values()));
        return "admin";
    }



}
