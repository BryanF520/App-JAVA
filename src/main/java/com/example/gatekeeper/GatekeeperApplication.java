package com.example.gatekeeper;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;


@SpringBootApplication
public class GatekeeperApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatekeeperApplication.class, args);
	}

	@PostConstruct
	public void init() {
    	TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
	}
}




