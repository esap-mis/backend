package ru.javavlsu.kb.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.javavlsu.kb")
public class EsapCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsapCoreApplication.class, args);
    }
}
