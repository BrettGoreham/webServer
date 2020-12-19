package webServer.admin;

import model.MealCategory;
import model.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import webServer.WhatIsForDinnerService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminConsoleController {

    @Autowired
    WhatIsForDinnerService whatIsForDinnerService;

    @GetMapping(value = "")
    public String showAdminHome(Model model) {
        List<MealCategory> mealCategories = whatIsForDinnerService.getMealCategories(false);
        model.addAttribute("unconfirmed", mealCategories.stream().filter(mealCategory -> StatusEnum.SUGGESTED.equals(mealCategory.getStatus())).collect(Collectors.toList()));
        return "admin";
    }



}
