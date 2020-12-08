package webServer.vinmonopolet;

import model.vinmonopolet.AlcoholForSale;
import model.vinmonopolet.SortedMaxLengthList;
import model.vinmonopolet.VinmonopoletBatchJob;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VinmonopoletService {

    public boolean setRankingChangesInLists(VinmonopoletBatchJob lastJob, VinmonopoletBatchJob currentJob) {
        if (lastJob == null) {
            return true;
        }

        boolean changeHasOccurred =
            setRankingChangeInList(
                lastJob.getOverallAlcoholForSalePricePerAlcoholLiter(),
                currentJob.getOverallAlcoholForSalePricePerAlcoholLiter()
            );

        for (Map.Entry<String, SortedMaxLengthList<AlcoholForSale>> entry : currentJob.getCategoryToAlcoholForSalePricePerAlcoholLiter().entrySet()) {
            boolean lastJobHasCategory =
                lastJob.getCategoryToAlcoholForSalePricePerAlcoholLiter().containsKey(entry.getKey());

            if (lastJobHasCategory) {

                boolean changeInCategory =
                    setRankingChangeInList(
                        lastJob.getCategoryToAlcoholForSalePricePerAlcoholLiter().get(entry.getKey()),
                        entry.getValue()
                    );

                changeHasOccurred = changeHasOccurred || changeInCategory;
            }
            else {
                changeHasOccurred = true;
            }
        }

        return changeHasOccurred;
    }


    public Map<String, Map<String, List<AlcoholForSale>>> getChangeMapBetweenTwoJobs(VinmonopoletBatchJob earlierBatchJob, VinmonopoletBatchJob laterBatchJob) {
        //hacking this to reuse it. so i dont have to do it myself. finds changes but doesnt have missing.
        setRankingChangesInLists(earlierBatchJob, laterBatchJob);

        Map<String, Map<String, List<AlcoholForSale>>> changeMap = new HashMap<>();

        Map<String, List<AlcoholForSale>> overallChanges =
            createMapWithChanges(
                laterBatchJob.getOverallAlcoholForSalePricePerAlcoholLiter(),
                earlierBatchJob.getOverallAlcoholForSalePricePerAlcoholLiter()
            );

        if (!overallChanges.isEmpty()) {
            changeMap.put(
                "OVERALL",
                overallChanges
            );
        }

        for(Map.Entry<String, SortedMaxLengthList<AlcoholForSale>> entry : laterBatchJob.getCategoryToAlcoholForSalePricePerAlcoholLiter().entrySet()) {
            Map<String, List<AlcoholForSale>> changes =
                createMapWithChanges(
                    entry.getValue(),
                    earlierBatchJob
                        .getCategoryToAlcoholForSalePricePerAlcoholLiter()
                        .get(entry.getKey())
                );

            if (!changes.isEmpty()) {
                changeMap.put(
                    entry.getKey(),
                    changes
                );
            }
        }

        return changeMap;
    }


    private Map<String, List<AlcoholForSale>> createMapWithChanges(SortedMaxLengthList<AlcoholForSale> laterList, SortedMaxLengthList<AlcoholForSale> earlierList) {
        Map<String, List<AlcoholForSale>> changeMap = new HashMap<>();

        if (earlierList == null) {
            changeMap.put("newCategory", null);
        }
        else {
            List<AlcoholForSale> newList = filterOutNoChanges(laterList).stream().filter(alcoholForSale -> alcoholForSale.getChangeInRanking().equals("New")).collect(Collectors.toList());
            if (!newList.isEmpty()) {
                changeMap.put("new", newList);
            }

            List<AlcoholForSale> positionChange = filterOutNoChanges(laterList).stream().filter(alcoholForSale -> !alcoholForSale.getChangeInRanking().equals("New")).collect(Collectors.toList());
            if (!positionChange.isEmpty()) {
                changeMap.put("positionChange", positionChange);
            }

            List<AlcoholForSale> gone = findAlcoholThatDoesNotExistInSecondList(earlierList, laterList);
            if (!gone.isEmpty()) {
                changeMap.put("gone", gone);
            }
        }

        return changeMap;
    }

    private List<AlcoholForSale> findAlcoholThatDoesNotExistInSecondList(SortedMaxLengthList<AlcoholForSale> earlierList, SortedMaxLengthList<AlcoholForSale> laterList) {
        List<String> productIdsInLaterList =
            laterList
                .stream()
                .map(AlcoholForSale::getVinmonopoletProductId)
                .collect(Collectors.toList());

        return earlierList
            .stream()
            .filter(alcoholForSale -> !productIdsInLaterList.contains(alcoholForSale.getVinmonopoletProductId()))
            .collect(Collectors.toList());
    }

    private List<AlcoholForSale> filterOutNoChanges(List<AlcoholForSale> alcoholForSaleList) {
        return alcoholForSaleList
            .stream()
            .filter(alcoholForSale -> !alcoholForSale.getChangeInRanking().equals("-"))
            .collect(Collectors.toList());
    }



    private boolean setRankingChangeInList(SortedMaxLengthList<AlcoholForSale> lastJobList, SortedMaxLengthList<AlcoholForSale> currentJobList) {
        List<String> lastListOfIds =
            lastJobList.stream().map(AlcoholForSale::getVinmonopoletProductId).collect(Collectors.toList());

        List<String> currentListOfIds =
            currentJobList.stream().map(AlcoholForSale::getVinmonopoletProductId).collect(Collectors.toList());


        boolean changeHasOccurred = false;
        for (int i  = 0; i < currentListOfIds.size(); i++) {
            int lastResult = lastListOfIds.indexOf(currentListOfIds.get(i));

            String change;
            if (lastResult == -1) {
                change = "New";
                changeHasOccurred = true;
            }
            else if (lastResult == i){
                change = "-";
            }
            else {
                int changeInPosition = lastResult - i;
                if (changeInPosition > 0) {
                    change = "+ " + changeInPosition;
                }
                else {
                    change = "- " + (-1 * changeInPosition);
                }
                changeHasOccurred = true;
            }

            currentJobList.get(i).setChangeInRanking(change);
        }

        return changeHasOccurred;
    }
}
