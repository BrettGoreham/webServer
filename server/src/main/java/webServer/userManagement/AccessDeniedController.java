package webServer.userManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accessDenied")
public class AccessDeniedController {

    @Value("${to.email.address}")
    String email;

    @GetMapping
    public String getAccessDenied(Model model) {
        model.addAttribute("email", email);
        return "userManagement/accessDenied";
    }
}
