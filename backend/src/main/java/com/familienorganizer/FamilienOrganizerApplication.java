package com.familienorganizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FamilienOrganizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilienOrganizerApplication.class, args);
    }
}
