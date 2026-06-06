package com.example.sagaorchestrationorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class SagaOrchestrationOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaOrchestrationOrderApplication.class, args);
    }

}
