package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.service.WishlistServiceInterface;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@AllArgsConstructor
@Validated
@Api(tags = "Wishlist Api")

public class WishlistController {

    //Linking layers using constructor injection

    private final WishlistServiceInterface wishlistService;

    // API : To add product to Wishlist

    @ApiOperation(value = "API : To add product to Wishlist")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('BORROWER','OWNER')")
    public ResponseEntity<ApiResponse> addWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {
        wishlistService.createWishlist(productId, request);
        return new ResponseEntity<>(new ApiResponse(true, "Add to wishlist"), HttpStatus.CREATED);
    }


    // API :Get all products of Wishlist

    @ApiOperation("API :Get all products of Wishlist")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('BORROWER','OWNER')")
    public ResponseEntity<List<ProductDto>> getWishlist(HttpServletRequest request) {
        // return the wishlist i.e. all products in the wishlist
        List<ProductDto> body = wishlistService.readWishlist(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To remove product from wishlist

    @ApiOperation("API : To remove product from wishlist")
    @DeleteMapping()
    @PreAuthorize("hasAnyAuthority('BORROWER','OWNER')")
    public ResponseEntity<ApiResponse> removeFromWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {
        //remove the required item from wishlist
        wishlistService.removeFromWishlist(request, productId);
        return new ResponseEntity<>(new ApiResponse(true, "Product removed successfully"), HttpStatus.NO_CONTENT);
    }
}