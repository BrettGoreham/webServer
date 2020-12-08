
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
    "priceValidFrom",
    "salesPrice",
    "salesPricePrLiter",
    "bottleReturnValue"
})
public class Price {

    @JsonProperty("priceValidFrom")
    private String priceValidFrom;
    @JsonProperty("salesPrice")
    private Double salesPrice;
    @JsonProperty("salesPricePrLiter")
    private Double salesPricePrLiter;
    @JsonProperty("bottleReturnValue")
    private Double bottleReturnValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("priceValidFrom")
    public String getPriceValidFrom() {
        return priceValidFrom;
    }

    @JsonProperty("priceValidFrom")
    public void setPriceValidFrom(String priceValidFrom) {
        this.priceValidFrom = priceValidFrom;
    }

    @JsonProperty("salesPrice")
    public Double getSalesPrice() {
        return salesPrice;
    }

    @JsonProperty("salesPrice")
    public void setSalesPrice(Double salesPrice) {
        this.salesPrice = salesPrice;
    }

    @JsonProperty("salesPricePrLiter")
    public Double getSalesPricePrLiter() {
        return salesPricePrLiter;
    }

    @JsonProperty("salesPricePrLiter")
    public void setSalesPricePrLiter(Double salesPricePrLiter) {
        this.salesPricePrLiter = salesPricePrLiter;
    }

    @JsonProperty("bottleReturnValue")
    public Double getBottleReturnValue() {
        return bottleReturnValue;
    }

    @JsonProperty("bottleReturnValue")
    public void setBottleReturnValue(Double bottleReturnValue) {
        this.bottleReturnValue = bottleReturnValue;
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
