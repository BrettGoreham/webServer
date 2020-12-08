package webServer.vinmonopolet;

import database.VinmonopoletBatchDao;
import model.vinmonopolet.AlcoholForSale;
import model.vinmonopolet.VinmonopoletBatchJob;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webServer.exceptions.InvalidInputException;
import webServer.exceptions.NoRecordsFoundException;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Validated
@RestController
@RequestMapping("/rest/vinmonopolet")
public class VinmonopoletRestServlet {

    private final VinmonopoletBatchDao vinmonopoletBatchDao;
    private final VinmonopoletService vinmonopoletService;
    public VinmonopoletRestServlet(VinmonopoletBatchDao vinmonopoletBatchDao, VinmonopoletService vinmonopoletService) {
        this.vinmonopoletBatchDao = vinmonopoletBatchDao;
        this.vinmonopoletService = vinmonopoletService;
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
        List<String> toReturn = vinmonopoletBatchDao.fetchAllCategoriesInDatabase();

        toReturn.sort(String::compareTo);
        return toReturn;
    }

    @GetMapping("info/categories/{date}")
    public List<String> getListOfCategoriesFromDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull Date date) {
        List<String> toReturn = vinmonopoletBatchDao.fetchAllCatgoriesFromDate(date);

        toReturn.sort(String::compareTo);
        return toReturn;
    }


    private static class CompareResult {

        private ComparedBatch earlierBatch;
        private ComparedBatch laterBatch;
        private Map<String, Map<String, List<AlcoholForSale>>> changeBetweenBatches;

        public CompareResult withEarlierBatch(VinmonopoletBatchJob batchJob) {
            earlierBatch =
                new ComparedBatch(
                    batchJob.getBatchId(),
                    batchJob.getDateOfRun(),
                    batchJob.getLastDateRunWasCheckedAsStillValid(),
                    batchJob.getSizeOfOverallTopList(),
                    batchJob.getSizeOfCategoryTopList()
                );

            return this;
        }

        public CompareResult withLaterBatch(VinmonopoletBatchJob batchJob) {
            laterBatch =
                new ComparedBatch(
                    batchJob.getBatchId(),
                    batchJob.getDateOfRun(),
                    batchJob.getLastDateRunWasCheckedAsStillValid(),
                    batchJob.getSizeOfOverallTopList(),
                    batchJob.getSizeOfCategoryTopList()
                );

            return this;
        }

        public CompareResult withChangeMap(Map<String, Map<String, List<AlcoholForSale>>> changeBetweenBatches) {
            this.changeBetweenBatches = changeBetweenBatches;

            return this;
        }

        public ComparedBatch getEarlierBatch() {
            return earlierBatch;
        }

        public ComparedBatch getLaterBatch() {
            return laterBatch;
        }

        public Map<String, Map<String, List<AlcoholForSale>>> getChangeBetweenBatches() {
            return changeBetweenBatches;
        }

        private static class ComparedBatch {
            private long batchId;
            private LocalDate dateOfRun;
            private LocalDate lastDateRunWasCheckedAsStillValid;
            private int sizeOfOverallTopList;
            private int  sizeOfCategoryTopList;

            public ComparedBatch(long batchId, LocalDate dateOfRun, LocalDate lastDateRunWasCheckedAsStillValid, int sizeOfOverallTopList, int sizeOfCategoryTopList) {
                this.batchId = batchId;
                this.dateOfRun = dateOfRun;
                this.lastDateRunWasCheckedAsStillValid = lastDateRunWasCheckedAsStillValid;
                this.sizeOfOverallTopList = sizeOfOverallTopList;
                this.sizeOfCategoryTopList = sizeOfCategoryTopList;
            }

            public long getBatchId() {
                return batchId;
            }

            public void setBatchId(long batchId) {
                this.batchId = batchId;
            }

            public LocalDate getDateOfRun() {
                return dateOfRun;
            }

            public void setDateOfRun(LocalDate dateOfRun) {
                this.dateOfRun = dateOfRun;
            }

            public LocalDate getLastDateRunWasCheckedAsStillValid() {
                return lastDateRunWasCheckedAsStillValid;
            }

            public void setLastDateRunWasCheckedAsStillValid(LocalDate lastDateRunWasCheckedAsStillValid) {
                this.lastDateRunWasCheckedAsStillValid = lastDateRunWasCheckedAsStillValid;
            }

            public int getSizeOfOverallTopList() {
                return sizeOfOverallTopList;
            }

            public void setSizeOfOverallTopList(int sizeOfOverallTopList) {
                this.sizeOfOverallTopList = sizeOfOverallTopList;
            }

            public int getSizeOfCategoryTopList() {
                return sizeOfCategoryTopList;
            }

            public void setSizeOfCategoryTopList(int sizeOfCategoryTopList) {
                this.sizeOfCategoryTopList = sizeOfCategoryTopList;
            }
        }
    }

    @GetMapping("/compare/date")
    public CompareResult getChangeBetweenDates(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull Date fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull Date toDate
    ) {

        if (fromDate.compareTo(toDate) >= 0) {
           throw new InvalidInputException("fromDate needs to be before toDate");
        }

        VinmonopoletBatchJob earlierBatchJob = vinmonopoletBatchDao.fetchBatchJobFromDate(fromDate);
        VinmonopoletBatchJob laterBatchJob =  vinmonopoletBatchDao.fetchBatchJobFromDate(toDate);

        checkForExistingJobs(fromDate, earlierBatchJob, toDate, laterBatchJob);

        return new CompareResult()
                    .withEarlierBatch(earlierBatchJob)
                    .withLaterBatch(laterBatchJob)
                    .withChangeMap(vinmonopoletService.getChangeMapBetweenTwoJobs(earlierBatchJob, laterBatchJob));
    }

    @GetMapping("/compare/id")
    public CompareResult getChangeBetweenIds(
        @RequestParam @NotNull long lowerId,
        @RequestParam @NotNull long higherId
    ) {
        if (lowerId >= higherId) {
            throw new InvalidInputException("lowerId needs to be lower than higherId");
        }

        VinmonopoletBatchJob batch1 = vinmonopoletBatchDao.fetchBatchJobFromId(lowerId);
        VinmonopoletBatchJob batch2 = vinmonopoletBatchDao.fetchBatchJobFromId(higherId);

        checkForExistingJobs(lowerId, batch1, higherId, batch2);

        return new CompareResult()
            .withEarlierBatch(batch1)
            .withLaterBatch(batch2)
            .withChangeMap(vinmonopoletService.getChangeMapBetweenTwoJobs(batch1, batch2));
    }

    @GetMapping("/compare/latest")
    public CompareResult getLatestTwoBatchesChanges() {

        VinmonopoletBatchJob mostRecentBatch = vinmonopoletBatchDao.fetchLastSuccessfulJob();
        if (mostRecentBatch == null) {
            throw new NullPointerException("No batches found in database");
        }

        VinmonopoletBatchJob batchBefore = vinmonopoletBatchDao.fetchBatchBeforeBatch(mostRecentBatch);
        if (batchBefore == null) {
            throw new NullPointerException("Only one batch found in database so cant compare");
        }

        return new CompareResult()
            .withEarlierBatch(batchBefore)
            .withLaterBatch(mostRecentBatch)
            .withChangeMap(vinmonopoletService.getChangeMapBetweenTwoJobs(batchBefore, mostRecentBatch));

    }

    private void checkForExistingJobs(Date earlierDate, VinmonopoletBatchJob earlierBatchJob, Date laterDate, VinmonopoletBatchJob laterBatchJob) {
        if(earlierBatchJob == null || laterBatchJob == null) {
            String errorMessage = "No Batch Jobs found for:";

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (earlierBatchJob == null) {
                errorMessage += " "  + dateFormat.format(earlierDate);
            }

            if (laterBatchJob == null) {
                errorMessage += " "  + dateFormat.format(laterDate);
            }

            throw new NoRecordsFoundException(errorMessage);
        }
    }

    private void checkForExistingJobs(
        long lowerBatchId,
        VinmonopoletBatchJob earlierBatchJob,
        long higherBatchId,
        VinmonopoletBatchJob laterBatchJob)
    {
        if(earlierBatchJob == null || laterBatchJob == null) {
            String errorMessage = "No Batch Jobs found for:";

            if (earlierBatchJob == null) {
                errorMessage += " "  + lowerBatchId;
            }

            if (laterBatchJob == null) {
                errorMessage += " "  + higherBatchId;
            }

            throw new NoRecordsFoundException(errorMessage);
        }
    }
}
