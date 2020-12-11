package webServer.userManagement;


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


    @GetMapping(value = "")
    public String userDetailsPage(Model model, Principal principal) {
        if (principal != null) {
            SecurityUserDetails securityUserDetails = validatePrinciple(principal);
            model.addAttribute("email", securityUserDetails.getUserDetails().getEmail());
            model.addAttribute("userName", securityUserDetails.getUsername());
        }

        return "userManagement/userPage";
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
