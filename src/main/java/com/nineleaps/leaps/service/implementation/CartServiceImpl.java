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
import java.util.List;

import static com.nineleaps.leaps.config.MessageStrings.CART_ITEM_INVALID;

@Service
@AllArgsConstructor
@Transactional
public class CartServiceImpl implements CartServiceInterface {
    private final CartRepository cartRepository;

    // Helper method to convert Cart entity to CartItemDto
    private static CartItemDto getDtoFromCart(Cart cart) {
        return new CartItemDto(cart);
    }

    // Add a product to the user's cart
    @Override
    public void addToCart(AddToCartDto addToCartDto, Product product, User user)  {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (Helper.notNull(cartItem)) {
            throw new CartItemAlreadyExistException("Product is already in the Cart: " + product.getId());
        }

        // Create a new Cart entity with the provided details and save it to the cart repository
        Cart cart = new Cart(product, user, addToCartDto.getQuantity(), addToCartDto.getRentalStartDate(), addToCartDto.getRentalEndDate(), product.getImageURL());
        cartRepository.save(cart);
    }

    // List all items in the user's cart
    @Override
    public CartDto listCartItems(User user) {
        List<Cart> cartList = cartRepository.findAllByUserOrderByCreateDateDesc(user);
        List<CartItemDto> cartItems = new ArrayList<>();

        // Iterate through cart items and create CartItemDto objects
        for (Cart cart : cartList) {
            CartItemDto cartItemDto = getDtoFromCart(cart);
            cartItems.add(cartItemDto);
        }

        double totalCost = 0;

        // Calculate the total cost, considering rental durations and quantities
        for (CartItemDto cartItemDto : cartItems) {
            long numberOfHours = 0;
            if (Helper.notNull(cartItemDto.getRentalStartDate()) && Helper.notNull(cartItemDto.getRentalEndDate())) {
                numberOfHours = ChronoUnit.HOURS.between(cartItemDto.getRentalStartDate(), cartItemDto.getRentalEndDate());

                if (numberOfHours == 0)
                    numberOfHours = 1; // Minimum 1-hour rental

                double perHourRent = cartItemDto.getProduct().getPrice() / 24;
                totalCost += (perHourRent * cartItemDto.getQuantity() * numberOfHours);
            }
        }

        // Calculate tax and final price
        double tax = 0.18 * totalCost;
        double finalPrice = totalCost + tax;

        // Create and return a CartDto with cart items and total cost details
        return new CartDto(cartItems, totalCost, Math.round(tax), Math.round(finalPrice), user.getId());
    }

    // Delete a cart item by product ID
    @Override
    public void deleteCartItem(Long productId, User user) throws CartItemNotExistException {
        // Find the cart item associated with the specified product and user
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), productId);

        // Check if the cart item exists
        if (!Helper.notNull(cartItem)) {
            throw new CartItemNotExistException(CART_ITEM_INVALID + productId);
        }

        // Delete the cart item from the repository
        cartRepository.deleteById(cartItem.getId());
    }

    // Delete all cart items for a user
    @Override
    public void deleteUserCartItems(User user) {
        cartRepository.deleteByUser(user);
    }

    // Update the quantity of a product in the cart
    @Override
    public void updateProductQuantity(UpdateProductQuantityDto updateProductQuantityDto, User user)
            throws CartItemNotExistException {
        // Find the cart item associated with the specified product and user
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId());

        // Check if the cart item exists
        if (!Helper.notNull(cartItem)) {
            throw new CartItemNotExistException(CART_ITEM_INVALID + updateProductQuantityDto.getProductId());
        }

        // If the updated quantity is zero or less, delete the cart item
        if (updateProductQuantityDto.getQuantity() <= 0) {
            deleteCartItem(updateProductQuantityDto.getProductId(), user);
            return;
        }

        // Update the quantity of the cart item and save it to the repository
        cartItem.setQuantity(updateProductQuantityDto.getQuantity());
        cartRepository.save(cartItem);
    }
}
