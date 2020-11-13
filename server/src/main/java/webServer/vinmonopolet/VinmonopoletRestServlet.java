package webServer.vinmonopolet;

import database.VinmonopoletBatchDao;
import model.vinmonopolet.VinmonopoletBatchJob;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webServer.exceptions.NoRecordsFoundException;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Validated
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

        if (batchJob ==null) {
            throw new NoRecordsFoundException();
        }
        return batchJob;
    }

    @GetMapping("/{date}")
    public VinmonopoletBatchJob get(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull Date date){

        VinmonopoletBatchJob batchJob = vinmonopoletBatchDao.fetchBatchJobFromDate(date);

        if(batchJob ==null) {
            throw new NoRecordsFoundException();
        }
        return batchJob;
    }

    private static class DateRange {
        private final Date earliestDate;
        private final Date latestDate;

        public DateRange(Date fromDate, Date toDate) {
            this.earliestDate = fromDate;
            this.latestDate = toDate;
        }

        public Date getEarliestDate() {
            return earliestDate;
        }

        public Date getLatestDate() {
            return latestDate;
        }
    }
    @GetMapping("/info/daterange")
    public DateRange getEarliestDate() {

        // unsure if this is correct, if you run the server in a timezone that is behind UTC this may give the day before..
        return new DateRange(
            Date.from(vinmonopoletBatchDao.fetchFirstRunDate().atStartOfDay(ZoneId.of("UTC")).toInstant()),
            Date.from(vinmonopoletBatchDao.fetchLastRunDate().atStartOfDay(ZoneId.of("UTC")).toInstant())
        );
    }

    @GetMapping("info/categories")
    public List<String> getListOfCategories() {
        return vinmonopoletBatchDao.fetchAllCategoriesInDatabase();
    }

    @GetMapping("info/categories/{date}")
    public List<String> getListOfCategoriesFromDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull Date date) {
        return vinmonopoletBatchDao.fetchAllCatgoriesFromDate(date);
    }
}
