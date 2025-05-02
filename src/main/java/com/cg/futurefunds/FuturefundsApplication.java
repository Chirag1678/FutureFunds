package com.cg.futurefunds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FuturefundsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuturefundsApplication.class, args);
	}

}
