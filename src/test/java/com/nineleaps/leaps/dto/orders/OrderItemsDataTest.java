package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.dto.orders.OrderItemsData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemsDataTest {

    @Test
    void testTotalOrders() {
        // Arrange
        OrderItemsData orderItemsData = new OrderItemsData();
        int initialTotalOrders = 5;

        // Act
        orderItemsData.incrementTotalOrders(initialTotalOrders);

        // Assert
        assertEquals(initialTotalOrders, orderItemsData.getTotalOrders());
    }
}
