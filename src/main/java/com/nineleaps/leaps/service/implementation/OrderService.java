package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.checkout.CheckoutItemDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderService implements OrderServiceInterface {
    private final CartServiceInterface cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    //This way we can access attributes from application.properties
    @Value("${BASE_URL}")
    private String baseURL;
    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    @Autowired
    public OrderService(CartServiceInterface cartService, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.cartService = cartService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Session createSession(List<CheckoutItemDto> checkoutItemDtoList) throws StripeException {

        //success and failure urls for stripe --> they are the mappings of the pages in frontend
        String successURL = baseURL + "payment/success";
        String failureURL = baseURL + "payment/failure";

        //set stripe secret key
        Stripe.apiKey = apiKey;

        List<SessionCreateParams.LineItem> sessionItemsLine = new ArrayList<>();

        //for each product compute SessionCreateParams.LineItem
        for (CheckoutItemDto checkoutItemDto : checkoutItemDtoList) {
            sessionItemsLine.add(createSessionLineItem(checkoutItemDto));
        }
        //build session param
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failureURL)
                .addAllLineItem(sessionItemsLine)
                .setSuccessUrl(successURL)
                .build();
        return Session.create(params);
    }

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
            OrderItem orderItem = new OrderItem(cartItemDto.getQuantity(), cartItemDto.getProduct().getPrice(), newOrder, cartItemDto.getProduct(), cartItemDto.getRentalStartDate(), cartItemDto.getRentalEndDate());
            orderItemRepository.save(orderItem);
        }
        //delete cart items after placing order
        cartService.deleteUserCartItems(user);
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


    //build each product in stripe checkout page
    private SessionCreateParams.LineItem createSessionLineItem(CheckoutItemDto checkoutItemDto) {
        return SessionCreateParams.LineItem.builder()
                //set price for each product
                .setPriceData(createPriceData(checkoutItemDto))
                //set quantity for each product
                .setQuantity(Long.parseLong(String.valueOf(checkoutItemDto.getQuantity())))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(CheckoutItemDto checkoutItemDto) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("inr")
                .setUnitAmount((long) (checkoutItemDto.getPrice() * 100))
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(checkoutItemDto.getProductName())
                                .build())
                .build();
    }
}
