package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.model.User;

import javax.servlet.http.HttpServletRequest;

public interface CartServiceInterface {

    void addToCart(AddToCartDto addToCartDto, HttpServletRequest request);

    CartDto listCartItems(HttpServletRequest request);

    void deleteCartItem(Long productId, HttpServletRequest request);

    void deleteUserCartItems(User user);

    void updateProductQuantity(UpdateProductQuantityDto updateProductQuantityDto, HttpServletRequest request);
}
