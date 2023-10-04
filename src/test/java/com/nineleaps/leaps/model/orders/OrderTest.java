package com.nineleaps.leaps.model.orders;
import com.nineleaps.leaps.model.User;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTest {

    @Test
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
    void testOrderUpdatedAt() {
        // Arrange
        Order order = new Order();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        order.setOrderUpdatedAt(updatedAt);

        // Assert
        assertEquals(updatedAt, order.getOrderUpdatedAt());
    }

    @Test
    void testOrderCreatedBy() {
        // Arrange
        Order order = new Order();
        Long createdBy = 1L;

        // Act
        order.setOrderCreatedBy(createdBy);

        // Assert
        assertEquals(createdBy, order.getOrderCreatedBy());
    }

    @Test
    void testOrderUpdatedBy() {
        // Arrange
        Order order = new Order();
        Long updatedBy = 2L;

        // Act
        order.setOrderUpdatedBy(updatedBy);

        // Assert
        assertEquals(updatedBy, order.getOrderUpdatedBy());
    }

    // Additional test cases for other properties

    @Test
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
    void testSetAuditColumnsUpdate() {
        // Arrange
        Order order = new Order();
        Long userId = 1L;

        // Act
        order.setAuditColumnsUpdate(userId);

        // Assert
        assertEquals(userId, order.getOrderUpdatedBy());
    }

    // Add more test cases as needed

}
