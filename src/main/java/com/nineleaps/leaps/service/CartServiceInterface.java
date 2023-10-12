package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.product.Product;

public interface CartServiceInterface {

    void addToCart(AddToCartDto addToCartDto, Product product, User user);

    CartDto listCartItems(User user);

    void deleteCartItem(Long productId, User user);

    void deleteUserCartItems(User user);

    void updateProductQuantity(UpdateProductQuantityDto updateProductQuantityDto, User user);
}
