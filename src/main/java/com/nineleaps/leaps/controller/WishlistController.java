package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.service.WishlistServiceInterface;
import com.nineleaps.leaps.service.implementation.ProductServiceImpl;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/wishlist")
@AllArgsConstructor
@Slf4j
@Api(tags = "Wishlist Api", description = "Contains api for adding and removing products to/from wishlist")
@SuppressWarnings("deprecation")
public class WishlistController {
    private final WishlistServiceInterface wishlistService;
    private final ProductServiceInterface productService;
    private final Helper helper;

    //Add product to Wishlist
    //change Product to ProductDto
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {
        //check if token is valid
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //check if product is valid
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }
        //check the same product cannot be added to wishlist
        for (Wishlist wishlist : wishlistService.readWishlist(user.getId())) {
            if (wishlist.getProduct().getId().equals(productId)) {
                return new ResponseEntity<>(new ApiResponse(false, "Product already in wishlist"), HttpStatus.CONFLICT);
            }
        }
        //add to wishlist
        Wishlist wishlist = new Wishlist(optionalProduct.get(), user);
        wishlistService.createWishlist(wishlist);
        return new ResponseEntity<>(new ApiResponse(true, "Add to wishlist"), HttpStatus.CREATED);
    }


    //Get all items of Wishlist
    @GetMapping("/list")
    public ResponseEntity<List<ProductDto>> getWishlist(HttpServletRequest request) {
        //check if token is valid
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //get user using token
        Long userId = user.getId();
        // return the wishlist i.e. all products in the wishlist
        List<Wishlist> body = wishlistService.readWishlist(userId);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Wishlist wishlist : body) {
            productDtos.add(ProductServiceImpl.getDtoFromProduct(wishlist.getProduct()));
        }
        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }

    //Remove items from wishlist
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse> removeFromWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {
        //verify token
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //get user using token
        Long userId = user.getId();
        //remove the required item from wishlist
        wishlistService.removeFromWishlist(userId, productId);
        return new ResponseEntity<>(new ApiResponse(true, "Product removed successfully"), HttpStatus.OK);
    }
}
