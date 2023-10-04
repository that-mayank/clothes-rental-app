package com.nineleaps.leaps.dto.orders;


import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;


class OrderItemDtoTest {

    @Test
    void orderItemDtoCreation() {
        Product product = new Product();
        product.setName("sample");
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(10.0);
        orderItem.setCreatedDate(LocalDateTime.now());
        orderItem.setRentalStartDate(LocalDateTime.now());
        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(5));
        orderItem.setImageUrl("/example-image.jpg");
        orderItem.setStatus("ACTIVE");
        OrderItemDto orderItemDto = new OrderItemDto(orderItem);
        assertEquals(orderItem.getId(), orderItemDto.getId());
        assertEquals(orderItem.getProduct().getId(), orderItemDto.getProductId());
        assertEquals(orderItem.getProduct().getName(),orderItemDto.getName());
        assertEquals(orderItem.getQuantity(),orderItemDto.getQuantity());
        assertEquals(orderItem.getPrice(),orderItemDto.getPricePerDay());
        assertEquals(orderItem.getPrice()*orderItem.getQuantity(),orderItemDto.getTotalPrice());
        assertEquals(orderItem.getCreatedDate(),orderItemDto.getCreatedDate());
        assertEquals(orderItem.getRentalStartDate(),orderItemDto.getRentalStartDate());
        assertEquals(orderItem.getRentalEndDate(),orderItemDto.getRentalEndDate());
        assertEquals(NGROK+orderItem.getImageUrl(),orderItemDto.getImageUrl());
        assertEquals(orderItem.getStatus(),orderItemDto.getStatus());
    }

    // Add more test cases for different scenarios if needed

    @Test
    void testSetters() {
        // Arrange
        OrderItemDto orderItemDto = new OrderItemDto();
        Long productId = 123L;
        int quantity = 5;
        double pricePerDay = 20.0;

        // Act
        orderItemDto.setProductId(productId);
        orderItemDto.setQuantity(quantity);
        orderItemDto.setPricePerDay(pricePerDay);

        // Assert
        assertEquals(productId, orderItemDto.getProductId());
        assertEquals(quantity, orderItemDto.getQuantity());
        assertEquals(pricePerDay, orderItemDto.getPricePerDay());
    }
}
