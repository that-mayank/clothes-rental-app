package com.nineleaps.leaps.model.orders;

import com.nineleaps.leaps.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class OrderItemTest {

    private Order order;
    private Product product;
    private OrderItem orderItem;
    private Validator validator;

    @BeforeEach
    void setUp() {
        order = mock(Order.class);
        product = mock(Product.class);
        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setName("Test Item");
        orderItem.setQuantity(2);
        orderItem.setPrice(19.99);
        orderItem.setCreatedDate(LocalDateTime.now());
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setRentalStartDate(LocalDateTime.now());
        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(7));
        orderItem.setImageUrl("https://example.com/image.jpg");
        orderItem.setStatus("Pending");
        orderItem.setSecurityDeposit(100.0);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testConstructor() {
        assertNotNull(orderItem.getId());
        assertEquals("Test Item", orderItem.getName());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(19.99, orderItem.getPrice());
        assertNotNull(orderItem.getCreatedDate());
        assertEquals(order, orderItem.getOrder());
        assertEquals(product, orderItem.getProduct());
        assertNotNull(orderItem.getRentalStartDate());
        assertNotNull(orderItem.getRentalEndDate());
        assertEquals("https://example.com/image.jpg", orderItem.getImageUrl());
        assertEquals("Pending", orderItem.getStatus());
        assertEquals(100.0, orderItem.getSecurityDeposit());
    }

    @Test
    void testValidation() {
        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertTrue(violations.isEmpty());
    }


}
