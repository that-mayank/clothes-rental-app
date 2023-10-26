package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;



@RestController
@RequestMapping("/api/v1/cart")
@AllArgsConstructor
@Api(tags = "Cart Api", description = "Contains api for adding products, updating products, list products and delete products in the cart")
@SuppressWarnings("deprecation")
@Slf4j
public class CartController {


    // Linking layers using constructor injection
    private final CartServiceInterface cartService;
    private final ProductServiceInterface productService;
    private final Helper helper;



    //API - Allows the user to Add products to cart
    @ApiOperation(value = "Add new product to cart")
    @PostMapping(value = "/add" , consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> addToCart(@RequestBody @Valid AddToCartDto addToCartDto, HttpServletRequest request) throws AuthenticationFailException, ProductNotExistException, QuantityOutOfBoundException {

        User user = helper.getUserFromToken(request);

        try {
            log.info("Adding product to cart: ProductId={}, User={}", addToCartDto.getProductId(), user.getEmail());
            Product product = productService.getProductById(addToCartDto.getProductId());
            cartService.addToCart(addToCartDto, product, user);
            log.info("Product added to cart successfully: ProductId={}, User={}", addToCartDto.getProductId(), user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Added to cart"), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error adding product to cart: ProductId={}, User={}", addToCartDto.getProductId(), user.getEmail(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Failed to add to cart"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //API - Allows the user to Get all products of cart
    @ApiOperation(value = "List products of cart")
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CartDto> getCartItems(HttpServletRequest request) throws AuthenticationFailException {
        User user = helper.getUserFromToken(request);

        try {
            log.info("Listing cart items for User={}", user.getEmail());
            CartDto cartDto = cartService.listCartItems(user);
            log.info("Cart items listed successfully for User={}", user.getEmail());
            return new ResponseEntity<>(cartDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error listing cart items for User={}", user.getEmail(), e);
            // Return an empty CartDto and INTERNAL_SERVER_ERROR status in case of an exception
            return new ResponseEntity<>(new CartDto(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //API - Allows the user to update the cart items
    @ApiOperation(value = "Update product in cart")
    @PutMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_VALUE) //productId
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> updateCartItem(@RequestBody @Valid AddToCartDto addToCartDto, HttpServletRequest request) throws AuthenticationFailException {
        User user = helper.getUserFromToken(request);

        try {
            log.info("Updating cart item: ProductId={}, User={}", addToCartDto.getProductId(), user.getEmail());
            cartService.updateCartItem(addToCartDto, user);
            log.info("Cart item has been updated successfully: ProductId={}, User={}", addToCartDto.getProductId(), user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Cart item has been updated"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating cart item: ProductId={}, User={}", addToCartDto.getProductId(), user.getEmail(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Failed to update cart item"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API - Allows the user to remove items from cart
    @ApiOperation(value = "Delete product from cart")
    @DeleteMapping(value = "/delete/{productId}") //productId
    @PreAuthorize("hasAnyAuthority( 'OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable("productId") Long productId, HttpServletRequest request) throws AuthenticationFailException {
        User user = helper.getUserFromToken(request);

        try {
            log.info("Deleting cart item: ProductId={}, User={}", productId, user.getEmail());
            cartService.deleteCartItem(productId, user);
            log.info("Item has been removed from cart successfully: ProductId={}, User={}", productId, user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Item has been removed from cart successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting cart item: ProductId={}, User={}", productId, user.getEmail(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Failed to delete cart item"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API - Allows the user to update quantity of individual products in the cart
    @ApiOperation(value = "Update product quantity in cart")
    @PutMapping(value = "/updateQuantity",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> updateQuantity(@RequestBody @Valid UpdateProductQuantityDto updateProductQuantityDto, HttpServletRequest request) {
        User user = helper.getUserFromToken(request);

        try {
            log.info("Updating product quantity in cart: ProductId={}, User={}", updateProductQuantityDto.getProductId(), user.getEmail());
            cartService.updateProductQuantity(updateProductQuantityDto, user);
            log.info("Product quantity has been updated successfully: ProductId={}, User={}", updateProductQuantityDto.getProductId(), user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Product quantity has been updated successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating product quantity in cart: ProductId={}, User={}", updateProductQuantityDto.getProductId(), user.getEmail(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Failed to update product quantity in cart"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
