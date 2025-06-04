package com.stormguard.stormguard_api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StormGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(StormGuardApplication.class, args);
    }

    @Bean
    public CommandLineRunner logDatasourceUrl(@Value("${spring.datasource.url:NOT_SET}") String url) {
        return args -> System.out.println("Datasource URL (Spring): " + url);
    }
}