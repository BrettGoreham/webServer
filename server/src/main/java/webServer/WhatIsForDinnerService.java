package webServer;

import database.MealDao;
import model.MealCategory;
import model.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhatIsForDinnerService {

    @Autowired
    private MealDao mealDao;

    public List<MealCategory> getMealCategories(boolean withMealOptions) {
        if(withMealOptions) {
            return mealDao.getAllMealCategoriesWithMealOptions();
        }
        else {
            return mealDao.getAllMealCategories();
        }
    }

    public List<MealCategory> getConfirmedMealCategories() {
        return mealDao.getAllConfirmedMealCategoriesWithConfirmedMealOptions();
    }

    public MealCategory categorySuggestionAddition(MealCategory mealCategory) {
        return mealDao.addSuggestedMealCategory(mealCategory);
    }

    public void updateCategoriesAndOptionsToAStatus(List<Integer> categoriesIds, List<Integer> optionsIds, StatusEnum statusEnum) {
        mealDao.updateCategoriesAndOptionsToAStatus(categoriesIds, optionsIds, statusEnum);
    }
}
