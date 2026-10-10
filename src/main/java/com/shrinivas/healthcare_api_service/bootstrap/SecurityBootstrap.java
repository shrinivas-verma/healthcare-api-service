package com.shrinivas.healthcare_api_service.bootstrap;


import com.shrinivas.healthcare_api_service.service.SecurityBootstrapService;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
public class SecurityBootstrap implements ApplicationRunner {

    private final SecurityBootstrapService bootstrapService;

    public SecurityBootstrap(SecurityBootstrapService bootstrapService) {
        this.bootstrapService = bootstrapService;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        bootstrapService.initialize();
    }
}