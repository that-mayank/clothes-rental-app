package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderDtoTest {

    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        order = new Order();
        // Set up the properties of the Order for testing

        // Set other properties of the Order
        order.setId(1L);  // Set the order ID
        order.setCreateDate(LocalDateTime.now());  // Set the created date
        order.setTotalPrice(99.99);  // Set the total price

        // Create a list of OrderItems for the Order
        List<OrderItem> orderItems = new ArrayList<>();

        // Create and set properties for the first OrderItem
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);  // Set the order item ID
        orderItem1.setProduct(new Product());  // Set the associated Product object
        orderItem1.getProduct().setId(1L);  // Set the product ID
        orderItem1.getProduct().setName("Sample Product");  // Set the product name
        orderItem1.setQuantity(1);  // Set the quantity
        orderItem1.setPrice(9.99);  // Set the price
        orderItem1.setCreatedDate(LocalDateTime.now());  // Set the created date
        // Set other properties of the first OrderItem as needed

        // Add the first OrderItem to the list
        orderItems.add(orderItem1);

        // Create and set properties for more OrderItems as needed
        // ...

        // Set the OrderItems list for the Order
        order.setOrderItems(orderItems);

        orderDto = new OrderDto(order);
    }

    @Test
    void getId() {
        assertEquals(order.getId(), orderDto.getId());
    }

    @Test
    void getCreatedDate() {
        assertEquals(order.getCreateDate(), orderDto.getCreatedDate());
    }

    @Test
    void getTotalPrice() {
        assertEquals(order.getTotalPrice(), orderDto.getTotalPrice());
    }


    @Test
    void getOrderItems() {
        assertEquals(order.getOrderItems().size(), orderDto.getOrderItems().size());

        // Retrieve the first OrderItem from the original Order
        OrderItem originalOrderItem = order.getOrderItems().get(0);

        // Retrieve the first OrderItemDto from the converted OrderDto
        OrderItemDto convertedOrderItemDto = orderDto.getOrderItems().get(0);

        // Compare the properties of the first OrderItem and OrderItemDto
        assertEquals(originalOrderItem.getId(), convertedOrderItemDto.getId());
        assertEquals(originalOrderItem.getProduct().getId(), convertedOrderItemDto.getProductId());
        // Adjust the assertions according to your implementation

        // ...
    }


    @Test
    void setId() {
        Long newId = 2L;
        orderDto.setId(newId);
        assertEquals(newId, orderDto.getId());
    }

    @Test
    void setCreatedDate() {
        LocalDateTime newCreatedDate = LocalDateTime.now().minusDays(1);
        orderDto.setCreatedDate(newCreatedDate);
        assertEquals(newCreatedDate, orderDto.getCreatedDate());
    }

    @Test
    void setTotalPrice() {
        double newTotalPrice = 199.99;
        orderDto.setTotalPrice(newTotalPrice);
        assertEquals(newTotalPrice, orderDto.getTotalPrice());
    }

    @Test
    void setOrderItems() {
        List<OrderItemDto> newOrderItems = new ArrayList<>();
        // Add new OrderItems to the list

        // Create and set properties for the first OrderItem
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);  // Set the order item ID
        orderItem1.setProduct(new Product());  // Set the associated Product object
        orderItem1.getProduct().setId(1L);  // Set the product ID
        orderItem1.getProduct().setName("Sample Product");  // Set the product name
        orderItem1.setQuantity(1);  // Set the quantity
        orderItem1.setPrice(9.99);  // Set the price
        // Set other properties of the first OrderItem as needed

        OrderItemDto orderItemDto1 = new OrderItemDto(orderItem1); // Create OrderItemDto with the corresponding OrderItem
        newOrderItems.add(orderItemDto1);
        // Add more OrderItems as needed

        orderDto.setOrderItems(newOrderItems);
        assertEquals(newOrderItems.size(), orderDto.getOrderItems().size());
        // You can add further assertions to verify the correctness of the OrderItems update
        // For example, compare the properties of the first OrderItemDto in the list
        assertEquals(newOrderItems.get(0).getId(), orderDto.getOrderItems().get(0).getId());
        assertEquals(newOrderItems.get(0).getName(), orderDto.getOrderItems().get(0).getName());
        // ...
    }

}
