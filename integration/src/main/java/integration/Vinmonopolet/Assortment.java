
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
    "assortmentId",
    "assortment",
    "validFrom",
    "listedFrom",
    "assortmentGrade"
})
public class Assortment {

    @JsonProperty("assortmentId")
    private String assortmentId;
    @JsonProperty("assortment")
    private String assortment;
    @JsonProperty("validFrom")
    private String validFrom;
    @JsonProperty("listedFrom")
    private String listedFrom;
    @JsonProperty("assortmentGrade")
    private String assortmentGrade;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("assortmentId")
    public String getAssortmentId() {
        return assortmentId;
    }

    @JsonProperty("assortmentId")
    public void setAssortmentId(String assortmentId) {
        this.assortmentId = assortmentId;
    }

    @JsonProperty("assortment")
    public String getAssortment() {
        return assortment;
    }

    @JsonProperty("assortment")
    public void setAssortment(String assortment) {
        this.assortment = assortment;
    }

    @JsonProperty("validFrom")
    public String getValidFrom() {
        return validFrom;
    }

    @JsonProperty("validFrom")
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    @JsonProperty("listedFrom")
    public String getListedFrom() {
        return listedFrom;
    }

    @JsonProperty("listedFrom")
    public void setListedFrom(String listedFrom) {
        this.listedFrom = listedFrom;
    }

    @JsonProperty("assortmentGrade")
    public String getAssortmentGrade() {
        return assortmentGrade;
    }

    @JsonProperty("assortmentGrade")
    public void setAssortmentGrade(String assortmentGrade) {
        this.assortmentGrade = assortmentGrade;
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
