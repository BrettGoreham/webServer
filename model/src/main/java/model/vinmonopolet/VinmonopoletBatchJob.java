package model.vinmonopolet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.*;

@JsonIgnoreProperties(value = { "alcoholForSaleComparator" })
public class VinmonopoletBatchJob {

    private long batchId;
    private LocalDate dateOfRun;
    private LocalDate lastDateRunWasCheckedAsStillValid;
    private String status;
    private int sizeOfOverallTopList;
    private int sizeOfCategoryTopList;
    private SortedMaxLengthList<AlcoholForSale> overallAlcoholForSalePricePerAlcoholLiter;
    private Map<String, SortedMaxLengthList<AlcoholForSale>> categoryToAlcoholForSalePricePerAlcoholLiter;

    private final Comparator<AlcoholForSale> alcoholForSaleComparator;

    public VinmonopoletBatchJob(
            long batchId,
            LocalDate dateOfRun,
            LocalDate lastDateRunWasCheckedAsStillValid,
            String status,
            int sizeOfOverallTopList,
            int sizeOfCategoryTopList) {
        this.batchId = batchId;
        this.dateOfRun = dateOfRun;
        this.lastDateRunWasCheckedAsStillValid = lastDateRunWasCheckedAsStillValid;
        this.status = status;
        this.sizeOfOverallTopList = sizeOfOverallTopList;
        this.sizeOfCategoryTopList = sizeOfCategoryTopList;

        //Batch job goal to compare the price per liter of alcohol.
        //name comparison to break ties just to get a decided order per run
        alcoholForSaleComparator = (o1, o2) -> {
            int doubleCompareValue = Double.compare(o1.getSalePricePerAlcoholLiter(), o2.getSalePricePerAlcoholLiter());
            if (doubleCompareValue == 0) {
                return o1.getName().compareTo(o2.getName());
            } else {
                return doubleCompareValue;
            }
        };


        overallAlcoholForSalePricePerAlcoholLiter = new SortedMaxLengthList<>(sizeOfOverallTopList, alcoholForSaleComparator);
        categoryToAlcoholForSalePricePerAlcoholLiter = new TreeMap<>(); // want output to be in alphabetical order in regards to category.
    }

    public void addAlcoholsToTopLists(List<AlcoholForSale> alcoholsToAdd) {
        for (AlcoholForSale alcoholForSale : alcoholsToAdd) {
            //deep copy is important here to keep change in ranking independent
            overallAlcoholForSalePricePerAlcoholLiter.add(alcoholForSale.deepCopy());

            SortedMaxLengthList<AlcoholForSale> categoryTopList =
                categoryToAlcoholForSalePricePerAlcoholLiter.getOrDefault(alcoholForSale.getCategory(), new SortedMaxLengthList<>(sizeOfCategoryTopList, alcoholForSaleComparator));

            categoryTopList.add(alcoholForSale);
            categoryToAlcoholForSalePricePerAlcoholLiter.put(alcoholForSale.getCategory(), categoryTopList);

        }
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public SortedMaxLengthList<AlcoholForSale> getOverallAlcoholForSalePricePerAlcoholLiter() {
        return overallAlcoholForSalePricePerAlcoholLiter;
    }

    public void setOverallAlcoholForSalePricePerAlcoholLiter(SortedMaxLengthList<AlcoholForSale> overallAlcoholForSalePricePerAlcoholLiter) {
        this.overallAlcoholForSalePricePerAlcoholLiter = overallAlcoholForSalePricePerAlcoholLiter;
    }

    public Map<String, SortedMaxLengthList<AlcoholForSale>> getCategoryToAlcoholForSalePricePerAlcoholLiter() {
        return categoryToAlcoholForSalePricePerAlcoholLiter;
    }

    public void setCategoryToAlcoholForSalePricePerAlcoholLiter(Map<String, SortedMaxLengthList<AlcoholForSale>> categoryToAlcoholForSalePricePerAlcoholLiter) {
        this.categoryToAlcoholForSalePricePerAlcoholLiter = categoryToAlcoholForSalePricePerAlcoholLiter;
    }

    public Comparator<AlcoholForSale> getAlcoholForSaleComparator() {
        return alcoholForSaleComparator;
    }
}
