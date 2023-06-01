package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.CartItemAlreadyExistException;
import com.nineleaps.leaps.exceptions.CartItemNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.CartRepository;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartServiceInterface {
    private final CartRepository cartRepository;
    private final ProductServiceInterface productService;
    private final ProductRepository productRepository;

    private static CartItemDto getDtoFromCart(Cart cart) {
        return new CartItemDto(cart);
    }

    @Override
    public void addToCart(AddToCartDto addToCartDto, Product product, User user) throws CartItemAlreadyExistException, QuantityOutOfBoundException {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (Helper.notNull(cartItem)) {
            throw new CartItemAlreadyExistException("Product is already in the Cart: " + product.getId());
        }

        Cart cart = new Cart(product, user, addToCartDto.getQuantity(), addToCartDto.getRentalStartDate(), addToCartDto.getRentalEndDate(), product.getImageURL());
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
            long numberOfHours = 0;
            if (Helper.notNull(cartItemDto.getRentalStartDate()) & Helper.notNull(cartItemDto.getRentalEndDate())) {
                numberOfHours = ChronoUnit.HOURS.between(cartItemDto.getRentalStartDate(), cartItemDto.getRentalEndDate()); // calculate using rental dates

                if (numberOfHours == 0)
                    numberOfHours = 1; // if the product is rented for less than 1 hour ==> 1 hour rented will be shown in total cost calculation
            }
            double perHourRent = cartItemDto.getProduct().getPrice() / 24;
            totalCost += (perHourRent * cartItemDto.getQuantity() * numberOfHours);
        }
        double tax = 0.18 * totalCost;
        double finalPrice = totalCost + tax;
        return new CartDto(cartItems, totalCost, tax, finalPrice);
    }

    @Override
    public void updateCartItem(AddToCartDto addToCartDto, User user) throws CartItemNotExistException, QuantityOutOfBoundException {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), addToCartDto.getProductId());
        if (!Helper.notNull(cartItem)) {
            throw new CartItemNotExistException("Cart Item is invalid: " + addToCartDto.getProductId());
        }
        if (addToCartDto.getQuantity() == 0) {
            deleteCartItem(addToCartDto.getProductId(), user);
            return;
        }

        //reduce quantity from product
//        Product product = productService.getProductById(addToCartDto.getProductId());
//        int quantity = product.getQuantity() - addToCartDto.getQuantity();
//        if (quantity < 0) {
//            throw new QuantityOutOfBoundException("Selected quantity " + addToCartDto.getQuantity() + " is more than available quantity " + product.getQuantity());
//        }
//        product.setQuantity(quantity);
//        productRepository.save(product);

        cartItem.setQuantity(addToCartDto.getQuantity());
        cartItem.setCreateDate(new Date());
        cartItem.setRentalStartDate(addToCartDto.getRentalStartDate());
        cartItem.setRentalEndDate(addToCartDto.getRentalEndDate());
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

    @Override
    public void deleteUserCartItems(User user) {
        cartRepository.deleteByUser(user);
    }

    @Override
    public void updateProductQuantity(UpdateProductQuantityDto updateProductQuantityDto, User user) throws CartItemNotExistException {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId());
        if (!Helper.notNull(cartItem)) {
            throw new CartItemNotExistException("Cart Item is invalid: " + updateProductQuantityDto.getProductId());
        }
        //if quantity is zero delete cart item
        if (updateProductQuantityDto.getQuantity() <= 0) {
            deleteCartItem(updateProductQuantityDto.getProductId(), user);
            return;
        }
        cartItem.setQuantity(updateProductQuantityDto.getQuantity());
        cartRepository.save(cartItem);
    }
}
