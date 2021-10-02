package webServer.userManagement;



import database.APIKeyDao;
import database.MealDao;
import model.user.UserMeal;
import model.user.UserMealCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import webServer.exceptions.InvalidInputException;
import webServer.exceptions.NoRecordsFoundException;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {

    @Autowired
    APIKeyDao apiKeyDao;

    @Autowired
    MealDao mealDao;

    @PostMapping("generateApiKey")
    public String generateApiKey(Principal principal) {

        SecurityUserDetails userDetails = validatePrinciple(principal);

        return apiKeyDao.generateAndSaveUniqueApiKey(userDetails.getId()).toString();
    }

    @DeleteMapping(value = "deleteApiKey", consumes = "application/json")
    public void deleteApiKey(Principal principal, @RequestBody UUID apiKey) {
        SecurityUserDetails userDetails = validatePrinciple(principal);

        apiKeyDao.deleteApiKeyForUser(apiKey, userDetails.getId());
    }

    private SecurityUserDetails validatePrinciple(Object principal) {
        if (principal== null) {

            throw new  IllegalArgumentException("Principal can not be null!");
        }
        else {
            if (principal instanceof SecurityUserDetails ) {
                return (SecurityUserDetails) principal;
            }
            else {
                return (SecurityUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            }
        }
    }


    @PostMapping("/meals")
    public UserMealCollection CreateMealCollection(Principal principal, @RequestBody String collectionName) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        return mealDao.createUserMealCollection(securityUserDetails.getId(), collectionName);
    }

    @DeleteMapping("/meals/{mealCollectionId}")
    public void DeleteMealCollection(Principal principal, @PathVariable int mealCollectionId) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        try {
            mealDao.deleteUserMealCollection(securityUserDetails.getId(), mealCollectionId);
        }
        catch (IllegalArgumentException e) {
            throw new NoRecordsFoundException("meal collection does not exist for user");
        }
    }

    @PutMapping("/meals/{mealCollectionId}")
    public void UpdateMealCollection(Principal principal, @PathVariable int mealCollectionId, @RequestBody String collectionName) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        try {
            mealDao.updateUserMealCollection(securityUserDetails.getId(), mealCollectionId, collectionName);
        }
        catch (IllegalArgumentException e) {
            throw new NoRecordsFoundException("meal collection does not exist for user");
        }
    }

    @PostMapping(path = "/meals/{mealCollectionId}/meals", consumes = "application/json")
    public UserMeal CreateMeal(Principal principal, @PathVariable int mealCollectionId, @RequestBody UserMeal meal) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        return mealDao.createUserMeal(securityUserDetails.getId(), mealCollectionId, meal);
    }

    @DeleteMapping("/meals/{mealCollectionId}/meals/{mealId}")
    public void deleteMeal(Principal principal, @PathVariable int mealCollectionId, @PathVariable int mealId) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        try {
            mealDao.deleteUserMeal(securityUserDetails.getId(), mealCollectionId, mealId);
        }
        catch (IllegalArgumentException e) {
            throw new NoRecordsFoundException("meal collection does not exist for user");
        }
    }

    @PutMapping(path = "/meals/{mealCollectionId}/meals/{mealId}", consumes = "application/json")
    public void updateMeal(Principal principal, @PathVariable int mealCollectionId, @PathVariable int mealId, @RequestBody UserMeal meal) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        try {
            mealDao.updateUserMeal(securityUserDetails.getId(), mealCollectionId, mealId, meal);
        }
        catch (IllegalArgumentException e) {
            throw new NoRecordsFoundException("meal collection does not exist for user");
        }
    }

    @PutMapping(path = "/meals/{mealCollectionId}/meals/{mealId}/disabled/{disabled}")
    public void updateDisabledForMeal(Principal principal, @PathVariable int mealCollectionId, @PathVariable int mealId, @PathVariable boolean disabled) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        try {
            mealDao.updateUserMealDisabledStatus(securityUserDetails.getId(), mealCollectionId, mealId, disabled);
        }
        catch (IllegalArgumentException e) {
            throw new NoRecordsFoundException("meal collection does not exist for user");
        }
    }


}
