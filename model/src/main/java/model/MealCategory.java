package model;

import java.util.ArrayList;
import java.util.List;

public class MealCategory {
    private int id;
    private String categoryName;
    private StatusEnum status;
    private List<MealOption> mealOptions;

    public MealCategory(){}
    public MealCategory( String categoryName, StatusEnum status) {
        this.categoryName = categoryName;
        this.status = status;
    }
    public MealCategory(int id, String categoryName, StatusEnum status) {
        this.id = id;
        this.categoryName = categoryName;
        this.status = status;
    }

    public MealCategory(int id, String categoryName, StatusEnum status, List<MealOption> mealOptions) {
        this.id = id;
        this.categoryName = categoryName;
        this.status = status;
        this.mealOptions = mealOptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public List<MealOption> getMealOptions() {
        if(mealOptions == null) {
            mealOptions = new ArrayList<>();
        }
        return mealOptions;
    }

    public void setMealOptions(List<MealOption> mealOptions) {
        this.mealOptions = mealOptions;
    }

}
