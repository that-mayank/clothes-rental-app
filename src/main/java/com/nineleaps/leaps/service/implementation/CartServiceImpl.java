package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.CartItemAlreadyExistException;
import com.nineleaps.leaps.exceptions.CartItemNotExistException;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.CartRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nineleaps.leaps.config.MessageStrings.CART_ITEM_INVALID;

@Service
@AllArgsConstructor
@Transactional
@Slf4j // Add SLF4J annotation for logging
public class CartServiceImpl implements CartServiceInterface {
    private final CartRepository cartRepository;
    private final Helper helper;
    private final ProductServiceInterface productService;

    // Helper method to convert Cart entity to CartItemDto
    static CartItemDto getDtoFromCart(Cart cart) {
        return new CartItemDto(cart);
    }

    @Override
    public void addToCart(AddToCartDto addToCartDto, HttpServletRequest request) {
        User user = helper.getUserFromToken(request);
        Product product = productService.getProductById(addToCartDto.getProductId());
        log.info("Adding product with ID {} to the cart for user: {}", product.getId(), user.getEmail());

        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
        Optional.ofNullable(cartItem)
                .ifPresent(item -> {
                    log.error("Product with ID {} is already in the Cart for user: {}", product.getId(), user.getEmail());
                    throw new CartItemAlreadyExistException("Product is already in the Cart: " + product.getId());
                });

        Cart cart = new Cart(product, user, addToCartDto.getQuantity(), addToCartDto.getRentalStartDate(), addToCartDto.getRentalEndDate(), product.getImageURL());
        cartRepository.save(cart);

        log.info("Product with ID {} added to the cart for user: {}", product.getId(), user.getEmail());
    }

    @Override
    public CartDto listCartItems(HttpServletRequest request) {
        User user = helper.getUserFromToken(request);
        log.info("Listing cart items for user: {}", user.getEmail());

        List<Cart> cartList = cartRepository.findAllByUserOrderByCreateDateDesc(user);
        List<CartItemDto> cartItems = new ArrayList<>();

        for (Cart cart : cartList) {
            CartItemDto cartItemDto = getDtoFromCart(cart);
            cartItems.add(cartItemDto);
        }

        double totalCost = 0;

        for (CartItemDto cartItemDto : cartItems) {
            long numberOfHours = 0;
            numberOfHours = ChronoUnit.HOURS.between(cartItemDto.getRentalStartDate(), cartItemDto.getRentalEndDate());

            if (numberOfHours == 0)
                numberOfHours = 1; // Minimum 1-hour rental

            double perHourRent = cartItemDto.getProduct().getPrice() / 24;
            totalCost += (perHourRent * cartItemDto.getQuantity() * numberOfHours);
        }

        double tax = 0.18 * totalCost;
        double finalPrice = totalCost + tax;

        log.info("Listed {} cart items for user: {} with a total cost of {} and a final price of {}",
                cartItems.size(), user.getEmail(), totalCost, finalPrice);

        return new CartDto(cartItems, totalCost, Math.round(tax), Math.round(finalPrice), user.getId());
    }

    @Override
    public void deleteCartItem(Long productId, HttpServletRequest request) throws CartItemNotExistException {
        User user = helper.getUserFromToken(request);
        log.info("Deleting cart item with product ID {} for user: {}", productId, user.getEmail());

        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), productId);

        if (!Helper.notNull(cartItem)) {
            log.error("Cart item with product ID {} does not exist for user: {}", productId, user.getEmail());
            throw new CartItemNotExistException(CART_ITEM_INVALID + productId);
        }

        cartRepository.deleteById(cartItem.getId());

        log.info("Deleted cart item with product ID {} for user: {}", productId, user.getEmail());
    }

    @Override
    public void deleteUserCartItems(User user) {
        log.info("Deleting all cart items for user: {}", user.getEmail());
        cartRepository.deleteByUser(user);
    }

    @Override
    public void updateProductQuantity(UpdateProductQuantityDto updateProductQuantityDto, HttpServletRequest request)
            throws CartItemNotExistException {
        User user = helper.getUserFromToken(request);
        log.info("Updating quantity for product with ID {} in the cart for user: {}", updateProductQuantityDto.getProductId(), user.getEmail());

        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId());

        if (!Helper.notNull(cartItem)) {
            log.error("Cart item with product ID {} does not exist for user: {}", updateProductQuantityDto.getProductId(), user.getEmail());
            throw new CartItemNotExistException(CART_ITEM_INVALID + updateProductQuantityDto.getProductId());
        }

        if (updateProductQuantityDto.getQuantity() <= 0) {
            log.info("Deleting cart item with product ID {} as the updated quantity is zero or less for user: {}", updateProductQuantityDto.getProductId(), user.getEmail());
            deleteCartItem(updateProductQuantityDto.getProductId(), request);
            return;
        }

        cartItem.setQuantity(updateProductQuantityDto.getQuantity());
        cartRepository.save(cartItem);

        log.info("Updated quantity for product with ID {} in the cart for user: {}", updateProductQuantityDto.getProductId(), user.getEmail());
    }
}
