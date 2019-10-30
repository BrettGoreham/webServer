
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

@RestController
public class BaseServlet {

    @Autowired
    private DogDao dogDao;

    @Autowired
    private ScheduledEmails scheduledEmails;

    @RequestMapping("/")
    public RedirectView index() {
        return new RedirectView("/whatIsForDinner");
    }

    @PostMapping("/emailPost")
    public String  emailPost(@RequestParam String email, @RequestParam String subject, @RequestParam String content) {
        StringBuilder subjectBuilder = new StringBuilder();

        subjectBuilder.append(subject).append("  from: ").append(email);

        scheduledEmails.sendEmailToBrett(subjectBuilder.toString(), content);
        return "Email recieved :D";
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