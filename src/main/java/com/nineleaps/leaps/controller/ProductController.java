package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.service.ProductServiceInterface;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@AllArgsConstructor
@Validated
@Api(tags = "Products Api")
public class ProductController {

    //Linking layers using constructor injection
    private final ProductServiceInterface productService;

    // API : Add product by owner
    @ApiOperation(value = "API : Add product by owner")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody @Valid ProductDto productDto, HttpServletRequest request) {
        // Calling service layer to save product
        productService.addProduct(productDto, request);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been added"), HttpStatus.CREATED);
    }

    // API : List all the products and same user cannot see their own products
    @ApiOperation(value = "API : List all the products and same user cannot see their own products")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    // Parameters required for pagination
    public ResponseEntity<List<ProductDto>> listProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize, HttpServletRequest request) {
        // Calling service to get list of products
        List<ProductDto> body = productService.listProducts(pageNumber, pageSize, request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To update product
    @ApiOperation(value = "API : To update product")
    @PutMapping(value = "{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable("productId") Long productId,
            @RequestBody @Valid ProductDto productDto,
            HttpServletRequest request) {
        // Calling service layer to update product
        productService.updateProduct(productId, productDto, request);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been updated"), HttpStatus.OK);
    }

    // API : List products by subcategory
    @ApiOperation(value = "API : List products by subcategory")
    @GetMapping(value = "{subcategoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('BORROWER')")
    public ResponseEntity<List<ProductDto>> listBySubcategoryId(@PathVariable("subcategoryId") Long subcategoryId, HttpServletRequest request) {
        //Calling service layer to fetch the products accordingly
        List<ProductDto> body = productService.listProductsById(subcategoryId, request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : List products by category
    @ApiOperation(value = "API : List products by category")
    @GetMapping(value = "{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<List<ProductDto>> listByCategoryId(@PathVariable("categoryId") Long categoryId, HttpServletRequest request) {
        // Calling service layer to fetch the products accordingly
        List<ProductDto> body = productService.listProductsByCategoryId(categoryId, request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To list by product by id
    @ApiOperation(value = "API : Get individual product details")
    @GetMapping(value = "{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> listByProductId(@PathVariable("productId") Long productId) {
        //Calling service layer to retrieve product
        ProductDto product = productService.listProductByid(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // API : List Products according to price range
    @ApiOperation(value = "API : Filter products according to price range")
    @GetMapping(value = "price-range", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    // Validation params, by default required = true
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(@RequestParam("minPrice") double minPrice, @RequestParam("maxPrice") double maxPrice) {
        // Calling service layer to get list of products
        List<ProductDto> body = productService.getProductsByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To search products
    @ApiOperation(value = "API : To search products")
    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("query") String query, HttpServletRequest request) {
        // Calling service layer to get products
        List<ProductDto> body = productService.searchProducts(query, request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To view recently added products in owner flow
    //List products in descending order for recently added functionality in owner flow
    @ApiOperation(value = "API : To view recently added products in owner flow")
    @GetMapping(value = "desc", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ProductDto>> listProductsDesc(HttpServletRequest request) {
        // Calling service layer to list products in descending order
        List<ProductDto> body = productService.listProductsDesc(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To list owner added specific products
    @ApiOperation(value = "API : To list owner added specific products")
    @GetMapping(value = "owner-owned", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ProductDto>> listOwnerProducts(HttpServletRequest request) {
        // Calling service layer to list products
        List<ProductDto> body = productService.listOwnerProducts(request);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To filter products
    @ApiOperation(value = "API : To filter products")
    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    // All params are required
    public ResponseEntity<List<ProductDto>> filterProducts(@RequestParam("size") String size, @RequestParam("subcategoryId") Long subcategoryId, @RequestParam("minPrice") double minPrice, @RequestParam("maxPrice") double maxPrice) {
        // Calling service layer to list filtered products
        List<ProductDto> body = productService.filterProducts(size, subcategoryId, minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : For soft deleting products
    @ApiOperation(value = "API : For soft deleting products")
    @DeleteMapping("{productId}")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("productId") Long productId, HttpServletRequest request) {
        // Calling service layer to delete products
        productService.deleteProduct(productId, request);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been deleted successfully."), HttpStatus.OK);
    }

    // API : For disabling products
    @ApiOperation(value = "API : For disabling products")
    @PutMapping(value = "disable", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> disableProducts(@RequestParam("productId") Long productId, @RequestParam(value = "quantity") int quantity, HttpServletRequest request) {
        // Calling service layer to disable product
        productService.disableProduct(productId, quantity, request);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been disabled"), HttpStatus.OK);
    }

    // API : For enabling products
    @ApiOperation(value = "API : For enabling products")
    @PutMapping(value = "enable", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> enableProducts(@RequestParam("productId") Long productId, @RequestParam(value = "quantity") int quantity, HttpServletRequest request) {
        // Calling service layer to enable product
        productService.enableProduct(productId, quantity, request);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been enabled"), HttpStatus.OK);
    }
}
