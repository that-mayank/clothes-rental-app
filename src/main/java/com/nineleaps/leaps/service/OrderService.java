package com.nineleaps.leaps.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import static com.nineleaps.leaps.service.ProductService.getDtoFromProduct;


@Service
@Transactional
@AllArgsConstructor
public class OrderService implements OrderServiceInterface {
    private final CartServiceInterface cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EmailService emailService;
    private final ProductRepository productRepository;

    @Override
    public void placeOrder(User user, String sessionId) {
        //retrieve the cart items for the user
        CartDto cartDto = cartService.listCartItems(user);
        List<CartItemDto> cartItemDtos = cartDto.getCartItems();

        //create order and save it
        Order newOrder = new Order();
        newOrder.setCreateDate(new Date());
        newOrder.setTotalPrice(cartDto.getTotalCost());
        newOrder.setSessionId(sessionId);
        newOrder.setUser(user);
        orderRepository.save(newOrder);

        for (CartItemDto cartItemDto : cartItemDtos) {
            //create cartItem and save each
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItemDto.getQuantity());
            orderItem.setPrice(cartItemDto.getProduct().getPrice());
            orderItem.setCreatedDate(LocalDateTime.now());
            orderItem.setProduct(cartItemDto.getProduct());
            orderItem.setOrder(newOrder);
            orderItem.setRentalStartDate(cartItemDto.getRentalStartDate());
            orderItem.setRentalEndDate(cartItemDto.getRentalEndDate());
            orderItem.setImageUrl(cartItemDto.getImageUrl());
            orderItem.setStatus("Order placed");
            //add to orderItem table
            orderItemRepository.save(orderItem);
            //Reduce quantity from product after placing order
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItemDto.getQuantity());
            productRepository.save(product);

        }
        //delete cart items after placing order
        cartService.deleteUserCartItems(user);
        // function to send email
        String email = user.getEmail();
        String subject = "Order placed";
        String message = "Dear " + user.getFirstName() + "Your Order has been successfully placed." + "The order details are as follows: /n" + user.getOrders();
        emailService.sendEmail(subject, message, email);
    }

    @Override
    public List<Order> listOrders(User user) {
        return orderRepository.findByUserOrderByCreateDateDesc(user);
    }

    @Override
    public Order getOrder(Long orderId, User user) throws OrderNotFoundException {
        List<Order> orders = listOrders(user);
        for (Order order : orders) {
            if (orderId.equals(order.getId())) {
                return order;
            }
        }
        throw new OrderNotFoundException("Order not found");
    }

    @Override
    public void orderStatus(OrderItem orderItem, String status) {
        orderItem.setStatus(status);
        orderItemRepository.save(orderItem);
        if (status.equals("ORDER RETURNED")) {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }
    }

    @Override
    public Map<String, Object> dashboard(User user) {
        double totalEarnings = 0;
        int totalNumberOfItems = 0;
        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    totalNumberOfItems += 1;
                    totalEarnings += orderItem.getPrice() * orderItem.getQuantity() * (ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate()));
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("totalNumberOfItems", totalNumberOfItems);
        result.put("totalEarnings", totalEarnings);
        return result;
    }

    @Override
    public Map<YearMonth, Map<String, Object>> onClickDasboard(User user) {
        Map<YearMonth, Double> totalEarningsByMonth = new HashMap<>();
        Map<YearMonth, Integer> totalItemsByMonth = new HashMap<>();

        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    int quantity = orderItem.getQuantity();
                    double price = orderItem.getPrice();
                    LocalDateTime rentalStartDate = orderItem.getRentalStartDate();
                    LocalDateTime rentalEndDate = orderItem.getRentalEndDate();

                    long rentalDurationInDays = ChronoUnit.DAYS.between(rentalStartDate, rentalEndDate);
                    double earnings = price * quantity * rentalDurationInDays;

                    YearMonth month = YearMonth.from(rentalStartDate);

                    totalEarningsByMonth.put(month, totalEarningsByMonth.getOrDefault(month, 0.0) + earnings);
                    totalItemsByMonth.put(month, totalItemsByMonth.getOrDefault(month, 0) + quantity);
                }
            }
        }

        Map<YearMonth, Map<String, Object>> result = new HashMap<>();
        for (YearMonth month : totalEarningsByMonth.keySet()) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("totalNumberOfItems", totalItemsByMonth.get(month));
            monthData.put("totalEarnings", totalEarningsByMonth.get(month));
            result.put(month, monthData);
        }

        return result;
    }


    public void getRentalPeriods() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        List<Long> rentalPeriods = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            //System.out.println(cartItem);
            LocalDate rentalStartDate = orderItem.getRentalStartDate().toLocalDate();
            LocalDate rentalEndDate = orderItem.getRentalEndDate().toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(rentalStartDate, rentalEndDate);

            //rentalPeriods.add(daysBetween);
            if (daysBetween == 2 || daysBetween == 3 || daysBetween == 1) {
                String email = orderItem.getOrder().getUser().getEmail();
                System.out.println(email);
                String subject = "Reminder: Your rental period is ending soon";
                String message = "Dear " + orderItem.getOrder().getUser().getFirstName() + ",\n" +
                        "This is a reminder that your rental period for the following item will end in " + daysBetween +
                        " days:\n" +
                        //"- " + orderItem.getOrder().getId() + "\n" +
                        "Please return the item before the end of the rental period to avoid any late fees.\n\n" +
                        "Thank you for choosing our rental service.\n\n" +
                        "Best regards,\n" +
                        "The Rental Service Team";
                emailService.sendEmail(subject, message, email);
            }

        }
    }

    @Override
    public Map<YearMonth, List<OrderItem>> getOrderedItemsByMonth(User user) {
        Map<YearMonth, List<OrderItem>> orderedItemsByMonth = new HashMap<>();

        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    LocalDateTime rentalStartDate = orderItem.getRentalStartDate();
                    YearMonth month = YearMonth.from(rentalStartDate);

                    // Retrieve the list of order items for the current month
                    List<OrderItem> monthOrderItems = orderedItemsByMonth.getOrDefault(month, new ArrayList<>());

                    // Add the current order item to the list
                    monthOrderItems.add(orderItem);

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
    public List<ProductDto> getRentedOutProducts(User user) {
        List<Product> products = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    products.add(orderItem.getProduct());
                }
            }
        }
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            productDtos.add(getDtoFromProduct(product));
        }
        return productDtos;
    }


    @Override
    public Document getPdf(User user) throws DocumentException {
        Document document = new Document();
        return document;
    }

    @Override
    public void addContent(Document document, User user) throws DocumentException {

        // Add header
        Font headingFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 30, BaseColor.BLACK);
        Chunk chunkHeading = new Chunk("Leaps", headingFont);
        Paragraph headingParagraph = new Paragraph(chunkHeading);
        headingParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(headingParagraph);
        // Add empty line
        document.add(new Paragraph(" "));

        // Add subheading
        Font subheadingFont = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 18, BaseColor.BLACK);
        Chunk chunkSubheading = new Chunk("Report for " + user.getFirstName() + " " + user.getLastName(), subheadingFont);
        Paragraph subheadingParagraph = new Paragraph(chunkSubheading);
        subheadingParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(subheadingParagraph);

        // Add empty line
        document.add(new Paragraph(" "));

        // Get the dashboard data
        Map<YearMonth, Map<String, Object>> dashboardData = onClickDasboard(user);

        // Determine the number of columns based on the data
        int numColumns = dashboardData.isEmpty() ? 0 : dashboardData.values().iterator().next().size();

        // Create table
        PdfPTable table = new PdfPTable(numColumns + 1); // setting columns

        // Set cell alignment
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        // Add table headers
        Font tableHeaderFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, BaseColor.BLACK);
        PdfPCell cell1 = new PdfPCell(new Phrase("Month", tableHeaderFont));
        PdfPCell cell2 = new PdfPCell(new Phrase("Total Earnings", tableHeaderFont));
        PdfPCell cell3 = new PdfPCell(new Phrase("Number of Items Sold", tableHeaderFont));
        setCellPadding(cell1);
        setCellPadding(cell2);
        setCellPadding(cell3);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);


        // Add the total earnings and total number of items sold per month to the document
        for (Map.Entry<YearMonth, Map<String, Object>> entry : dashboardData.entrySet()) {
            YearMonth month = entry.getKey();
            Map<String, Object> monthData = entry.getValue();


            String monthString = month.toString();
            String earnings = monthData.get("totalEarnings").toString();
            String numberOfItems = monthData.get("totalNumberOfItems").toString();


            table.addCell(monthString);
            table.addCell(earnings);
            table.addCell(numberOfItems);
        }
        document.add(table);
    }

    @Override
    public OrderItem getOrderItem(Long orderItemId, User user) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(orderItemId);
        if (optionalOrderItem.isPresent()) {
            return optionalOrderItem.get();
        }
        return null;
    }

    private void setCellPadding(PdfPCell cell) {
        cell.setPadding(6);
    }


}
