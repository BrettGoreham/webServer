
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
    "gtin",
    "isMainGtin"
})
public class Barcode {

    @JsonProperty("gtin")
    private String gtin;
    @JsonProperty("isMainGtin")
    private Boolean isMainGtin;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("gtin")
    public String getGtin() {
        return gtin;
    }

    @JsonProperty("gtin")
    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    @JsonProperty("isMainGtin")
    public Boolean getIsMainGtin() {
        return isMainGtin;
    }

    @JsonProperty("isMainGtin")
    public void setIsMainGtin(Boolean isMainGtin) {
        this.isMainGtin = isMainGtin;
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
