package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderItemDtoTest {

    private OrderItemDto orderItemDto;

    @BeforeEach
    void setUp() {
        // Create a mock OrderItem object for testing
        OrderItem orderItem = Mockito.mock(OrderItem.class);
        Product product = Mockito.mock(Product.class);
        ProductUrl productUrl = Mockito.mock(ProductUrl.class);

        when(orderItem.getId()).thenReturn(1L);
        when(orderItem.getProduct()).thenReturn(product);
        when(orderItem.getQuantity()).thenReturn(2);
        when(orderItem.getPrice()).thenReturn(50.0);
        when(orderItem.getCreatedDate()).thenReturn(LocalDateTime.now());
        when(orderItem.getRentalStartDate()).thenReturn(LocalDateTime.now());
        when(orderItem.getRentalEndDate()).thenReturn(LocalDateTime.now());
        when(orderItem.getImageUrl()).thenReturn("/xyz.jpeg");
        when(orderItem.getStatus()).thenReturn("Pending");

        when(product.getId()).thenReturn(101L);
        when(product.getName()).thenReturn("Product1");

        when(productUrl.getUrl()).thenReturn("/xyz.jpeg");

        when(orderItem.getPrice()).thenReturn(50.0);

        orderItemDto = new OrderItemDto(orderItem);
    }

    @Test
    void getId() {
        assertEquals(1L, orderItemDto.getId());
    }

    @Test
    void getProductId() {
        assertEquals(101L, orderItemDto.getProductId());
    }

    @Test
    void getName() {
        assertEquals("Product1", orderItemDto.getName());
    }

    @Test
    void getQuantity() {
        assertEquals(2, orderItemDto.getQuantity());
    }

    @Test
    void getPricePerDay() {
        assertEquals(50.0, orderItemDto.getPricePerDay());
    }

    @Test
    void getTotalPrice() {
        assertEquals(100.0, orderItemDto.getTotalPrice());
    }

    @Test
    void getCreatedDate() {
        assertEquals(LocalDateTime.now().toLocalDate(), orderItemDto.getCreatedDate().toLocalDate());
    }

    @Test
    void getRentalStartDate() {
        assertEquals(LocalDateTime.now().toLocalDate(), orderItemDto.getCreatedDate().toLocalDate());
    }

    @Test
    void getRentalEndDate() {
        assertEquals(LocalDateTime.now().toLocalDate(), orderItemDto.getCreatedDate().toLocalDate());
    }

    @Test
    void getImageUrl() {
        assertEquals(NGROK + "/xyz.jpeg", orderItemDto.getImageUrl());
    }

    @Test
    void getStatus() {
        assertEquals("Pending", orderItemDto.getStatus());
    }

    @Test
    void setId() {
        orderItemDto.setId(2L);
        assertEquals(2L, orderItemDto.getId());
    }

    @Test
    void setProductId() {
        orderItemDto.setProductId(102L);
        assertEquals(102L, orderItemDto.getProductId());
    }

    @Test
    void setName() {
        orderItemDto.setName("Product2");
        assertEquals("Product2", orderItemDto.getName());
    }

    @Test
    void setQuantity() {
        orderItemDto.setQuantity(3);
        assertEquals(3, orderItemDto.getQuantity());
    }

    @Test
    void setPricePerDay() {
        orderItemDto.setPricePerDay(60.0);
        assertEquals(60.0, orderItemDto.getPricePerDay());
    }

    @Test
    void setTotalPrice() {
        orderItemDto.setTotalPrice(120.0);
        assertEquals(120.0, orderItemDto.getTotalPrice());
    }

    @Test
    void setCreatedDate() {
        orderItemDto.setCreatedDate(LocalDateTime.now());
        assertEquals(LocalDateTime.now().toLocalDate(), orderItemDto.getCreatedDate().toLocalDate());
    }

    @Test
    void setRentalStartDate() {
        orderItemDto.setRentalStartDate(LocalDateTime.now());
        assertEquals(LocalDateTime.now().toLocalDate(), orderItemDto.getRentalStartDate().toLocalDate());
    }

    @Test
    void setRentalEndDate() {
        orderItemDto.setRentalEndDate(LocalDateTime.now());
        assertEquals(LocalDateTime.now().toLocalDate(), orderItemDto.getRentalEndDate().toLocalDate());
    }

    @Test
    void setImageUrl() {
        orderItemDto.setImageUrl("/abc.jpeg");
        assertEquals("/abc.jpeg", orderItemDto.getImageUrl());
    }

    @Test
    void setStatus() {
        orderItemDto.setStatus("Delivered");
        assertEquals("Delivered", orderItemDto.getStatus());
    }
}
