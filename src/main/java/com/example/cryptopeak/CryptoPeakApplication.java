package com.example.cryptopeak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoPeakApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoPeakApplication.class, args);
	}

}
