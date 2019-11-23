package webServer.userManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/register")
public class RegisterUserController {

    @Autowired
    UserDetailServiceImpl userDetailsService;

    @GetMapping("")
    public String index() {
        return "register";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam String username, @RequestParam String password, @RequestParam String email ) {
        userDetailsService.createAndSaveUser(username, password, email);

        return "redirect:/register";
    }
}
