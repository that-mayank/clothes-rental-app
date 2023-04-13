package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.exceptions.CartItemAlreadyExistException;
import com.nineleaps.leaps.exceptions.CartItemNotExistException;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.CartRepository;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartService implements CartServiceInterface {
    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    private static CartItemDto getDtoFromCart(Cart cart) {
        return new CartItemDto(cart);
    }

    @Override
    public void addToCart(AddToCartDto addToCartDto, Product product, User user) throws CartItemAlreadyExistException {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (Helper.notNull(cartItem)) {
            throw new CartItemAlreadyExistException("Product is already in the Cart: " + product.getId());
        }
        Cart cart = new Cart(product, user, addToCartDto.getQuantity());
        cartRepository.save(cart);
    }

    @Override
    public CartDto listCartItems(User user) {
        List<Cart> cartList = cartRepository.findAllByUserOrderByCreateDateDesc(user);
        List<CartItemDto> cartItems = new ArrayList<>();
        for (Cart cart : cartList) {
            CartItemDto cartItemDto = getDtoFromCart(cart);
            cartItems.add(cartItemDto);
        }
        double totalCost = 0;
        for (CartItemDto cartItemDto : cartItems) {
            totalCost += (cartItemDto.getProduct().getPrice() * cartItemDto.getQuantity());
        }
        return new CartDto(cartItems, totalCost);
    }

    @Override
    public void updateCartItem(AddToCartDto addToCartDto, User user) throws CartItemNotExistException {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), addToCartDto.getProductId());
        if (!Helper.notNull(cartItem)) {
            throw new CartItemNotExistException("Cart Item is invalid: " + addToCartDto.getProductId());
        }
        if (addToCartDto.getQuantity() == 0) {
            deleteCartItem(addToCartDto.getProductId(), user);
            return;
        }
        cartItem.setQuantity(addToCartDto.getQuantity());
        cartItem.setCreateDate(new Date());
        cartRepository.save(cartItem);
    }

    @Override
    public void deleteCartItem(Long productId, User user) throws CartItemNotExistException {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), productId);
        if (!Helper.notNull(cartItem)) {
            throw new CartItemNotExistException("Cart Item is invalid: " + productId);
        }
        cartRepository.deleteById(cartItem.getId());
    }
}
