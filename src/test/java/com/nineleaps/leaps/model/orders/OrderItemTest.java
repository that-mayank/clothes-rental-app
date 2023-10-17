package com.nineleaps.leaps.model.orders;

import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemTest {

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
    }

    @Test
    void getId() {
        orderItem.setId(1L);
        assertEquals(1L, orderItem.getId());
    }

    @Test
    void getName() {
        orderItem.setName("Product A");
        assertEquals("Product A", orderItem.getName());
    }

    @Test
    void getQuantity() {
        orderItem.setQuantity(3);
        assertEquals(3, orderItem.getQuantity());
    }

    @Test
    void getPrice() {
        orderItem.setPrice(19.99);
        assertEquals(19.99, orderItem.getPrice(), 0.001);
    }

    @Test
    void getCreatedDate() {
        LocalDateTime createdDate = LocalDateTime.now();
        orderItem.setCreatedDate(createdDate);
        assertEquals(createdDate, orderItem.getCreatedDate());
    }

    @Test
    void getOrder() {
        Order order = new Order();
        orderItem.setOrder(order);
        assertEquals(order, orderItem.getOrder());
    }

    @Test
    void getProduct() {
        Product product = new Product();
        orderItem.setProduct(product);
        assertEquals(product, orderItem.getProduct());
    }

    @Test
    void getRentalStartDate() {
        LocalDateTime rentalStartDate = LocalDateTime.now();
        orderItem.setRentalStartDate(rentalStartDate);
        assertEquals(rentalStartDate, orderItem.getRentalStartDate());
    }

    @Test
    void getRentalEndDate() {
        LocalDateTime rentalEndDate = LocalDateTime.now().plusDays(7);
        orderItem.setRentalEndDate(rentalEndDate);
        assertEquals(rentalEndDate, orderItem.getRentalEndDate());
    }

    @Test
    void getImageUrl() {
        orderItem.setImageUrl("image_url.jpg");
        assertEquals("image_url.jpg", orderItem.getImageUrl());
    }

    @Test
    void getStatus() {
        orderItem.setStatus("Pending");
        assertEquals("Pending", orderItem.getStatus());
    }

    @Test
    void getSecurityDeposit() {
        orderItem.setSecurityDeposit(50.0);
        assertEquals(50.0, orderItem.getSecurityDeposit(), 0.001);
    }

    @Test
    void getOwnerId() {
        orderItem.setOwnerId(123L);
        assertEquals(123L, orderItem.getOwnerId());
    }

    @Test
    void setId() {
        orderItem.setId(2L);
        assertEquals(2L, orderItem.getId());
    }

    @Test
    void setName() {
        orderItem.setName("Product B");
        assertEquals("Product B", orderItem.getName());
    }

    @Test
    void setQuantity() {
        orderItem.setQuantity(5);
        assertEquals(5, orderItem.getQuantity());
    }

    @Test
    void setPrice() {
        orderItem.setPrice(29.99);
        assertEquals(29.99, orderItem.getPrice(), 0.001);
    }

    @Test
    void setCreatedDate() {
        LocalDateTime createdDate = LocalDateTime.now();
        orderItem.setCreatedDate(createdDate);
        assertEquals(createdDate, orderItem.getCreatedDate());
    }

    @Test
    void setOrder() {
        Order order = new Order();
        orderItem.setOrder(order);
        assertEquals(order, orderItem.getOrder());
    }

    @Test
    void setProduct() {
        Product product = new Product();
        orderItem.setProduct(product);
        assertEquals(product, orderItem.getProduct());
    }

    @Test
    void setRentalStartDate() {
        LocalDateTime rentalStartDate = LocalDateTime.now();
        orderItem.setRentalStartDate(rentalStartDate);
        assertEquals(rentalStartDate, orderItem.getRentalStartDate());
    }

    @Test
    void setRentalEndDate() {
        LocalDateTime rentalEndDate = LocalDateTime.now().plusDays(7);
        orderItem.setRentalEndDate(rentalEndDate);
        assertEquals(rentalEndDate, orderItem.getRentalEndDate());
    }

    @Test
    void setImageUrl() {
        orderItem.setImageUrl("new_image.jpg");
        assertEquals("new_image.jpg", orderItem.getImageUrl());
    }

    @Test
    void setStatus() {
        orderItem.setStatus("Shipped");
        assertEquals("Shipped", orderItem.getStatus());
    }

    @Test
    void setSecurityDeposit() {
        orderItem.setSecurityDeposit(60.0);
        assertEquals(60.0, orderItem.getSecurityDeposit(), 0.001);
    }

    @Test
    void setOwnerId() {
        orderItem.setOwnerId(456L);
        assertEquals(456L, orderItem.getOwnerId());
    }
}
