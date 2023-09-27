package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderReceivedDtoTest {

    @Test
    void orderReceivedDtoCreation() {
        // Create a sample order item
        Product product = new Product();
        product.setId(1L);
        product.setName("Sample Product");

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
        orderItem.setPrice(10.0);
        orderItem.setRentalStartDate(LocalDateTime.now());
        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(5));
        orderItem.setImageUrl("/api/example-image.jpg");
        orderItem.setStatus("ACTIVE");
        orderItem.setOrder(order);

        OrderReceivedDto orderReceivedDto = new OrderReceivedDto(orderItem);

        assertEquals("Sample Product", orderReceivedDto.getName());
        assertEquals(2, orderReceivedDto.getQuantity());
        assertEquals(orderItem.getRentalStartDate(), orderReceivedDto.getRentalStartDate());
        assertEquals(orderItem.getRentalEndDate(), orderReceivedDto.getRentalEndDate());

        // Calculate expected rental cost
        long rentalDays = ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate());
        double expectedRentalCost = Math.round(orderItem.getPrice() * orderItem.getQuantity() * rentalDays);
        assertEquals(expectedRentalCost, orderReceivedDto.getRentalCost());

        assertEquals(NGROK+"/api/example-image.jpg", orderReceivedDto.getImageUrl());
        assertEquals(1L, orderReceivedDto.getProductId());
        assertEquals(1L, orderReceivedDto.getBorrowerId());
        assertEquals("John Doe", orderReceivedDto.getBorrowerName());
        assertEquals("john.doe@example.com", orderReceivedDto.getBorrowerEmail());
        assertEquals("1234567890", orderReceivedDto.getBorrowerPhoneNumber());
    }

    @Test
    void orderReceivedDtoCreationNullDates() {
        // Create a sample order item with null rental dates
        Product product = new Product();
        product.setName("Sample Product");

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
        orderItem.setPrice(10.0);
        orderItem.setImageUrl("/api/example-image.jpg");
        orderItem.setStatus("ACTIVE");
        orderItem.setOrder(order);

        // Set rental dates to null
        orderItem.setRentalStartDate(null);
        orderItem.setRentalEndDate(null);

        OrderReceivedDto orderReceivedDto = new OrderReceivedDto(orderItem);

        // Expected rental cost is 0.0 when rental dates are null
        assertEquals(0.0, orderReceivedDto.getRentalCost());
        assertNull(orderReceivedDto.getRentalStartDate());
        assertNull(orderReceivedDto.getRentalEndDate());
    }

    @Test
    void orderReceivedDtoCreationNoApiInImageUrl() {
        // Create a sample order item with no "/api" in the image URL
        Product product = new Product();
        product.setName("Sample Product");

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
        orderItem.setPrice(10.0);
        orderItem.setRentalStartDate(LocalDateTime.now());
        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(5));
        orderItem.setImageUrl("/api/example-image.jpg");  // No "/api" in the image URL
        orderItem.setStatus("ACTIVE");
        orderItem.setOrder(order);

        OrderReceivedDto orderReceivedDto = new OrderReceivedDto(orderItem);

        // The image URL should start with "/api"
        assertEquals(NGROK+"/api/example-image.jpg", orderReceivedDto.getImageUrl());
    }
}
