package com.batch.settlement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
@SpringBootApplication
public class SettlementApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(SettlementApplication.class, args)));
	}

}
