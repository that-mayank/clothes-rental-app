package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderReceivedDtoTest {

    private OrderReceivedDto orderReceivedDto;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");

        Order order = new Order();
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("1234567890");

        order.setUser(user);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(100.0);
        orderItem.setRentalStartDate(LocalDateTime.now());
        orderItem.setRentalEndDate(LocalDateTime.now());
        orderItem.setImageUrl("/api/v1/image.jpg");
        orderItem.setStatus("ORDERED");
        orderItem.setOrder(order);

        orderReceivedDto = new OrderReceivedDto(orderItem);
        orderReceivedDto.setRentalCost(200.0);
    }

    @Test
    void getName() {
        assertEquals("Product", orderReceivedDto.getName());
    }

    @Test
    void getQuantity() {
        assertEquals(2, orderReceivedDto.getQuantity());
    }

    @Test
    void getRentalStartDate() {
        assertEquals(LocalDateTime.now().toLocalDate(), orderReceivedDto.getRentalStartDate().toLocalDate());
    }

    @Test
    void getRentalEndDate() {
        assertEquals(LocalDateTime.now().toLocalDate(), orderReceivedDto.getRentalEndDate().toLocalDate());
    }

    @Test
    void getRentalCost() {
        assertEquals(200.0, orderReceivedDto.getRentalCost());
    }

    @Test
    void getImageUrl() {
        assertEquals("/api/v1/image.jpg", orderReceivedDto.getImageUrl());
    }

    @Test
    void getProductId() {
        assertEquals(1L, orderReceivedDto.getProductId());
    }

    @Test
    void getBorrowerId() {
        assertEquals(1L, orderReceivedDto.getBorrowerId());
    }

    @Test
    void getBorrowerName() {
        assertEquals("John Doe", orderReceivedDto.getBorrowerName());
    }

    @Test
    void getBorrowerEmail() {
        assertEquals("john.doe@example.com", orderReceivedDto.getBorrowerEmail());
    }

    @Test
    void getBorrowerPhoneNumber() {
        assertEquals("1234567890", orderReceivedDto.getBorrowerPhoneNumber());
    }

    @Test
    void setName() {
        orderReceivedDto.setName("Mayank");
        assertEquals("Mayank", orderReceivedDto.getName());
    }

    @Test
    void setQuantity() {
        orderReceivedDto.setQuantity(10);
        assertEquals(10, orderReceivedDto.getQuantity());
    }

    @Test
    void setRentalStartDate() {
        orderReceivedDto.setRentalStartDate(LocalDateTime.now().plusDays(10));
        assertEquals(LocalDateTime.now().plusDays(10).toLocalDate(), orderReceivedDto.getRentalStartDate().toLocalDate());
    }

    @Test
    void setRentalEndDate() {
        orderReceivedDto.setRentalEndDate(LocalDateTime.now().plusDays(10));
        assertEquals(LocalDateTime.now().plusDays(10).toLocalDate(), orderReceivedDto.getRentalEndDate().toLocalDate());
    }

    @Test
    void setRentalCost() {
        orderReceivedDto.setRentalCost(400.0);
        assertEquals(400.0, orderReceivedDto.getRentalCost());
    }

    @Test
    void setImageUrl() {
        orderReceivedDto.setImageUrl("/api/v1/image");
        assertEquals("/api/v1/image", orderReceivedDto.getImageUrl());
    }

    @Test
    void setProductId() {
        orderReceivedDto.setProductId(22L);
        assertEquals(22L, orderReceivedDto.getProductId());
    }

    @Test
    void setBorrowerId() {
        orderReceivedDto.setBorrowerId(99L);
        assertEquals(99L, orderReceivedDto.getBorrowerId());
    }

    @Test
    void setBorrowerName() {
        orderReceivedDto.setBorrowerName("mayank");
        assertEquals("mayank", orderReceivedDto.getBorrowerName());
    }

    @Test
    void setBorrowerEmail() {
        orderReceivedDto.setBorrowerEmail("mayank@gmail.com");
        assertEquals("mayank@gmail.com", orderReceivedDto.getBorrowerEmail());
    }

    @Test
    void setBorrowerPhoneNumber() {
        orderReceivedDto.setBorrowerPhoneNumber("9999999999");
        assertEquals("9999999999", orderReceivedDto.getBorrowerPhoneNumber());
    }
}