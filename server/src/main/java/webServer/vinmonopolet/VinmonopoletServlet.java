package webServer.vinmonopolet;

import integration.Vinmonopolet.GetDetailResponse;
import integration.VinmonopoletIntegration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vinmonopolet")
public class VinmonopoletServlet {

    private VinmonopoletIntegration vinmonopoletIntegration;
    public VinmonopoletServlet(VinmonopoletIntegration vinmonopoletIntegration) {
        this.vinmonopoletIntegration = vinmonopoletIntegration;
    }

    @GetMapping
    public String get(){
        try {
            List<GetDetailResponse> saleObjects = vinmonopoletIntegration.callVinmonopoletForDetails(1048575, 21914);
            System.out.println(saleObjects.size());
        }
        catch (Exception e) {
            return "failure";
        }

        return "success";
    }
}
