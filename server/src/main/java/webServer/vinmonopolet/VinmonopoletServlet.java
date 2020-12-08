package webServer.vinmonopolet;

import database.VinmonopoletBatchDao;
import model.vinmonopolet.AlcoholForSale;
import model.vinmonopolet.SortedMaxLengthList;
import model.vinmonopolet.VinmonopoletBatchJob;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import webServer.exceptions.NoRecordsFoundException;

import javax.ws.rs.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping("/vinmonopolet")
public class VinmonopoletServlet {

    VinmonopoletBatchDao vinmonopoletBatchDao;
    public VinmonopoletServlet(VinmonopoletBatchDao vinmonopoletBatchDao) {
        this.vinmonopoletBatchDao = vinmonopoletBatchDao;
    }

    @GetMapping("")
    public String get(Model model){
        VinmonopoletBatchJob batchJob = vinmonopoletBatchDao.fetchLastSuccessfulJob();

        if (batchJob ==null) {
            model.addAttribute("error", "No Results found please check back tomorrow.");
        }
        else {
            model.addAttribute("batchJob", batchJob);
            model.addAttribute("firstDate", vinmonopoletBatchDao.fetchFirstRunDate());
            model.addAttribute("todaysDate", LocalDate.now());
            model.addAttribute("date", batchJob.getLastDateRunWasCheckedAsStillValid());
        }
        return "vinmonopolet/vinmonopolet";
    }

    @GetMapping("{date}")
    public String getDate(Model model, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        VinmonopoletBatchJob batchJob = vinmonopoletBatchDao.fetchBatchJobFromDate(date);

        if (batchJob ==null) {
            model.addAttribute("error", "No Results found please check back tomorrow.");
        }
        else {
            model.addAttribute("batchJob", batchJob);
            model.addAttribute("firstDate", vinmonopoletBatchDao.fetchFirstRunDate());
            model.addAttribute("todaysDate", LocalDate.now());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            model.addAttribute("date", simpleDateFormat.format(date));
        }
        return "vinmonopolet/vinmonopolet";
    }

    @GetMapping("{date}/{category}")
    public String getDateCategory(Model model, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date, @PathVariable String category) {

        VinmonopoletBatchJob batchJob = vinmonopoletBatchDao.fetchBatchJobFromDate(date);

        SortedMaxLengthList<AlcoholForSale> categoryTopList =
            batchJob
                .getCategoryToAlcoholForSalePricePerAlcoholLiter()
                .getOrDefault(category, null);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy");

        model.addAttribute("topList", categoryTopList);
        model.addAttribute("date", simpleDateFormat.format(date));
        model.addAttribute("category", category);
        model.addAttribute("firstDate", vinmonopoletBatchDao.fetchFirstRunDate());
        model.addAttribute("todaysDate", LocalDate.now());

        return "vinmonopolet/vinmonopoletCategory";
    }
}
