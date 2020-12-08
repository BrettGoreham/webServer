CREATE TABLE VINMONOPOLET_BATCH (
    batchId INT NOT NULL AUTO_INCREMENT,
    status VARCHAR(20),
    dateOfRun Date,
    lastDateRunWasCheckedAsStillValid Date,
    sizeOfOverallTopList INT NOT NULL,
    sizeOfCategoryTopList INT NOT NULL,
    PRIMARY KEY (batchId)
);

CREATE TABLE VINMONOPOLET_BATCH_TOP_LIST_RECORDS(
    id INT NOT NULL AUTO_INCREMENT,
    fk_vinmonopoletBatchID Int NOT NULL,
    listCategory VARCHAR(30) NOT NULL,
    vinmonopoletProductId VARCHAR(10) NOT NULL,
    productName VARCHAR(200) NOT NULL,
    productCategory VARCHAR(30) NOT NULL,
    salePrice DOUBLE,
    saleVolume DOUBLE,
    alcoholPercentage DOUBLE,
    salePricePerLiter DOUBLE,
    salePricePerAlcoholLiter DOUBLE,

    PRIMARY KEY (id),
    FOREIGN KEY (fk_vinmonopoletBatchID) REFERENCES VINMONOPOLET_BATCH(batchId)
);

/**
  long batchId,
            Date dateOfRun,
            Date lastDateRunWasCheckedAsStillValid,
            String status,
            int sizeOfOverallTopList,
            int sizeOfCategoryTopList


  private String name;
    private String category;
    private String vinmonopoletProductId;

    private double salePrice;
    private double saleVolume;
    private double alcoholPercentage;
    private double salePricePerLiter;
    private double salePricePerAlcoholUnit
*/