package model.vinmonopolet;

import java.time.LocalDate;
import java.util.*;

public class VinmonopoletBatchJob {

    private long batchId;
    private LocalDate dateOfRun;
    private LocalDate lastDateRunWasCheckedAsStillValid;
    private String status;
    private int sizeOfOverallTopList;
    private int sizeOfCategoryTopList;
    private SortedMaxLengthList<AlcoholForSale> overallAlcoholForSalePricePerAlcoholUnit;
    private Map<String, SortedMaxLengthList<AlcoholForSale>> categoryToAlcoholForSalePricePerAlcoholUnit;

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

        alcoholForSaleComparator = Comparator.comparingDouble(AlcoholForSale::getSalePricePerAlcoholUnit);

        overallAlcoholForSalePricePerAlcoholUnit = new SortedMaxLengthList<>(sizeOfOverallTopList, alcoholForSaleComparator);
        categoryToAlcoholForSalePricePerAlcoholUnit = new HashMap<>();
    }

    public void addAlcoholsToTopLists(List<AlcoholForSale> alcoholsToAdd) {
        for (AlcoholForSale alcoholForSale : alcoholsToAdd) {
            overallAlcoholForSalePricePerAlcoholUnit.add(alcoholForSale);

            SortedMaxLengthList<AlcoholForSale> categoryTopList =
                categoryToAlcoholForSalePricePerAlcoholUnit.getOrDefault(alcoholForSale.getCategory(), new SortedMaxLengthList<>(sizeOfCategoryTopList, alcoholForSaleComparator));

            categoryTopList.add(alcoholForSale);
            categoryToAlcoholForSalePricePerAlcoholUnit.put(alcoholForSale.getCategory(), categoryTopList);

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

    public SortedMaxLengthList<AlcoholForSale> getOverallAlcoholForSalePricePerAlcoholUnit() {
        return overallAlcoholForSalePricePerAlcoholUnit;
    }

    public void setOverallAlcoholForSalePricePerAlcoholUnit(SortedMaxLengthList<AlcoholForSale> overallAlcoholForSalePricePerAlcoholUnit) {
        this.overallAlcoholForSalePricePerAlcoholUnit = overallAlcoholForSalePricePerAlcoholUnit;
    }

    public Map<String, SortedMaxLengthList<AlcoholForSale>> getCategoryToAlcoholForSalePricePerAlcoholUnit() {
        return categoryToAlcoholForSalePricePerAlcoholUnit;
    }

    public void setCategoryToAlcoholForSalePricePerAlcoholUnit(Map<String, SortedMaxLengthList<AlcoholForSale>> categoryToAlcoholForSalePricePerAlcoholUnit) {
        this.categoryToAlcoholForSalePricePerAlcoholUnit = categoryToAlcoholForSalePricePerAlcoholUnit;
    }

    public Comparator<AlcoholForSale> getAlcoholForSaleComparator() {
        return alcoholForSaleComparator;
    }
}
