
package integration.Vinmonopolet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "barcodes",
    "orderPack",
    "minimumOrderQuantity",
    "packagingWeight"
})
public class Logistics {

    @JsonProperty("barcodes")
    private List<Barcode> barcodes = null;
    @JsonProperty("orderPack")
    private String orderPack;
    @JsonProperty("minimumOrderQuantity")
    private Double minimumOrderQuantity;
    @JsonProperty("packagingWeight")
    private Double packagingWeight;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("barcodes")
    public List<Barcode> getBarcodes() {
        return barcodes;
    }

    @JsonProperty("barcodes")
    public void setBarcodes(List<Barcode> barcodes) {
        this.barcodes = barcodes;
    }

    @JsonProperty("orderPack")
    public String getOrderPack() {
        return orderPack;
    }

    @JsonProperty("orderPack")
    public void setOrderPack(String orderPack) {
        this.orderPack = orderPack;
    }

    @JsonProperty("minimumOrderQuantity")
    public Double getMinimumOrderQuantity() {
        return minimumOrderQuantity;
    }

    @JsonProperty("minimumOrderQuantity")
    public void setMinimumOrderQuantity(Double minimumOrderQuantity) {
        this.minimumOrderQuantity = minimumOrderQuantity;
    }

    @JsonProperty("packagingWeight")
    public Double getPackagingWeight() {
        return packagingWeight;
    }

    @JsonProperty("packagingWeight")
    public void setPackagingWeight(Double packagingWeight) {
        this.packagingWeight = packagingWeight;
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
