package com.nineleaps.leaps.service.implementation;

import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.notifications.PushNotificationRequest;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private Helper helper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartServiceInterface cartService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PushNotificationServiceImpl pushNotificationService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

    }

    @Test
    void placeOrder_Success() {

        // Arrange

        User user = new User();

        String razorpayId = "razorpay123";

        CartDto cartDto = new CartDto();

        List<CartItemDto> cartItemDtos = new ArrayList<>();

        cartDto.setCartItems(cartItemDtos);

        when(helper.getUser(request)).thenReturn(user);

        when(cartService.listCartItems(request)).thenReturn(cartDto);

        // Act

        assertDoesNotThrow(() -> orderService.placeOrder(request, razorpayId));

        // Assert

        verify(orderRepository, times(1)).save(any(Order.class));

        verify(orderItemRepository, times(0)).save(any(OrderItem.class));

        verify(productRepository, times(0)).save(any());

        verify(cartService, times(1)).deleteUserCartItems(user);

        verify(pushNotificationService, times(0)).sendPushNotificationToToken(any(PushNotificationRequest.class));

    }

    @Test
    void placeOrder_WithCartItem() {

        // Arrange

        User user = new User();

        user.setId(3L);

        user.setDeviceToken("test");

        Product product = new Product();

        product.setId(1L);

        product.setUser(user);

        ProductUrl productUrl = new ProductUrl();

        productUrl.setId(1L);

        productUrl.setUrl("testUrl");

        product.setImageURL(List.of(productUrl, productUrl));

        String razorpayId = "razorpay123";

        CartDto cartDto = new CartDto();

        List<CartItemDto> cartItemDtos = new ArrayList<>();

        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.setProduct(product);

        cartItemDto.setRentalStartDate(LocalDateTime.now());

        cartItemDto.setRentalEndDate(LocalDateTime.now().plusDays(10));

        cartItemDtos.add(cartItemDto);

        cartDto.setCartItems(cartItemDtos);

        when(helper.getUser(request)).thenReturn(user);

        when(cartService.listCartItems(request)).thenReturn(cartDto);

        // Act

        assertDoesNotThrow(() -> orderService.placeOrder(request, razorpayId));

        // Assert

        verify(orderRepository, times(1)).save(any(Order.class));

        verify(orderItemRepository, times(1)).save(any(OrderItem.class));

        verify(productRepository, times(1)).save(any());

        verify(cartService, times(1)).deleteUserCartItems(user);

        verify(pushNotificationService, times(1)).sendPushNotificationToToken(any(PushNotificationRequest.class));

    }

    @Test
    void placeOrder_NullCartItem() {

        // Arrange

        User user = new User();

        String razorpayId = "razorpay123";

        CartDto cartDto = new CartDto();

        List<CartItemDto> cartItemDtos = null;

        cartDto.setCartItems(cartItemDtos);

        when(cartService.listCartItems(request)).thenReturn(cartDto);

        // Act and Assert

        assertThrows(RuntimeException.class, () -> {

            // Your code that is expected to throw an exception

            orderService.placeOrder(request, razorpayId);

        });

        verify(orderRepository, times(0)).save(any(Order.class));

        verify(orderItemRepository, times(0)).save(any(OrderItem.class));

        verify(productRepository, times(0)).save(any());

        verify(cartService, times(0)).deleteUserCartItems(user);

        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());

        verify(pushNotificationService, times(0)).sendPushNotificationToToken(any(PushNotificationRequest.class));

    }

    @Test
    void listOrders() {

        // Arrange

        User user = new User();

        Order order1 = new Order();

        order1.setId(1L);

        Order order2 = new Order();

        order2.setId(2L);

        List<Order> orders = new ArrayList<>();

        orders.add(order1);

        orders.add(order2);

        when(helper.getUser(request)).thenReturn(user);

        when(orderRepository.findByUserOrderByCreateDateDesc(user)).thenReturn(orders);

        // Act

        List<OrderDto> orderDtos = orderService.listOrders(request);

        // Assert

        assertNotNull(orderDtos);

        assertEquals(2, orderDtos.size());

        assertEquals(1L, orderDtos.get(0).getId());

        assertEquals(2L, orderDtos.get(1).getId());

    }

    @Test
    void getOrder_OrderExists() {

        // Arrange

        User user = new User();

        Long orderId = 1L;

        Order order = new Order();

        order.setId(orderId);

        order.setUser(user);

        when(helper.getUser(request)).thenReturn(user);

        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(Optional.of(order));

        // Act

        Order retrievedOrder = orderService.getOrder(orderId, request);

        // Assert

        assertNotNull(retrievedOrder);

        assertEquals(orderId, retrievedOrder.getId());

    }

    @Test
    void getOrder_OrderNotFound() {

        // Arrange

        User user = new User();

        Long orderId = 1L;

        when(helper.getUser(request)).thenReturn(user);

        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(Optional.empty());

        // Act and Assert

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId, request));

    }

    @Test
    void orderStatus() {

        // Arrange

        User user = new User();
        user.setId(1L);

        OrderItem orderItem = new OrderItem();

        orderItem.setOwnerId(user.getId());

        String status = "ORDER_RETURNED";

        when(helper.getUser(request)).thenReturn(user);

        // Act

        assertDoesNotThrow(() -> orderService.orderStatus(request, orderItem.getId(), status));

        // Assert

        assertEquals(status, orderItem.getStatus());

    }

    @Test
    void sendDelayChargeEmail() {

        // Arrange

        User user = new User();

        user.setId(1L);

        user.setEmail("xyz@gmail.com");

        Product product = new Product();

        product.setName("product");

        Order order = new Order();

        order.setUser(user);

        OrderItem orderItem = new OrderItem();

        orderItem.setProduct(product);

        orderItem.setOrder(order);

        orderItem.setOwnerId(user.getId());

        orderItem.setRentalStartDate(LocalDateTime.now());

        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(4));

        double securityDeposit = 100.0;

        // Act and Assert

        assertDoesNotThrow(() -> orderService.sendDelayChargeEmail(orderItem, securityDeposit));

    }

    @Test
    void calculateDelayCharge() {

        // Arrange

        LocalDateTime rentalEndDate = LocalDateTime.now().minusDays(5);

        double securityDeposit = 100.0;

        // Act

        double delayCharge = orderService.calculateDelayCharge(rentalEndDate, securityDeposit);

        // Assert

        assertEquals(50.0, delayCharge);

    }

    @Test
    void calculateRemainingDeposit() {

        // Arrange

        OrderItem orderItem = new OrderItem();

        LocalDateTime rentalEndDate = LocalDateTime.now().minusDays(5);

        double securityDeposit = 100.0;

        // Act

        double remainingDeposit = orderService.calculateRemainingDeposit(securityDeposit, rentalEndDate, orderItem);

        // Assert

        assertEquals(50.0, remainingDeposit);

    }

    @Test
    void onClickDashboardYearWiseData() {

        // Create a user

        User user = new User();

        user.setId(5L);

// Create a product associated with the user

        Product product = new Product();

        product.setUser(user);

        product.setId(1L);

        Product product2 = new Product();

        product2.setUser(user);

        product2.setId(1L);

        List<Order> orders = new ArrayList<>();

// Create order 1 with valid product data

        Order order1 = new Order();

        order1.setId(1L);

        OrderItem orderItem1 = new OrderItem();

        orderItem1.setProduct(product); // Set the product associated with the user

        orderItem1.setQuantity(2);

        orderItem1.setPrice(50.0);

        orderItem1.setRentalStartDate(LocalDateTime.now().minusMonths(2));

        orderItem1.setRentalEndDate(LocalDateTime.now().minusMonths(1));

        order1.setOrderItems(Collections.singletonList(orderItem1));

        order1.setUser(user);

        orders.add(order1);

// Create order 2 with valid product data

        Order order2 = new Order();

        order2.setId(2L);

        OrderItem orderItem2 = new OrderItem();

        orderItem2.setProduct(product2); // Set the product associated with the user

        orderItem2.setQuantity(3);

        orderItem2.setPrice(60.0);

        orderItem2.setRentalStartDate(LocalDateTime.now().minusMonths(1));

        orderItem2.setRentalEndDate(LocalDateTime.now());

        order2.setOrderItems(Collections.singletonList(orderItem2));

        order2.setUser(user);

        orders.add(order2);

        // Mock the orderRepository.findAll() to return the orders

        when(helper.getUser(request)).thenReturn(user);

        when(orderRepository.findAll()).thenReturn(orders);

        // Act

        Map<Year, Map<YearMonth, Map<String, Object>>> result = orderService.onClickDashboardYearWiseData(request);

        // Assert

        assertNotNull(result);

        assertEquals(1, result.size());

        Year year1 = Year.now();

        assertTrue(result.containsKey(year1));

        Map<YearMonth, Map<String, Object>> year1Data = result.get(year1);

        assertNotNull(year1Data);

        assertEquals(2, year1Data.size());

        Year year2 = Year.now();

        assertTrue(result.containsKey(year2));

        Map<YearMonth, Map<String, Object>> year2Data = result.get(year2);

        assertNotNull(year2Data);

        assertEquals(2, year2Data.size());

    }

    @Test
    void testOrderStatusOrderReturned() {
        // Create a mock OrderItem and Product
        User user = new User();
        user.setId(1L);
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(2); // Example quantity
        orderItem.setOwnerId(user.getId());
        orderItem.setOrder(order);
        Product product = new Product();
        product.setAvailableQuantities(5); // Example available quantities
        product.setRentedQuantities(3); // Example rented quantities
        orderItem.setProduct(product);

        // Define the status
        String status = "ORDER RETURNED";

        // Mock the behavior of the repositories
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(productRepository.save(product)).thenReturn(product);
        when(helper.getUser(request)).thenReturn(user);

        // Call the method
        orderService.orderStatus(request,orderItem.getId(), status);

        // Assertions
        assertEquals("ORDER RETURNED", orderItem.getStatus());
        assertEquals(7, product.getAvailableQuantities()); // 5 (original) + 2 (returned)
        assertEquals(1, product.getRentedQuantities()); // 3 (original) - 2 (returned)

        // Verify that save methods were called
        verify(orderItemRepository, times(1)).save(orderItem);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testOrderStatusOtherStatus() {
        // Create a mock OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);

        // Define a different status
        String status = "SHIPPED"; // Example other status

        // Mock the behavior of the repository
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);

        // Call the method
        orderService.orderStatus(request, orderItem.getId(), status);

        // Assertions
        assertEquals("SHIPPED", orderItem.getStatus());

        // Verify that save method was called
        verify(orderItemRepository, times(1)).save(orderItem);
    }

    @Test
    void testGetOrderedItemsByMonthBwDates() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);
        user.setEmail("xyz@gmail.com");
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("abc@gmail.com");
        LocalDateTime startDate = LocalDateTime.of(2023, 9, 1, 0, 0);  // September 1, 2023
        LocalDateTime endDate = LocalDateTime.of(2023, 9, 30, 23, 59);  // September 30, 2023

        Order order1 = new Order();
        order1.setUser(user);
        Order order2 = new Order();
        order2.setUser(user);

        OrderItem orderItem1 = new OrderItem();
        OrderItem orderItem2 = new OrderItem();

        Product product1 = new Product();
        Product product2 = new Product();

        product1.setUser(user);
        product2.setUser(user);

        LocalDateTime rentalStartDate1 = LocalDateTime.of(2023, 9, 15, 10, 0);  // September 15, 2023
        LocalDateTime rentalStartDate2 = LocalDateTime.of(2023, 9, 20, 12, 0);  // September 20, 2023
        LocalDateTime rentalEndDate1 = LocalDateTime.of(2023, 10, 15, 0, 0);
        LocalDateTime rentalEndDate2 = LocalDateTime.of(2023, 10, 20, 0, 0);

        orderItem1.setProduct(product1);
        orderItem1.setRentalStartDate(rentalStartDate1);
        orderItem1.setRentalEndDate(rentalEndDate1);
        orderItem1.setImageUrl("/api/asdfgh.jpj");
        orderItem1.setOwnerId(user2.getId());
        orderItem1.setOrder(order1);

        orderItem2.setProduct(product2);
        orderItem2.setRentalStartDate(rentalStartDate2);
        orderItem2.setRentalEndDate(rentalEndDate2);
        orderItem2.setImageUrl("/api/asdfgh.jpj");
        orderItem2.setOwnerId(user2.getId());
        orderItem2.setOrder(order2);

        order1.setOrderItems(Collections.singletonList(orderItem1));
        order2.setOrderItems(Collections.singletonList(orderItem2));

        // Mock the behavior of orderRepository.findAll()
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));
        when(helper.getUser(request)).thenReturn(user);

        // Call the method to be tested
        Map<YearMonth, List<OrderReceivedDto>> result = orderService.getOrderedItemsByMonthBwDates(request, startDate, endDate);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify that orderRepository.findAll() was called
        verify(orderRepository, times(1)).findAll();
    }


    @Test
     void testGetOrderedItemsByMonthBwDatesNoItemsInDateRange() {
        // Create a user
        User user = new User();
        user.setId(1L); // Example user ID

        // Create an example date range
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 3, 31, 23, 59);

        // Create an example Order and OrderItem outside the date range
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setRentalStartDate(LocalDateTime.of(2023, 4, 15, 0, 0)); // Outside the date range
        orderItem.setProduct(new Product());
        orderItem.getProduct().setUser(user);

        // Mock the behavior of the repository
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        when(helper.getUser(request)).thenReturn(user);

        // Call the method
        Map<YearMonth, List<OrderReceivedDto>> orderedItemsByMonth = orderService.getOrderedItemsByMonthBwDates(request, startDate, endDate);

        // Assertions
        assertNotNull(orderedItemsByMonth);
        YearMonth yearMonth = YearMonth.of(2023, 4); // Outside the date range
        assertFalse(orderedItemsByMonth.containsKey(yearMonth));

        // Verify that the repository method was called
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderedItemsByMonth() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);
        user.setEmail("xyz@gmail.com");

        Order order1 = new Order();
        order1.setUser(user);
        Order order2 = new Order();
        order2.setUser(user);

        Product product1 = new Product();
        product1.setUser(user);
        Product product2 = new Product();
        product2.setUser(user);

        LocalDateTime rentalStartDate1 = LocalDateTime.of(2023, 9, 15, 10, 0);  // September 15, 2023
        LocalDateTime rentalStartDate2 = LocalDateTime.of(2023, 9, 20, 12, 0);  // September 20, 2023
        LocalDateTime rentalEndDate1 = LocalDateTime.of(2023, 10, 15, 0, 0);
        LocalDateTime rentalEndDate2 = LocalDateTime.of(2023, 10, 20, 0, 0);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(product1);
        orderItem1.setRentalStartDate(rentalStartDate1);
        orderItem1.setRentalEndDate(rentalEndDate1);
        orderItem1.setImageUrl("/api/asdfgh.jpg");
        orderItem1.setOwnerId(2L);
        orderItem1.setOrder(order1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(product2);
        orderItem2.setRentalStartDate(rentalStartDate2);
        orderItem2.setRentalEndDate(rentalEndDate2);
        orderItem2.setImageUrl("/api/qwerty.jpg");
        orderItem2.setOwnerId(2L);
        orderItem2.setOrder(order2);

        order1.setOrderItems(Collections.singletonList(orderItem1));
        order2.setOrderItems(Collections.singletonList(orderItem2));

        // Mock the behavior of orderRepository.findAll()
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1,order2));
        when(helper.getUser(request)).thenReturn(user);

        // Call the method to be tested
        Map<YearMonth, List<OrderReceivedDto>> result = orderService.getOrderedItemsByMonth(request);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());  // Asserting the number of months in the result


        // Verify that orderRepository.findAll() was called
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderItemsBySubCategories() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        SubCategory subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setSubcategoryName("sample subcategory");
        List<SubCategory> subCategoryList = new ArrayList<>();
        subCategoryList.add(subCategory);
        Order order1 = new Order();
        order1.setUser(user);
        Order order2 = new Order();
        order2.setUser(user);


        Product product1 = new Product();
        Product product2 = new Product();

        product1.setUser(user);
        product1.setSubCategories(subCategoryList);
        product2.setUser(user);
        product2.setSubCategories(subCategoryList);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(product1);
        orderItem1.setImageUrl("/api/asdfgh.jpg");
        orderItem1.setOwnerId(2L);
        orderItem1.setOrder(order1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(product2);
        orderItem2.setImageUrl("/api/qwerty.jpg");
        orderItem2.setOwnerId(2L);
        orderItem2.setOrder(order2);

        LocalDateTime rentalStartDate1 = LocalDateTime.of(2023, 9, 15, 10, 0);  // September 15, 2023
        LocalDateTime rentalStartDate2 = LocalDateTime.of(2023, 9, 20, 12, 0);  // September 20, 2023
        LocalDateTime rentalEndDate1 = LocalDateTime.of(2023, 10, 15, 0, 0);
        LocalDateTime rentalEndDate2 = LocalDateTime.of(2023, 10, 20, 0, 0);

        orderItem1.setProduct(product1);
        orderItem1.setRentalStartDate(rentalStartDate1);
        orderItem1.setRentalEndDate(rentalEndDate1);

        orderItem2.setProduct(product2);
        orderItem2.setRentalStartDate(rentalStartDate2);
        orderItem2.setRentalEndDate(rentalEndDate2);

        order1.setOrderItems(Collections.singletonList(orderItem1));
        order2.setOrderItems(Collections.singletonList(orderItem2));

        // Mock the behavior of orderRepository.findAll()
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));
        when(helper.getUser(request)).thenReturn(user);

        // Call the method to be tested
        Map<YearMonth, Map<String, OrderItemsData>> result = orderService.getOrderItemsBySubCategories(request);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.isEmpty());


        // Ensure that the correct subcategory is present in the result
        assertTrue(result.values().stream()
                .anyMatch(subcategoryMap -> subcategoryMap.containsKey("sample subcategory")));


        // Verify that orderRepository.findAll() was called
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderItemsByCategories() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("sample category");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);

        Order order1 = new Order();
        order1.setUser(user);

        Product product1 = new Product();
        product1.setUser(user);
        product1.setCategories(categoryList);

        OrderItem orderItem1 = new OrderItem();

        // Setting up order items
        orderItem1.setProduct(product1);
        orderItem1.setImageUrl("/api/asdfgh.jpg");
        orderItem1.setOwnerId(1L);
        orderItem1.setOrder(order1);

        LocalDateTime rentalStartDate1 = LocalDateTime.of(2023, 9, 15, 10, 0);  // September 15, 2023
        LocalDateTime rentalEndDate1 = LocalDateTime.of(2023, 10, 15, 0, 0);

        orderItem1.setProduct(product1);
        orderItem1.setRentalStartDate(rentalStartDate1);
        orderItem1.setRentalEndDate(rentalEndDate1);

        order1.setOrderItems(Collections.singletonList(orderItem1));

        // Mock the behavior of orderRepository.findAll()
        when(orderRepository.findAll()).thenReturn(List.of(order1));
        when(helper.getUser(request)).thenReturn(user);

        // Call the method to be tested
        Map<YearMonth, Map<String, OrderItemsData>> result = orderService.getOrderItemsByCategories(request);

        // Verify the result
        assertNotNull(result);
        assertEquals(1,result.size());

        // Ensure that the correct category is present in the result
        assertTrue(result.values().stream()
                .anyMatch(categoryMap -> categoryMap.containsKey("sample category")));


        // Verify that orderRepository.findAll() was called
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetRentedOutProducts() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);

        // Mock OrderItem and Product
        OrderItem orderItem = new OrderItem();
        Product product = new Product();
        orderItem.setProduct(product);

        // Mock Page and Pageable
        Page<OrderItem> orderItemPage = new PageImpl<>(Collections.singletonList(orderItem));
        Pageable pageable = PageRequest.of(0, 10);

        // Mock the behavior of orderItemRepository.findByOwnerId
        when(orderItemRepository.findByOwnerId(pageable, user.getId())).thenReturn(orderItemPage);
        when(helper.getUser(request)).thenReturn(user);

        // Call the method to be tested
        List<ProductDto> productDtoList = orderService.getRentedOutProducts(request, 0, 10);

        // Verify the result
        assertNotNull(productDtoList);
        assertEquals(1, productDtoList.size());

        // Ensure the ProductDto is correctly created from OrderItem
        ProductDto productDto = productDtoList.get(0);
        assertEquals(product.getId(), productDto.getId());

        // Verify that orderItemRepository.findByOwnerId was called
        verify(orderItemRepository, times(1)).findByOwnerId(pageable, user.getId());
    }

    @Test
    void testGetOrderItem() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);

        // Create a sample order item
        OrderItem orderItem = new OrderItem();
        orderItem.setId(100L);
        Order order = new Order();
        order.setUser(user);
        orderItem.setOrder(order);

        // Mock the behavior of orderItemRepository.findById
        when(orderItemRepository.findById(100L)).thenReturn(Optional.of(orderItem));
        when(helper.getUser(request)).thenReturn(user);

        // Call the method to be tested
        OrderItem resultOrderItem = orderService.getOrderItem(100L, user);

        // Verify the result when the order item is found and associated with the user
        assertNotNull(resultOrderItem);
        assertEquals(orderItem, resultOrderItem);

        // Call the method with a different order item ID
        OrderItem nonMatchingResult = orderService.getOrderItem(200L, user);

        // Verify that the order item is not found and null is returned
        assertNull(nonMatchingResult);

        // Call the method with a different user
        User differentUser = new User();
        differentUser.setId(2L);
        OrderItem resultForDifferentUser = orderService.getOrderItem(100L, differentUser);

        // Verify that the order item is not associated with the different user and null is returned
        assertNull(resultForDifferentUser);

        // Verify that orderItemRepository.findById was called
        verify(orderItemRepository, times(3)).findById(anyLong());
    }

    @Test
    void testGetRentalPeriods() {
        // Mock necessary data
        User user = new User();
        user.setEmail("user@example.com");
        user.setFirstName("John");

        OrderItem orderItem1 = new OrderItem();

        Order order1 = new Order();
        order1.setUser(user);
        orderItem1.setOrder(order1);

        LocalDateTime rentalStartDate1 = LocalDateTime.now().minusDays(3);
        LocalDateTime rentalEndDate1 = LocalDateTime.now().minusDays(1);
        orderItem1.setRentalStartDate(rentalStartDate1);
        orderItem1.setRentalEndDate(rentalEndDate1);

        // Mock the behavior of orderItemRepository.findAll()
        when(orderItemRepository.findAll()).thenReturn(List.of(orderItem1));
        when(helper.getUser(request)).thenReturn(user);

        // Call the method to be tested
        orderService.getRentalPeriods();


        // Verify that orderItemRepository.findAll() was called
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    void testGenerateInvoicePDF() throws DocumentException, IOException {
        // Mock necessary data
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        Address address = new Address();
        address.setAddressLine1("sample addressline 1");
        address.setAddressLine2("sample address line 2");

        List<Address> addressList = new ArrayList<>();
        addressList.add(address);
        user.setAddresses(addressList);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setCreateDate(LocalDateTime.of(2023, 9, 25, 10, 30));
        order.setTotalPrice(150.0);

        List<OrderItem> orderItems = Arrays.asList(
                createOrderItem("Product 1", 2, 30.0, "Brand A", LocalDateTime.of(2023, 9, 15, 8, 0), LocalDateTime.of(2023, 9, 18, 8, 0), 50.0),
                createOrderItem("Product 2", 1, 50.0, "Brand B", LocalDateTime.of(2023, 9, 20, 10, 0), LocalDateTime.of(2023, 9, 23, 10, 0), 70.0)
        );

        byte[] pdfBytes;
        pdfBytes = orderService.generateInvoicePDF(orderItems, user, order);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length>0);


    }

    // Helper method to create an order item
    private OrderItem createOrderItem(String name, int quantity, double price, String brand, LocalDateTime rentalStartDate, LocalDateTime rentalEndDate, double securityDeposit) {
        OrderItem orderItem = new OrderItem();
        Product product = new Product();
        product.setBrand(brand);

        orderItem.setName(name);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        orderItem.setProduct(product);
        orderItem.setRentalStartDate(rentalStartDate);
        orderItem.setRentalEndDate(rentalEndDate);
        orderItem.setSecurityDeposit(securityDeposit);

        return orderItem;
    }

    @Test
    void testGetOrdersItemByStatus() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        Product product1 = new Product();
        Product product2 = new Product();

        product1.setUser(user);
        product2.setUser(user);

        OrderItem orderItem1 = new OrderItem();
        OrderItem orderItem2 = new OrderItem();
        OrderItem orderItem3 = new OrderItem();

        orderItem1.setProduct(product1);
        orderItem1.setStatus("SHIPPED");
        orderItem1.setId(1L);

        orderItem2.setProduct(product2);
        orderItem2.setStatus("DELIVERED");
        orderItem2.setId(2L);

        orderItem3.setProduct(product1);
        orderItem3.setStatus("SHIPPED");
        orderItem3.setId(3L);

        // Mock the behavior of orderItemRepository.findAll()
        when(orderItemRepository.findAll()).thenReturn(Arrays.asList(orderItem1, orderItem2, orderItem3));
        when(helper.getUser(request)).thenReturn(user);

        // Call the method to be tested
        List<OrderItemDto> result = orderService.getOrdersItemByStatus("SHIPPED", request);

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());

        // Assert that the returned order items match the expected ones
        assertTrue(result.stream().anyMatch(dto -> orderItem1.getId() != null && dto.getId() != null && dto.getId().equals(orderItem1.getId())));
        assertTrue(result.stream().anyMatch(dto -> orderItem3.getId() != null && dto.getId() != null && dto.getId().equals(orderItem3.getId())));

        // Verify that orderItemRepository.findAll() was called
        verify(orderItemRepository, times(1)).findAll();
    }
}