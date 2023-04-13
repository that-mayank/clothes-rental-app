package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.*;
import com.nineleaps.leaps.utils.Helper;
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
    private final CategoryServiceInterface categoryService;

    @Autowired
    public ProductController(ProductServiceInterface productService, SubCategoryServiceInterface subCategoryService, CategoryServiceInterface categoryService) {
        this.productService = productService;
        this.subCategoryService = subCategoryService;
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody @Valid ProductDto productDto) {
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());
        productService.addProduct(productDto, subCategories, categories);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been added"), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDto>> listProducts() {
        List<ProductDto> body = productService.listProducts();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("productId") Long productId, @RequestBody @Valid ProductDto productDto) {
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());
        productService.updateProduct(productId, productDto, subCategories, categories);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been updated"), HttpStatus.OK);
    }

    //list by subcategory id
    @PutMapping("/listbyid/{subcategoryId}")
    public ResponseEntity<List<Product>> listById(@PathVariable("subcategoryId") Long subcategoryId) {
        //check if subcategory is valid
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (!optionalSubCategory.isPresent()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        //fetch the products accordingly
        List<Product> body = productService.listProductsById(subcategoryId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //list by category id
    @PutMapping("/listbycategoryId/{categoryId}")
    public ResponseEntity<List<ProductDto>> listByCategoryId(@PathVariable("categoryId") Long categoryId) {
        //check if category id is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (!optionalCategory.isPresent()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        // fetch the products accordingly
        List<ProductDto> body = productService.listProductsByCategoryId(categoryId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //list by product id
    @PutMapping("/listByProductId/{productId}")
    public ResponseEntity<ProductDto> listByProductId(@PathVariable("productId") Long productId) {
        //check if product id is valid
        ProductDto product = productService.listProductByid(productId);
        if (!Helper.notNull(product)) {
            return new ResponseEntity<>(new ProductDto(), HttpStatus.NOT_FOUND);
        }
        //fetch the product details
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}
