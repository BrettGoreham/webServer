package webServer.secretSanta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webServer.RecaptchaValidationService;
import webServer.scheduledTasks.ScheduledEmails;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/secretSanta")
public class SecretSantaServlet {

    @Autowired
    private ScheduledEmails scheduledEmails;

    @Autowired
    private RecaptchaValidationService recaptchaValidationService;


    @GetMapping("")
    public String index() {
        return "secretSanta";
    }

    @GetMapping("/block")
    public String block(Model model, @RequestParam String people) throws JsonProcessingException {

        ArrayList<Person> a= new ObjectMapper().readValue(people, new TypeReference<>() {});
        model.addAttribute("people", a);
        return "secretSantaBlocking";
    }

    @GetMapping("/summary")
    public String summary(Model model, @RequestParam String people, @RequestParam String blocking) throws JsonProcessingException {

        ArrayList<Person> a= new ObjectMapper().readValue(people, new TypeReference<>() {});
        HashMap<String, ArrayList<String>> b= new ObjectMapper().readValue(blocking, new TypeReference<>() {});

        model.addAttribute("people", a);
        model.addAttribute("blocking", b);
        return "secretSantaSummary";
    }


    @PostMapping("deliver")
    @ResponseBody
    public void deliver(@RequestBody SecretSantaRequest secretSantaRequest) {

        Boolean isRecaptchaValid = recaptchaValidationService.checkRecaptchaString(secretSantaRequest.getRecaptchaResult());

        if (isRecaptchaValid) {
            List<Pair<Person, Person>> pairs = SecretSantaKt.prep(secretSantaRequest.getPeople(), secretSantaRequest.getBlocking());

            for(Pair<Person, Person> pair : pairs) {
                scheduledEmails.sendSecretSantaEmail(pair);
            }
        }
        else {
            throw new BadRequestException("Recaptcha Failed To Verify");
        }
    }
}

