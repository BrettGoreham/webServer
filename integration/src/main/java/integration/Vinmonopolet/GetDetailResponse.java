
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
    "basic",
    "logistics",
    "origins",
    "properties",
    "classification",
    "ingredients",
    "description",
    "assortment",
    "prices",
    "lastChanged"
})
public class GetDetailResponse {

    @JsonProperty("basic")
    private Basic basic;
    @JsonProperty("logistics")
    private Logistics logistics;
    @JsonProperty("origins")
    private Origins origins;
    @JsonProperty("properties")
    private Properties properties;
    @JsonProperty("classification")
    private Classification classification;
    @JsonProperty("ingredients")
    private Ingredients ingredients;
    @JsonProperty("description")
    private Description description;
    @JsonProperty("assortment")
    private Assortment assortment;
    @JsonProperty("prices")
    private List<Price> prices = null;
    @JsonProperty("lastChanged")
    private LastChanged lastChanged;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("basic")
    public Basic getBasic() {
        return basic;
    }

    @JsonProperty("basic")
    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    @JsonProperty("logistics")
    public Logistics getLogistics() {
        return logistics;
    }

    @JsonProperty("logistics")
    public void setLogistics(Logistics logistics) {
        this.logistics = logistics;
    }

    @JsonProperty("origins")
    public Origins getOrigins() {
        return origins;
    }

    @JsonProperty("origins")
    public void setOrigins(Origins origins) {
        this.origins = origins;
    }

    @JsonProperty("properties")
    public Properties getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @JsonProperty("classification")
    public Classification getClassification() {
        return classification;
    }

    @JsonProperty("classification")
    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    @JsonProperty("ingredients")
    public Ingredients getIngredients() {
        return ingredients;
    }

    @JsonProperty("ingredients")
    public void setIngredients(Ingredients ingredients) {
        this.ingredients = ingredients;
    }

    @JsonProperty("description")
    public Description getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Description description) {
        this.description = description;
    }

    @JsonProperty("assortment")
    public Assortment getAssortment() {
        return assortment;
    }

    @JsonProperty("assortment")
    public void setAssortment(Assortment assortment) {
        this.assortment = assortment;
    }

    @JsonProperty("prices")
    public List<Price> getPrices() {
        return prices;
    }

    @JsonProperty("prices")
    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    @JsonProperty("lastChanged")
    public LastChanged getLastChanged() {
        return lastChanged;
    }

    @JsonProperty("lastChanged")
    public void setLastChanged(LastChanged lastChanged) {
        this.lastChanged = lastChanged;
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
