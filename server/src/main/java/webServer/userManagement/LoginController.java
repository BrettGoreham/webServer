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

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping(value = "")
    public String showLoginPage() {
        return "userManagement/login";
    }

    @GetMapping(value = "/loginFailed")
    public String loginError(Model model) {

        model.addAttribute("error", "true");
        return "userManagement/login";
    }

    @GetMapping(value = "/logout")
    public String logout(SessionStatus session) {
        SecurityContextHolder.getContext().setAuthentication(null);
        session.setComplete();
        return "redirect:/";
    }

    @PostMapping(value = "/postLogin")
    public String postLogin(Model model, HttpSession session) {

        // read principal out of security context and set it to session
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        validatePrinciple(authentication.getPrincipal());
        User loggedInUser = ((SecurityUserDetails) authentication.getPrincipal()).getUserDetails();
        model.addAttribute("currentUser", loggedInUser.getUsername());
        session.setAttribute("userId", loggedInUser.getId());
        return "redirect:/admin";
    }

    private void validatePrinciple(Object principal) {
        if (!(principal instanceof SecurityUserDetails)) {
            throw new  IllegalArgumentException("Principal can not be null!");
        }
    }
}
