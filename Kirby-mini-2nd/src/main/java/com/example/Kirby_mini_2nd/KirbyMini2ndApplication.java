package com.example.Kirby_mini_2nd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.example.Kirby_mini_2nd.repository.repo"})
@EntityScan(basePackages = {"com.example.Kirby_mini_2nd.repository.entity"})
@EnableScheduling
public class KirbyMini2ndApplication {
	public static void main(String[] args) {
		SpringApplication.run(KirbyMini2ndApplication.class, args);
	}
}
