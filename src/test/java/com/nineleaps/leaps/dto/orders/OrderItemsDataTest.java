package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.dto.orders.OrderItemsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("OrderItemsData Tests")
class OrderItemsDataTest {

    @Test
    @DisplayName("test total orders")
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
