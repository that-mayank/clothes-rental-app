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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RestController
@RequestMapping("/api/v1/cart")
@AllArgsConstructor
@Validated
@Api(tags = "Cart Api")

public class CartController {

    //Linking layers using constructor injection

    private final CartServiceInterface cartService;
    private final ProductServiceInterface productService;
    private final Helper helper;

    // API : To add products to cart for particular user

    @ApiOperation(value = "API : To add products to cart for particular user")
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('BORROWER')")

    // Validate the addToCartDto object

    public ResponseEntity<ApiResponse> addToCart(@RequestBody @Valid AddToCartDto addToCartDto, HttpServletRequest request) throws ProductNotExistException {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);


        // Retrieve product from id

        Product product = productService.getProductById(addToCartDto.getProductId());

        // Calling service layer to add product to cart

        cartService.addToCart(addToCartDto, product, user);
        return new ResponseEntity<>(new ApiResponse(true, "Added to cart"), HttpStatus.CREATED);
    }

    // API : To list products of cart for particular user

    @ApiOperation(value = "API : To list products of cart for particular user")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")

    public ResponseEntity<CartDto> getCartItems(HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Calling service layer to get all products from cart

        CartDto cartDto = cartService.listCartItems(user);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    // API : To remove product from cart for particular user

    @ApiOperation(value = "API : To remove product from cart for particular user")
    @DeleteMapping("/delete/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('BORROWER')")

    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable("productId") Long productId, HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Calling service layer to remove product from cart

        cartService.deleteCartItem(productId, user);
        return new ResponseEntity<>(new ApiResponse(true, "Item has been removed from cart successfully"), HttpStatus.NO_CONTENT);
    }

    // API : To update product quantity in cart for particular user

    @ApiOperation(value = "API : To update products quantity in cart for particular user")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")

    // Validate the updateProductQuantityDto object

    public ResponseEntity<ApiResponse> updateQuantity(@RequestBody @Valid UpdateProductQuantityDto updateProductQuantityDto, HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Calling service layer to update product quantity in cart

        cartService.updateProductQuantity(updateProductQuantityDto, user);
        return new ResponseEntity<>(new ApiResponse(true, "Product quantity has been updated successfully"), HttpStatus.OK);
    }
}
