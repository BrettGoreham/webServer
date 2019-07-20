package webServer;

import database.MealDao;
import model.MealCategory;
import model.MealOption;
import model.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dinner")
public class WhatAmIHavingForDinnerRestServlet {

    @Autowired
    private WhatIsForDinnerService whatIsForDinnerService;


    @GetMapping("/getMealCategories")
    public List<MealCategory> getMealCategories(@RequestParam(required = false, defaultValue = "false") boolean withMealOptions) {
        return whatIsForDinnerService.getMealCategories(withMealOptions);
    }

    @GetMapping("/getMealCategories/confirmed")
    public List<MealCategory> getConfirmedMealCategories() {
        return whatIsForDinnerService.getConfirmedMealCategories();
    }



    @PostMapping("/addSuggestion")
    public MealCategory categorySuggestionAddition(@RequestParam String categoryName) {
        return whatIsForDinnerService.categorySuggestionAddition(
            new MealCategory(categoryName, StatusEnum.SUGGESTED)
        );
    }

    @PostMapping("/addSuggestionWithMeals")
    public MealCategory categorySuggestionAddition(@RequestParam String categoryName, @RequestBody List<String> mealNames) {
        MealCategory mealCategory = new MealCategory(categoryName, StatusEnum.SUGGESTED);

        List<MealOption> options = mealCategory.getMealOptions();
        for (String meal : mealNames) {
            options.add(new MealOption(meal, StatusEnum.SUGGESTED));
        }

        return whatIsForDinnerService.categorySuggestionAddition(mealCategory);
    }


    public static class AcceptSuggestionsRequestWrapper {
        private List<Integer> categoriesIds;
        private List<Integer> optionsIds;

        public List<Integer> getCategoriesIds() {return categoriesIds;}
        public void setCategoriesIds(List<Integer> categoriesIds) {this.categoriesIds = categoriesIds;}
        public List<Integer> getOptionsIds() {return optionsIds;}
        public void setOptionsIds(List<Integer>  optionsIds) {this.optionsIds = optionsIds;}
    }

    @PostMapping("/acceptSuggestions")
    public void acceptSuggestions(@RequestBody AcceptSuggestionsRequestWrapper AcceptSuggestionsRequestWrapper) {


        whatIsForDinnerService.updateCategoriesAndOptionsToAStatus(
            AcceptSuggestionsRequestWrapper.getCategoriesIds(),
            AcceptSuggestionsRequestWrapper.getOptionsIds(),
            StatusEnum.CONFIRMED
        );
    }
}
