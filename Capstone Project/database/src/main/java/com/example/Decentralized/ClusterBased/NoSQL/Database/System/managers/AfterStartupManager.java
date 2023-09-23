package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.services.BootstrappingService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AfterStartupManager implements ApplicationRunner {

    private final BootstrappingService bootstrappingService;

    public AfterStartupManager(BootstrappingService bootstrappingService) {
        this.bootstrappingService = bootstrappingService;
    }

    @Override
    public void run(ApplicationArguments args) {
        bootstrappingService.pingBootstrapperForInitData();
    }


}
