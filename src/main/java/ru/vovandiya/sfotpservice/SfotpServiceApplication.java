package ru.vovandiya.sfotpservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SfotpServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SfotpServiceApplication.class, args);
    }

}
