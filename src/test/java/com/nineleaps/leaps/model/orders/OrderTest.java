package com.nineleaps.leaps.model.orders;
import com.nineleaps.leaps.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("Order Tests")
class OrderTest {

    @Test
    @DisplayName("Test setting and getting orderCreatedAt")
    void testOrderCreatedAt() {
        // Arrange
        Order order = new Order();
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        order.setOrderCreatedAt(createdAt);

        // Assert
        assertEquals(createdAt, order.getOrderCreatedAt());
    }

    @Test
    @DisplayName("Test setting and getting orderUpdatedAt")
    void testOrderUpdatedAt() {
        // Arrange
        Order order = new Order();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        order.setOrderUpdatedAt(updatedAt);

        // Assert
        assertEquals(updatedAt, order.getOrderUpdatedAt());
    }

    // Other tests...

    @Test
    @DisplayName("Test setting audit columns for create")
    void testSetAuditColumnsCreate() {
        // Arrange
        Order order = new Order();
        User user = new User(); // Create a User instance if needed

        // Act
        order.setAuditColumnsCreate(user);

        // Assert
        assertEquals(user.getCreatedAt(), order.getOrderCreatedAt());
        assertEquals(user.getCreatedBy(), order.getOrderCreatedBy());
    }

    @Test
    @DisplayName("Test setting audit columns for update")
    void testSetAuditColumnsUpdate() {
        // Arrange
        Order order = new Order();
        Long userId = 1L;

        // Act
        order.setAuditColumnsUpdate(userId);

        // Assert
        assertEquals(userId, order.getOrderUpdatedBy());
    }



}
