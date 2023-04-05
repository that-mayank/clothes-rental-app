package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductServiceInterface productService;
    private final SubCategoryServiceInterface subCategoryService;

    @Autowired
    public ProductController(ProductServiceInterface productService, SubCategoryServiceInterface subCategoryService) {
        this.productService = productService;
        this.subCategoryService = subCategoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody ProductDto productDto) {
        List<SubCategory> subCategories = new ArrayList<>();
        for (Long subcategoryId : productDto.getSubcategoryIds()) {
            Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
            if (!optionalSubCategory.isPresent()) {
                return new ResponseEntity<ApiResponse>(new ApiResponse(false, "category is invalid"), HttpStatus.CONFLICT);
            }
            subCategories.add(optionalSubCategory.get());
        }
        productService.addProduct(productDto, subCategories);
        return new ResponseEntity<ApiResponse>(new ApiResponse(true, "Product has been added"), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDto>> listProducts() {
        List<ProductDto> body = productService.listProducts();
        return new ResponseEntity<List<ProductDto>>(body, HttpStatus.OK);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("productId") Long productId, @RequestBody @Valid ProductDto productDto) {
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }
        List<SubCategory> subCategories = new ArrayList<>();
        for (Long subcategoryId : productDto.getSubcategoryIds()) {
            Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
            if (!optionalSubCategory.isPresent()) {
                return new ResponseEntity<ApiResponse>(new ApiResponse(false, "category is invalid"), HttpStatus.CONFLICT);
            }
            subCategories.add(optionalSubCategory.get());
        }
        productService.updateProduct(productId, productDto, subCategories);
        return new ResponseEntity<ApiResponse>(new ApiResponse(true, "Product has been updated"), HttpStatus.OK);
    }

    //list by subcategory id
    @PutMapping("/listbyid/{subcategoryId}")
    public ResponseEntity<List<Product>> listById(@PathVariable("subcategoryId") Long subcategoryId) {
        //check if subcategory is valid
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (!optionalSubCategory.isPresent()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        //fetch the subcategories accordingly
        List<Product> body = productService.listProductsById(subcategoryId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


}
