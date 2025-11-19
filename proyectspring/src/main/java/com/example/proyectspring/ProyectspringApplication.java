package com.example.proyectspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProyectspringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectspringApplication.class, args);
	}

}
