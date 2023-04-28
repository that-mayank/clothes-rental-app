package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.service.AuthenticationServiceInterface;
import com.nineleaps.leaps.service.implementation.ProductService;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.service.WishlistServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistServiceInterface wishlistService;
    private final AuthenticationServiceInterface authenticationService;
    private final ProductServiceInterface productService;


    //Add product to Wishlist
    //change Product to ProductDto
    //product id and user id required for wishlist
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addWishlist(@RequestBody Product product, @RequestParam("token") String token) {
        //check if token is valid
        authenticationService.authenticate(token);
        //get the user
        User user = authenticationService.getUser(token);
        //check if product is valid
        Optional<Product> optionalProduct = productService.readProduct(product.getId());
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }
        //add to wishlist
        Wishlist wishlist = new Wishlist(product, user);
        wishlistService.createWishlist(wishlist);
        return new ResponseEntity<>(new ApiResponse(true, "Add to wishlist"), HttpStatus.CREATED);
    }

    //Get all items of Wishlist
    @GetMapping("/{token}")
    public ResponseEntity<List<ProductDto>> getWishlist(@PathVariable("token") String token) {
        //check if token is valid
        authenticationService.authenticate(token);
        //get user using token
        Long userId = authenticationService.getUser(token).getId();
        // return the wishlist i.e. all products in the wishlist
        List<Wishlist> body = wishlistService.readWishlist(userId);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Wishlist wishlist : body) {
            productDtos.add(ProductService.getDtoFromProduct(wishlist.getProduct()));
        }
        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }

    //Remove items from wishlist
    @DeleteMapping("/remove/{token}")
    public ResponseEntity<ApiResponse> removeFromWishlist(@PathVariable("token") String token, @RequestBody Product product) {
        //verify token
        authenticationService.authenticate(token);
        //get user using token
        Long userId = authenticationService.getUser(token).getId();
        //remove the required item from wishlist
        wishlistService.removeFromWishlist(userId, product);
        return new ResponseEntity<>(new ApiResponse(true, "Product removed successfully"), HttpStatus.OK);
    }
}
