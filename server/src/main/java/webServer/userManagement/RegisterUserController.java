package webServer.userManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import webServer.scheduledTasks.ScheduledEmails;

@Controller
@RequestMapping("/register")
public class RegisterUserController {

    @Autowired
    UserDetailServiceImpl userDetailsService;

    @GetMapping("")
    public String index() {
        return "userManagement/register";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam String username, @RequestParam String password, @RequestParam String email ) {
        userDetailsService.createAndSaveUser(username, password, email);

        return "redirect:register";
    }

    @GetMapping("/confirmation")
    public String confirmRegistration(@RequestParam String token) {
        userDetailsService.confirmRegistration(token);
        return "userManagement/registerConfirm";
    }

}
