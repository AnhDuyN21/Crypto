package com.example.Java_Crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavaCryptoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaCryptoApplication.class, args);
	}

}
