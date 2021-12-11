package model.user;

import java.util.List;

public class UserMealCollection {

    private int id;
    private String collectionName;
    private long userId;
    private List<UserMeal> userMeals;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<UserMeal> getUserMeals() {
        return userMeals;
    }

    public void setUserMeals(List<UserMeal> userMeals) {
        this.userMeals = userMeals;
    }
}
