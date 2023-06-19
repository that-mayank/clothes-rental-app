package com.nineleaps.leaps.config;


import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.service.implementation.OrderServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class SchedulerConfig {


    private final OrderServiceImpl reminderService;
    private final OrderItemRepository orderItemRepository;

    //@Scheduled(cron = "0 0 12 * * ?") // Runs every day at 12 PM 0 0 12 * * ? */10 * * * * *
    public void sendReminderEmails() {
        reminderService.getRentalPeriods();
        log.trace("email sent");


    }

    //@Scheduled(cron = "*/20 * * * * *")
    public void checkRentalPeriods() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Query the database to retrieve rental records that have ended
        List<OrderItem> expiredOrderItems = orderItemRepository.findByRentalEndDateLessThanEqual(currentDateTime);
        // Iterate through the expired order items
        for (OrderItem orderItem : expiredOrderItems) {
            if (orderItem.getStatus().equals("DELIVERED")) {
                double securityDeposit = orderItem.getSecurityDeposit();
                reminderService.sendDelayChargeEmail(orderItem, securityDeposit);
                log.trace("email sent");
            }
        }
    }
}

