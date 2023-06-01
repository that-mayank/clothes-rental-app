package com.nineleaps.leaps.config;


import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
@AllArgsConstructor
public class SchedulerConfig {


    private final OrderService reminderService;
    private final OrderItemRepository orderItemRepository;

    //@Scheduled(cron = "0 0 12 * * ?") // Runs every day at 12 PM 0 0 12 * * ? */10 * * * * *
    public void sendReminderEmails() {
       reminderService.getRentalPeriods();
       System.out.println("email sent");

    }

    //@Scheduled(cron = "*/20 * * * * *")
    public void checkRentalPeriods() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Query the database to retrieve rental records that have ended
        List<OrderItem> expiredOrderItems = orderItemRepository.findByRentalEndDateLessThanEqual(currentDateTime);
        System.out.println(expiredOrderItems);
        // Iterate through the expired order items
        for (OrderItem orderItem : expiredOrderItems) {
            if (orderItem.getStatus() == "DELIVERED") {
                double securityDeposit = orderItem.getSecurityDeposit();
                reminderService.sendDelayChargeEmail(orderItem, securityDeposit);
                System.out.println("email sent");
            }
        }
    }
}

