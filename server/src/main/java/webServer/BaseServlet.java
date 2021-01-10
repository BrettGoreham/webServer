
package webServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import webServer.scheduledTasks.ScheduledEmails;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/rest")
public class BaseServlet {

    @Autowired
    private ScheduledEmails scheduledEmails;

    @Autowired
    private RecaptchaValidationService recaptchaValidationService;

    @PostMapping("/emailPost")
    public String  emailPost(@RequestParam String recaptchaCode, @RequestParam String email, @RequestParam String subject, @RequestParam String content) {
        Boolean isRecaptchaValid = recaptchaValidationService.checkRecaptchaString(recaptchaCode);

        if (isRecaptchaValid) {

            scheduledEmails.sendEmailToToEmailAddr(subject + "  from: " + email, content);
            return "Email received :D";
        }
        else {
            return "Recaptcha Validation Failed :(, Email failed to send";
        }
    }
}