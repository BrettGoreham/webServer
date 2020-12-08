
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
    "mainProductTypeId",
    "mainProductTypeName",
    "subProductTypeId",
    "subProductTypeName",
    "productTypeId",
    "productTypeName",
    "productGroupId",
    "productGroupName"
})
public class Classification {

    @JsonProperty("mainProductTypeId")
    private String mainProductTypeId;
    @JsonProperty("mainProductTypeName")
    private String mainProductTypeName;
    @JsonProperty("subProductTypeId")
    private String subProductTypeId;
    @JsonProperty("subProductTypeName")
    private String subProductTypeName;
    @JsonProperty("productTypeId")
    private String productTypeId;
    @JsonProperty("productTypeName")
    private String productTypeName;
    @JsonProperty("productGroupId")
    private String productGroupId;
    @JsonProperty("productGroupName")
    private String productGroupName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("mainProductTypeId")
    public String getMainProductTypeId() {
        return mainProductTypeId;
    }

    @JsonProperty("mainProductTypeId")
    public void setMainProductTypeId(String mainProductTypeId) {
        this.mainProductTypeId = mainProductTypeId;
    }

    @JsonProperty("mainProductTypeName")
    public String getMainProductTypeName() {
        return mainProductTypeName;
    }

    @JsonProperty("mainProductTypeName")
    public void setMainProductTypeName(String mainProductTypeName) {
        this.mainProductTypeName = mainProductTypeName;
    }

    @JsonProperty("subProductTypeId")
    public String getSubProductTypeId() {
        return subProductTypeId;
    }

    @JsonProperty("subProductTypeId")
    public void setSubProductTypeId(String subProductTypeId) {
        this.subProductTypeId = subProductTypeId;
    }

    @JsonProperty("subProductTypeName")
    public String getSubProductTypeName() {
        return subProductTypeName;
    }

    @JsonProperty("subProductTypeName")
    public void setSubProductTypeName(String subProductTypeName) {
        this.subProductTypeName = subProductTypeName;
    }

    @JsonProperty("productTypeId")
    public String getProductTypeId() {
        return productTypeId;
    }

    @JsonProperty("productTypeId")
    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    @JsonProperty("productTypeName")
    public String getProductTypeName() {
        return productTypeName;
    }

    @JsonProperty("productTypeName")
    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    @JsonProperty("productGroupId")
    public String getProductGroupId() {
        return productGroupId;
    }

    @JsonProperty("productGroupId")
    public void setProductGroupId(String productGroupId) {
        this.productGroupId = productGroupId;
    }

    @JsonProperty("productGroupName")
    public String getProductGroupName() {
        return productGroupName;
    }

    @JsonProperty("productGroupName")
    public void setProductGroupName(String productGroupName) {
        this.productGroupName = productGroupName;
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
