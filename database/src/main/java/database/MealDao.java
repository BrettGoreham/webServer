package database;

import model.MealCategory;
import model.MealOption;
import model.MealOptionRecipe;
import model.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository

public class MealDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static String getMealCategories = "select * from MEAL_CATEGORIES";

    private final static String getMealCategoriesWithMealOptions =
        new StringBuilder()
            .append("select category.id as id, category.category_name, category.status as category_status,")
            .append("options.id as option_id, options.meal_name, options.status as option_status, options.fk_meal_category ")
            .append("from MEAL_CATEGORIES category left join MEAL_OPTIONS options ")
            .append("ON category.id = options.fk_meal_category ORDER BY id;").toString();

    private final static String getCONFIRMEDMealCategoriesWithMealOptions =
        new StringBuilder()
            .append("select category.id as id, category.category_name, category.status as category_status, ")
            .append("options.id as option_id, options.meal_name, options.status as option_status, options.fk_meal_category ")
            .append("from MEAL_CATEGORIES category left join MEAL_OPTIONS options ")
            .append("ON category.id = options.fk_meal_category ")
            .append("WHERE category.status = 'CONFIRMED' AND options.status = 'CONFIRMED' ")
            .append("ORDER BY id;").toString();

    private final static String getMealOptionInfoByCategoryAndOptionName =
        new StringBuilder()
            .append("SELECT options.id as id, options.description as description, options.main_ingredients as main_ingredients, category.id as category_id, ")
            .append("recipes.id as recipe_id, recipes.title as recipe_title, recipes.ingredients as recipe_ingredients, recipes.instructions as recipe_instructions ")
            .append("FROM MEAL_CATEGORIES category ")
            .append("INNER JOIN MEAL_OPTIONS options ON category.id = options.fk_meal_category ")
            .append("LEFT JOIN MEAL_OPTION_RECIPES recipes ON options.id = recipes.fk_meal_option ")
            .append("WHERE category.category_name = ? AND options.meal_name = ?").toString();



    private final static String insertMealCategoryWithNoMealOptions = "INSERT INTO MEAL_CATEGORIES ( category_name, status) VALUES (?,?)";

    private final static String insertMealOptionsForMealCategory = "INSERT INTO MEAL_OPTIONS (fk_meal_category, meal_name, status) VALUES (?, ?, ?)";

    private final static String insertNewMealOptionRecipe = "INSERT INTO MEAL_OPTION_RECIPES (fk_meal_option, title, ingredients, instructions) VALUES (?, ?, ?, ?)";

    private final static String updateMealOptionRecipe = "UPDATE MEAL_OPTION_RECIPES SET title = ?, ingredients = ?, instructions = ? WHERE id = ?";

    private final static String updateCategoriesStatus = "UPDATE MEAL_CATEGORIES SET status = :statval WHERE id IN (:ids);";

    private final static String updateOptionsStatus = "UPDATE MEAL_OPTIONS SET status = :statval WHERE id IN (:ids);";

    private final static String getCountOfSuggestedCategories = "SELECT category_name FROM MEAL_CATEGORIES WHERE status = 'SUGGESTED'";

    private final static String getCountOfSuggestedOptions = "SELECT meal_name FROM MEAL_OPTIONS WHERE status = 'SUGGESTED'";

    private final static String updateOptionDescriptionAndIngredients = "UPDATE MEAL_OPTIONS SET description = :description, main_ingredients = :ingredients WHERE id = :id;";

    public List<MealCategory> getAllMealCategories(){
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(getMealCategories);
        List<MealCategory> categories = new ArrayList<MealCategory>();

        for (Map<String, Object> row : rows)  {
            categories.add(
                new MealCategory(
                    (Integer) row.get("id"),
                    (String) row.get("category_name"),
                    StatusEnum.valueOf((String) row.get("status"))
                )
            );
        }

        return categories;
    }

    public List<MealCategory> getAllMealCategoriesWithMealOptions(){

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(getMealCategoriesWithMealOptions);


        return mapMealCategoriesWithMealOptions(rows);
    }

    public List<MealCategory> getAllConfirmedMealCategoriesWithConfirmedMealOptions() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(getCONFIRMEDMealCategoriesWithMealOptions);

        return mapMealCategoriesWithMealOptions(rows);
    }

    private List<MealCategory> mapMealCategoriesWithMealOptions(List<Map<String, Object>> rows) {
        List<MealCategory> categories = new ArrayList<>();

        MealCategory currentCategory = null;


        for (Map<String, Object> row : rows)  {
            int id = (Integer) row.get("id");
            if (currentCategory == null || currentCategory.getId() != id) {
                if(currentCategory != null) {
                    categories.add(currentCategory);
                }
                currentCategory = new MealCategory(
                    id,
                    (String) row.get("category_name"),
                    StatusEnum.valueOf((String) row.get("category_status"))
                );
            }

            if(row.get("option_id") != null ) {
                currentCategory
                    .getMealOptions()
                    .add(new MealOption(
                            (int) row.get("option_id"),
                            (String) row.get("meal_name"),
                            StatusEnum.valueOf((String) row.get("category_status")),
                            id
                        )
                    );
            }

        }

        if (currentCategory != null) {
            categories.add(currentCategory);
        }
        return categories;
    }

    public MealCategory addSuggestedMealCategory(MealCategory mealCategory) {

        if(mealCategory.getId() == 0) {
            addMealCategoryWithoutMealOptions(mealCategory);
        }

        if (!mealCategory.getMealOptions().isEmpty()) {
            addMealOptionsOfAMealCategory(mealCategory.getId(), mealCategory.getMealOptions());
        }

        return mealCategory;
    }

    private void addMealCategoryWithoutMealOptions(MealCategory mealCategory) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(insertMealCategoryWithNoMealOptions, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, mealCategory.getCategoryName());
            ps.setString(2, mealCategory.getStatus().name());
            return ps;
        }, keyHolder);

        mealCategory.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    private void addMealOptionsOfAMealCategory(int mealCategoryId, List<MealOption> options) {
        jdbcTemplate.batchUpdate(insertMealOptionsForMealCategory, new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MealOption mealOption = options.get(i);
                ps.setInt(1, mealCategoryId);
                ps.setString(2, mealOption.getMealName());
                ps.setString(3, mealOption.getStatus().name());
            }

            public int getBatchSize() {
                return options.size();
            }
        });
    }





    public void updateCategoriesAndOptionsToAStatus(List<Integer> categoryIds, List<Integer> optionIds, StatusEnum statusEnum) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            updateCategoriesToStatus(categoryIds, statusEnum);
        }

        if (optionIds != null && !optionIds.isEmpty()) {
            updateOptionsToStatus(optionIds, statusEnum);
        }
    }



    private void updateCategoriesToStatus(List<Integer> categoryIds, StatusEnum status) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("statval", status.name());
        parameters.addValue("ids", categoryIds);

        namedParameterJdbcTemplate.update(updateCategoriesStatus, parameters);
    }

    private void updateOptionsToStatus(List<Integer> optionsIds, StatusEnum status) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("statval", status.name());
        parameters.addValue("ids", optionsIds);

        namedParameterJdbcTemplate.update(updateOptionsStatus, parameters);
    }


    public List<String> getSuggestedMealCategoriesExist() {

        return jdbcTemplate.queryForList(getCountOfSuggestedCategories, String.class);
    }

    public List<String> getSuggestedMealOptionsExist() {

        return jdbcTemplate.queryForList(getCountOfSuggestedOptions, String.class);
    }

    public MealOption getMealOptionWithRecipesFromStrings(String mealCategory, String mealName) {

        List<Map<String,Object>> resultSet = jdbcTemplate.queryForList(getMealOptionInfoByCategoryAndOptionName, mealCategory, mealName);

        MealOption mealOption = null;
        List<MealOptionRecipe> mealOptionRecipes = new ArrayList<>();
        for( Map<String,Object> resultMap : resultSet) {

            if (mealOption == null) {
                mealOption = new MealOption();
                mealOption.setId((int) resultMap.get("id"));
                mealOption.setDescriptionOfMealOption((String) resultMap.get("description"));
                mealOption.setMainIngredients(DatabaseUtil.splitCommaDelimatedStringFromDatabase((String) resultMap.get("main_ingredients")));
                mealOption.setFkMealCategory((int) resultMap.get("category_id"));

                mealOption.setMealName(mealName);
            }

            Integer recipeid = (Integer) resultMap.get("recipe_id");
            if (recipeid != null) {
                MealOptionRecipe recipe = new MealOptionRecipe();

                recipe.setId(recipeid);
                recipe.setTitle((String) resultMap.get("recipe_title"));
                recipe.setIngredients((String) resultMap.get("recipe_ingredients"));
                recipe.setInstructions((String) resultMap.get("recipe_instructions"));

                mealOptionRecipes.add(recipe);
            }
        }

        mealOption.setMealOptionRecipes(mealOptionRecipes);
        return mealOption;
    }


    public void updateOptionsDescriptionsAndIngredients(int id, String description, List<String> ingredients) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        parameters.addValue("description", description);
        parameters.addValue("ingredients", DatabaseUtil.createCommaDelimatedStringForDatabase(ingredients));


        namedParameterJdbcTemplate.update(updateOptionDescriptionAndIngredients, parameters);
    }


    /** this returns the id of the added recipe.*/
    public int addRecipeToMealOption(int mealOptionId, String title, String ingredients, String instructions ) {


        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(insertNewMealOptionRecipe, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, mealOptionId);
            ps.setString(2, title);
            ps.setString(3, ingredients);
            ps.setString(4, instructions);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int updateRecipe(int recipeId, String title, String ingredients, String instructions) {


        jdbcTemplate.update(updateMealOptionRecipe, title, ingredients, instructions, recipeId);

        return recipeId;
    }
}
