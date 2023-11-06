package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
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
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/product")
@AllArgsConstructor
@Validated
@Api(tags = "Products Api")
public class ProductController {

    //Linking layers using constructor injection
    private final ProductServiceInterface productService;
    private final SubCategoryServiceInterface subCategoryService;
    private final CategoryServiceInterface categoryService;
    private final Helper helper;

    // API : Add product by owner
    @ApiOperation(value = "API : Add product by owner")
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> addProduct(
            @RequestBody @Valid ProductDto productDto,
            HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard Statement : Validating quantity should not be zero
        if (productDto.getTotalQuantity() <= 0) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Quantity cannot be zero"),
                    HttpStatus.BAD_REQUEST);
        }

        // Guard Statement : Validating price should not be zero
        if (productDto.getPrice() <= 0) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Price cannot be zero"),
                    HttpStatus.BAD_REQUEST);
        }

        // Retrieving Categories and Subcategories from DTO
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());

        // Calling service layer to save product
        productService.addProduct(productDto, subCategories, categories, user);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Product has been added"),
                HttpStatus.CREATED);
    }

    // API : List all the products and same user cannot see their own products
    @ApiOperation(value = "API : List all the products and same user cannot see their own products")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    // Parameters required for pagination
    public ResponseEntity<List<ProductDto>> listProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Calling service to get list of products
        List<ProductDto> body = productService.listProducts(pageNumber, pageSize, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To update product
    @ApiOperation(value = "API : To update product")
    @PutMapping(value = "/update/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable("productId") Long productId,
            @RequestBody @Valid ProductDto productDto,
            HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard Statement : Check if product is present in DB or not
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Product is invalid"),
                    HttpStatus.NOT_FOUND);
        }

        // Retrieving Categories and Subcategories from DTO
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());

        // Calling service layer to update product
        productService.updateProduct(productId, productDto, subCategories, categories, user);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Product has been updated"),
                HttpStatus.OK);
    }

    // API : List products by subcategory
    @ApiOperation(value = "API : List products by subcategory")
    @GetMapping(value = "/listBySubcategoryId/{subcategoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('BORROWER')")
    public ResponseEntity<List<ProductDto>> listBySubcategoryId(
            @PathVariable("subcategoryId") Long subcategoryId,
            HttpServletRequest request) {

        // Guard Statement : Check if subcategory is valid
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (optionalSubCategory.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        //Calling service layer to fetch the products accordingly
        List<ProductDto> body = productService.listProductsById(subcategoryId, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : List products by category
    @ApiOperation(value = "API : List products by category")
    @GetMapping(value = "/listByCategoryId/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<List<ProductDto>> listByCategoryId(
            @PathVariable("categoryId") Long categoryId,
            HttpServletRequest request) {

        //Guard Statement : Check if category id is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Calling service layer to fetch the products accordingly
        List<ProductDto> body = productService.listProductsByCategoryId(categoryId, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To list by product by id
    @ApiOperation(value = "API : Get individual product details")
    @GetMapping(value = "/listByProductId/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProductDto> listByProductId(@PathVariable("productId") Long productId) {

        //Calling service layer to retrieve product
        ProductDto product = productService.listProductByid(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // API : List Products according to price range
    @ApiOperation(value = "API : Filter products according to price range")
    @GetMapping(value = "/listByPriceRange", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    // Validation params, by default required = true
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(
            @RequestParam("minPrice") double minPrice,
            @RequestParam("maxPrice") double maxPrice) {

        // Calling service layer to get list of products
        List<ProductDto> body = productService.getProductsByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To search products
    @ApiOperation(value = "API : To search products")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("query") String query, HttpServletRequest request ) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Calling service layer to get products
        List<ProductDto> body = productService.searchProducts(query, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To view recently added products in owner flow
    //List products in descending order for recently added functionality in owner flow
    @ApiOperation(value = "API : To view recently added products in owner flow")
    @GetMapping(value = "/listInDesc", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ProductDto>> listProductsDesc(HttpServletRequest request)  {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Calling service layer to list products in descending order
        List<ProductDto> body = productService.listProductsDesc(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To list owner added specific products
    @ApiOperation(value = "API : To list owner added specific products")
    @GetMapping(value = "/listOwnerProducts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ProductDto>> listOwnerProducts(HttpServletRequest request)  {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Calling service layer to list products
        List<ProductDto> body = productService.listOwnerProducts(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To filter products
    @ApiOperation(value = "API : To filter products")
    @GetMapping(value = "/filterProducts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    // All params are required
    public ResponseEntity<List<ProductDto>> filterProducts(
            @RequestParam("size") String size,
            @RequestParam("subcategoryId") Long subcategoryId,
            @RequestParam("minPrice") double minPrice,
            @RequestParam("maxPrice") double maxPrice) {

        // Calling service layer to list filtered products
        List<ProductDto> body = productService.filterProducts(size, subcategoryId, minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : For soft deleting products
    @ApiOperation(value = "API : For soft deleting products")
    @DeleteMapping("/deleteProduct/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable("productId") Long productId,
            HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard statement : To check user is valid
        if (!Helper.notNull(user)) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "User is invalid!"),
                    HttpStatus.NOT_FOUND);
        }

        // Guard statement : To check product is valid
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Product is invalid!"),
                    HttpStatus.NOT_FOUND);
        }

        // Calling service layer to delete products
        productService.deleteProduct(optionalProduct.get().getId(), user.getId());
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Product has been deleted successfully."),
                HttpStatus.OK);
    }

    // API : For disabling products
    @ApiOperation(value = "API : For disabling products")
    @PutMapping(value = "/disableProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> disableProducts(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", required = false) int quantity,
            HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard Statement : To check product belongs to current user
        Product product = productService.getProduct(productId, user.getId());
        if (!Helper.notNull(product)) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "The product does not belong to current user"),
                    HttpStatus.FORBIDDEN);
        }

        // Calling service layer to disable product
        productService.disableProduct(product, quantity);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Product has been disabled"),
                HttpStatus.OK);
    }

    // API : For enabling products
    @ApiOperation(value = "API : For enabling products")
    @PutMapping(value = "/enableProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> enableProducts(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", required = false) int quantity,
            HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard Statement : To check product belongs to current user
        Product product = productService.getProduct(productId, user.getId());
        if (!Helper.notNull(product)) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "The product does not belong to current user"),
                    HttpStatus.FORBIDDEN);
        }

        // Calling service layer to enable product
        productService.enableProduct(product, quantity);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Product has been enabled"),
                HttpStatus.OK);
    }
}
