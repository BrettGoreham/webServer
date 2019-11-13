package webServer;

import model.MealCategory;
import model.MealOption;
import model.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/whatIsForDinner")
public class WhatAmIHavingForDinnerServlet {

    @Autowired
    private WhatIsForDinnerService whatIsForDinnerService;
    @Autowired
    private RecaptchaValidationService recaptchaValidationService;

    @GetMapping("")
    public String index(Model model) {

        List<MealCategory> meals = whatIsForDinnerService.getConfirmedMealCategories();
        Collections.shuffle(meals);
        for(MealCategory mealCategory :  meals) {
            Collections.shuffle(mealCategory.getMealOptions());
        }
        model.addAttribute("mealCategories", meals);
        return "dinner";
    }

    @GetMapping("suggestionsForm")
    public String suggestions(Model model) {
        List<MealCategory> meals = whatIsForDinnerService.getMealCategories(true);

        Comparator<MealCategory> mealCategoryComparator = Comparator.comparing(MealCategory::getCategoryName);
        Collections.sort(meals, mealCategoryComparator);

        Comparator<MealOption> mealOptionComparator = Comparator.comparing(MealOption::getMealName);
        for(MealCategory meal : meals) {
            Collections.sort(meal.getMealOptions(), mealOptionComparator);
        }

        model.addAttribute("mealCategories", meals);
        return "suggestionsForm";
    }

    @GetMapping(path = "{mealcategory}/{mealName}/info")
    public String mealOptionInfo(@PathVariable(value = "mealcategory") String mealCategory,
                                 @PathVariable(value = "mealName") String mealName,
                                 Model model) {

        MealOption mealOption = whatIsForDinnerService.getMealOptionWithRecipesFromStrings(mealCategory, mealName);
        model.addAttribute("mealCategory", mealCategory);
        model.addAttribute("mealName", mealName);
        model.addAttribute("mealOption", mealOption);
        return "mealInfo";
    }

    @GetMapping("suggestionsForm/submit")
    public RedirectView  submit(@RequestParam String recaptchaCode, @RequestParam String categoryName, @RequestParam(required = false, value="mealOptions") String[] mealOptions) {

        boolean isCaptchaCorrect =  recaptchaValidationService.checkRecaptchaString(recaptchaCode);
        if(isCaptchaCorrect) {
            List<MealCategory> a = whatIsForDinnerService.getMealCategories(true);

            MealCategory mealCategory = null;
            for (int i = 0; i < a.size(); i++) {
                if (categoryName.equalsIgnoreCase(a.get(i).getCategoryName())) {
                    mealCategory = a.get(i);
                }
            }

            if (mealCategory == null) {
                mealCategory = new MealCategory(categoryName, StatusEnum.SUGGESTED);
            }

            List<MealOption> suggestions = new ArrayList<>();
            if (mealOptions != null) {
                for (String meal : mealOptions) {
                    suggestions.add(new MealOption(meal, StatusEnum.SUGGESTED));
                }
            }

            mealCategory.setMealOptions(suggestions);

            whatIsForDinnerService.categorySuggestionAddition(mealCategory);
        }
        else {
            throw new RuntimeException("Captcha Failed to validate.");
        }

        return new RedirectView("/whatIsForDinner/suggestionsForm");
    }

}
