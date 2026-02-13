package com.example.ProyectoSaS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProyectoSaSApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoSaSApplication.class, args);
	}

}
