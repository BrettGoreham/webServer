
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
    "ecoLabellingId",
    "ecoLabelling",
    "storagePotentialId",
    "storagePotential",
    "organic",
    "biodynamic",
    "ethicallyCertified",
    "vintageControlled",
    "sweetWine",
    "freeOrLowOnGluten",
    "kosher",
    "locallyProduced",
    "noAddedSulphur",
    "environmentallySmart",
    "productionMethodStorage"
})
public class Properties {

    @JsonProperty("ecoLabellingId")
    private String ecoLabellingId;
    @JsonProperty("ecoLabelling")
    private String ecoLabelling;
    @JsonProperty("storagePotentialId")
    private String storagePotentialId;
    @JsonProperty("storagePotential")
    private String storagePotential;
    @JsonProperty("organic")
    private Boolean organic;
    @JsonProperty("biodynamic")
    private Boolean biodynamic;
    @JsonProperty("ethicallyCertified")
    private Boolean ethicallyCertified;
    @JsonProperty("vintageControlled")
    private Boolean vintageControlled;
    @JsonProperty("sweetWine")
    private Boolean sweetWine;
    @JsonProperty("freeOrLowOnGluten")
    private Boolean freeOrLowOnGluten;
    @JsonProperty("kosher")
    private Boolean kosher;
    @JsonProperty("locallyProduced")
    private Boolean locallyProduced;
    @JsonProperty("noAddedSulphur")
    private Boolean noAddedSulphur;
    @JsonProperty("environmentallySmart")
    private Boolean environmentallySmart;
    @JsonProperty("productionMethodStorage")
    private String productionMethodStorage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ecoLabellingId")
    public String getEcoLabellingId() {
        return ecoLabellingId;
    }

    @JsonProperty("ecoLabellingId")
    public void setEcoLabellingId(String ecoLabellingId) {
        this.ecoLabellingId = ecoLabellingId;
    }

    @JsonProperty("ecoLabelling")
    public String getEcoLabelling() {
        return ecoLabelling;
    }

    @JsonProperty("ecoLabelling")
    public void setEcoLabelling(String ecoLabelling) {
        this.ecoLabelling = ecoLabelling;
    }

    @JsonProperty("storagePotentialId")
    public String getStoragePotentialId() {
        return storagePotentialId;
    }

    @JsonProperty("storagePotentialId")
    public void setStoragePotentialId(String storagePotentialId) {
        this.storagePotentialId = storagePotentialId;
    }

    @JsonProperty("storagePotential")
    public String getStoragePotential() {
        return storagePotential;
    }

    @JsonProperty("storagePotential")
    public void setStoragePotential(String storagePotential) {
        this.storagePotential = storagePotential;
    }

    @JsonProperty("organic")
    public Boolean getOrganic() {
        return organic;
    }

    @JsonProperty("organic")
    public void setOrganic(Boolean organic) {
        this.organic = organic;
    }

    @JsonProperty("biodynamic")
    public Boolean getBiodynamic() {
        return biodynamic;
    }

    @JsonProperty("biodynamic")
    public void setBiodynamic(Boolean biodynamic) {
        this.biodynamic = biodynamic;
    }

    @JsonProperty("ethicallyCertified")
    public Boolean getEthicallyCertified() {
        return ethicallyCertified;
    }

    @JsonProperty("ethicallyCertified")
    public void setEthicallyCertified(Boolean ethicallyCertified) {
        this.ethicallyCertified = ethicallyCertified;
    }

    @JsonProperty("vintageControlled")
    public Boolean getVintageControlled() {
        return vintageControlled;
    }

    @JsonProperty("vintageControlled")
    public void setVintageControlled(Boolean vintageControlled) {
        this.vintageControlled = vintageControlled;
    }

    @JsonProperty("sweetWine")
    public Boolean getSweetWine() {
        return sweetWine;
    }

    @JsonProperty("sweetWine")
    public void setSweetWine(Boolean sweetWine) {
        this.sweetWine = sweetWine;
    }

    @JsonProperty("freeOrLowOnGluten")
    public Boolean getFreeOrLowOnGluten() {
        return freeOrLowOnGluten;
    }

    @JsonProperty("freeOrLowOnGluten")
    public void setFreeOrLowOnGluten(Boolean freeOrLowOnGluten) {
        this.freeOrLowOnGluten = freeOrLowOnGluten;
    }

    @JsonProperty("kosher")
    public Boolean getKosher() {
        return kosher;
    }

    @JsonProperty("kosher")
    public void setKosher(Boolean kosher) {
        this.kosher = kosher;
    }

    @JsonProperty("locallyProduced")
    public Boolean getLocallyProduced() {
        return locallyProduced;
    }

    @JsonProperty("locallyProduced")
    public void setLocallyProduced(Boolean locallyProduced) {
        this.locallyProduced = locallyProduced;
    }

    @JsonProperty("noAddedSulphur")
    public Boolean getNoAddedSulphur() {
        return noAddedSulphur;
    }

    @JsonProperty("noAddedSulphur")
    public void setNoAddedSulphur(Boolean noAddedSulphur) {
        this.noAddedSulphur = noAddedSulphur;
    }

    @JsonProperty("environmentallySmart")
    public Boolean getEnvironmentallySmart() {
        return environmentallySmart;
    }

    @JsonProperty("environmentallySmart")
    public void setEnvironmentallySmart(Boolean environmentallySmart) {
        this.environmentallySmart = environmentallySmart;
    }

    @JsonProperty("productionMethodStorage")
    public String getProductionMethodStorage() {
        return productionMethodStorage;
    }

    @JsonProperty("productionMethodStorage")
    public void setProductionMethodStorage(String productionMethodStorage) {
        this.productionMethodStorage = productionMethodStorage;
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
