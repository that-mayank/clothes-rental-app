package com.nineleaps.leaps.service.implementation;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.dto.pushNotification.PushNotificationRequest;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.OrderServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;

import static com.nineleaps.leaps.config.MessageStrings.*;
import static com.nineleaps.leaps.service.implementation.ProductServiceImpl.getDtoFromProduct;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderServiceInterface {
    private final OrderRepository orderRepository;
    private final CartServiceInterface cartService;
    private final OrderItemRepository orderItemRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final ProductRepository productRepository;
    private final PushNotificationServiceImpl pushNotificationService;

    @Override
    public void placeOrder(User user, String sessionId) {
        //retrieve the cart items for the user
        CartDto cartDto = cartService.listCartItems(user);
        List<CartItemDto> cartItemDtos = cartDto.getCartItems();
        //create order and save it
        Order newOrder = new Order();
        newOrder.setCreateDate(LocalDateTime.now());
        newOrder.setTotalPrice(cartDto.getTotalCost());
        newOrder.setSessionId(sessionId);
        newOrder.setUser(user);

        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItemDto cartItemDto : cartItemDtos) {
            //create cartItem and save each
            OrderItem orderItem = new OrderItem();
            orderItem.setName(cartItemDto.getProduct().getName());
            orderItem.setQuantity(cartItemDto.getQuantity());
            orderItem.setPrice(cartItemDto.getProduct().getPrice());
            orderItem.setCreatedDate(LocalDateTime.now());
            orderItem.setProduct(cartItemDto.getProduct());
            orderItem.setOrder(newOrder);
            orderItem.setRentalStartDate(cartItemDto.getRentalStartDate());
            orderItem.setRentalEndDate(cartItemDto.getRentalEndDate());
            orderItem.setImageUrl(cartItemDto.getProduct().getImageURL().get(0).getUrl());
            orderItem.setStatus("Order placed");
            orderItem.setOwnerId(cartItemDto.getProduct().getUser().getId());
            //add to orderItem table
            orderItemRepository.save(orderItem);
            orderItemList.add(orderItem);
            //Reduce quantity from product after placing order
            Product product = orderItem.getProduct();
            product.setRentedQuantities(product.getRentedQuantities() + cartItemDto.getQuantity());
            product.setAvailableQuantities(product.getAvailableQuantities() - cartItemDto.getQuantity());
            productRepository.save(product);
        }
        newOrder.setOrderItems(orderItemList);
        orderRepository.save(newOrder);
        //delete cart items after placing order
        cartService.deleteUserCartItems(user);

        // function to send email
        String email = user.getEmail();
        String subject = "Order placed";
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(DEAR_PREFIX).append(user.getFirstName()).append(" ").append(user.getLastName()).append(",\n");
        messageBuilder.append("Your Order has been successfully placed.\n");
        messageBuilder.append("Here are the details of your order:\n");
        Order latestOrder = newOrder;
        messageBuilder.append("Order ID: ").append(latestOrder.getId()).append("\n");
        List<OrderItem> orderItems = latestOrder.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            String productName = orderItem.getName();
            int quantity = orderItem.getQuantity();
            long rentalPeriod = ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate());
            double price = orderItem.getPrice() * orderItem.getQuantity() * rentalPeriod;

            messageBuilder.append("Product: ").append(productName).append("\n");
            messageBuilder.append("Quantity: ").append(quantity).append("\n");
            messageBuilder.append("Price: ").append(price).append("\n");
        }
        messageBuilder.append("Total Price of order: ").append(latestOrder.getTotalPrice()).append("\n\n");
        String message = messageBuilder.toString();
        emailServiceImpl.sendEmail(subject, message, email);


        for(OrderItem orderItem:orderItemList){
            String deviceToken = orderItem.getProduct().getUser().getDeviceToken();
            log.debug("Device token of owner from oder service is: {}",deviceToken);
            var request = PushNotificationRequest.builder()
                    .title("Order info")
                    .message("Order placed")
                    .token(deviceToken)
                    .build();
            pushNotificationService.sendPushNotificationToToken(request);
        }
    }

    @Override
    public List<OrderDto> listOrders(User user) {
        List<Order> orders = orderRepository.findByUserOrderByCreateDateDesc(user);
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderDto orderDto = new OrderDto(order);
            orderDtos.add(orderDto);
        }
        return orderDtos;
    }

    @Override
    public Order getOrder(Long orderId, User user) throws OrderNotFoundException {
        Optional<Order> optionalOrder = orderRepository.findByIdAndUserId(orderId, user.getId());
        if (optionalOrder.isEmpty()) {
            throw new OrderNotFoundException("Order not found");
        }
        return optionalOrder.get();
    }

    @Override
    public void orderStatus(OrderItem orderItem, String status) {
        orderItem.setStatus(status);
        orderItemRepository.save(orderItem);
        if (status.equals("ORDER RETURNED")) {
            Product product = orderItem.getProduct();
            product.setAvailableQuantities(product.getAvailableQuantities() + orderItem.getQuantity());
            product.setRentedQuantities(Math.max(product.getRentedQuantities() - orderItem.getQuantity(), 0));
            productRepository.save(product);
        }
    }

    public void sendDelayChargeEmail(OrderItem orderItem, double securityDeposit) {
        String email = orderItem.getOrder().getUser().getEmail();
        String subject = "\"Reminder: Your rental period is ended.";
        String message = DEAR_PREFIX + orderItem.getOrder().getUser().getFirstName() + ",\n\n" +
                "We regret to inform you that your rental period has exceeded the expected return date. " +
                "As a result, a delay charge has been deducted from your security deposit.\n\n" +
                "Rental Details:\n" +
                "Order ID: " + orderItem.getId() + "\n" +
                "Item Name: " + orderItem.getProduct().getName() + "\n" +
                "Rental Start Date: " + orderItem.getRentalStartDate() + "\n" +
                "Rental End Date: " + orderItem.getRentalEndDate() + "\n" +
                "Security Deposit: " + securityDeposit + "\n" +
                "Delay Charge: " + calculateDelayCharge(orderItem.getRentalEndDate(), securityDeposit) + "\n" +
                "Remaining Deposit: " + calculateRemainingDeposit(securityDeposit, orderItem.getRentalEndDate(), orderItem) + "\n\n" +
                "Please contact us if you have any questions or concerns.\n" +
                "Thank you for your understanding.";
        emailServiceImpl.sendEmail(subject, message, email);
    }

    double calculateDelayCharge(LocalDateTime rentalEndDate, double securityDeposit) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        long delayDays = ChronoUnit.DAYS.between(rentalEndDate, currentDateTime);
        if (delayDays > 0) {
            return (securityDeposit * 10.0 / 100) * delayDays;
        } else {
            return 0.0;
        }
    }

    double calculateRemainingDeposit(double securityDeposit, LocalDateTime rentalEndDate, OrderItem orderItem) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        long delayDays = ChronoUnit.DAYS.between(rentalEndDate, currentDateTime);
        if (delayDays > 0) {
            double delayCharge = (securityDeposit * 10.0 / 100) * delayDays;
            double remainingAmount = securityDeposit - delayCharge;
            if (remainingAmount >= 0) {
                orderItem.setSecurityDeposit(remainingAmount);
                orderItemRepository.save(orderItem);
            } else {
                orderItem.setSecurityDeposit(0);
                orderItemRepository.save(orderItem);
            }
            return remainingAmount;
        } else {
            return securityDeposit;
        }
    }

    @Override
    public Map<Year, Map<YearMonth, Map<String, Object>>> onClickDashboardYearWiseData(User user) {
        Map<Year, Map<YearMonth, Double>> totalEarningsByYearMonth = new HashMap<>();
        Map<Year, Map<YearMonth, Integer>> totalItemsByYearMonth = new HashMap<>();

        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    int quantity = orderItem.getQuantity();
                    double price = orderItem.getPrice();
                    LocalDateTime rentalStartDate = orderItem.getRentalStartDate();
                    LocalDateTime rentalEndDate = orderItem.getRentalEndDate();

                    long rentalDurationInDays = ChronoUnit.DAYS.between(rentalStartDate, rentalEndDate);
                    double earnings = price * quantity * rentalDurationInDays;

                    Year year = Year.from(rentalStartDate);
                    YearMonth month = YearMonth.from(rentalStartDate);

                    totalEarningsByYearMonth.computeIfAbsent(year, k -> new HashMap<>())
                            .merge(month, earnings, Double::sum);

                    totalItemsByYearMonth.computeIfAbsent(year, k -> new HashMap<>())
                            .merge(month, quantity, Integer::sum);
                }
            }
        }

        Map<Year, Map<YearMonth, Map<String, Object>>> result = new HashMap<>();
        for (Map.Entry<Year, Map<YearMonth, Double>> yearEntry : totalEarningsByYearMonth.entrySet()) {
            Year year = yearEntry.getKey();
            Map<YearMonth, Double> earningsByMonth = yearEntry.getValue();
            Map<YearMonth, Integer> itemsByMonth = totalItemsByYearMonth.get(year);
            Map<YearMonth, Map<String, Object>> yearData = new HashMap<>();
            for (Map.Entry<YearMonth, Double> monthEntry : earningsByMonth.entrySet()) {
                YearMonth month = monthEntry.getKey();
                Map<String, Object> monthData = new HashMap<>();
                monthData.put(TOTAL_NUMBER, itemsByMonth.get(month));
                monthData.put(TOTAL_INCOME, monthEntry.getValue());
                yearData.put(month, monthData);
            }
            result.put(year, yearData);
        }

        return result;
    }

    @Override
    public Map<YearMonth, List<OrderReceivedDto>> getOrderedItemsByMonthBwDates(User user, LocalDateTime startDate, LocalDateTime endDate) {
        Map<YearMonth, List<OrderReceivedDto>> orderedItemsByMonth = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user) && orderItem.getRentalStartDate().isAfter(startDate) && orderItem.getRentalStartDate().isBefore(endDate)) {
                    LocalDateTime rentalStartDate = orderItem.getRentalStartDate();
                    YearMonth month = YearMonth.from(rentalStartDate);
                    // Retrieve the list of order items for the current month
                    List<OrderReceivedDto> monthOrderItems = orderedItemsByMonth.getOrDefault(month, new ArrayList<>());
                    // Add the current order item to the list
                    monthOrderItems.add(new OrderReceivedDto(orderItem));
                    // Update the map with the updated list of order items
                    orderedItemsByMonth.put(month, monthOrderItems);
                }
            }
        }
        return orderedItemsByMonth;
    }

    @Override
    public Map<YearMonth, List<OrderReceivedDto>> getOrderedItemsByMonth(User user) {
        Map<YearMonth, List<OrderReceivedDto>> orderedItemsByMonth = new HashMap<>();

        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    LocalDateTime rentalStartDate = orderItem.getRentalStartDate();
                    YearMonth month = YearMonth.from(rentalStartDate);
                    // Retrieve the list of order items for the current month
                    List<OrderReceivedDto> monthOrderItems = orderedItemsByMonth.getOrDefault(month, new ArrayList<>());
                    // Add the current order item to the list
                    monthOrderItems.add(new OrderReceivedDto(orderItem));
                    // Update the map with the updated list of order items
                    orderedItemsByMonth.put(month, monthOrderItems);
                }
            }
        }
        return orderedItemsByMonth;
    }

    @Override
    public Map<YearMonth, Map<String, OrderItemsData>> getOrderItemsBySubCategories(User user) {
        Map<YearMonth, Map<String, OrderItemsData>> orderItemsSubcategoryWise = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    List<SubCategory> subcategories = orderItem.getProduct().getSubCategories();
                    YearMonth month = YearMonth.from(orderItem.getRentalStartDate());
                    // Retrieve the map of subcategories for the current month
                    Map<String, OrderItemsData> orderItemsBySubcategoryPerMonth = orderItemsSubcategoryWise.getOrDefault(month, new HashMap<>());
                    for (SubCategory subcategory : subcategories) {
                        // Retrieve the order items data for the current subcategory and month
                        OrderItemsData orderItemsData = orderItemsBySubcategoryPerMonth.getOrDefault(subcategory.getSubcategoryName(), new OrderItemsData());
                        // Increment the total number of orders for the current subcategory
                        orderItemsData.incrementTotalOrders(orderItem.getQuantity());
                        // Retrieve the list of order items for the current subcategory and month
                        List<OrderReceivedDto> orderItemsBySubcategory = orderItemsData.getOrderItems();
                        // If the list doesn't exist, create a new one
                        if (orderItemsBySubcategory == null) {
                            orderItemsBySubcategory = new ArrayList<>();
                        }
                        // Add the current order item to the list
                        OrderReceivedDto orderReceivedDto = new OrderReceivedDto(orderItem);
                        orderItemsBySubcategory.add(orderReceivedDto);
                        // Update the order items data with the updated list of order items
                        orderItemsData.setOrderItems(orderItemsBySubcategory);
                        // Update the map with the updated order items data for the current subcategory and month
                        orderItemsBySubcategoryPerMonth.put(subcategory.getSubcategoryName(), orderItemsData);

                        // Update the map with the updated map of subcategories per month
                        orderItemsSubcategoryWise.put(month, orderItemsBySubcategoryPerMonth);
                    }
                }
            }
        }
        return orderItemsSubcategoryWise;
    }

    @Override
    public Map<YearMonth, Map<String, OrderItemsData>> getOrderItemsByCategories(User user) {
        Map<YearMonth, Map<String, OrderItemsData>> orderItemsCategoryWise = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    List<Category> categories = orderItem.getProduct().getCategories();
                    YearMonth month = YearMonth.from(orderItem.getRentalStartDate());
                    // Retrieve the map of subcategories for the current month
                    Map<String, OrderItemsData> orderItemsByCategoryPerMonth = orderItemsCategoryWise.getOrDefault(month, new HashMap<>());
                    for (Category category : categories) {
                        // Retrieve the order items data for the current subcategory and month
                        OrderItemsData orderItemsData = orderItemsByCategoryPerMonth.getOrDefault(category.getCategoryName(), new OrderItemsData());
                        // Increment the total number of orders for the current subcategory
                        orderItemsData.incrementTotalOrders(orderItem.getQuantity());
                        // Retrieve the list of order items for the current subcategory and month
                        List<OrderReceivedDto> orderItemsByCategory = orderItemsData.getOrderItems();
                        // If the list doesn't exist, create a new one
                        if (orderItemsByCategory == null) {
                            orderItemsByCategory = new ArrayList<>();
                        }
                        // Add the current order item to the list
                        OrderReceivedDto orderReceivedDto = new OrderReceivedDto(orderItem);
                        orderItemsByCategory.add(orderReceivedDto);
                        // Update the order items data with the updated list of order items
                        orderItemsData.setOrderItems(orderItemsByCategory);
                        // Update the map with the updated order items data for the current subcategory and month
                        orderItemsByCategoryPerMonth.put(category.getCategoryName(), orderItemsData);
                        // Update the map with the updated map of subcategories per month
                        orderItemsCategoryWise.put(month, orderItemsByCategoryPerMonth);
                    }
                }
            }
        }
        return orderItemsCategoryWise;

    }

    @Override
    public List<ProductDto> getRentedOutProducts(User user, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OrderItem> page = orderItemRepository.findByOwnerId(pageable, user.getId());
        List<ProductDto> productDtoList = new ArrayList<>();
        for (var orderItem : page.getContent()) {
            ProductDto productDto = new ProductDto(orderItem.getProduct());
            productDtoList.add(productDto);
        }
        return productDtoList;
    }

    @Override
    public OrderItem getOrderItem(Long orderItemId, User user) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(orderItemId);
        if (optionalOrderItem.isPresent() && optionalOrderItem.get().getOrder().getUser().equals(user)) {
            return optionalOrderItem.get();
        }
        return null;
    }

    public void getRentalPeriods() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        for (OrderItem orderItem : orderItems) {
            LocalDate rentalStartDate = orderItem.getRentalStartDate().toLocalDate();
            LocalDate rentalEndDate = orderItem.getRentalEndDate().toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(rentalStartDate, rentalEndDate);

            if (daysBetween == 2 || daysBetween == 3 || daysBetween == 1) {
                String email = orderItem.getOrder().getUser().getEmail();
                String subject = "Reminder: Your rental period is ending soon";
                String message = DEAR_PREFIX + orderItem.getOrder().getUser().getFirstName() + ",\n" +
                        "This is a reminder that your rental period for the following item will end in " + daysBetween +
                        " days:\n" +
                        //"- " + orderItem.getOrder().getId() + "\n" +
                        "Please return the item before the end of the rental period to avoid any late fees.\n\n" +
                        "Thank you for choosing our rental service.\n\n" +
                        "Best regards,\n" +
                        "The Rental Service Team";
                emailServiceImpl.sendEmail(subject, message, email);
            }
        }
    }

    public byte[] generateInvoicePDF(List<OrderItem> orderItems, User user, Order order) throws IOException, DocumentException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Display overall order details (summary)
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Order Date: " + dateFormat.format(convertToDate(order.getCreateDate()))));
            document.add(new Paragraph("Name: " + user.getFirstName() + " " + user.getLastName()));
            document.add(new Paragraph(("Address: "+ order.getUser().getAddresses())));
            document.add(new Paragraph("\n"));

            // Create and populate order items table
            PdfPTable table = new PdfPTable(new float[]{20, 20, 15, 18, 35, 35,35});
            table.setWidthPercentage(100);

            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            PdfPCell cell1 = new PdfPCell(new Phrase("Product Name", tableHeaderFont));
            PdfPCell cell2 = new PdfPCell(new Phrase("Quantity", tableHeaderFont));
            PdfPCell cell3 = new PdfPCell(new Phrase("Price", tableHeaderFont));
            PdfPCell cell4 = new PdfPCell(new Phrase("Product Brand", tableHeaderFont));
            PdfPCell cell5 = new PdfPCell(new Phrase("Rental Start Date", tableHeaderFont));
            PdfPCell cell6 = new PdfPCell(new Phrase("Rental End Date", tableHeaderFont));
            PdfPCell cell7 = new PdfPCell(new Phrase("Security Deposit", tableHeaderFont));

            cell1.setFixedHeight(20);
            cell2.setFixedHeight(20);
            cell3.setFixedHeight(20);
            cell4.setFixedHeight(20);
            cell5.setFixedHeight(20);
            cell6.setFixedHeight(20);
            cell7.setFixedHeight(20);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            table.addCell(cell7);

            // Populate order items in the table
            for (OrderItem orderItem : orderItems) {
                table.addCell(orderItem.getName());
                table.addCell(String.valueOf(orderItem.getQuantity()));
                table.addCell(String.valueOf(orderItem.getPrice()));
                table.addCell(orderItem.getProduct().getBrand());
                table.addCell(dateFormat.format(convertToDate(orderItem.getRentalStartDate())));
                table.addCell(dateFormat.format(convertToDate(orderItem.getRentalEndDate())));
                table.addCell(String.valueOf(orderItem.getSecurityDeposit()));
            }

            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Grand Total: " + order.getTotalPrice()));


        } finally {
            document.close();
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public List<OrderItemDto> getOrdersItemByStatus(String shippingStatus, User user) {
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (var orderItem : orderItemList) {
            if(orderItem.getProduct().getUser().equals(user) && orderItem.getStatus().equals(shippingStatus)) orderItemDtos.add(new OrderItemDto(orderItem));
        }
        return orderItemDtos;
    }


    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


}
