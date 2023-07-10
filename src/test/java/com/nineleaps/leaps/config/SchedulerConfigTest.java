package com.nineleaps.leaps.config;

import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class SchedulerConfigTest {

    @Mock
    private OrderServiceImpl reminderService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private SchedulerConfig schedulerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendReminderEmails() {
        // Arrange
        // Mock any necessary data or behavior

        // Act
        schedulerConfig.sendReminderEmails();

        // Assert
        // Add appropriate assertions based on the expected behavior
        // For example, verify that the reminderService method was called
        verify(reminderService).getRentalPeriods();
        // Add additional assertions if necessary
    }

//    @Test
//    void checkRentalPeriods() {
//        // Arrange
//        LocalDateTime currentDateTime = LocalDateTime.now();
//
//        // Create a mock OrderItem
//        OrderItem orderItem = new OrderItem();
//        orderItem.setStatus("DELIVERED");
//        orderItem.setSecurityDeposit(100.0);
//
//        // Create a list of expired order items with the mock OrderItem
//        List<OrderItem> expiredOrderItems = Collections.singletonList(orderItem);
//
//        // Mock the repository method to return the list of expired order items
//        when(orderItemRepository.findByRentalEndDateLessThanEqual(currentDateTime))
//                .thenReturn(expiredOrderItems);
//
//        // Act
//        schedulerConfig.checkRentalPeriods();
//
//        // Assert
//        // Verify that the reminderService method was called with the mock OrderItem
//        verify(reminderService).sendDelayChargeEmail(orderItem, 100.0);
//    }

}
