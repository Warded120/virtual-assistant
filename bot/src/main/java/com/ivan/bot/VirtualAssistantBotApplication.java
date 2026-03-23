package com.ivan.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class VirtualAssistantBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtualAssistantBotApplication.class, args);
    }
}
