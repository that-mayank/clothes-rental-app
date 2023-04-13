package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;

public interface CartServiceInterface {

    public void addToCart(AddToCartDto addToCartDto, Product product, User user);

    public CartDto listCartItems(User user);

    public void updateCartItem(AddToCartDto addToCartDto, User user);

    public void deleteCartItem(Long productId, User user);
}
