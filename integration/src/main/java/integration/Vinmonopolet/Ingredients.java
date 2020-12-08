
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
    "grapes",
    "ingredients",
    "sugar",
    "acid"
})
public class Ingredients {

    @JsonProperty("grapes")
    private List<Grape> grapes = null;
    @JsonProperty("ingredients")
    private String ingredients;
    @JsonProperty("sugar")
    private String sugar;
    @JsonProperty("acid")
    private String acid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("grapes")
    public List<Grape> getGrapes() {
        return grapes;
    }

    @JsonProperty("grapes")
    public void setGrapes(List<Grape> grapes) {
        this.grapes = grapes;
    }

    @JsonProperty("ingredients")
    public String getIngredients() {
        return ingredients;
    }

    @JsonProperty("ingredients")
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    @JsonProperty("sugar")
    public String getSugar() {
        return sugar;
    }

    @JsonProperty("sugar")
    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    @JsonProperty("acid")
    public String getAcid() {
        return acid;
    }

    @JsonProperty("acid")
    public void setAcid(String acid) {
        this.acid = acid;
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
