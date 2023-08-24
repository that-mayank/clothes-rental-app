package com.nineleaps.leaps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class LeapsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeapsApplication.class, args);
        log.info("Application is running...");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static final String NGROK = "https://eb20-180-151-211-125.ngrok-free.app";

}