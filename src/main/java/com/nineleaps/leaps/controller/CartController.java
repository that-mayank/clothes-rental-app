package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RestController
@RequestMapping("/api/v1/cart")
@AllArgsConstructor
@Api(tags = "Cart Api", description = "Contains api for adding products, updating products, list products and delete products in the cart")
@SuppressWarnings("deprecation")
public class CartController {
    private final CartServiceInterface cartService;
    private final ProductServiceInterface productService;
    private final Helper helper;
    private final OrderItemRepository orderItemRepository;


    //Add to cart
    @ApiOperation(value = "Add new product to cart")
    @PostMapping("/add") //change the code accordingly so that duplicate items cannot be added *Done*
    public ResponseEntity<ApiResponse> addToCart(@RequestBody AddToCartDto addToCartDto, HttpServletRequest request) throws AuthenticationFailException, ProductNotExistException, QuantityOutOfBoundException {
        //authenticate token is valid or not
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //get product
        Product product = productService.getProductById(addToCartDto.getProductId());

        //add to cart
        cartService.addToCart(addToCartDto, product, user);
        return new ResponseEntity<>(new ApiResponse(true, "Added to cart"), HttpStatus.CREATED);
    }

    //Get products of cart
    @ApiOperation(value = "List products of cart")
    @GetMapping("/list")
    public ResponseEntity<CartDto> getCartItems(HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        CartDto cartDto = cartService.listCartItems(user);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    //update the cart
    @ApiOperation(value = "Update product in cart")
    @PutMapping("/update") //productId
    public ResponseEntity<ApiResponse> updateCartItem(@RequestBody @Valid AddToCartDto addToCartDto, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        cartService.updateCartItem(addToCartDto, user);
        return new ResponseEntity<>(new ApiResponse(true, "Cart item has been updated"), HttpStatus.OK);
    }

    //remove from cart
    @ApiOperation(value = "Delete product from cart")
    @DeleteMapping("/delete/{productId}") //productId
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable("productId") Long productId, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        cartService.deleteCartItem(productId, user);
        return new ResponseEntity<>(new ApiResponse(true, "Item has been removed from cart successfully"), HttpStatus.OK);
    }

    //update cart quantity
    @PutMapping("/updateQuantity")
    public ResponseEntity<ApiResponse> updateQuantity(@RequestBody @Valid UpdateProductQuantityDto updateProductQuantityDto, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        cartService.updateProductQuantity(updateProductQuantityDto, user);
        return new ResponseEntity<>(new ApiResponse(true, "Product quantity has been updated successfully"), HttpStatus.OK);
    }
}
