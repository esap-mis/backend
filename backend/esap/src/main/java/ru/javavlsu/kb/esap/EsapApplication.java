package ru.javavlsu.kb.esap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EsapApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsapApplication.class, args);
	}
}
