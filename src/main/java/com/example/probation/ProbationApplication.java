package com.example.probation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class ProbationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProbationApplication.class, args);
    }

}
