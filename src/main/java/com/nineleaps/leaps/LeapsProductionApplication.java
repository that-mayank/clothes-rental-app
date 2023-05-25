package com.nineleaps.leaps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
@EnableScheduling
public class LeapsProductionApplication {




    public static void main(String[] args) {
        SpringApplication.run(LeapsProductionApplication.class, args);
        System.out.println("Application is running......");
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static String ngrok_url ="https://4a2d-106-51-70-135.ngrok-free.app" ;

}