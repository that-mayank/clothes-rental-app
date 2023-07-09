package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderReceivedDtoTest {

    private OrderItem orderItem;
    private OrderReceivedDto orderReceivedDto;



    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
        // Set up the properties of the OrderItem for testing

        // Set the associated Product object for the OrderItem
        Product product = new Product();
        product.setName("Sample Product");
        product.setId(1L);  // Set the product ID
        // Set other properties of the Product object as needed
        orderItem.setProduct(product);

        // Set the order associated with the OrderItem
        Order order = new Order();
        order.setId(1L);  // Set the order ID
        // Set other properties of the Order object as needed
        orderItem.setOrder(order);

        // Set the user associated with the Order
        User user = new User();
        user.setId(1L);  // Set the user ID
        user.setFirstName("John");  // Set the user's first name
        user.setLastName("Doe");  // Set the user's last name
        user.setEmail("john.doe@example.com");  // Set the user's email
        user.setPhoneNumber("1234567890");  // Set the user's phone number
        order.setUser(user);

        // Set other properties of the OrderItem
        orderItem.setId(1L);  // Set the order item ID
        orderItem.setQuantity(1);  // Set the quantity
        orderItem.setPrice(9.99);  // Set the price
        orderItem.setCreatedDate(LocalDateTime.now());  // Set the created date
        // Set other properties of the OrderItem as needed

        // Set the image URL
        orderItem.setImageUrl(NGROK + "/api/v1/file/view/images/image.jpg");

        // Set rental start date and end date
        LocalDateTime rentalStartDate = LocalDateTime.of(2023, 7, 1, 0, 0);
        LocalDateTime rentalEndDate = LocalDateTime.of(2023, 7, 5, 0, 0);
        orderItem.setRentalStartDate(rentalStartDate);
        orderItem.setRentalEndDate(rentalEndDate);

        orderReceivedDto = new OrderReceivedDto(orderItem);
    }


        @Test
    void getName() {
        assertEquals(orderItem.getProduct().getName(), orderReceivedDto.getName());
    }

    @Test
    void getQuantity() {
        assertEquals(orderItem.getQuantity(), orderReceivedDto.getQuantity());
    }

    @Test
    void getRentalStartDate() {
        assertEquals(orderItem.getRentalStartDate(), orderReceivedDto.getRentalStartDate());
    }

    @Test
    void getRentalEndDate() {
        assertEquals(orderItem.getRentalEndDate(), orderReceivedDto.getRentalEndDate());
    }

    @Test
    void getRentalCost() {
        Duration rentalDuration = Duration.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate());
        double expectedRentalCost = Math.round(orderItem.getPrice() * orderItem.getQuantity() * rentalDuration.toDays());
        assertEquals(expectedRentalCost, orderReceivedDto.getRentalCost());
    }


    @Test
    void getImageUrl() {
        assertEquals(NGROK+"/api/v1/file/view/images/image.jpg", orderReceivedDto.getImageUrl());
    }

    @Test
    void getProductId() {
        assertEquals(orderItem.getProduct().getId(), orderReceivedDto.getProductId());
    }

    @Test
    void getBorrowerId() {
        assertEquals(orderItem.getOrder().getUser().getId(), orderReceivedDto.getBorrowerId());
    }

    @Test
    void getBorrowerName() {
        assertEquals("John Doe", orderReceivedDto.getBorrowerName());
    }

    @Test
    void getBorrowerEmail() {
        assertEquals(orderItem.getOrder().getUser().getEmail(), orderReceivedDto.getBorrowerEmail());
    }

    @Test
    void getBorrowerPhoneNumber() {
        assertEquals(orderItem.getOrder().getUser().getPhoneNumber(), orderReceivedDto.getBorrowerPhoneNumber());
    }

    @Test
    void setName() {
        String newName = "New Product Name";
        orderReceivedDto.setName(newName);
        assertEquals(newName, orderReceivedDto.getName());
    }

    @Test
    void setQuantity() {
        int newQuantity = 10;
        orderReceivedDto.setQuantity(newQuantity);
        assertEquals(newQuantity, orderReceivedDto.getQuantity());
    }

    @Test
    void setRentalStartDate() {
        LocalDateTime newStartDate = LocalDateTime.now().minusDays(1);
        orderReceivedDto.setRentalStartDate(newStartDate);
        assertEquals(newStartDate, orderReceivedDto.getRentalStartDate());
    }

    @Test
    void setRentalEndDate() {
        LocalDateTime newEndDate = LocalDateTime.now().plusDays(14);
        orderReceivedDto.setRentalEndDate(newEndDate);
        assertEquals(newEndDate, orderReceivedDto.getRentalEndDate());
    }

    @Test
    void setRentalCost() {
        double newRentalCost = 50.0;
        orderReceivedDto.setRentalCost(newRentalCost);
        assertEquals(newRentalCost, orderReceivedDto.getRentalCost());
    }

    @Test
    void setImageUrl() {
        String newImageUrl = "/api/images/new-image.jpg";
        orderReceivedDto.setImageUrl(newImageUrl);
        assertEquals(newImageUrl, orderReceivedDto.getImageUrl());
    }

    @Test
    void setProductId() {
        Long newProductId = 2L;
        orderReceivedDto.setProductId(newProductId);
        assertEquals(newProductId, orderReceivedDto.getProductId());
    }

    @Test
    void setBorrowerId() {
        Long newBorrowerId = 2L;
        orderReceivedDto.setBorrowerId(newBorrowerId);
        assertEquals(newBorrowerId, orderReceivedDto.getBorrowerId());
    }

    @Test
    void setBorrowerName() {
        String newBorrowerName = "Jane Doe";
        orderReceivedDto.setBorrowerName(newBorrowerName);
        assertEquals(newBorrowerName, orderReceivedDto.getBorrowerName());
    }

    @Test
    void setBorrowerEmail() {
        String newBorrowerEmail = "jane.doe@example.com";
        orderReceivedDto.setBorrowerEmail(newBorrowerEmail);
        assertEquals(newBorrowerEmail, orderReceivedDto.getBorrowerEmail());
    }

    @Test
    void setBorrowerPhoneNumber() {
        String newBorrowerPhoneNumber = "9876543210";
        orderReceivedDto.setBorrowerPhoneNumber(newBorrowerPhoneNumber);
        assertEquals(newBorrowerPhoneNumber, orderReceivedDto.getBorrowerPhoneNumber());
    }
}
