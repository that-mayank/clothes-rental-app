package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.AuthenticationServiceInterface;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartServiceInterface cartService;
    private final AuthenticationServiceInterface authenticationService;
    private final ProductServiceInterface productService;

    @Autowired
    public CartController(CartServiceInterface cartService, AuthenticationServiceInterface authenticationService, ProductServiceInterface productService) {
        this.cartService = cartService;
        this.authenticationService = authenticationService;
        this.productService = productService;
    }

    //Add to cart
    @PostMapping("/add") //change the code accordingly so that duplicate items cannot be added *Done*
    public ResponseEntity<ApiResponse> addToCart(@RequestBody AddToCartDto addToCartDto, @RequestParam("token") String token) throws AuthenticationFailException, ProductNotExistException {
        //authenticate token is valid or not
        authenticationService.authenticate(token);
        //get user using token
        User user = authenticationService.getUser(token);
        //get product
        Product product = productService.getProductById(addToCartDto.getProductId());
        //add to cart
        cartService.addToCart(addToCartDto, product, user);
        return new ResponseEntity<>(new ApiResponse(true, "Added to cart"), HttpStatus.CREATED);
    }

    //Get products of cart
    @GetMapping("/list")
    public ResponseEntity<CartDto> getCartItems(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        CartDto cartDto = cartService.listCartItems(user);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    //update the cart
    @PutMapping("/update") //productId
    public ResponseEntity<ApiResponse> updateCartItem(@RequestBody @Valid AddToCartDto addToCartDto, @RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        cartService.updateCartItem(addToCartDto, user);
        return new ResponseEntity<>(new ApiResponse(true, "Cart item has been updated"), HttpStatus.OK);
    }

    //remove from cart
    @DeleteMapping("/delete/{productId}") //productId
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable("productId") Long productId, @RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        cartService.deleteCartItem(productId, user);
        return new ResponseEntity<>(new ApiResponse(true, "Item has been removed from cart successfully"), HttpStatus.OK);
    }
}
