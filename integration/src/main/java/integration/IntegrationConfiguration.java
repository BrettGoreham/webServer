package integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vinmonopolet.restclient.ApiClient;
import vinmonopolet.restclient.api.DefaultApi;

@Configuration
public class IntegrationConfiguration {

    @Bean
    public DefaultApi defaultApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }

    @Bean
    public ApiClient apiClient(@Value("${vinmonopolet.Key}") String headerKey){
        ApiClient apiClient = new ApiClient();

        apiClient.addDefaultHeader("Ocp-Apim-Subscription-Key", headerKey);

        return apiClient;
    }
}
