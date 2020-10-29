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


    private final VinmonopoletIntegration vinmonopoletIntegration;
    private final VinmonopoletBatchDao vinmonopoletBatchDao;
    private final int pageSize;
    private final int overallSizeList;
    private final int categorySizeList;

    private final static String COMPLETE = "COMPLETE";
    private final static String FAILED = "FAILED";

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

    //seconds minutes hours daysofmonth monthsofyear daysofweek"
    //vinmonopolet updates at 5:45CET
    //Server runs in UTC which is 1 hour behind CET so this runs at 6:00CET.
    @Scheduled(cron = "0 0 6 * * *", zone="GMT+1")
    public void processVinmonopoletData(){

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

            List<AlcoholForSale> validAlcoholsToRank = filterOutInvalidResults(pageFromVinmonopolet);

            currentBatchJob.addAlcoholsToTopLists(validAlcoholsToRank);

            if (pageFromVinmonopolet.size() < pageSize && !currentBatchJob.getStatus().equals(FAILED)) {
                currentBatchJob.setStatus(COMPLETE);
            }
            isDone = checkForFinishedStatus(currentBatchJob.getStatus());
        }

        VinmonopoletBatchJob lastSuccessfulJob = vinmonopoletBatchDao.fetchLastSuccessfulJob();

        if (lastSuccessfulJob == null || areThereChangesToTopLists(lastSuccessfulJob, currentBatchJob)) {
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
        if(lastSuccessfulJob.getCategoryToAlcoholForSalePricePerAlcoholLiter().entrySet().size() != currentBatchJob.getCategoryToAlcoholForSalePricePerAlcoholLiter().entrySet().size()) {
            return true;
        }


        boolean areTopListsTheSame =
            lastSuccessfulJob.getOverallAlcoholForSalePricePerAlcoholLiter()
                .containsAll(currentBatchJob.getOverallAlcoholForSalePricePerAlcoholLiter());

        if (!areTopListsTheSame) {
            return true;
        }

        Map<String, SortedMaxLengthList<AlcoholForSale>> lastCategoryTopLists = lastSuccessfulJob.getCategoryToAlcoholForSalePricePerAlcoholLiter();
        Map<String, SortedMaxLengthList<AlcoholForSale>> currentCategoryTopLists = currentBatchJob.getCategoryToAlcoholForSalePricePerAlcoholLiter();

        for (Map.Entry<String, SortedMaxLengthList<AlcoholForSale>> currentEntry : currentCategoryTopLists.entrySet()) {

            if (!lastCategoryTopLists.containsKey(currentEntry.getKey())) {
               return true; //new category case;
            }
            else {
                SortedMaxLengthList<AlcoholForSale> currentList = currentEntry.getValue();
                SortedMaxLengthList<AlcoholForSale> lastList = lastCategoryTopLists.get(currentEntry.getKey());

                boolean areListsTheSame = currentList.containsAll(lastList);
                if (!areListsTheSame) {
                    return true;
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
