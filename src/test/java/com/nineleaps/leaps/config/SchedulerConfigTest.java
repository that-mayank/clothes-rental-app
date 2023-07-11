package com.nineleaps.leaps.config;

import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.service.implementation.EmailServiceImpl;
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

    @Mock
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendReminderEmails() {
        schedulerConfig.sendReminderEmails();
        verify(reminderService).getRentalPeriods();

    }
    @Test
        void checkRentalPeriods() {
            // Arrange
            LocalDateTime currentDateTime = LocalDateTime.now();

            // Create a mock OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setStatus("DELIVERED");
            orderItem.setSecurityDeposit(100.0);
            orderItem.setName("t-shirt");

            // Create a list of expired order items with the mock OrderItem
            List<OrderItem> expiredOrderItems = Collections.singletonList(orderItem);

            // Create a mock OrderItemRepository
            OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
            when(orderItemRepository.findByRentalEndDateLessThanEqual(currentDateTime))
                    .thenReturn(expiredOrderItems);

            // Create an instance of SchedulerConfig with the mock dependencies
            SchedulerConfig schedulerConfig = new SchedulerConfig(reminderService,orderItemRepository);

            // Act
            schedulerConfig.checkRentalPeriods();

        }
    }




