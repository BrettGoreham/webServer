
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
    "characteristics",
    "freshness",
    "fullness",
    "bitterness",
    "sweetness",
    "tannins",
    "recommendedFood"
})
public class Description {

    @JsonProperty("characteristics")
    private Characteristics characteristics;
    @JsonProperty("freshness")
    private String freshness;
    @JsonProperty("fullness")
    private String fullness;
    @JsonProperty("bitterness")
    private String bitterness;
    @JsonProperty("sweetness")
    private String sweetness;
    @JsonProperty("tannins")
    private String tannins;
    @JsonProperty("recommendedFood")
    private List<RecommendedFood> recommendedFood = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("characteristics")
    public Characteristics getCharacteristics() {
        return characteristics;
    }

    @JsonProperty("characteristics")
    public void setCharacteristics(Characteristics characteristics) {
        this.characteristics = characteristics;
    }

    @JsonProperty("freshness")
    public String getFreshness() {
        return freshness;
    }

    @JsonProperty("freshness")
    public void setFreshness(String freshness) {
        this.freshness = freshness;
    }

    @JsonProperty("fullness")
    public String getFullness() {
        return fullness;
    }

    @JsonProperty("fullness")
    public void setFullness(String fullness) {
        this.fullness = fullness;
    }

    @JsonProperty("bitterness")
    public String getBitterness() {
        return bitterness;
    }

    @JsonProperty("bitterness")
    public void setBitterness(String bitterness) {
        this.bitterness = bitterness;
    }

    @JsonProperty("sweetness")
    public String getSweetness() {
        return sweetness;
    }

    @JsonProperty("sweetness")
    public void setSweetness(String sweetness) {
        this.sweetness = sweetness;
    }

    @JsonProperty("tannins")
    public String getTannins() {
        return tannins;
    }

    @JsonProperty("tannins")
    public void setTannins(String tannins) {
        this.tannins = tannins;
    }

    @JsonProperty("recommendedFood")
    public List<RecommendedFood> getRecommendedFood() {
        return recommendedFood;
    }

    @JsonProperty("recommendedFood")
    public void setRecommendedFood(List<RecommendedFood> recommendedFood) {
        this.recommendedFood = recommendedFood;
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
