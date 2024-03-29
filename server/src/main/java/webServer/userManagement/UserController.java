package webServer.userManagement;


import database.APIKeyDao;
import database.MealDao;
import model.user.UserMealCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    APIKeyDao apiKeyDao;

    @Autowired
    MealDao mealDao;

    @GetMapping(value = "")
    public String userDetailsPage(Model model, Principal principal) {

        SecurityUserDetails securityUserDetails = validatePrinciple(principal);
        model.addAttribute("email", securityUserDetails.getUserDetails().getEmail());
        model.addAttribute("userName", securityUserDetails.getUsername());

        model.addAttribute("apiKey", apiKeyDao.getApiKeyForUser(securityUserDetails.getId()));

        return "userManagement/userPage";
    }

    @GetMapping("/twofa")
    public String getTwoFaPage(Model model, Principal principal) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        model.addAttribute("openTokens", apiKeyDao.getAllTokensForUser(securityUserDetails.getId()));

        return "userManagement/twoFa";
    }


    @GetMapping("/meals")
    public String getMealCollections(Model model, Principal principal) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        List<UserMealCollection> mealCollections = mealDao.getAllUserMealCollections(securityUserDetails.user.getId());
        model.addAttribute("mealCollections", mealCollections);
        return "userManagement/mealCollections";
    }



    @GetMapping("/meals/{mealCollectionId}")
    public String getMealCollection(@PathVariable(value = "mealCollectionId") int mealCollectionId, Model model, Principal principal) {
        SecurityUserDetails securityUserDetails = validatePrinciple(principal);

        UserMealCollection mealCollection = mealDao.getUserMealCollection(securityUserDetails.user.getId(), mealCollectionId);

        model.addAttribute("mealCollection", mealCollection);
        return "userManagement/mealCollection";
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
}
