package com.yourstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcommerceStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceStoreApplication.class, args);
    }
}


