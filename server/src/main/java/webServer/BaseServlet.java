
package webServer;

import database.DogDao;
import database.MealDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import model.DogStat;
import org.springframework.web.servlet.view.RedirectView;
import webServer.scheduledTasks.ScheduledEmails;

import java.util.ArrayList;
import java.util.List;

@RestController("/rest")
public class BaseServlet {

    @Autowired
    private DogDao dogDao;

    @Autowired
    private ScheduledEmails scheduledEmails;

    @Autowired
    private RecaptchaValidationService recaptchaValidationService;

    @PostMapping("/emailPost")
    public String  emailPost(@RequestParam String recaptchaCode, @RequestParam String email, @RequestParam String subject, @RequestParam String content) {
        Boolean isRecaptchaValid = recaptchaValidationService.checkRecaptchaString(recaptchaCode);

        if (isRecaptchaValid) {

            StringBuilder subjectBuilder = new StringBuilder();

            subjectBuilder.append(subject).append("  from: ").append(email);

            scheduledEmails.sendEmailToToEmailAddr(subjectBuilder.toString(), content);
            return "Email recieved :D";
        }
        else {
            return "Recaptcha Validation Failed :(, Email failed to send";
        }
    }

    @GetMapping("/dogStats")
    public List<DogStat> getDogStats(){
        List<DogStat> dogStats = dogDao.getAllDogStats();

        return dogStats;
    }

    @PostMapping(path = "/dogStats/add", consumes = "application/json", produces = "application/json")
    public void addToDogStats(@RequestBody ArrayList<DogStat> dogStats){
        System.out.println(dogStats.size());
        dogDao.insertDogStats(dogStats);
    }

}