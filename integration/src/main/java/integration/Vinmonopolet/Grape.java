
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
    "grapeId",
    "grapeDesc",
    "grapePct"
})
public class Grape {

    @JsonProperty("grapeId")
    private String grapeId;
    @JsonProperty("grapeDesc")
    private String grapeDesc;
    @JsonProperty("grapePct")
    private String grapePct;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("grapeId")
    public String getGrapeId() {
        return grapeId;
    }

    @JsonProperty("grapeId")
    public void setGrapeId(String grapeId) {
        this.grapeId = grapeId;
    }

    @JsonProperty("grapeDesc")
    public String getGrapeDesc() {
        return grapeDesc;
    }

    @JsonProperty("grapeDesc")
    public void setGrapeDesc(String grapeDesc) {
        this.grapeDesc = grapeDesc;
    }

    @JsonProperty("grapePct")
    public String getGrapePct() {
        return grapePct;
    }

    @JsonProperty("grapePct")
    public void setGrapePct(String grapePct) {
        this.grapePct = grapePct;
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
