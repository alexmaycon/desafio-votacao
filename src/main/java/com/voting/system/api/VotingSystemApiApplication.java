package com.voting.system.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VotingSystemApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VotingSystemApiApplication.class, args);
    }
}
