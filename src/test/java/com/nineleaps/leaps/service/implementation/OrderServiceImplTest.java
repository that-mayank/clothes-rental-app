package com.nineleaps.leaps.service.implementation;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;

import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.Product;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
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
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

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
    void sendDelayChargeEmail_ShouldSendEmailWithDelayChargeInformation() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        Order order = new Order();
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        order.setUser(user);
        orderItem.setOrder(order);
        orderItem.setId(1L);
        Product product = new Product();
        product.setName("Product 1");
        orderItem.setProduct(product);
        orderItem.setRentalStartDate(LocalDateTime.now().minusDays(7));
        orderItem.setRentalEndDate(LocalDateTime.now().minusDays(3));
        double securityDeposit = 100.0;
        String expectedSubject = "\"Reminder: Your rental period is ended.";
        String expectedMessage = "Dear John,\n\n" +
                "We regret to inform you that your rental period has exceeded the expected return date. " +
                "As a result, a delay charge has been deducted from your security deposit.\n\n" +
                "Rental Details:\n" +
                "Order ID: 1\n" +
                "Item Name: Product 1\n" +
                "Rental Start Date: " + orderItem.getRentalStartDate() + "\n" +
                "Rental End Date: " + orderItem.getRentalEndDate() + "\n" +
                "Security Deposit: " + securityDeposit + "\n" +
                "Delay Charge: " + orderService.calculateDelayCharge(orderItem.getRentalEndDate(), securityDeposit) + "\n" +
                "Remaining Deposit: " + orderService.calculateRemainingDeposit(securityDeposit, orderItem.getRentalEndDate(), orderItem) + "\n\n" +
                "Please contact us if you have any questions or concerns.\n" +
                "Thank you for your understanding.";

        // Stub the behavior of the email service
        when(emailService.sendEmail((expectedSubject), (expectedMessage), (user.getEmail())))
                .thenReturn(true); // Assuming the sendEmail method returns a boolean indicating success

        // Act
        orderService.sendDelayChargeEmail(orderItem, securityDeposit);

        // Assert
        verify(emailService).sendEmail((expectedSubject), (expectedMessage), (user.getEmail()));
    }

    @Test
    void onClickDashboard_ShouldReturnCorrectDataForUserOrders() {
        // Arrange
        User user = new User();
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderServiceImpl orderService = new OrderServiceImpl(orderRepository, null, null, emailService, productRepository);

        // Create test data
        Order order1 = new Order();
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(new Product());
        orderItem1.getProduct().setUser(user);
        orderItem1.setQuantity(2);
        orderItem1.setPrice(10.0);
        orderItem1.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem1.setRentalEndDate(LocalDateTime.of(2023, 1, 10, 0, 0));
        order1.getOrderItems().add(orderItem1);

        Order order2 = new Order();
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(new Product());
        orderItem2.getProduct().setUser(user);
        orderItem2.setQuantity(3);
        orderItem2.setPrice(20.0);
        orderItem2.setRentalStartDate(LocalDateTime.of(2023, 2, 1, 0, 0));
        orderItem2.setRentalEndDate(LocalDateTime.of(2023, 2, 10, 0, 0));
        order2.getOrderItems().add(orderItem2);

        // Configure the mock orderRepository to return the test data
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // Invoke the method under test
        Map<YearMonth, Map<String, Object>> result = orderService.onClickDasboard(user);

        // Verify the result
        assertEquals(2, result.size());

        // Verify the data for the first month
        YearMonth month1 = YearMonth.of(2023, 1);
        Map<String, Object> monthData1 = result.get(month1);
        assertEquals(2, monthData1.get("totalNumberOfItems"));
        assertEquals(180.0, monthData1.get("totalEarnings"));

        // Verify the data for the second month
        YearMonth month2 = YearMonth.of(2023, 2);
        Map<String, Object> monthData2 = result.get(month2);
        assertEquals(3, monthData2.get("totalNumberOfItems"));
        assertEquals(540.0, monthData2.get("totalEarnings"));
    }

    @Test
     void testOnClickDashboardYearWiseData() {
        // Mock data
        User user = new User();


        Order order1 = new Order();
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setQuantity(2);
        orderItem1.setPrice(10.0);
        orderItem1.setRentalStartDate(LocalDateTime.of(2022, 1, 1, 0, 0));
        orderItem1.setRentalEndDate(LocalDateTime.of(2022, 1, 10, 0, 0));
        orderItem1.setProduct(new Product());
        orderItem1.getProduct().setUser(user);
        orderItem1.setOrder(order1);
        List<OrderItem> orderItems1 = new ArrayList<>();
        orderItems1.add(orderItem1);
        order1.setOrderItems(orderItems1);

        Order order2 = new Order();
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setQuantity(3);
        orderItem2.setPrice(15.0);
        orderItem2.setRentalStartDate(LocalDateTime.of(2022, 2, 1, 0, 0));
        orderItem2.setRentalEndDate(LocalDateTime.of(2022, 2, 28, 0, 0));
        orderItem2.setProduct(new Product());
        orderItem2.getProduct().setUser(user);
        orderItem2.setOrder(order2);
        List<OrderItem> orderItems2 = new ArrayList<>();
        orderItems2.add(orderItem2);
        order2.setOrderItems(orderItems2);

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        // Execute the function
        Map<Year, Map<YearMonth, Map<String, Object>>> result = orderService.onClickDashboardYearWiseData(user);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());

        Year year = Year.of(2022);
        Map<YearMonth, Map<String, Object>> yearData = result.get(year);
        assertNotNull(yearData);
        assertEquals(2, yearData.size());

        YearMonth month1 = YearMonth.of(2022, 1);
        YearMonth month2 = YearMonth.of(2022, 2);

        Map<String, Object> monthData1 = yearData.get(month1);
        Map<String, Object> monthData2 = yearData.get(month2);

        assertNotNull(monthData1);
        assertNotNull(monthData2);

        assertEquals(2, monthData1.get("totalNumberOfItems"));
        assertEquals(180.0, monthData1.get("totalEarnings"));
        assertEquals(3, monthData2.get("totalNumberOfItems"));
        assertEquals(1215.0, monthData2.get("totalEarnings"));
    }

    @Test
    void getOrderedItemsByMonthBwDates_WhenNoOrdersWithinDateRange_ReturnsEmptyMap() {
        // Arrange
        User user = new User();  // Create a user object for testing
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);  // Specify the start date for the date range
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);  // Specify the end date for the date range

        // Act
        Map<YearMonth, List<OrderReceivedDto>> orderedItemsByMonth = orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate);

        // Assert
        assertTrue(orderedItemsByMonth.isEmpty(), "The map of ordered items should be empty");
    }

    @Test
    void getOrderedItemsByMonthBwDates_WhenOrdersWithinDateRange_ReturnsMapWithOrderedItems() {
        // Arrange
        User user = new User();  // Create a user object for testing
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderServiceImpl orderService = new OrderServiceImpl(orderRepository, cartService, orderItemRepository, emailService, productRepository);
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);  // Specify the start date for the date range
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);  // Specify the end date for the date range

        // Create some orders within the date range for the user
        Order order = new Order();
        order.setUser(user);
        Product product1 = new Product();
        product1.setUser(user);
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(product1);
        orderItem1.setImageUrl("NGROK/api/v1/file/view/1685304299973_image.png");
        orderItem1.setRentalStartDate(LocalDateTime.of(2023, 1, 15, 0, 0));
        orderItem1.setRentalEndDate(LocalDateTime.of(2023, 1, 31, 0, 0));
        orderItem1.setOrder(order);
        OrderItem orderItem2 = new OrderItem();
        Product product2 = new Product();
        product2.setUser(user);
        orderItem2.setProduct(product2);
        orderItem2.setImageUrl(NGROK+"/api/v1/file/view/test_image.png");
        orderItem2.setRentalStartDate(LocalDateTime.of(2023, 1, 15, 0, 0));
        orderItem2.setRentalEndDate(LocalDateTime.of(2023, 1, 31, 0, 0));
        orderItem2.setOrder(order);
        OrderItem orderItem3 = new OrderItem();
        Product product3 = new Product();
        orderItem3.setImageUrl(NGROK+"/api/v1/file/view/test_image.png");
        product3.setUser(user);
        orderItem3.setProduct(product3);
        orderItem3.setRentalStartDate(LocalDateTime.of(2023, 1, 25, 0, 0));
        orderItem3.setRentalEndDate(LocalDateTime.of(2023, 1, 31, 0, 0));
        orderItem3.setOrder(order);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);
        orderItems.add(orderItem3);
        order.setOrderItems(orderItems);

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        Map<YearMonth, List<OrderReceivedDto>> orderedItemsByMonth = orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate);

        // Print the contents of the orderedItemsInJanuary list
        List<OrderReceivedDto> orderedItemsInJanuary = orderedItemsByMonth.get(YearMonth.of(2023, 1));

        // Assert
        assertFalse(orderedItemsByMonth.isEmpty(), "The map of ordered items should not be empty");
        assertEquals(1, orderedItemsByMonth.size(), "The map should contain entries for each month with ordered items");

        // Additional assertions to check the specific order items within each month
        assertNotNull(orderedItemsInJanuary, "The map should contain an entry for January 2023");
        assertEquals(3, orderedItemsInJanuary.size(), "January 2023 should have 3 ordered items");
    }

    @Test
    void getOrderedItemsByMonth_ReturnsOrderedItemsGroupedByMonth() {
        // Prepare test data
        User user = new User();

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setName("test1");
        Product product1 = new Product();
        product1.setUser(user);
        orderItem1.setProduct(product1);
        orderItem1.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem1.setRentalEndDate(LocalDateTime.of(2023, 1, 28, 0, 0));
        orderItem1.setImageUrl(NGROK+"/api/v1/file/view/test1_image.png");

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setName("test2");
        Product product2 = new Product();
        product2.setUser(user);
        orderItem2.setProduct(product2);
        orderItem2.setRentalStartDate(LocalDateTime.of(2023, 2, 1, 0, 0));
        orderItem2.setRentalEndDate(LocalDateTime.of(2023, 2, 28, 0, 0));
        orderItem2.setImageUrl(NGROK+"/api/v1/file/view/test2_image.png");

        OrderItem orderItem3 = new OrderItem();
        orderItem3.setName("test3");
        Product product3 = new Product();
        product3.setUser(user);
        orderItem3.setProduct(product3);
        orderItem3.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem3.setRentalEndDate(LocalDateTime.of(2023, 1, 31, 0, 0));
        orderItem3.setImageUrl(NGROK+"/api/v1/file/view/test3_image.png");

        Order order1 = new Order();
        order1.setOrderItems(List.of(orderItem1));
        orderItem1.setOrder(order1);
        order1.setUser(user);

        Order order2 = new Order();
        order2.setOrderItems(List.of(orderItem2));
        orderItem2.setOrder(order2);
        order2.setUser(user);

        Order order3 = new Order();
        order3.setOrderItems(List.of(orderItem3));
        orderItem3.setOrder(order3);
        order3.setUser(user);

        List<Order> orders = List.of(order1, order2, order3);

        // Mock repository methods
        when(orderRepository.findAll()).thenReturn(orders);

        // Invoke the method
        Map<YearMonth, List<OrderReceivedDto>> result = orderService.getOrderedItemsByMonth(user);

        // Assert imageUrl of each OrderItem
        List<String> expectedImageUrls = Arrays.asList(
                NGROK+"/api/v1/file/view/test1_image.png",
                NGROK+"/api/v1/file/view/test3_image.png",
                NGROK+"/api/v1/file/view/test2_image.png"
        );
        List<String> actualImageUrls = result.values().stream()
                .flatMap(List::stream)
                .map(OrderReceivedDto::getImageUrl)
                .collect(Collectors.toList());

        assertEquals(expectedImageUrls, actualImageUrls);
    }

    @Test
    void getOrderItemsBySubCategories() {
        // Prepare test data
        User user = new User();

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setName("test1");
        Product product1 = new Product();
        product1.setUser(user);
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setSubcategoryName("subcategory1");
        product1.setSubCategories(List.of(subCategory1));
        orderItem1.setProduct(product1);
        orderItem1.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem1.setRentalEndDate(LocalDateTime.of(2023, 1, 28, 0, 0));
        orderItem1.setImageUrl(NGROK+"/api/v1/file/view/test1_image.png");


        OrderItem orderItem2 = new OrderItem();
        orderItem2.setName("test2");
        Product product2 = new Product();
        product2.setUser(user);
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setSubcategoryName("subcategory2");
        product2.setSubCategories(List.of(subCategory2));
        orderItem2.setProduct(product2);
        orderItem2.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem2.setRentalEndDate(LocalDateTime.of(2023, 1, 28, 0, 0));
        orderItem2.setImageUrl(NGROK+"/api/v1/file/view/test2_image.png");

        OrderItem orderItem3 = new OrderItem();
        orderItem3.setName("test3");
        Product product3 = new Product();
        product3.setUser(user);
        SubCategory subCategory3 = new SubCategory();
        subCategory3.setSubcategoryName("subcategory1");
        product3.setSubCategories(List.of(subCategory3));
        orderItem3.setProduct(product3);
        orderItem3.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem3.setRentalEndDate(LocalDateTime.of(2023, 1, 28, 0, 0));
        orderItem3.setImageUrl(NGROK+"/api/v1/file/view/test3_image.png");

        Order order1 = new Order();
        order1.setOrderItems(List.of(orderItem1));
        orderItem1.setOrder(order1);
        order1.setUser(user);

        Order order2 = new Order();
        order2.setOrderItems(List.of(orderItem2));
        orderItem2.setOrder(order2);
        order2.setUser(user);

        Order order3 = new Order();
        order3.setOrderItems(List.of(orderItem3));
        orderItem3.setOrder(order3);
        order3.setUser(user);

        List<Order> orders = List.of(order1, order2, order3);

        // Mock repository methods
        when(orderRepository.findAll()).thenReturn(orders);

        // Invoke the method
        Map<YearMonth, Map<String, OrderItemsData>> result = orderService.getOrderItemsBySubCategories(user);

        // Assert the result
        Map<YearMonth, Map<String, OrderItemsData>> expected = new HashMap<>();
        Map<String, OrderItemsData> januaryData = new HashMap<>();
        OrderItemsData subcategory1Data = new OrderItemsData();
        subcategory1Data.incrementTotalOrders(2);
        subcategory1Data.setOrderItems(List.of(new OrderReceivedDto(orderItem1), new OrderReceivedDto(orderItem3)));
        januaryData.put("subcategory1", subcategory1Data);
        expected.put(YearMonth.of(2023, 1), januaryData);
        Map<String, OrderItemsData> februaryData = new HashMap<>();
        OrderItemsData subcategory2Data = new OrderItemsData();
        subcategory2Data.incrementTotalOrders(1);
        subcategory2Data.setOrderItems(List.of(new OrderReceivedDto(orderItem2)));
        februaryData.put("subcategory2", subcategory2Data);
        expected.put(YearMonth.of(2023, 2), februaryData);

//        assertEquals(expected, result);

        // Verify the image URLs
        List<OrderReceivedDto> januarySubcategory1OrderItems = subcategory1Data.getOrderItems();
        assertEquals(2, januarySubcategory1OrderItems.size());
        assertEquals(NGROK+"/api/v1/file/view/test1_image.png", januarySubcategory1OrderItems.get(0).getImageUrl());
        assertEquals(NGROK+"/api/v1/file/view/test3_image.png", januarySubcategory1OrderItems.get(1).getImageUrl());

        List<OrderReceivedDto> februarySubcategory2OrderItems = subcategory2Data.getOrderItems();
        assertEquals(1, februarySubcategory2OrderItems.size());
        assertEquals(NGROK+"/api/v1/file/view/test2_image.png", februarySubcategory2OrderItems.get(0).getImageUrl());

        // Assert imageUrl of each OrderItem
        List<String> expectedImageUrls = Arrays.asList(
                NGROK+"/api/v1/file/view/test2_image.png",
                NGROK+"/api/v1/file/view/test1_image.png",
                NGROK+"/api/v1/file/view/test3_image.png"
        );
        List<String> actualImageUrls = result.values().stream()
                .flatMap(subcategoryData -> subcategoryData.values().stream())
                .flatMap(orderItemsData -> orderItemsData.getOrderItems().stream())
                .map(OrderReceivedDto::getImageUrl)
                .collect(Collectors.toList());


        assertEquals(expectedImageUrls, actualImageUrls);
    }

    @Test
    void getOrderItemsByCategories() {
        // Prepare test data
        User user = new User();

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setName("test1");
        Product product1 = new Product();
        product1.setUser(user);
        Category category1 = new Category();
        category1.setCategoryName("category1");
        product1.setCategories(List.of(category1));
        orderItem1.setProduct(product1);
        orderItem1.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem1.setRentalEndDate(LocalDateTime.of(2023, 1, 28, 0, 0));
        orderItem1.setImageUrl(NGROK+"/api/v1/file/view/test1_image.png");


        OrderItem orderItem2 = new OrderItem();
        orderItem2.setName("test2");
        Product product2 = new Product();
        product2.setUser(user);
        Category category2 = new Category();
        category2.setCategoryName("category2");
        product2.setCategories(List.of(category2));
        orderItem2.setProduct(product2);
        orderItem2.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem2.setRentalEndDate(LocalDateTime.of(2023, 1, 28, 0, 0));
        orderItem2.setImageUrl(NGROK+"/api/v1/file/view/test2_image.png");

        OrderItem orderItem3 = new OrderItem();
        orderItem3.setName("test3");
        Product product3 = new Product();
        product3.setUser(user);
        Category category3 = new Category();
        category3.setCategoryName("category1");
        product3.setCategories(List.of(category3));
        orderItem3.setProduct(product3);
        orderItem3.setRentalStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        orderItem3.setRentalEndDate(LocalDateTime.of(2023, 1, 28, 0, 0));
        orderItem3.setImageUrl(NGROK+"/api/v1/file/view/test3_image.png");

        Order order1 = new Order();
        order1.setOrderItems(List.of(orderItem1));
        orderItem1.setOrder(order1);
        order1.setUser(user);

        Order order2 = new Order();
        order2.setOrderItems(List.of(orderItem2));
        orderItem2.setOrder(order2);
        order2.setUser(user);

        Order order3 = new Order();
        order3.setOrderItems(List.of(orderItem3));
        orderItem3.setOrder(order3);
        order3.setUser(user);

        List<Order> orders = List.of(order1, order2, order3);

        // Mock repository methods
        when(orderRepository.findAll()).thenReturn(orders);

        // Invoke the method
        Map<YearMonth, Map<String, OrderItemsData>> result = orderService.getOrderItemsByCategories(user);

        // Assert the result
        Map<YearMonth, Map<String, OrderItemsData>> expected = new HashMap<>();
        Map<String, OrderItemsData> januaryData = new HashMap<>();
        OrderItemsData category1Data = new OrderItemsData();
        category1Data.incrementTotalOrders(2);
        category1Data.setOrderItems(List.of(new OrderReceivedDto(orderItem1), new OrderReceivedDto(orderItem3)));
        januaryData.put("category1", category1Data);
        expected.put(YearMonth.of(2023, 1), januaryData);
        Map<String, OrderItemsData> februaryData = new HashMap<>();
        OrderItemsData category2Data = new OrderItemsData();
        category2Data.incrementTotalOrders(1);
        category2Data.setOrderItems(List.of(new OrderReceivedDto(orderItem2)));
        februaryData.put("category2", category2Data);
        expected.put(YearMonth.of(2023, 2), februaryData);

        // Verify the image URLs
        List<OrderReceivedDto> januaryCategory1OrderItems = category1Data.getOrderItems();
        assertEquals(2, januaryCategory1OrderItems.size());
        assertEquals(NGROK+"/api/v1/file/view/test1_image.png", januaryCategory1OrderItems.get(0).getImageUrl());
        assertEquals(NGROK+"/api/v1/file/view/test3_image.png", januaryCategory1OrderItems.get(1).getImageUrl());

        // Verify the image URLs (continued)
        List<OrderReceivedDto> februaryCategory2OrderItems = category2Data.getOrderItems();
        assertEquals(1, februaryCategory2OrderItems.size());
        assertEquals(NGROK+"/api/v1/file/view/test2_image.png", februaryCategory2OrderItems.get(0).getImageUrl());

        // Verify the repository method invocations
        verify(orderRepository).findAll();

        // Assert imageUrl of each OrderItem
        List<String> expectedImageUrls = Arrays.asList(
                NGROK+"/api/v1/file/view/test2_image.png",
                NGROK+"/api/v1/file/view/test1_image.png",
                NGROK+"/api/v1/file/view/test3_image.png"
        );
        List<String> actualImageUrls = result.values().stream()
                .flatMap(subcategoryData -> subcategoryData.values().stream())
                .flatMap(orderItemsData -> orderItemsData.getOrderItems().stream())
                .map(OrderReceivedDto::getImageUrl)
                .collect(Collectors.toList());
    }


//    @Test
//    void getRentedOutProducts() {
//        // Prepare test data
//        User user = new User();
//
//        Product product1 = new Product();
//        product1.setUser(user);
//        product1.setId(1L);
//        product1.setName("Product 1");
//
//        Product product2 = new Product();
//        product2.setUser(user);
//        product2.setId(2L);
//        product2.setName("Product 2");
//
//        Product product3 = new Product(); // Product not rented by the user
//
//        Order order = new Order();
//        order.setUser(user);
//
//        OrderItem orderItem1 = new OrderItem();
//        orderItem1.setOrder(order);
//        orderItem1.setProduct(product1);
//
//        OrderItem orderItem2 = new OrderItem();
//        orderItem2.setOrder(order);
//        orderItem2.setProduct(product2);
//
//        List<Product> products = List.of(product1, product2, product3);
//        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);
//        List<Order> orders = List.of(order);
//
//        // Mock the repository methods
//        when(orderRepository.findAll()).thenReturn(orders);
//        when(orderItemRepository.findAll()).thenReturn(orderItems);
//        when(productRepository.findAll()).thenReturn(products);
//
//        // Invoke the method
//        List<ProductDto> result = orderService.getRentedOutProducts(user);
//
//        // Assert the result
//        List<ProductDto> expected = List.of(getDtoFromProduct(product1), getDtoFromProduct(product2));
//        assertEquals(expected, result);
//
//        // Verify the repository method invocations
//        verify(orderRepository).findAll();
//        verify(orderItemRepository).findAll();
//        verify(productRepository).findAll();
//    }


    @Test
    void getPdf() {
        // Prepare test data
        User user = new User();

        // Invoke the method
        Document result = orderService.getPdf(user);

        // Assert the result
        assertNotNull(result);
    }

    @Test
    void addContent() throws DocumentException, IOException {
        // Prepare test data
        User user = new User();
        Document document = new Document();
        document.open();

        // Invoke the method
        orderService.addContent(document, user);
        assertNotNull(user);
        // No assertion is needed as the method modifies the document
    }

    @Test
    void getOrderItem() {
        // Prepare test data
        User user = new User();
        Long orderItemId = 1L;
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(new Order());
        orderItem.getOrder().setUser(user);

        // Mock the repository method
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));

        // Invoke the method
        OrderItem result = orderService.getOrderItem(orderItemId, user);

        // Assert the result
        assertEquals(orderItem, result);

        // Verify the repository method invocation
        verify(orderItemRepository).findById(orderItemId);
    }

//    @Test
//    void getRentalPeriods() {
//        // Prepare test data
//        User user = new User();
//        Order order = new Order();
//        order.setUser(user);
//
//        OrderItem orderItem1 = new OrderItem();
//        orderItem1.setRentalStartDate(LocalDateTime.now().minusDays(3));
//        orderItem1.setRentalEndDate(LocalDateTime.now().plusDays(3));
//        orderItem1.setOrder(order);
//
//        OrderItem orderItem2 = new OrderItem();
//        orderItem2.setRentalStartDate(LocalDateTime.now().minusDays(2));
//        orderItem2.setRentalEndDate(LocalDateTime.now().plusDays(2));
//        orderItem2.setOrder(order);
//
//        OrderItem orderItem3 = new OrderItem();
//        orderItem3.setRentalStartDate(LocalDateTime.now().minusDays(1));
//        orderItem3.setRentalEndDate(LocalDateTime.now().plusDays(1));
//        orderItem3.setOrder(order);
//
//        List<OrderItem> orderItems = List.of(orderItem1, orderItem2, orderItem3);
//        order.setOrderItems(orderItems);
//
//        // Mock the repository method
//        when(orderItemRepository.findAll()).thenReturn(orderItems);
//
//        // Invoke the method
//        orderService.getRentalPeriods();
//
//        // Verify the email service method invocation
//        verify(emailService, times(3)).sendEmail(anyString(), anyString(), anyString());
//    }
}