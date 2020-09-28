package webServer.vinmonopolet;

import integration.VinmonopoletIntegration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VinmonopoletProcessor {


    public VinmonopoletProcessor(VinmonopoletIntegration vinmonopoletIntegration,
                                 @Value("${vinmonopolet.pageSize}") String pageSize ) {

    }

    @Scheduled(fixedDelay = 1000000000, initialDelay = 1000)
    public void processVinmonopoletData(){

    }
}
