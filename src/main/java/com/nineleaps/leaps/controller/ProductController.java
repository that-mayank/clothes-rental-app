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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            // Check if the product quantity and price are valid
            if (productDto.getTotalQuantity() <= 0) {
                log.error("Quantity cannot be zero");
                return new ResponseEntity<>(new ApiResponse(false, "Quantity cannot be zero"), HttpStatus.BAD_REQUEST);
            }
            if (productDto.getPrice() <= 0) {
                log.error("Price cannot be zero");
                return new ResponseEntity<>(new ApiResponse(false, "Price cannot be zero"), HttpStatus.BAD_REQUEST);
            }

            // Fetch categories and subcategories based on provided IDs
            List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
            List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());

            // Add the product
            productService.addProduct(productDto, subCategories, categories, user);

            log.info("Product has been added successfully");
            return new ResponseEntity<>(new ApiResponse(true, "Product has been added"), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error while adding a product: " + e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse(false, "An error occurred while adding the product"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // API to list all products
    @ApiOperation(value = "List all the products and same user cannot see his/her own products in borrower flow")
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> listProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "1000", required = false) int pageSize, HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Listing products for user: {}", user.getEmail());

            // Fetch and return the list of products
            List<ProductDto> body = productService.listProducts(pageNumber, pageSize, user);

            log.info("Listed {} products successfully.", body.size());
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while listing products: " + e.getMessage(), e);
            List<ProductDto> emptyList = new ArrayList<>(); // Create an empty list
            return new ResponseEntity<>(emptyList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // API to update a product belonging to the owner.
    @ApiOperation(value = "Update product of owner")
    @PutMapping(value = "/update/{productId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("productId") Long productId, @RequestBody @Valid ProductDto productDto, HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Updating product with ID: {} by user: {}", productId, user.getEmail());

            // Check if the provided product ID is valid
            Optional<Product> optionalProduct = productService.readProduct(productId);
            if (optionalProduct.isEmpty()) {
                log.error("Product with ID {} is invalid", productId);
                return new ResponseEntity<>(new ApiResponse(false, "Product is invalid"), HttpStatus.NOT_FOUND);
            }

            // Fetch categories and subcategories based on provided IDs
            List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
            List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());

            // Update the product
            productService.updateProduct(productId, productDto, subCategories, categories, user);

            log.info("Product with ID {} has been updated successfully", productId);
            return new ResponseEntity<>(new ApiResponse(true, "Product has been updated"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while updating a product: " + e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse(false, "An error occurred while updating the product"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // API to list products by a specific subcategory ID.
    @ApiOperation(value = "List product by subcategory id")
    @GetMapping(value = "/listBySubcategoryId/{subcategoryId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> listBySubcategoryId(@PathVariable("subcategoryId") Long subcategoryId, HttpServletRequest request) {
        try {
            // Check if the provided subcategory ID is valid
            Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
            if (optionalSubCategory.isEmpty()) {
                log.error("Subcategory with ID {} is invalid", subcategoryId);
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
            }

            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Listing products for subcategory ID: {} by user: {}", subcategoryId, user.getEmail());

            // Fetch the products for the specified subcategory
            List<ProductDto> body = productService.listProductsById(subcategoryId, user);

            log.info("Listed {} products for subcategory ID: {}", body.size(), subcategoryId);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while listing products by subcategory ID: " + e.getMessage(), e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // API to list products by a specific category ID.
    @ApiOperation(value = "List products by category id")
    @GetMapping(value = "/listByCategoryId/{categoryId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> listByCategoryId(@PathVariable("categoryId") Long categoryId, HttpServletRequest request) {
        try {
            // Check if the provided category ID is valid
            Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
            if (optionalCategory.isEmpty()) {
                log.error("Category with ID {} is invalid", categoryId);
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
            }

            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Listing products for category ID: {} by user: {}", categoryId, user.getEmail());

            // Fetch the products for the specified category
            List<ProductDto> body = productService.listProductsByCategoryId(categoryId, user);

            log.info("Listed {} products for category ID: {}", body.size(), categoryId);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while listing products by category ID: " + e.getMessage(), e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // API to get details of an individual product by its ID.
    @ApiOperation(value = "Get individual product details")
    @GetMapping(value = "/listByProductId/{productId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ProductDto> listByProductId(@PathVariable("productId") Long productId) {
        try {
            log.info("Fetching details of product with ID: {}", productId);

            // Check if the provided product ID is valid
            ProductDto product = productService.listProductByid(productId);

            if (product != null) {
                log.info("Details of product with ID {} retrieved successfully", productId);
                return new ResponseEntity<>(product, HttpStatus.OK);
            } else {
                log.error("Product with ID {} not found", productId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error while fetching product details: " + e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // API to filter products based on a price range.
    @ApiOperation(value = "Filter products according to price range")
    @GetMapping(value="/listByPriceRange" , produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(@RequestParam("minPrice") double minPrice, @RequestParam("maxPrice") double maxPrice) {
        try {
            log.info("Filtering products by price range: minPrice={}, maxPrice={}", minPrice, maxPrice);

            // Fetch products within the specified price range
            List<ProductDto> body = productService.getProductsByPriceRange(minPrice, maxPrice);

            log.info("Filtered {} products by price range: minPrice={}, maxPrice={}", body.size(), minPrice, maxPrice);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while filtering products by price range: " + e.getMessage(), e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API to search for products based on a query string.
    @ApiOperation(value = "Search product api")
    @GetMapping(value="/search",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("query") String query, HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Searching for products with query: '{}' by user: {}", query, user.getEmail());

            // Search for products based on the query
            List<ProductDto> body = productService.searchProducts(query, user);

            log.info("Found {} products matching the query: '{}'", body.size(), query);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while searching for products with query: '{}' - {}", query, e.getMessage(), e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // API to list products in descending order for recently added functionality in owner flow
    @GetMapping(value="/listInDesc",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ProductDto>> listProductsDesc(HttpServletRequest request) throws AuthenticationFailException {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Listing products in descending order of addition for user: {}", user.getEmail());

            // Fetch products in descending order of addition
            List<ProductDto> body = productService.listProductsDesc(user);

            log.info("Listed {} products in descending order of addition", body.size());
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while listing products in descending order: " + e.getMessage(), e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API to list products added by the current user (owner)
    @GetMapping(value="/listOwnerProducts",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // API for My Rentals
    public ResponseEntity<List<ProductDto>> listOwnerProducts(HttpServletRequest request) throws AuthenticationFailException {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Listing products added by owner: {}", user.getEmail());

            // Fetch products added by the owner
            List<ProductDto> body = productService.listOwnerProducts(user);

            log.info("Listed {} products added by owner: {}", body.size(), user.getEmail());
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while listing owner's products: " + e.getMessage(), e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API to filter products based on size, subcategory ID, and price range
    @GetMapping(value="/filterProducts",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('BORROWER')")
    public ResponseEntity<List<ProductDto>> filterProducts(
            @RequestParam("size") String size,
            @RequestParam("subcategoryId") Long subcategoryId,
            @RequestParam("minPrice") double minPrice,
            @RequestParam("maxPrice") double maxPrice) {
        try {
            log.info("Filtering products based on criteria - size: {}, subcategoryId: {}, minPrice: {}, maxPrice: {}", size, subcategoryId, minPrice, maxPrice);

            // Fetch products based on specified criteria (size, subcategory, price range)
            List<ProductDto> body = productService.filterProducts(size, subcategoryId, minPrice, maxPrice);

            log.info("Filtered {} products based on criteria - size: {}, subcategoryId: {}, minPrice: {}, maxPrice: {}", body.size(), size, subcategoryId, minPrice, maxPrice);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while filtering products based on criteria: " + e.getMessage(), e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API to soft-delete a product
    @DeleteMapping("/deleteProduct/{productId}")
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("productId") Long productId, HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Deleting product with ID {} by user: {}", productId, user.getEmail());

            // Check if the user is valid
            if (!Helper.notNull(user)) {
                log.error("User is invalid while deleting product with ID: {}", productId);
                return new ResponseEntity<>(new ApiResponse(false, "User is invalid!"), HttpStatus.BAD_REQUEST);
            }

            // Retrieve the product based on the provided product ID
            Optional<Product> optionalProduct = productService.readProduct(productId);

            // Check if the product exists and is valid
            if (optionalProduct.isEmpty()) {
                log.error("Product is invalid while deleting product with ID: {}", productId);
                return new ResponseEntity<>(new ApiResponse(false, "Product is invalid!"), HttpStatus.BAD_REQUEST);
            }

            // Soft delete the product
            productService.deleteProduct(optionalProduct.get().getId(), user);

            log.info("Product with ID {} has been deleted successfully by user: {}", productId, user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Product has been deleted successfully."), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while deleting product with ID " + productId + ": " + e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Error while deleting the product."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // API to disable a product
    @GetMapping(value="/disableProduct",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ResponseEntity<ApiResponse> disableProducts(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", required = false) int quantity,
            HttpServletRequest request) {
        try {
            User user = helper.getUserFromToken(request);

            log.info("Disabling product with ID {} by user: {}", productId, user.getEmail());

            // Retrieve the product based on the provided product ID and user ID
            Product product = productService.getProduct(productId, user.getId());

            // Check if the product belongs to the current user
            if (!Helper.notNull(product)) {
                log.error("Product with ID {} does not belong to the current user: {}", productId, user.getEmail());
                return new ResponseEntity<>(new ApiResponse(false, "The product does not belong to the current user"), HttpStatus.FORBIDDEN);
            }

            // Disable the product with an optional quantity
            productService.disableProduct(product, quantity, user);

            log.info("Product with ID {} has been disabled by user: {}", productId, user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Product has been disabled"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while disabling product with ID " + productId + ": " + e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Error while disabling the product."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API to enable a product
    @GetMapping(value="/enableProduct",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ResponseEntity<ApiResponse> enableProducts(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", required = false) int quantity,
            HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            log.info("Enabling product with ID {} by user: {}", productId, user.getEmail());

            // Retrieve the product based on the provided product ID and user ID
            Product product = productService.getProduct(productId, user.getId());

            // Check if the product belongs to the current user
            if (!Helper.notNull(product)) {
                log.error("Product with ID {} does not belong to the current user: {}", productId, user.getEmail());
                return new ResponseEntity<>(new ApiResponse(false, "The product does not belong to the current user"), HttpStatus.FORBIDDEN);
            }

            // Enable the product with an optional quantity
            productService.enableProduct(product, quantity, user);

            log.info("Product with ID {} has been enabled by user: {}", productId, user.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Product has been enabled"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while enabling product with ID " + productId + ": " + e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Error while enabling the product."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}