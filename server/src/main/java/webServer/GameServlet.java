package webServer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/games")
public class GameServlet {

    @GetMapping("eightarmedjuan")
    public String eightArmedJuan() {
        return "eightArmedJuan";
    }
}
