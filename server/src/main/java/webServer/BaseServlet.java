
package webServer;

import database.DogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import model.DogStat;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BaseServlet {

    @Autowired
    private DogDao dogDao;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
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