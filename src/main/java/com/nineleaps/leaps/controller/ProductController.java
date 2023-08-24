package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/product")
@AllArgsConstructor
@Api(tags = "Products Api", description = "Contains api for adding products, listing products, updating products and soft deleting products")
@SuppressWarnings("deprecation")
public class ProductController {
    private final ProductServiceInterface productService;
    private final SubCategoryServiceInterface subCategoryService;
    private final CategoryServiceInterface categoryService;
    private final Helper helper;


    @ApiOperation(value = "Add product to owner")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody @Valid ProductDto productDto, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        if (productDto.getTotalQuantity() <= 0) {
            return new ResponseEntity<>(new ApiResponse(false, "Quantity cannot be zero"), HttpStatus.BAD_REQUEST);
        }
        if (productDto.getPrice() <= 0) {
            return new ResponseEntity<>(new ApiResponse(false, "Price cannot be zero"), HttpStatus.BAD_REQUEST);
        }
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());
        productService.addProduct(productDto, subCategories, categories, user);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been added"), HttpStatus.CREATED);
    }

    @ApiOperation(value = "List all the products and same user cannot see his/her own products in borrower flow")
    @GetMapping("/list")
    public ResponseEntity<List<ProductDto>> listProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "1000", required = false) int pageSize, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        List<ProductDto> body = productService.listProducts(pageNumber, pageSize, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ApiOperation(value = "Update product of owner")
    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("productId") Long productId, @RequestBody @Valid ProductDto productDto, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());
        productService.updateProduct(productId, productDto, subCategories, categories, user);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been updated"), HttpStatus.OK);
    }

    //list by subcategory id
    @ApiOperation(value = "List product by subcategory id")
    @GetMapping("/listBySubcategoryId/{subcategoryId}")
    public ResponseEntity<List<ProductDto>> listBySubcategoryId(@PathVariable("subcategoryId") Long subcategoryId, HttpServletRequest request) {
        //check if subcategory is valid
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (!optionalSubCategory.isPresent()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //fetch the products accordingly
        List<ProductDto> body = productService.listProductsById(subcategoryId, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //list by category id
    @ApiOperation(value = "List products by category id")
    @GetMapping("/listByCategoryId/{categoryId}")
    public ResponseEntity<List<ProductDto>> listByCategoryId(@PathVariable("categoryId") Long categoryId, HttpServletRequest request) {
        //check if category id is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (!optionalCategory.isPresent()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        // fetch the products accordingly
        List<ProductDto> body = productService.listProductsByCategoryId(categoryId, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //list by product id
    @ApiOperation(value = "Get individual product details")
    @GetMapping("/listByProductId/{productId}")
    public ResponseEntity<ProductDto> listByProductId(@PathVariable("productId") Long productId) {
        //check if product id is valid
        ProductDto product = productService.listProductByid(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    //List Products according to price range
    @ApiOperation(value = "Filter products according to price range")
    @GetMapping("/listByPriceRange")
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(@RequestParam("minPrice") double minPrice, @RequestParam("maxPrice") double maxPrice) {
        List<ProductDto> body = productService.getProductsByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ApiOperation(value = "Search product api")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("query") String query, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        List<ProductDto> body = productService.searchProducts(query, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ApiOperation(value = "Api for recently added in owner flow")
    @GetMapping("/listInDesc") //List products in descending order for recently added functionality in owner flow
    public ResponseEntity<List<ProductDto>> listProductsDesc(HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        List<ProductDto> body = productService.listProductsDesc(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ApiOperation(value = "Only show products which user had added")
    @GetMapping("/listOwnerProducts") // Api for My Rentals
    public ResponseEntity<List<ProductDto>> listOwnerProducts(HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        List<ProductDto> body = productService.listOwnerProducts(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ApiOperation(value = "Filter api")
    @GetMapping("/filterProducts")
    public ResponseEntity<List<ProductDto>> filterProducts(@RequestParam("size") String size, @RequestParam("subcategoryId") Long subcategoryId, @RequestParam("minPrice") double minPrice, @RequestParam("maxPrice") double maxPrice) {
        List<ProductDto> body = productService.filterProducts(size, subcategoryId, minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ApiOperation(value = "Api for soft deleting products")
    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("productId") Long productId, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        if (!Helper.notNull(user)) {
            return new ResponseEntity<>(new ApiResponse(false, "User is invalid!"), HttpStatus.NOT_FOUND);
        }
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid!"), HttpStatus.NOT_FOUND);
        }
        productService.deleteProduct(optionalProduct.get().getId(), user.getId());
        return new ResponseEntity<>(new ApiResponse(true, "Product has been deleted successfully."), HttpStatus.OK);
    }

    @GetMapping("/disableProduct")
    public ResponseEntity<ApiResponse> disableProducts(@RequestParam("productId") Long productId, @RequestParam(value = "quantity", required = false) int quantity, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Product product = productService.getProduct(productId, user.getId());
        if (!Helper.notNull(product)) {
            return new ResponseEntity<>(new ApiResponse(false, "The product does not belong to current user"), HttpStatus.FORBIDDEN);
        }
        productService.disableProduct(product, quantity);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been disabled"), HttpStatus.OK);
    }

    @GetMapping("/enableProduct")
    public ResponseEntity<ApiResponse> enableProducts(@RequestParam("productId") Long productId, @RequestParam(value = "quantity", required = false) int quantity, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Product product = productService.getProduct(productId, user.getId());
        if (!Helper.notNull(product)) {
            return new ResponseEntity<>(new ApiResponse(false, "The product does not belong to current user"), HttpStatus.FORBIDDEN);
        }
        productService.enableProduct(product, quantity);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been enabled"), HttpStatus.OK);
    }
}
