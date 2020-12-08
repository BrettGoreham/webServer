
package integration.Vinmonopolet;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "productId",
    "productShortName",
    "productLongName",
    "volume",
    "alcoholContent",
    "vintage",
    "ageLimit",
    "packagingMaterialId",
    "packagingMaterial",
    "volumTypeId",
    "volumType",
    "corkTypeId",
    "corkType",
    "bottlePerSalesUnit",
    "introductionDate",
    "productStatusSaleId",
    "productStatusSaleName",
    "productStatusSaleValidFrom"
})
public class Basic {

    @JsonProperty("productId")
    private String productId;
    @JsonProperty("productShortName")
    private String productShortName;
    @JsonProperty("productLongName")
    private String productLongName;
    @JsonProperty("volume")
    private Double volume;
    @JsonProperty("alcoholContent")
    private Double alcoholContent;
    @JsonProperty("vintage")
    private String vintage;
    @JsonProperty("ageLimit")
    private String ageLimit;
    @JsonProperty("packagingMaterialId")
    private String packagingMaterialId;
    @JsonProperty("packagingMaterial")
    private String packagingMaterial;
    @JsonProperty("volumTypeId")
    private String volumTypeId;
    @JsonProperty("volumType")
    private String volumType;
    @JsonProperty("corkTypeId")
    private String corkTypeId;
    @JsonProperty("corkType")
    private String corkType;
    @JsonProperty("bottlePerSalesUnit")
    private String bottlePerSalesUnit;
    @JsonProperty("introductionDate")
    private String introductionDate;
    @JsonProperty("productStatusSaleId")
    private String productStatusSaleId;
    @JsonProperty("productStatusSaleName")
    private String productStatusSaleName;
    @JsonProperty("productStatusSaleValidFrom")
    private String productStatusSaleValidFrom;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("productId")
    public String getProductId() {
        return productId;
    }

    @JsonProperty("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonProperty("productShortName")
    public String getProductShortName() {
        return productShortName;
    }

    @JsonProperty("productShortName")
    public void setProductShortName(String productShortName) {
        this.productShortName = productShortName;
    }

    @JsonProperty("productLongName")
    public String getProductLongName() {
        return productLongName;
    }

    @JsonProperty("productLongName")
    public void setProductLongName(String productLongName) {
        this.productLongName = productLongName;
    }

    @JsonProperty("volume")
    public Double getVolume() {
        return volume;
    }

    @JsonProperty("volume")
    public void setVolume(Double volume) {
        this.volume = volume;
    }

    @JsonProperty("alcoholContent")
    public Double getAlcoholContent() {
        return alcoholContent;
    }

    @JsonProperty("alcoholContent")
    public void setAlcoholContent(Double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    @JsonProperty("vintage")
    public String getVintage() {
        return vintage;
    }

    @JsonProperty("vintage")
    public void setVintage(String vintage) {
        this.vintage = vintage;
    }

    @JsonProperty("ageLimit")
    public String getAgeLimit() {
        return ageLimit;
    }

    @JsonProperty("ageLimit")
    public void setAgeLimit(String ageLimit) {
        this.ageLimit = ageLimit;
    }

    @JsonProperty("packagingMaterialId")
    public String getPackagingMaterialId() {
        return packagingMaterialId;
    }

    @JsonProperty("packagingMaterialId")
    public void setPackagingMaterialId(String packagingMaterialId) {
        this.packagingMaterialId = packagingMaterialId;
    }

    @JsonProperty("packagingMaterial")
    public String getPackagingMaterial() {
        return packagingMaterial;
    }

    @JsonProperty("packagingMaterial")
    public void setPackagingMaterial(String packagingMaterial) {
        this.packagingMaterial = packagingMaterial;
    }

    @JsonProperty("volumTypeId")
    public String getVolumTypeId() {
        return volumTypeId;
    }

    @JsonProperty("volumTypeId")
    public void setVolumTypeId(String volumTypeId) {
        this.volumTypeId = volumTypeId;
    }

    @JsonProperty("volumType")
    public String getVolumType() {
        return volumType;
    }

    @JsonProperty("volumType")
    public void setVolumType(String volumType) {
        this.volumType = volumType;
    }

    @JsonProperty("corkTypeId")
    public String getCorkTypeId() {
        return corkTypeId;
    }

    @JsonProperty("corkTypeId")
    public void setCorkTypeId(String corkTypeId) {
        this.corkTypeId = corkTypeId;
    }

    @JsonProperty("corkType")
    public String getCorkType() {
        return corkType;
    }

    @JsonProperty("corkType")
    public void setCorkType(String corkType) {
        this.corkType = corkType;
    }

    @JsonProperty("bottlePerSalesUnit")
    public String getBottlePerSalesUnit() {
        return bottlePerSalesUnit;
    }

    @JsonProperty("bottlePerSalesUnit")
    public void setBottlePerSalesUnit(String bottlePerSalesUnit) {
        this.bottlePerSalesUnit = bottlePerSalesUnit;
    }

    @JsonProperty("introductionDate")
    public String getIntroductionDate() {
        return introductionDate;
    }

    @JsonProperty("introductionDate")
    public void setIntroductionDate(String introductionDate) {
        this.introductionDate = introductionDate;
    }

    @JsonProperty("productStatusSaleId")
    public String getProductStatusSaleId() {
        return productStatusSaleId;
    }

    @JsonProperty("productStatusSaleId")
    public void setProductStatusSaleId(String productStatusSaleId) {
        this.productStatusSaleId = productStatusSaleId;
    }

    @JsonProperty("productStatusSaleName")
    public String getProductStatusSaleName() {
        return productStatusSaleName;
    }

    @JsonProperty("productStatusSaleName")
    public void setProductStatusSaleName(String productStatusSaleName) {
        this.productStatusSaleName = productStatusSaleName;
    }

    @JsonProperty("productStatusSaleValidFrom")
    public String getProductStatusSaleValidFrom() {
        return productStatusSaleValidFrom;
    }

    @JsonProperty("productStatusSaleValidFrom")
    public void setProductStatusSaleValidFrom(String productStatusSaleValidFrom) {
        this.productStatusSaleValidFrom = productStatusSaleValidFrom;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
