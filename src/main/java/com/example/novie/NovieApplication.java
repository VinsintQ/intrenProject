package com.example.novie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NovieApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovieApplication.class, args);
	}

}
