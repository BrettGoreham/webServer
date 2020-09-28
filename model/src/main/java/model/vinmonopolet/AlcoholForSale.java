package model.vinmonopolet;

public class AlcoholForSale {
    private String name;

    private String category;

    private int vinmonopoletProductId;

    private double salePrice;
    private double saleVolume;

    private double alcoholPercentage;


    private double salePricePerLiter;
    private double salePricePerAlcoholUnit;


    public AlcoholForSale(
        String name,
        String category,
        int vinmonopoletProductId,
        double salePrice,
        double saleVolume,
        double alcoholPercentage) {

        this.name = name;
        this.category = category;
        this.vinmonopoletProductId = vinmonopoletProductId;
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

    public int getVinmonopoletProductId() {
        return vinmonopoletProductId;
    }

    public void setVinmonopoletProductId(int vinmonopoletProductId) {
        this.vinmonopoletProductId = vinmonopoletProductId;
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


}
