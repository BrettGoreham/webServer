package model;

import java.util.List;

public class MealOptionRecipe {
    private int id;
    private int mealOptionId;
    private String title;
    private String ingredients;
    private String instructions;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMealOptionId() {
        return mealOptionId;
    }

    public void setMealOptionId(int mealOptionId) {
        this.mealOptionId = mealOptionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
