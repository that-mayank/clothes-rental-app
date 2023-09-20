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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;




@RestController
@RequestMapping("/api/v1/wishlist")
@AllArgsConstructor
@Slf4j
@Api(tags = "Wishlist Api", description = "Contains APIs for adding and removing products to/from wishlist")
@SuppressWarnings("deprecation")
public class WishlistController {

    /**
     * Status Code: 200 - HttpStatus.OK
     * Description: The request was successful, and the response contains the requested data.

     * Status Code: 201 - HttpStatus.CREATED
     * Description: The request was successful, and a new resource has been created as a result.

     * Status Code: 409 - HttpStatus.CONFLICT
     * Description: The request could be already found are requested

     * Status Code: 404 - HttpStatus.NOT_FOUND
     * Description: The requested resource could not be found but may be available in the future.

     */

    // Wishlist service for wishlist-related operations
    private final WishlistServiceInterface wishlistService;

    // Product service for product-related operations
    private final ProductServiceInterface productService;

    // Helper class for utility methods
    private final Helper helper;

    // API to add a product to the wishlist
    @PostMapping(value = "/add",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> addWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Check if the product is valid
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }

        // Check if the same product is already in the wishlist
        for (Wishlist wishlist : wishlistService.readWishlist(user.getId())) {
            if (wishlist.getProduct().getId().equals(productId)) {
                return new ResponseEntity<>(new ApiResponse(false, "Product already in wishlist"), HttpStatus.CONFLICT);
            }
        }

        // Add the product to the wishlist
        Wishlist wishlist = new Wishlist(optionalProduct.get(), user);
        wishlistService.createWishlist(wishlist);
        return new ResponseEntity<>(new ApiResponse(true, "Added to wishlist"), HttpStatus.CREATED);
    }

    // API to get all items in the wishlist
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> getWishlist(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Get the user's ID
        Long userId = user.getId();

        // Get the wishlist for the user
        List<Wishlist> body = wishlistService.readWishlist(userId);
        List<ProductDto> productDos = new ArrayList<>();

        // Convert wishlist items to ProductDto
        for (Wishlist wishlist : body) {
            productDos.add(ProductServiceImpl.getDtoFromProduct(wishlist.getProduct()));
        }

        // Return the wishlist
        return new ResponseEntity<>(productDos, HttpStatus.OK);
    }

    // API to remove items from the wishlist
    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> removeFromWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Get the user's ID
        Long userId = user.getId();

        // Remove the specified item from the wishlist
        wishlistService.removeFromWishlist(userId, productId);
        return new ResponseEntity<>(new ApiResponse(true, "Product removed successfully"), HttpStatus.OK);
    }
}

