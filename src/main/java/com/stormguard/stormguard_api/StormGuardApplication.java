package com.stormguard.stormguard_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StormGuardApplication {

	public String PORT = System.getenv("PORT") != null ? System.getenv("PORT") : "8080";

	public static void main(String[] args) {
		SpringApplication.run(StormGuardApplication.class, args);
	}

}
