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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/v1/product")
@AllArgsConstructor
@Api(tags = "Products Api", description = "Contains api for adding products, listing products, updating products and soft deleting products")
@SuppressWarnings("deprecation")
public class ProductController {


    /**
     * Status Code: 200 - HttpStatus.OK
     * Description: The request was successful, and the response contains the requested data.

     * Status Code: 201 - HttpStatus.CREATED
     * Description: The request was successful, and a new resource has been created as a result.

     * Status Code: 204 - HttpStatus.NO_CONTENT
     * Description: The request was successful, but there is no content to send in the response payload.

     * Status Code: 400 - HttpStatus.BAD_REQUEST
     * Description: The request could not be understood or was missing required parameters,
     * and no further information is available.

     * Status Code: 401 - HttpStatus.UNAUTHORIZED
     * Description: Authentication failed or user does not have permissions for the requested operation.

     * Status Code: 403 - HttpStatus.FORBIDDEN
     * Description: The authenticated user does not have access to the requested resource.

     * Status Code: 404 - HttpStatus.NOT_FOUND
     * Description: The requested resource could not be found but may be available in the future.

     * Status Code: 500 - HttpStatus.INTERNAL_SERVER_ERROR
     * Description: An error occurred on the server and no more specific message is suitable.
     */


    // linking layers
    private final ProductServiceInterface productService;
    private final SubCategoryServiceInterface subCategoryService;
    private final CategoryServiceInterface categoryService;
    private final Helper helper;




    // API to add a product to the owner's inventory.
    @ApiOperation(value = "Add product to owner")
    @PostMapping(value = "/add",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody @Valid ProductDto productDto, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Check if the product quantity and price are valid
        if (productDto.getTotalQuantity() <= 0) {
            return new ResponseEntity<>(new ApiResponse(false, "Quantity cannot be zero"), HttpStatus.BAD_REQUEST);
        }
        if (productDto.getPrice() <= 0) {
            return new ResponseEntity<>(new ApiResponse(false, "Price cannot be zero"), HttpStatus.BAD_REQUEST);
        }

        // Fetch categories and subcategories based on provided IDs
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());

        // Add the product
        productService.addProduct(productDto, subCategories, categories, user);

        // Return success response
        return new ResponseEntity<>(new ApiResponse(true, "Product has been added"), HttpStatus.CREATED);
    }



    // API to list all products
    @ApiOperation(value = "List all the products and same user cannot see his/her own products in borrower flow")
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> listProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "1000", required = false) int pageSize, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Fetch and return the list of products
        List<ProductDto> body = productService.listProducts(pageNumber, pageSize, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }



    // API to update a product belonging to the owner.
    @ApiOperation(value = "Update product of owner")
    @PutMapping(value = "/update/{productId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("productId") Long productId, @RequestBody @Valid ProductDto productDto, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Check if the provided product ID is valid
        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
        }

        // Fetch categories and subcategories based on provided IDs
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());

        // Update the product
        productService.updateProduct(productId, productDto, subCategories, categories, user);

        // Return success response
        return new ResponseEntity<>(new ApiResponse(true, "Product has been updated"), HttpStatus.OK);
    }



    // API to list products by a specific subcategory ID.
    @ApiOperation(value = "List product by subcategory id")
    @GetMapping(value = "/listBySubcategoryId/{subcategoryId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> listBySubcategoryId(@PathVariable("subcategoryId") Long subcategoryId, HttpServletRequest request) {
        // Check if the provided subcategory ID is valid
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (optionalSubCategory.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Fetch the products for the specified subcategory
        List<ProductDto> body = productService.listProductsById(subcategoryId, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }



    // API to list products by a specific category ID.
    @ApiOperation(value = "List products by category id")
    @GetMapping(value = "/listByCategoryId/{categoryId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> listByCategoryId(@PathVariable("categoryId") Long categoryId, HttpServletRequest request) {
        // Check if the provided category ID is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Fetch the products for the specified category
        List<ProductDto> body = productService.listProductsByCategoryId(categoryId, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    // API to get details of an individual product by its ID.
    @ApiOperation(value = "Get individual product details")
    @GetMapping(value = "/listByProductId/{productId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ProductDto> listByProductId(@PathVariable("productId") Long productId) {
        // Check if the provided product ID is valid
        ProductDto product = productService.listProductByid(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }


    // API to filter products based on a price range.
    @ApiOperation(value = "Filter products according to price range")
    @GetMapping(value="/listByPriceRange" , produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(@RequestParam("minPrice") double minPrice, @RequestParam("maxPrice") double maxPrice) {
        // Fetch products within the specified price range
        List<ProductDto> body = productService.getProductsByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API to search for products based on a query string.
    @ApiOperation(value = "Search product api")
    @GetMapping(value="/search",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("query") String query, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Search for products based on the query
        List<ProductDto> body = productService.searchProducts(query, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    // API to list products in descending order for recently added functionality in owner flow
    @GetMapping(value="/listInDesc",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ProductDto>> listProductsDesc(HttpServletRequest request) throws AuthenticationFailException {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Fetch products in descending order of addition
        List<ProductDto> body = productService.listProductsDesc(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API to list products added by the current user (owner)
    @GetMapping(value="/listOwnerProducts",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // API for My Rentals
    public ResponseEntity<List<ProductDto>> listOwnerProducts(HttpServletRequest request) throws AuthenticationFailException {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Fetch products added by the owner
        List<ProductDto> body = productService.listOwnerProducts(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API to filter products based on size, subcategory ID, and price range
    @GetMapping(value="/filterProducts",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('BORROWER')")
    public ResponseEntity<List<ProductDto>> filterProducts(
            @RequestParam("size") String size,
            @RequestParam("subcategoryId") Long subcategoryId,
            @RequestParam("minPrice") double minPrice,
            @RequestParam("maxPrice") double maxPrice) {
        // Fetch products based on specified criteria (size, subcategory, price range)
        List<ProductDto> body = productService.filterProducts(size, subcategoryId, minPrice, maxPrice);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API to soft-delete a product
    @DeleteMapping("/deleteProduct/{productId}")
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("productId") Long productId, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Check if the user is valid
        if (!Helper.notNull(user)) {
            return new ResponseEntity<>(new ApiResponse(false, "User is invalid!"), HttpStatus.BAD_REQUEST);
        }

        // Retrieve the product based on the provided product ID
        Optional<Product> optionalProduct = productService.readProduct(productId);

        // Check if the product exists and is valid
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Product is invalid!"), HttpStatus.BAD_REQUEST);
        }

        // Soft delete the product
        productService.deleteProduct(optionalProduct.get().getId(), user);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been deleted successfully."), HttpStatus.OK);
    }


    // API to disable a product
    @GetMapping(value="/disableProduct",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ResponseEntity<ApiResponse> disableProducts(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", required = false) int quantity,
            HttpServletRequest request) {
        User user = helper.getUserFromToken(request);

        // Retrieve the product based on the provided product ID and user ID
        Product product = productService.getProduct(productId, user.getId());

        // Check if the product belongs to the current user
        if (!Helper.notNull(product)) {
            return new ResponseEntity<>(new ApiResponse(false, "The product does not belong to the current user"), HttpStatus.FORBIDDEN);
        }

        // Disable the product with an optional quantity
        productService.disableProduct(product, quantity, user);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been disabled"), HttpStatus.OK);
    }

    // API to enable a product
    @GetMapping(value="/enableProduct",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ResponseEntity<ApiResponse> enableProducts(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", required = false) int quantity,
            HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Retrieve the product based on the provided product ID and user ID
        Product product = productService.getProduct(productId, user.getId());

        // Check if the product belongs to the current user
        if (!Helper.notNull(product)) {
            return new ResponseEntity<>(new ApiResponse(false, "The product does not belong to the current user"), HttpStatus.FORBIDDEN);
        }

        // Enable the product with an optional quantity
        productService.enableProduct(product, quantity,user);
        return new ResponseEntity<>(new ApiResponse(true, "Product has been enabled"), HttpStatus.OK);
    }



}