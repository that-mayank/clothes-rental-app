package com.nineleaps.leaps.model.orders;

import com.nineleaps.leaps.model.orders.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("OrderItem Tests")
class OrderItemTest {

    @Test
    @DisplayName("Test setting and getting ownerId")
    void testOwnerId() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        Long ownerId = 123L;

        // Act
        orderItem.setOwnerId(ownerId);

        // Assert
        assertEquals(ownerId, orderItem.getOwnerId());
    }


    @Test
    @DisplayName("Test setting and getting quantity")
    void testQuantity() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        int quantity = 5;

        // Act
        orderItem.setQuantity(quantity);

        // Assert
        assertEquals(quantity, orderItem.getQuantity());
    }

    @Test
    @DisplayName("Test setting and getting createdDate")
    void testCreatedDate() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        LocalDateTime createdDate = LocalDateTime.now();

        // Act
        orderItem.setCreatedDate(createdDate);

        // Assert
        assertEquals(createdDate, orderItem.getCreatedDate());
    }



}

