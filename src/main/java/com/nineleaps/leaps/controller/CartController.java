package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.service.CartServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/cart")
@AllArgsConstructor
@Validated
@Api(tags = "Cart Api")
public class CartController {

    //Linking layers using constructor injection
    private final CartServiceInterface cartService;

    // API : To add products to cart for particular user
    @ApiOperation(value = "API : To add products to cart for particular user")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('BORROWER')")
    // Validate the addToCartDto object
    public ResponseEntity<ApiResponse> addToCart(@RequestBody @Valid AddToCartDto addToCartDto, HttpServletRequest request) {
        // Calling service layer to add product to cart
        cartService.addToCart(addToCartDto, request);
        return new ResponseEntity<>(new ApiResponse(true, "Added to cart"), HttpStatus.CREATED);
    }

    // API : To list products of cart for particular user
    @ApiOperation(value = "API : To list products of cart for particular user")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<CartDto> getCartItems(HttpServletRequest request) {
        // Calling service layer to get all products from cart
        CartDto cartDto = cartService.listCartItems(request);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    // API : To remove product from cart for particular user
    @ApiOperation(value = "API : To remove product from cart for particular user")
    @DeleteMapping("{productId}")
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable("productId") Long productId, HttpServletRequest request) {
        // Calling service layer to remove product from cart
        cartService.deleteCartItem(productId, request);
        return new ResponseEntity<>(new ApiResponse(true, "Item has been removed from cart successfully"), HttpStatus.NO_CONTENT);
    }

    // API : To update product quantity in cart for particular user
    @ApiOperation(value = "API : To update products quantity in cart for particular user")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('BORROWER')")
    // Validate the updateProductQuantityDto object
    public ResponseEntity<ApiResponse> updateQuantity(@RequestBody @Valid UpdateProductQuantityDto updateProductQuantityDto, HttpServletRequest request) {
        // Calling service layer to update product quantity in cart
        cartService.updateProductQuantity(updateProductQuantityDto, request);
        return new ResponseEntity<>(new ApiResponse(true, "Product quantity has been updated successfully"), HttpStatus.OK);
    }
}
