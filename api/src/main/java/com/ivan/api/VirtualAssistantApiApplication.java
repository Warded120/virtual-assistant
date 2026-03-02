package com.ivan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class VirtualAssistantApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualAssistantApiApplication.class, args);
    }

}
