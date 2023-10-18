package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.service.WishlistServiceInterface;
import com.nineleaps.leaps.service.implementation.ProductServiceImpl;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/wishlist")
@AllArgsConstructor
@Validated
@Api(tags = "Wishlist Api")

public class WishlistController {

    //Linking layers using constructor injection

    private final WishlistServiceInterface wishlistService;
    private final ProductServiceInterface productService;
    private final Helper helper;

    // API : To add product to Wishlist

    @ApiOperation(value = "API : To add product to Wishlist")
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> addWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // Guard Statement : Check if product is valid

        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }

        // Guard Statement : Check the same product cannot be added to wishlist

        List<Wishlist> userWishlist = wishlistService.readWishlist(user.getId());
        if (userWishlist.stream().anyMatch(wishlist -> wishlist.getProduct().getId().equals(productId))) {
            return new ResponseEntity<>(new ApiResponse(false, "Product already in wishlist"), HttpStatus.CONFLICT);
        }

        // Calling service layer to add product to wishlist

        Wishlist wishlist = new Wishlist(optionalProduct.get(), user);
        wishlistService.createWishlist(wishlist);
        return new ResponseEntity<>(new ApiResponse(true, "Add to wishlist"), HttpStatus.CREATED);
    }


    // API :Get all products of Wishlist

    @ApiOperation("API :Get all products of Wishlist")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)

    public ResponseEntity<List<ProductDto>> getWishlist(HttpServletRequest request) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // return the wishlist i.e. all products in the wishlist

        List<Wishlist> body = wishlistService.readWishlist(user.getId());
        List<ProductDto> productDtos = new ArrayList<>();
        for (Wishlist wishlist : body) {
            productDtos.add(ProductServiceImpl.getDtoFromProduct(wishlist.getProduct()));
        }
        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }

    // API : To remove product from wishlist

    @ApiOperation("API : To remove product from wishlist")
    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse> removeFromWishlist(@RequestParam("productId") Long productId, HttpServletRequest request) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        //remove the required item from wishlist

        wishlistService.removeFromWishlist(user.getId(), productId);
        return new ResponseEntity<>(new ApiResponse(true, "Product removed successfully"), HttpStatus.NO_CONTENT);
    }
}
