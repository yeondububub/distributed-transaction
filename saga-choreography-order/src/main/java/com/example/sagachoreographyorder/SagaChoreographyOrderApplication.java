package com.example.sagachoreographyorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class SagaChoreographyOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaChoreographyOrderApplication.class, args);
    }

}
