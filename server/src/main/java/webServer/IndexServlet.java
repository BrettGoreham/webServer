package webServer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class IndexServlet {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
