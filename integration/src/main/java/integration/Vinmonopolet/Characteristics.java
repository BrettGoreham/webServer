
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
    "colour",
    "odour",
    "taste"
})
public class Characteristics {

    @JsonProperty("colour")
    private String colour;
    @JsonProperty("odour")
    private String odour;
    @JsonProperty("taste")
    private String taste;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("colour")
    public String getColour() {
        return colour;
    }

    @JsonProperty("colour")
    public void setColour(String colour) {
        this.colour = colour;
    }

    @JsonProperty("odour")
    public String getOdour() {
        return odour;
    }

    @JsonProperty("odour")
    public void setOdour(String odour) {
        this.odour = odour;
    }

    @JsonProperty("taste")
    public String getTaste() {
        return taste;
    }

    @JsonProperty("taste")
    public void setTaste(String taste) {
        this.taste = taste;
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
