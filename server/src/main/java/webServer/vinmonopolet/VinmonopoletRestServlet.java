package webServer.vinmonopolet;

import database.VinmonopoletBatchDao;
import model.vinmonopolet.VinmonopoletBatchJob;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import webServer.exceptions.NoRecordsFoundException;

import java.util.Date;


@RestController
@RequestMapping("/rest/vinmonopolet")
public class VinmonopoletRestServlet {

    private final VinmonopoletBatchDao vinmonopoletBatchDao;
    public VinmonopoletRestServlet(VinmonopoletBatchDao vinmonopoletBatchDao) {
        this.vinmonopoletBatchDao = vinmonopoletBatchDao;
    }

    @GetMapping("")
    public VinmonopoletBatchJob get() {
        VinmonopoletBatchJob batchJob = vinmonopoletBatchDao.fetchLastSuccessfulJob();

        if(batchJob ==null) {
            throw new NoRecordsFoundException();
        }
        return batchJob;
    }

    @GetMapping("/date")
    public VinmonopoletBatchJob get(@RequestParam("date")
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date){

        VinmonopoletBatchJob batchJob = vinmonopoletBatchDao.fetchBatchJobFromDate(date);

        if(batchJob ==null) {
            throw new NoRecordsFoundException();
        }
        return batchJob;
    }
}
