package integration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import integration.Vinmonopolet.GetDetailResponse;
import org.springframework.stereotype.Service;
import vinmonopolet.restclient.ApiException;
import vinmonopolet.restclient.api.DefaultApi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class VinmonopoletIntegration {

    private final DefaultApi defaultApi;

    public VinmonopoletIntegration(DefaultApi defaultApi) {
        this.defaultApi = defaultApi;
    }

    public List<GetDetailResponse> callVinmonopoletForDetails(Integer maxResults, Integer start) throws ApiException {
        List<Object> apiResponse = defaultApi.gETDETAILSNORMAL(null,null,null,null,null,null,maxResults,start);

        //if anyone reads this im sorry i didnt want to do mapping myself.
        Gson gson = new Gson();
        String s = gson.toJson(apiResponse);
        Type listType = new TypeToken<ArrayList<GetDetailResponse>>(){}.getType();

        return gson.fromJson(s, listType);
    }
}
