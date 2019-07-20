package model;

public class MealOption {
    private int id;
    private String mealName;
    private StatusEnum status;
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
}
