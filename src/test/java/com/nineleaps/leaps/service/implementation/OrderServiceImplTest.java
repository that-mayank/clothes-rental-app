package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.ProductUrl;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private CartServiceInterface cartService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void placeOrder_ShouldCreateOrderAndSaveOrderItems() {
//        // Arrange
//        User user = new User();
//        String sessionId = "ABC123";
//
//        CartDto cartDto = new CartDto();
//        List<CartItemDto> cartItemDtos = new ArrayList<>();
//        CartItemDto cartItemDto1 = new CartItemDto();
//        Product product = new Product();
//        List<ProductUrl> productUrls = new ArrayList<>();
//        ProductUrl productUrl = new ProductUrl();
//        productUrl.setUrl("image_url_1.jpg");
//        productUrls.add(productUrl);
//        product.setImageURL(productUrls);
//        cartItemDto1.setProduct(new Product());
//        cartItemDto1.setQuantity(2);
//        cartItemDto1.setRentalStartDate(LocalDateTime.now().minusDays(2));
//        cartItemDto1.setRentalEndDate(LocalDateTime.now());
//        cartItemDtos.add(cartItemDto1);
//        cartDto.setCartItems(cartItemDtos);
//
//        Order newOrder = new Order();
//        newOrder.setCreateDate(LocalDateTime.now());
//        newOrder.setTotalPrice(10.0);
//        newOrder.setSessionId(sessionId);
//        newOrder.setUser(user);
//
//        when(cartService.listCartItems(user)).thenReturn(cartDto);
//        when(orderRepository.save(Mockito.<Order>any())).thenReturn(newOrder);
//        when(productRepository.save(Mockito.<Product>any())).thenReturn(new Product());
//
//        // Act
//        orderService.placeOrder(user, sessionId);
//
//        // Assert
//        verify(orderRepository, times(1)).save(Mockito.<Order>any());
//        verify(orderItemRepository, times(1)).save(Mockito.<OrderItem>any());
//        verify(cartService, times(1)).deleteUserCartItems(user);
//        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
//    }

    @Test
    void listOrders_ShouldReturnListOfOrders() {
        // Arrange
        User user = new User();
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        when(orderRepository.findByUserOrderByCreateDateDesc(user)).thenReturn(orders);

        // Act
        List<OrderDto> result = orderService.listOrders(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOrder_WithExistingOrderAndUser_ShouldReturnOrder() throws OrderNotFoundException {
        // Arrange
        Long orderId = 1L;
        User user = new User();
        Order order = new Order();
        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(java.util.Optional.of(order));

        // Act
        Order result = orderService.getOrder(orderId, user);

        // Assert
        assertNotNull(result);
        assertEquals(order, result);
    }

    @Test
    void getOrder_WithNonExistingOrder_ShouldThrowOrderNotFoundException() {
        // Arrange
        Long orderId = 1L;
        User user = new User();
        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId, user));
    }

    @Test
    void orderStatus_WithReturnedStatus_ShouldUpdateProductQuantities() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(2);
        orderItem.setProduct(new Product());

        // Act
        orderService.orderStatus(orderItem, "ORDER RETURNED");

        // Assert
        verify(orderItemRepository, times(1)).save(orderItem);
        verify(productRepository, times(1)).save(orderItem.getProduct());
        assertEquals(2, orderItem.getProduct().getAvailableQuantities());
        assertEquals(0, orderItem.getProduct().getRentedQuantities());
    }

    @Test
    void orderStatus_WithNonReturnedStatus_ShouldNotUpdateProductQuantities() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(2);
        orderItem.setProduct(new Product());

        // Act
        orderService.orderStatus(orderItem, "ORDER PLACED");

        // Assert
        verify(orderItemRepository, times(1)).save(orderItem);
        verify(productRepository, never()).save(orderItem.getProduct());
        assertEquals(0, orderItem.getProduct().getAvailableQuantities());
        assertEquals(0, orderItem.getProduct().getRentedQuantities());
    }

    @Test
    void dashboard_ShouldReturnDashboardData() {
        // Arrange
        User user = new User();
        Order order = new Order();
        OrderItem orderItem1 = new OrderItem();
        Product product = new Product(); // Create a new product
        orderItem1.setProduct(product);
        orderItem1.setQuantity(2);
        orderItem1.setPrice(10.0);
        orderItem1.setRentalStartDate(LocalDateTime.now().minusDays(2));
        orderItem1.setRentalEndDate(LocalDateTime.now().plusDays(4));
        order.getOrderItems().add(orderItem1);

        product.setUser(user); // Set the user on the product

        when(orderRepository.findAll()).thenReturn(List.of(order));

        // Act
        Map<String, Object> result = orderService.dashboard(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.get("totalNumberOfItems"));
        assertEquals(120.0, result.get("totalEarnings"));
    }




    @Test
    void dashboard() {
    }

    @Test
    void sendDelayChargeEmail() {
    }

    @Test
    void onClickDasboard() {
    }

    @Test
    void onClickDashboardYearWiseData() {
    }

    @Test
    void getOrderedItemsByMonthBwDates() {
    }

    @Test
    void getOrderedItemsByMonth() {
    }

    @Test
    void getOrderItemsBySubCategories() {
    }

    @Test
    void getOrderItemsByCategories() {
    }

    @Test
    void getRentedOutProducts() {
    }

    @Test
    void getPdf() {
    }

    @Test
    void addContent() {
    }

    @Test
    void getOrderItem() {
    }

    @Test
    void getRentalPeriods() {
    }
}