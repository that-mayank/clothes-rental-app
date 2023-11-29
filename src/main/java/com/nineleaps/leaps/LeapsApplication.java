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
        //Added to explain log obfuscation
        String password = "Yokes&12!<>;";
        log.info("password :{}", password);
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static final String NGROK = "https://25e1-106-51-70-135.ngrok-free.app";
    public static final String BUCKET_NAME = "leapsimagebucket";

    public static final Integer ACCOUNT_LOCK_DURATION_MINUTES = 2;
    public static final Integer MAX_LOGIN_ATTEMPTS = 3;




}