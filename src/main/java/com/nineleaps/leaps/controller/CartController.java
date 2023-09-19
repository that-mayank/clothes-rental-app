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
import com.nineleaps.leaps.repository.OrderItemRepository;
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
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RestController
@RequestMapping("/api/v1/cart")
@AllArgsConstructor
@Api(tags = "Cart Api", description = "Contains api for adding products, updating products, list products and delete products in the cart")
@SuppressWarnings("deprecation")
public class CartController {


    // Linking layers using constructor injection
    private final CartServiceInterface cartService;
    private final ProductServiceInterface productService;
    private final Helper helper;



    //API - Allows the user to Add products  to cart
    @ApiOperation(value = "Add new product to cart")
    @PostMapping(value = "/add" , consumes = MediaType.APPLICATION_JSON_VALUE) //change the code accordingly so that duplicate items cannot be added *Done*
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC - Role Based Access Control
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> addToCart(@RequestBody @Valid AddToCartDto addToCartDto, HttpServletRequest request) throws AuthenticationFailException, ProductNotExistException, QuantityOutOfBoundException {

        // Fetch token form header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

        // Extract user from token
        User user = helper.getUser(token);

        // Calling service layer to get product
        Product product = productService.getProductById(addToCartDto.getProductId());

        // Calling service layer to add products to cart
        cartService.addToCart(addToCartDto, product, user);

        // Status Code - 201-HttpStatus.CREATED
        return new ResponseEntity<>(new ApiResponse(true, "Added to cart"), HttpStatus.CREATED);
    }


    //API - Allows the user to Get all products of cart
    @ApiOperation(value = "List products of cart")
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC - Role Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CartDto> getCartItems(HttpServletRequest request) throws AuthenticationFailException {
        // Fetch token from header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

        // Extract user from token
        User user = helper.getUser(token);

        // Calling service layer to list cart items
        CartDto cartDto = cartService.listCartItems(user);

        // Status code - 200-HttpStatus.OK
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    //API - Allows the user to update the cart items
    @ApiOperation(value = "Update product in cart")
    @PutMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_VALUE) //productId
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC - Role Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> updateCartItem(@RequestBody @Valid AddToCartDto addToCartDto, HttpServletRequest request) throws AuthenticationFailException {
        // Fetch token from headers
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

        // Extract user from token
        User user = helper.getUser(token);

        // Calling service layer for updating cart item
        cartService.updateCartItem(addToCartDto, user);

        // Status code - 200-HttpStatus.OK
        return new ResponseEntity<>(new ApiResponse(true, "Cart item has been updated"), HttpStatus.OK);
    }

    // API - Allows the user to remove items from cart
    @ApiOperation(value = "Delete product from cart")
    @DeleteMapping(value = "/delete/{productId}") //productId
    @PreAuthorize("hasAnyAuthority( 'OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC - Role Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable("productId") Long productId, HttpServletRequest request) throws AuthenticationFailException {
        // Fetch token from user
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        // Extract user from token
        User user = helper.getUser(token);

        // Calling service layer to delete items from cart
        cartService.deleteCartItem(productId, user);

        // Status code - 200-HttpStatus.OK
        return new ResponseEntity<>(new ApiResponse(true, "Item has been removed from cart successfully"), HttpStatus.OK);
    }

    // API - Allows the user to update quantity of individual products in the cart
    @ApiOperation(value = "Update product quantity in cart")
    @PutMapping(value = "/updateQuantity",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> updateQuantity(@RequestBody @Valid UpdateProductQuantityDto updateProductQuantityDto, HttpServletRequest request) {
        // Fetch token from header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

        // Extract user from token
        User user = helper.getUser(token);

        // Calling service layer to update product's quantity in the cart
        cartService.updateProductQuantity(updateProductQuantityDto, user);

        // Status code - 200-HttpStatus.OK
        return new ResponseEntity<>(new ApiResponse(true, "Product quantity has been updated successfully"), HttpStatus.OK);
    }
}
