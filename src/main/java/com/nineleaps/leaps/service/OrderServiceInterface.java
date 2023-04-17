package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.checkout.CheckoutItemDto;
import com.nineleaps.leaps.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import java.util.List;

public interface OrderServiceInterface {
    public Session createSession(List<CheckoutItemDto> checkoutItemDtoList) throws StripeException;

    public void placeOrder(User user, String sessionId);
}
