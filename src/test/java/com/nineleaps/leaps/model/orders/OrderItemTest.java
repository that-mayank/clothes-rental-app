package com.nineleaps.leaps.model.orders;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemTest {

    @Test
    void testOwnerId() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        Long ownerId = 123L;

        // Act
        orderItem.setOwnerId(ownerId);

        // Assert
        assertEquals(ownerId, orderItem.getOwnerId());
    }

    // Additional test cases for other properties

}
