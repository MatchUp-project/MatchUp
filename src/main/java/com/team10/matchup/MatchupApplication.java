package com.team10.matchup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MatchupApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchupApplication.class, args);
    }
}
