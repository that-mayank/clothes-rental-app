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
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> addWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            // Check if the product is valid
            Optional<Product> optionalProduct = productService.readProduct(productId);
            if (optionalProduct.isEmpty()) {
                log.error("Product is invalid: {}", productId);
                return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
            }

            // Check if the same product is already in the wishlist
            for (Wishlist wishlist : wishlistService.readWishlist(user.getId())) {
                Long wishlistProductId = wishlist.getProduct().getId();
                if (wishlistProductId.equals(productId)) {
                    log.info("Product already in wishlist: {}", productId);
                    return new ResponseEntity<>(new ApiResponse(false, "Product already in wishlist"), HttpStatus.CONFLICT);
                }
            }

            // Add the product to the wishlist
            Wishlist wishlist = new Wishlist(optionalProduct.get(), user);
            wishlistService.createWishlist(wishlist);
            log.info("Product added to wishlist: {}", productId);

            return new ResponseEntity<>(new ApiResponse(true, "Added to wishlist"), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error while adding product to wishlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error adding to wishlist"));
        }
    }

    // API to get all items in the wishlist
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> getWishlist(HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            // Get the user's ID
            Long userId = user.getId();

            // Get the wishlist for the user
            List<Wishlist> body = wishlistService.readWishlist(userId);
            List<ProductDto> productDtos = new ArrayList<>();

            // Convert wishlist items to ProductDto
            for (Wishlist wishlist : body) {
                productDtos.add(ProductServiceImpl.getDtoFromProduct(wishlist.getProduct()));
            }

            log.info("Wishlist items fetched for user: {}", user.getEmail());

            // Return the wishlist
            return new ResponseEntity<>(productDtos, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while fetching wishlist items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // API to remove items from the wishlist
    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> removeFromWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            // Get the user's ID
            Long userId = user.getId();

            // Remove the specified item from the wishlist
            wishlistService.removeFromWishlist(userId, productId);
            log.info("Product removed from wishlist: User={}, ProductId={}", user.getEmail(), productId);

            return new ResponseEntity<>(new ApiResponse(true, "Product removed successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while removing product from wishlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error removing product from wishlist"));
        }
    }
}

