package com.nineleaps.leaps.config;


import com.nineleaps.leaps.service.OrderService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {


    private final OrderService reminderService;

    public SchedulerConfig(OrderService reminderService) {
        this.reminderService = reminderService;
    }

    //@Scheduled(cron = "0 0 12 * * ?") // Runs every day at 12 PM 0 0 12 * * ? */10 * * * * *
    public void sendReminderEmails() {
       reminderService.getRentalPeriods();
       System.out.println("email sent");

    }
}

