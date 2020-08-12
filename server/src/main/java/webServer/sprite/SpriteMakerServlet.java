package webServer.sprite;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sprites")
public class SpriteMakerServlet {

    @GetMapping("maker")
    public String index() {
        return "spriteMaker";
    }
}
