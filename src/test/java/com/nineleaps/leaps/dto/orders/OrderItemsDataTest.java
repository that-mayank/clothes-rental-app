package com.nineleaps.leaps.dto.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemsDataTest {

    private OrderItemsData orderItemsData;

    @BeforeEach
    void setUp() {
        orderItemsData = new OrderItemsData();
    }

    @Test
    void getTotalOrders() {
        // Set the totalOrders using the setter
        orderItemsData.setTotalOrders(10);

        // Get the totalOrders using the getter and assert
        int totalOrders = orderItemsData.getTotalOrders();
        assertEquals(10, totalOrders);
    }

    @Test
    void setTotalOrders() {
        // Set the totalOrders using the setter
        orderItemsData.setTotalOrders(5);

        // Get the totalOrders using the getter and assert
        int totalOrders = orderItemsData.getTotalOrders();
        assertEquals(5, totalOrders);
    }
}
