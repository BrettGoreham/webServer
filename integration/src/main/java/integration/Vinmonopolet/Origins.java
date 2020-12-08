
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
    "origin",
    "production",
    "localQualityClassifId",
    "localQualityClassif"
})
public class Origins {

    @JsonProperty("origin")
    private Origin origin;
    @JsonProperty("production")
    private Production production;
    @JsonProperty("localQualityClassifId")
    private String localQualityClassifId;
    @JsonProperty("localQualityClassif")
    private String localQualityClassif;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("origin")
    public Origin getOrigin() {
        return origin;
    }

    @JsonProperty("origin")
    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    @JsonProperty("production")
    public Production getProduction() {
        return production;
    }

    @JsonProperty("production")
    public void setProduction(Production production) {
        this.production = production;
    }

    @JsonProperty("localQualityClassifId")
    public String getLocalQualityClassifId() {
        return localQualityClassifId;
    }

    @JsonProperty("localQualityClassifId")
    public void setLocalQualityClassifId(String localQualityClassifId) {
        this.localQualityClassifId = localQualityClassifId;
    }

    @JsonProperty("localQualityClassif")
    public String getLocalQualityClassif() {
        return localQualityClassif;
    }

    @JsonProperty("localQualityClassif")
    public void setLocalQualityClassif(String localQualityClassif) {
        this.localQualityClassif = localQualityClassif;
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
