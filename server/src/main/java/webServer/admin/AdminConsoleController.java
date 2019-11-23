package webServer.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminConsoleController {

    @GetMapping(value = "")
    public String showAdminHome() {
        return "admin";
    }
}
