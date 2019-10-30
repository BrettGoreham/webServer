package model;

import java.util.List;

public class MealOption {
    private int id;
    private String mealName;
    private StatusEnum status;
    private String DescriptionOfMealOption;
    private List<String> mainIngredients;
    private List<MealOptionRecipe> mealOptionRecipes;
    private int fkMealCategory;

    public MealOption() {}
    public MealOption(String mealName, StatusEnum status) {
        this.mealName = mealName;
        this.status = status;
    }
    public MealOption(int id, String mealName, StatusEnum status, int fkMealCategory) {
        this.id = id;
        this.mealName = mealName;
        this.status = status;
        this.fkMealCategory = fkMealCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public int getFkMealCategory() {
        return fkMealCategory;
    }

    public void setFkMealCategory(int fkMealCategory) {
        this.fkMealCategory = fkMealCategory;
    }

    public String getDescriptionOfMealOption() {
        return DescriptionOfMealOption;
    }

    public void setDescriptionOfMealOption(String descriptionOfMealOption) {
        DescriptionOfMealOption = descriptionOfMealOption;
    }

    public List<String> getMainIngredients() {
        return mainIngredients;
    }

    public void setMainIngredients(List<String> mainIngredients) {
        this.mainIngredients = mainIngredients;
    }

    public List<MealOptionRecipe> getMealOptionRecipes() {
        return mealOptionRecipes;
    }

    public void setMealOptionRecipes(List<MealOptionRecipe> mealOptionRecipes) {
        this.mealOptionRecipes = mealOptionRecipes;
    }
}
