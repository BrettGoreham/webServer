package webServer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexServlet {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/twoFactorAuthentication")
    public String twofa() {
        return "twoFactorExplain";
    }


    @GetMapping("/games/stianstorment")
    public String game() {return "game"; }
}
