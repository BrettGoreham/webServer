package webServer.userManagement;


import model.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping(value = "")
    public String showLoginPage() {
        return "userManagement/login";
    }

    @GetMapping(value = "/loginFailed")
    public String loginError(RedirectAttributes attributes) {

        attributes.addFlashAttribute("error", "Incorrect Username and/or Password");
        return "redirect:/login";
    }

    @GetMapping(value = "/logout")
    public String logout(SessionStatus session) {
        SecurityContextHolder.getContext().setAuthentication(null);
        session.setComplete();
        return "redirect:/";
    }

    @PostMapping(value = "/postLogin")
    public String postLogin(RedirectAttributes redirectAttributes) {

        // read principal out of security context and set it to session
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        validatePrinciple(authentication.getPrincipal());
        User loggedInUser = ((SecurityUserDetails) authentication.getPrincipal()).getUserDetails();

        redirectAttributes.addFlashAttribute("userName", loggedInUser.getUsername());
        redirectAttributes.addFlashAttribute("email", loggedInUser.getEmail());
        return "redirect:/user";
    }

    private void validatePrinciple(Object principal) {
        if (!(principal instanceof SecurityUserDetails)) {
            throw new  IllegalArgumentException("Principal can not be null!");
        }
    }
}
