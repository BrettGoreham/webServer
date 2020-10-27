package model.vinmonopolet;

import java.util.Objects;

public class AlcoholForSale {
    private String name;
    private String category;
    private String vinmonopoletProductId;
    private String saleStatus;

    private double salePrice;
    private double saleVolume;
    private double alcoholPercentage;
    private double salePricePerLiter;
    private double salePricePerAlcoholUnit;


    public AlcoholForSale(
        String name,
        String category,
        String vinmonopoletProductId,
        String saleStatus,
        double salePrice,
        double saleVolume,
        double alcoholPercentage) {

        this.name = name;
        this.category = category;
        this.vinmonopoletProductId = vinmonopoletProductId;
        this.saleStatus = saleStatus;
        this.salePrice = salePrice;
        this.saleVolume = saleVolume;
        this.alcoholPercentage = alcoholPercentage;

        this.salePricePerLiter = salePrice / saleVolume;
        this.salePricePerAlcoholUnit = salePricePerLiter / alcoholPercentage;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVinmonopoletProductId() {
        return vinmonopoletProductId;
    }

    public void setVinmonopoletProductId(String vinmonopoletProductId) {
        this.vinmonopoletProductId = vinmonopoletProductId;
    }

    public String getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getSaleVolume() {
        return saleVolume;
    }

    public void setSaleVolume(double saleVolume) {
        this.saleVolume = saleVolume;
    }

    public double getAlcoholPercentage() {
        return alcoholPercentage;
    }

    public void setAlcoholPercentage(double alcoholPercentage) {
        this.alcoholPercentage = alcoholPercentage;
    }

    public double getSalePricePerLiter() {
        return salePricePerLiter;
    }

    public void setSalePricePerLiter(double salePricePerLiter) {
        this.salePricePerLiter = salePricePerLiter;
    }

    public double getSalePricePerAlcoholUnit() {
        return salePricePerAlcoholUnit;
    }

    public void setSalePricePerAlcoholUnit(double salePricePerAlcoholUnit) {
        this.salePricePerAlcoholUnit = salePricePerAlcoholUnit;
    }

    public String generateUrlToProduct() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("vinmonopolet.no/p/");
        stringBuilder.append(vinmonopoletProductId);

        return "vinmonopolet.no/p/" + vinmonopoletProductId;
    }

    public String generatePictureUrl(int height, int width) {
        return "https://bilder.vinmonopolet.no/cache/" + width + "x" + height + "-0/" + vinmonopoletProductId + "-1.jpg";
    }

    /**
     *
     * note I have not included sales status in that database as its not important only for filtering
     * results that come from vinmonopolet.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlcoholForSale that = (AlcoholForSale) o;
        return Double.compare(that.salePrice, salePrice) == 0 &&
            Double.compare(that.saleVolume, saleVolume) == 0 &&
            Double.compare(that.alcoholPercentage, alcoholPercentage) == 0 &&
            Double.compare(that.salePricePerLiter, salePricePerLiter) == 0 &&
            Double.compare(that.salePricePerAlcoholUnit, salePricePerAlcoholUnit) == 0 &&
            name.equals(that.name) &&
            category.equals(that.category) &&
            vinmonopoletProductId.equals(that.vinmonopoletProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, vinmonopoletProductId, salePrice, saleVolume, alcoholPercentage, salePricePerLiter, salePricePerAlcoholUnit);
    }
}

