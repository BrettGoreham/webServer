
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
    "countryId",
    "country",
    "regionId",
    "region",
    "subRegionId",
    "subRegion"
})
public class Origin {

    @JsonProperty("countryId")
    private String countryId;
    @JsonProperty("country")
    private String country;
    @JsonProperty("regionId")
    private String regionId;
    @JsonProperty("region")
    private String region;
    @JsonProperty("subRegionId")
    private String subRegionId;
    @JsonProperty("subRegion")
    private String subRegion;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("countryId")
    public String getCountryId() {
        return countryId;
    }

    @JsonProperty("countryId")
    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("regionId")
    public String getRegionId() {
        return regionId;
    }

    @JsonProperty("regionId")
    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @JsonProperty("region")
    public String getRegion() {
        return region;
    }

    @JsonProperty("region")
    public void setRegion(String region) {
        this.region = region;
    }

    @JsonProperty("subRegionId")
    public String getSubRegionId() {
        return subRegionId;
    }

    @JsonProperty("subRegionId")
    public void setSubRegionId(String subRegionId) {
        this.subRegionId = subRegionId;
    }

    @JsonProperty("subRegion")
    public String getSubRegion() {
        return subRegion;
    }

    @JsonProperty("subRegion")
    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
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
