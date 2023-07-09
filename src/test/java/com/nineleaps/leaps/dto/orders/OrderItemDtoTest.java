package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.orders.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemDtoTest {

    private OrderItem orderItem;
    private OrderItemDto orderItemDto;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
        // Set up the properties of the OrderItem for testing

        // Set the associated Product object for the OrderItem
        Product product = new Product();
        product.setId(1L);  // Set the product ID
        product.setName("Sample Product");
        // Set other properties of the Product object as needed
        orderItem.setProduct(product);

        // Set other properties of the OrderItem
        orderItem.setId(1L);  // Set the order item ID
        orderItem.setQuantity(1);  // Set the quantity
        orderItem.setPrice(9.99);  // Set the price
        orderItem.setCreatedDate(LocalDateTime.now());  // Set the created date
        // Set other properties of the OrderItem as needed

        // Set rental start date and end date
        LocalDateTime rentalStartDate = LocalDateTime.of(2023, 7, 1, 0, 0);
        LocalDateTime rentalEndDate = LocalDateTime.of(2023, 7, 5, 0, 0);
        orderItem.setRentalStartDate(rentalStartDate);
        orderItem.setRentalEndDate(rentalEndDate);

        // Set the image URL
        orderItem.setImageUrl("/api/v1/file/view/test_image.png");

        // Set the status
        orderItem.setStatus("Pending");

        orderItemDto = new OrderItemDto(orderItem);
    }

    @Test
    void getId() {
        assertEquals(orderItem.getId(), orderItemDto.getId());
    }

    @Test
    void getProductId() {
        assertEquals(orderItem.getProduct().getId(), orderItemDto.getProductId());
    }

    @Test
    void getName() {
        assertEquals(orderItem.getProduct().getName(), orderItemDto.getName());
    }

    @Test
    void getQuantity() {
        assertEquals(orderItem.getQuantity(), orderItemDto.getQuantity());
    }

    @Test
    void getPricePerDay() {
        assertEquals(orderItem.getPrice(), orderItemDto.getPricePerDay());
    }

    @Test
    void getTotalPrice() {
        double expectedTotalPrice = orderItem.getPrice() * orderItem.getQuantity();
        assertEquals(expectedTotalPrice, orderItemDto.getTotalPrice());
    }

    @Test
    void getCreatedDate() {
        assertEquals(orderItem.getCreatedDate(), orderItemDto.getCreatedDate());
    }

    @Test
    void getRentalStartDate() {
        assertEquals(orderItem.getRentalStartDate(), orderItemDto.getRentalStartDate());
    }

    @Test
    void getRentalEndDate() {
        assertEquals(orderItem.getRentalEndDate(), orderItemDto.getRentalEndDate());
    }

    @Test
    void getImageUrl() {
        assertEquals(NGROK + "/api/v1/file/view/test_image.png", orderItemDto.getImageUrl());
    }

    @Test
    void getStatus() {
        assertEquals(orderItem.getStatus(), orderItemDto.getStatus());
    }

    @Test
    void setId() {
        Long newId = 2L;
        orderItemDto.setId(newId);
        assertEquals(newId, orderItemDto.getId());
    }

    @Test
    void setProductId() {
        Long newProductId = 2L;
        orderItemDto.setProductId(newProductId);
        assertEquals(newProductId, orderItemDto.getProductId());
    }

    @Test
    void setName() {
        String newName = "New Product Name";
        orderItemDto.setName(newName);
        assertEquals(newName, orderItemDto.getName());
    }

    @Test
    void setQuantity() {
        int newQuantity = 10;
        orderItemDto.setQuantity(newQuantity);
        assertEquals(newQuantity, orderItemDto.getQuantity());
    }

    @Test
    void setPricePerDay() {
        double newPricePerDay = 15.99;
        orderItemDto.setPricePerDay(newPricePerDay);
        assertEquals(newPricePerDay, orderItemDto.getPricePerDay());
    }

    @Test
    void setTotalPrice() {
        double newTotalPrice = 99.99;
        orderItemDto.setTotalPrice(newTotalPrice);
        assertEquals(newTotalPrice, orderItemDto.getTotalPrice());
    }

    @Test
    void setCreatedDate() {
        LocalDateTime newCreatedDate = LocalDateTime.now().minusDays(1);
        orderItemDto.setCreatedDate(newCreatedDate);
        assertEquals(newCreatedDate, orderItemDto.getCreatedDate());
    }

    @Test
    void setRentalStartDate() {
        LocalDateTime newStartDate = LocalDateTime.now().minusDays(1);
        orderItemDto.setRentalStartDate(newStartDate);
        assertEquals(newStartDate, orderItemDto.getRentalStartDate());
    }

    @Test
    void setRentalEndDate() {
        LocalDateTime newEndDate = LocalDateTime.now().plusDays(14);
        orderItemDto.setRentalEndDate(newEndDate);
        assertEquals(newEndDate, orderItemDto.getRentalEndDate());
    }

    @Test
    void setImageUrl() {
        String newImageUrl = "/api/images/new-image.jpg";
        orderItemDto.setImageUrl(newImageUrl);
        assertEquals(newImageUrl, orderItemDto.getImageUrl());
    }

    @Test
    void setStatus() {
        String newStatus = "Completed";
        orderItemDto.setStatus(newStatus);
        assertEquals(newStatus, orderItemDto.getStatus());
    }
}
