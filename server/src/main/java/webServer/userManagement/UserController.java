package webServer.userManagement;


import database.APIKeyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    APIKeyDao apiKeyDao;

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
