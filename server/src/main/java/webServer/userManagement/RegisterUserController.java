package webServer.userManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webServer.RecaptchaValidationService;
import webServer.scheduledTasks.ScheduledEmails;

@Controller
@RequestMapping("/register")
public class RegisterUserController {

    @Autowired
    UserDetailServiceImpl userDetailsService;

    @Autowired
    RecaptchaValidationService recaptchaValidationService;


    @GetMapping("")
    public String index(Model model) {

        return "userManagement/register";
    }

    @PostMapping("/submit")
        public String submit(RedirectAttributes redirectAttributes, @RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam(name = "g-recaptcha-response") String recaptchaCode ) {

        if (recaptchaValidationService.checkRecaptchaString(recaptchaCode)) {
            if (areValuesValid(username, password, email)) {
                try {
                    userDetailsService.createAndSaveUser(username, password, email);
                } catch (DuplicateKeyException e) {
                    redirectAttributes.addFlashAttribute("error", "Username Or email are already in use. Please Try again");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Input Username, Password, And/Or Email is invalid");
            }
        }
        else {
            redirectAttributes.addFlashAttribute("error", "Recaptcha validation failed");
        }

        redirectAttributes.addAttribute("success", "Registration email sent");

        return "redirect:/register";
    }

    private boolean areValuesValid(String username, String password, String email) {
        return !username.isEmpty() && !password.isEmpty() && !email.isEmpty();
    }

    @GetMapping("/confirmation")
    public String confirmRegistration(@RequestParam String token) {
        userDetailsService.confirmRegistration(token);
        return "userManagement/registerConfirm";
    }

}
