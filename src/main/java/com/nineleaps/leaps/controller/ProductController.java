package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.products.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.*;
import com.nineleaps.leaps.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServiceInterface productService;
    private final SubCategoryServiceInterface subCategoryService;
    private final CategoryServiceInterface categoryService;
    private final AuthenticationServiceInterface authenticationService;


    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody @Valid ProductDto productDto, @RequestParam("token") String token) throws AuthenticationFailException {
        //authenticate the token
        authenticationService.authenticate(token);
        //retrieve user for token
        User user = authenticationService.getUser(token);
        //check quantity and price should not be zero
        if (productDto.getQuantity() <= 0 || productDto.getPrice() <= 0) {
            return new ResponseEntity<>(new ApiResponse(false, "Quantity or Price cannot be zero"), HttpStatus.BAD_REQUEST);
        }
        //Add the product
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());
        productService.addProduct(productDto, subCategories, categories, user);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been added"), HttpStatus.CREATED);
    }

    @GetMapping("/list") //implementing Pagination **DONE
    public ResponseEntity<List<ProductDto>> listProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize, @RequestParam("token") String token) throws AuthenticationFailException {
        //check if token is valid
        authenticationService.authenticate(token);
        //Retrieving the user
        User user = authenticationService.getUser(token);
        List<ProductDto> body = productService.listProducts(pageNumber, pageSize, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //list product by owner id i.e. user id
    @GetMapping("/listOwnerProducts") // Api for My Rentals
    public ResponseEntity<List<ProductDto>> listOwnerProducts(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        List<ProductDto> body = productService.listOwnerProducts(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/listInDesc") //List products in descending order for recently added functionality in owner flow
    public ResponseEntity<List<ProductDto>> listProductsDesc(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        List<ProductDto> body = productService.listProductsDesc(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("productId") Long productId, @RequestBody @Valid ProductDto productDto, @RequestParam("token") String token) throws AuthenticationFailException {
        //authenticate the token
        authenticationService.authenticate(token);
        //get user
        User user = authenticationService.getUser(token);
        //check quantity and price should not be zero
        if (productDto.getQuantity() == 0 || productDto.getPrice() == 0) {
            return new ResponseEntity<>(new ApiResponse(false, "Quantity and Price cannot be zero"), HttpStatus.BAD_REQUEST);
        }

        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());
        //check if the product is associated with that user or not
        productService.updateProduct(productId, productDto, subCategories, categories, user);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been updated"), HttpStatus.OK);
    }

    //list by subcategory id
    @GetMapping("/listById/{subcategoryId}")
    public ResponseEntity<List<ProductDto>> listById(@PathVariable("subcategoryId") Long subcategoryId) {
        //check if subcategory is valid
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (!optionalSubCategory.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //fetch the products accordingly
        List<ProductDto> body = productService.listProductsById(subcategoryId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //list by category id
    @GetMapping("/listByCategoryId/{categoryId}")
    public ResponseEntity<List<ProductDto>> listByCategoryId(@PathVariable("categoryId") Long categoryId) {
        //check if category id is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (!optionalCategory.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // fetch the products accordingly
        List<ProductDto> body = productService.listProductsByCategoryId(categoryId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //list by product id
    @GetMapping("/listByProductId/{productId}")
    public ResponseEntity<ProductDto> listByProductId(@RequestParam("productId") Long productId) {
        //check if product id is valid
        ProductDto product = productService.listProductByid(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }


    //List Products according to price range
    @GetMapping("/listByPriceRange")
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(@RequestParam("minPrice") double minPrice, @RequestParam("maxPrice") double maxPrice) {
        List<ProductDto> body = productService.getProductsByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("query") String query) {
        List<ProductDto> body = productService.searchProducts(query);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/filterProducts")
    public ResponseEntity<List<ProductDto>> filterProducts(@RequestParam("size") String size, @RequestParam("brand") String brand, @RequestParam("subcategoryId") Long subcategoryId, @RequestParam("minPrice") double minPrice, @RequestParam("maxPrice") double maxPrice) {
        List<ProductDto> body = productService.filterProducts(size, brand, subcategoryId, minPrice, maxPrice);
        if (body.isEmpty()) { // isEmpty is used to check if the list is empty or not
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
