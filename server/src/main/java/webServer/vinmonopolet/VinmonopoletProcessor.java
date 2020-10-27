package webServer.vinmonopolet;

import database.VinmonopoletBatchDao;
import integration.Vinmonopolet.GetDetailResponse;
import integration.VinmonopoletIntegration;
import model.vinmonopolet.AlcoholForSale;
import model.vinmonopolet.SortedMaxLengthList;
import model.vinmonopolet.VinmonopoletBatchJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vinmonopolet.restclient.ApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Profile("vinmonopoletBatch")
public class VinmonopoletProcessor {


    private VinmonopoletIntegration vinmonopoletIntegration;
    private VinmonopoletBatchDao vinmonopoletBatchDao;
    private int pageSize;
    private int overallSizeList;
    private int categorySizeList;

    private final String COMPLETE = "COMPLETE";
    private final String FAILED = "FAILED";

    public VinmonopoletProcessor(VinmonopoletIntegration vinmonopoletIntegration,
                                 VinmonopoletBatchDao vinmonopoletBatchDao,
                                 @Value("${vinmonopolet.pageSize}") int pageSize,
                                 @Value("${vinmonopolet.overallSizeList}") int overallSizeList,
                                 @Value("${vinmonopolet.categorySizeList}") int categorySizeList) {

        this.vinmonopoletIntegration = vinmonopoletIntegration;
        this.vinmonopoletBatchDao = vinmonopoletBatchDao;
        this.pageSize = pageSize;
        this.overallSizeList = overallSizeList;
        this.categorySizeList = categorySizeList;
    }

    @Scheduled(fixedDelay = 1000000000, initialDelay = 1000)
    //seconds minutes hours daysofmonth monthsofyear daysofweek"
    //@Scheduled(cron = "0 0 20 * * MON-FRI")
    public void processVinmonopoletData(){

        VinmonopoletBatchJob lastSuccessfulJob = vinmonopoletBatchDao.fetchLastSuccessfulJob();

        //TODO get LastRunAndBatch
        VinmonopoletBatchJob currentBatchJob =
            new VinmonopoletBatchJob(
                -1,
                LocalDate.now(),
                LocalDate.now(),
                "RUNNING",
                overallSizeList,
                categorySizeList
            );

        int resultCountFromVinmonopolet = 0;
        boolean isDone = false;

        while(!isDone) {

            List<AlcoholForSale> pageFromVinmonopolet = getPageFromVinmonopolet(resultCountFromVinmonopolet, currentBatchJob);

            resultCountFromVinmonopolet += pageFromVinmonopolet.size();

            System.out.println("results from Vinmonopolet so far: " + resultCountFromVinmonopolet);

            List<AlcoholForSale> validAlcoholsToRank = filterOutInvalidResults(pageFromVinmonopolet);

            currentBatchJob.addAlcoholsToTopLists(validAlcoholsToRank);

            if (pageFromVinmonopolet.size() < pageSize) {
                currentBatchJob.setStatus(COMPLETE);
            }
            isDone = checkForFinishedStatus(currentBatchJob.getStatus());
        }

        if (areThereChangesToTopLists(lastSuccessfulJob, currentBatchJob)) {
            vinmonopoletBatchDao.saveVinmonopoletBatchJob(currentBatchJob);
        } else {
            vinmonopoletBatchDao.updatePreviousBatchesValidDateToDate(LocalDate.now(), lastSuccessfulJob.getBatchId());
        }

    }

    private boolean areThereChangesToTopLists(VinmonopoletBatchJob lastSuccessfulJob, VinmonopoletBatchJob currentBatchJob) {
        //in case i change parameters and want new list to take over.
        if  (lastSuccessfulJob.getSizeOfOverallTopList() != currentBatchJob.getSizeOfOverallTopList() ||
                lastSuccessfulJob.getSizeOfCategoryTopList() != currentBatchJob.getSizeOfCategoryTopList()) {
            return true;
        }

        //in case vinmonopolet changes categories
        if(lastSuccessfulJob.getCategoryToAlcoholForSalePricePerAlcoholUnit().entrySet().size() != currentBatchJob.getCategoryToAlcoholForSalePricePerAlcoholUnit().entrySet().size()) {
            return true;
        }

        for(int i = 0; i <lastSuccessfulJob.getOverallAlcoholForSalePricePerAlcoholUnit().size(); i++) {

            if (! lastSuccessfulJob.getOverallAlcoholForSalePricePerAlcoholUnit().get(i)
                    .equals(currentBatchJob.getOverallAlcoholForSalePricePerAlcoholUnit().get(i))) {
                return true; //unmatching value in top list case;
            }
        }

        Map<String, SortedMaxLengthList<AlcoholForSale>> lastCategoryTopLists = lastSuccessfulJob.getCategoryToAlcoholForSalePricePerAlcoholUnit();
        Map<String, SortedMaxLengthList<AlcoholForSale>> currentCategoryTopLists = currentBatchJob.getCategoryToAlcoholForSalePricePerAlcoholUnit();

        for (Map.Entry<String, SortedMaxLengthList<AlcoholForSale>> currentEntry : currentCategoryTopLists.entrySet()) {

            if (!lastCategoryTopLists.containsKey(currentEntry.getKey())) {
               return true; //new category case;
            }
            else {
                SortedMaxLengthList<AlcoholForSale> currentList = currentEntry.getValue();
                SortedMaxLengthList<AlcoholForSale> lastList = lastCategoryTopLists.get(currentEntry.getKey());

                for(int i = 0; i < currentList.size(); i++) {
                    if(! currentList.get(i).equals(lastList.get(i))) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    private boolean checkForFinishedStatus(String status) {
        return status.equals(FAILED) || status.equals(COMPLETE);
    }

    private List<AlcoholForSale> filterOutInvalidResults(List<AlcoholForSale> pageFromVinmonopolet) {
        return pageFromVinmonopolet
            .stream()
            .filter(alcoholForSale -> alcoholForSale.getAlcoholPercentage() > 0)
            .filter(alcoholForSale -> !alcoholForSale.getCategory().toLowerCase().contains("alkoholfri"))
            .filter(alcoholForSale ->  !alcoholForSale.getSaleStatus().toLowerCase().contains("utsolgt"))
            .collect(Collectors.toList());
    }

    public List<AlcoholForSale> getPageFromVinmonopolet(int startOfPage, VinmonopoletBatchJob batchJob) {
        try {
            List<GetDetailResponse> responses = vinmonopoletIntegration.callVinmonopoletForDetails(pageSize, startOfPage);

            return
                responses
                    .stream()
                    .map(this::mapFromWebServiceResponse)
                    .collect(Collectors.toList());
        }
        catch (ApiException e) {
            //edge case where last page size was still a full page but was all of the records
            if (e.getResponseBody().contains("Parameter START must be lower than the total amount of records")) {
                batchJob.setStatus(COMPLETE);
            }
            else {
                batchJob.setStatus(FAILED);
            }
            return new ArrayList<>();
        }
    }

    public AlcoholForSale mapFromWebServiceResponse(GetDetailResponse detailResponse) {
        return new AlcoholForSale(
            detailResponse.getBasic().getProductLongName(),
            detailResponse.getClassification().getSubProductTypeName(),
            detailResponse.getBasic().getProductId(),
            detailResponse.getBasic().getProductStatusSaleName(),
            detailResponse.getPrices().isEmpty() ? 0 : detailResponse.getPrices().get(0).getSalesPrice(),
            detailResponse.getBasic().getVolume(),
            detailResponse.getBasic().getAlcoholContent()
        );
    }
}
