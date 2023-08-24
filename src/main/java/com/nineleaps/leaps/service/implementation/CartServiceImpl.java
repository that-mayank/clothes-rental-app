package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.CartItemAlreadyExistException;
import com.nineleaps.leaps.exceptions.CartItemNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.CartRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nineleaps.leaps.config.MessageStrings.CART_ITEM_INVALID;

@Service
@AllArgsConstructor
@Transactional
public class CartServiceImpl implements CartServiceInterface {
    private final CartRepository cartRepository;

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
            if (Helper.notNull(cartItemDto.getRentalStartDate()) && Helper.notNull(cartItemDto.getRentalEndDate())) {
                numberOfHours = ChronoUnit.HOURS.between(cartItemDto.getRentalStartDate(), cartItemDto.getRentalEndDate()); // calculate using rental dates

                if (numberOfHours == 0)
                    numberOfHours = 1; // if the product is rented for less than 1 hour ==> 1 hour rented will be shown in total cost calculation
            }
            double perHourRent = cartItemDto.getProduct().getPrice() / 24;
            totalCost += (perHourRent * cartItemDto.getQuantity() * numberOfHours);
        }
        double tax = 0.18 * totalCost;
        double finalPrice = totalCost + tax;
        return new CartDto(cartItems, totalCost, Math.round(tax), Math.round(finalPrice), user.getId());
    }

    @Override
    public void updateCartItem(AddToCartDto addToCartDto, User user) throws CartItemNotExistException, QuantityOutOfBoundException {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), addToCartDto.getProductId());
        if (!Helper.notNull(cartItem)) {
            throw new CartItemNotExistException(CART_ITEM_INVALID + addToCartDto.getProductId());
        }
        if (addToCartDto.getQuantity() == 0) {
            deleteCartItem(addToCartDto.getProductId(), user);
            return;
        }

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
            throw new CartItemNotExistException(CART_ITEM_INVALID + productId);
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
            throw new CartItemNotExistException(CART_ITEM_INVALID + updateProductQuantityDto.getProductId());
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
