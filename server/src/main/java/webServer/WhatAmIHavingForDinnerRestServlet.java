package webServer;

import database.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dinner")
public class WhatAmIHavingForDinnerRestServlet {

    @Autowired
    private WhatIsForDinnerService whatIsForDinnerService;

    @Autowired
    private RecaptchaValidationService recaptchaValidationService;

    private static class SaveMealOptionRequest {
        private int id;
        private String description;
        private String ingredients;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIngredients() {
            return ingredients;
        }

        public void setIngredients(String ingredients) {
            this.ingredients = ingredients;
        }
    }

    @PostMapping("/saveMealOptionDetail")
    public boolean saveMealOptionDetail(@RequestBody SaveMealOptionRequest saveMealOptionRequest){

        return whatIsForDinnerService.updateMealOptionInfo(
                    saveMealOptionRequest.getId(),
                    saveMealOptionRequest.getDescription(),
                    DatabaseUtil.splitCommaDelimatedStringFromDatabase(saveMealOptionRequest.getIngredients())
        );
    }

    /**
     *
     * This is used for both submitting new and updating old recipes.
     *
     * if recipeId is available it is an update. if not it is an insert.
     */
    @PostMapping("/recipeSubmit")
    public int recipeSubmit(@RequestParam String recaptchaCode, @RequestParam String mealOptionId, @RequestParam(required = false) String recipeId, @RequestParam String title, @RequestParam String ingredients, @RequestParam String instructions) {

        boolean isRecaptchaValid = recaptchaValidationService.checkRecaptchaString(recaptchaCode);
        if (isRecaptchaValid) {
            Integer intMealOptionId = Integer.parseInt(mealOptionId);
            Integer intRecipeId = recipeId == null ? null : Integer.parseInt(recipeId);

            return whatIsForDinnerService.submitMealOptionRecipe(
                intMealOptionId,
                intRecipeId,
                title,
                ingredients,
                instructions
            );
        }
        else {
            return -1;
        }

    }
}
