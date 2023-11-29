//package com.nineleaps.leaps.controller;
//
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.common.ApiResponse;
//import com.nineleaps.leaps.dto.product.ProductDto;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.model.categories.Category;
//import com.nineleaps.leaps.model.categories.SubCategory;
//import com.nineleaps.leaps.model.product.Product;
//import com.nineleaps.leaps.service.CategoryServiceInterface;
//import com.nineleaps.leaps.service.ProductServiceInterface;
//import com.nineleaps.leaps.service.SubCategoryServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import javax.servlet.http.HttpServletRequest;
//import java.util.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//@Tag("unit_tests")
//@DisplayName("Test case file for Product Controller")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class ProductControllerTest {
//    @Mock
//    private ProductServiceInterface productService;
//
//    @Mock
//    private CategoryServiceInterface categoryService;
//
//    @Mock
//    private SubCategoryServiceInterface subCategoryService;
//
//    @Mock
//    private Helper helper;
//
//    @InjectMocks
//    private ProductController productController;
//    @Mock
//    private HttpServletRequest request;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("Add product")
//    void testAddProduct() {
//        // Prepare a ProductDto
//        ProductDto productDto = new ProductDto();
//        productDto.setTotalQuantity(10);
//        productDto.setPrice(50.0);
//        // Add other required fields
//
//        // Prepare a mock request
//        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
//
//        // Prepare a mock User
//        User mockUser = new User();
//
//        // Mock helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(mockUser);
//
//        // Mock categoryService.getCategoriesFromIds and subCategoryService.getSubCategoriesFromIds
//        List<Category> mockCategories = new ArrayList<>();
//        // Add mock categories
//        when(categoryService.getCategoriesFromIds(anyList())).thenReturn(mockCategories);
//        List<SubCategory> mockSubCategories = new ArrayList<>();
//        // Add mock subcategories
//        when(subCategoryService.getSubCategoriesFromIds(anyList())).thenReturn(mockSubCategories);
//
//        // Mock productService.addProduct
//        doNothing().when(productService).addProduct(any(ProductDto.class), anyList(), anyList(), any(User.class));
//
//
//        // Call the API method
//        ResponseEntity<ApiResponse> response = productController.addProduct(productDto, mockRequest);
//
//        // Assert the response
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
//        assertEquals("Product has been added", response.getBody().getMessage());
//    }
//
//    @Test
//    @DisplayName("Add product with invalid quantity")
//    void testAddProduct_InvalidQuantity() {
//        // Mock data with invalid quantity
//        ProductDto productDto = new ProductDto();
//        productDto.setTotalQuantity(-10);  // Invalid quantity
//        productDto.setPrice(100);
//
//        // Call the API
//        ResponseEntity<ApiResponse> responseEntity = productController.addProduct(productDto, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        assertEquals("Quantity cannot be zero", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("add product with invalid price")
//    void testAddProduct_InvalidPrice() {
//        // Mock data with invalid price
//        ProductDto productDto = new ProductDto();
//        productDto.setTotalQuantity(10);
//        productDto.setPrice(-100);  // Invalid price
//
//        // Call the API
//        ResponseEntity<ApiResponse> responseEntity = productController.addProduct(productDto, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        assertEquals("Price cannot be zero", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("list products")
//     void testListProducts() {
//        // Mock the User
//        User user = new User();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock the ProductService response
//        List<ProductDto> mockProducts = Arrays.asList(new ProductDto(), new ProductDto()); // Mock list of products
//        when(productService.listProducts(anyInt(), anyInt(), eq(user))).thenReturn(mockProducts);
//
//        // Call the API
//        ResponseEntity<List<ProductDto>> responseEntity = productController.listProducts(0, 10, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(mockProducts, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("update product")
//     void testUpdateProduct() {
//        // Mock the User
//        User user = new User();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock the ProductService response
//        ProductDto productDto = new ProductDto();
//        productDto.setCategoryIds(Arrays.asList(1L, 2L));
//        productDto.setSubcategoryIds(Arrays.asList(3L, 4L));
//
//        // Mock the optional product
//        Product product = new Product();
//        Optional<Product> optionalProduct = Optional.of(product);
//        when(productService.readProduct(anyLong())).thenReturn(optionalProduct);
//
//        // Mock the call to update the product
//        doNothing().when(productService).updateProduct(anyLong(), eq(productDto), anyList(), anyList(), eq(user));
//
//        // Call the API
//        ResponseEntity<ApiResponse> responseEntity = productController.updateProduct(123L, productDto, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertTrue(Objects.requireNonNull(responseEntity.getBody()).isSuccess());
//        assertEquals("Product has been updated", responseEntity.getBody().getMessage());
//    }
//    @Test
//    @DisplayName("Update product with invalid product ID")
//    void testUpdateProduct_InvalidProductId() {
//        // Mock invalid product ID
//        Long invalidProductId = 999L;
//
//        // Mock data
//        ProductDto productDto = new ProductDto();
//
//        // Mock ProductService response for invalid product ID
//        when(productService.readProduct(invalidProductId)).thenReturn(Optional.empty());
//
//        // Call the API with an invalid product ID
//        ResponseEntity<ApiResponse> responseEntity = productController.updateProduct(invalidProductId, productDto, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//        assertEquals("Product is invalid", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("list products by subcategory id")
//     void testListBySubcategoryId() {
//        // Mock the User
//        User user = new User();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock the SubCategoryService response
//        SubCategory subCategory = new SubCategory();
//        Optional<SubCategory> optionalSubCategory = Optional.of(subCategory);
//        when(subCategoryService.readSubCategory(anyLong())).thenReturn(optionalSubCategory);
//
//        // Mock the ProductService response
//        List<ProductDto> products = Arrays.asList(new ProductDto(), new ProductDto());
//        when(productService.listProductsById(anyLong(), eq(user))).thenReturn(products);
//
//        // Call the API
//        ResponseEntity<List<ProductDto>> responseEntity = productController.listBySubcategoryId(123L, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(products, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("list products with invalid subcategory ID")
//     void testListBySubcategoryId_InvalidSubCategory() {
//        // Mock the SubCategoryService response for an invalid subcategory ID
//        when(subCategoryService.readSubCategory(anyLong())).thenReturn(Optional.empty());
//
//        // Call the API with an invalid subcategory ID
//        ResponseEntity<List<ProductDto>> responseEntity = productController.listBySubcategoryId(456L, request);
//
//        // Verify the response for an invalid subcategory ID
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//        assertTrue(Objects.requireNonNull(responseEntity.getBody()).isEmpty());
//    }
//
//    @Test
//    @DisplayName("list products by category ID")
//     void testListByCategoryId() {
//        // Mock the User
//        User user = new User();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock the CategoryService response
//        Category category = new Category();
//        Optional<Category> optionalCategory = Optional.of(category);
//        when(categoryService.readCategory(anyLong())).thenReturn(optionalCategory);
//
//        // Mock the ProductService response
//        List<ProductDto> products = Arrays.asList(new ProductDto(), new ProductDto());
//        when(productService.listProductsByCategoryId(anyLong(), eq(user))).thenReturn(products);
//
//        // Call the API
//        ResponseEntity<List<ProductDto>> responseEntity = productController.listByCategoryId(123L, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(products, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("List products by invalid category ID")
//     void testListByCategoryId_InvalidCategory() {
//        // Mock the CategoryService response for an invalid category ID
//        when(categoryService.readCategory(anyLong())).thenReturn(Optional.empty());
//
//        // Call the API with an invalid category ID
//        ResponseEntity<List<ProductDto>> responseEntity = productController.listByCategoryId(456L, request);
//
//        // Verify the response for an invalid category ID
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//        assertTrue(Objects.requireNonNull(responseEntity.getBody()).isEmpty());
//    }
//
//    @Test
//    @DisplayName("list by product ID")
//     void testListByProductId() {
//        // Mock the ProductService response
//        ProductDto productDto = new ProductDto();
//        when(productService.listProductByid(anyLong())).thenReturn(productDto);
//
//        // Call the API
//        ResponseEntity<ProductDto> responseEntity = productController.listByProductId(123L);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(productDto, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("list product by invalid product ID")
//     void testListByProductId_InvalidProduct() {
//        // Mock the ProductService response for an invalid product ID
//        when(productService.listProductByid(anyLong())).thenReturn(null);
//
//        // Call the API with an invalid product ID
//        ResponseEntity<ProductDto> responseEntity = productController.listByProductId(4560000L);
//
//        // Verify the response for an invalid product ID
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        Assertions.assertNull(responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("get products by price range")
//     void testGetProductsByPriceRange() {
//        // Mock ProductService response
//        List<ProductDto> productDtoList = new ArrayList<>();
//        when(productService.getProductsByPriceRange(anyDouble(), anyDouble())).thenReturn(productDtoList);
//
//        // Call the API
//        ResponseEntity<List<ProductDto>> responseEntity = productController.getProductsByPriceRange(20.0, 200.0);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(productDtoList, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("search products")
//     void testSearchProducts() {
//        String query = "example query";
//
//        // Mock User and ProductService response
//        User user = new User();
//        List<ProductDto> productDtoList = new ArrayList<>();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.searchProducts(query, user)).thenReturn(productDtoList);
//
//        // Call the API
//        ResponseEntity<List<ProductDto>> responseEntity = productController.searchProducts(query, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(productDtoList, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("list products desc")
//     void testListProductsDesc() {
//        // Mock User and ProductService response
//        User user = new User();
//        List<ProductDto> productDtoList = new ArrayList<>();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.listProductsDesc(user)).thenReturn(productDtoList);
//
//        // Call the API
//        ResponseEntity<List<ProductDto>> responseEntity = productController.listProductsDesc(request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(productDtoList, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("list owner products")
//     void testListOwnerProducts() {
//        // Mock User and ProductService response
//        User user = new User();
//        List<ProductDto> productDtoList = new ArrayList<>();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.listOwnerProducts(user)).thenReturn(productDtoList);
//
//        // Call the API
//        ResponseEntity<List<ProductDto>> responseEntity = productController.listOwnerProducts(request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(productDtoList, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("filter products")
//     void testFilterProducts() {
//        // Mock ProductService response
//        List<ProductDto> productDtoList = new ArrayList<>();
//        when(productService.filterProducts(anyString(), anyLong(), anyDouble(), anyDouble())).thenReturn(productDtoList);
//
//        // Call the API with sample parameters
//        ResponseEntity<List<ProductDto>> responseEntity = productController.filterProducts("M", 1L, 10.0, 100.0);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(productDtoList, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("delete product")
//     void testDeleteProduct() {
//        // Sample product ID
//        User user = new User();
//        Product product = new Product();
//        product.setId(1L);
//        product.setUser(user);
//
//        // Mock ProductService response
//        when(productService.readProduct(product.getId())).thenReturn(Optional.of(product));
//        doNothing().when(productService).deleteProduct(product.getId(), user);
//
//        // Call the API
//        ResponseEntity<ApiResponse> responseEntity = productController.deleteProduct(product.getId(), request);
//
//        // Verify the response
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//
//    }
//
//    @Test
//    @DisplayName("soft delete")
//    void testDeleteProduct_SuccessfulSoftDelete() {
//        // Mock valid user
//        User user = new User();
//        user.setId(1L);
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock a product that belongs to the user
//        Product product = new Product();
//        product.setId(999L);
//        when(productService.readProduct(anyLong())).thenReturn(Optional.of(product));
//        doNothing().when(productService).deleteProduct(anyLong(), any(User.class));
//
//        // Call the API with a valid product
//        ResponseEntity<ApiResponse> responseEntity = productController.deleteProduct(999L, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Product has been deleted successfully.", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("delete product with invalid product ID")
//     void testDeleteProductInvalidProductId() {
//        Long productId = 1L; // Sample product ID
//
//        // Mock ProductService response for an invalid product ID
//        when(productService.readProduct(productId)).thenReturn(Optional.empty());
//
//        // Call the API with an invalid product ID
//        ResponseEntity<ApiResponse> responseEntity = productController.deleteProduct(productId, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        Assertions.assertFalse(Objects.requireNonNull(responseEntity.getBody()).isSuccess());
//        assertEquals("User is invalid!", responseEntity.getBody().getMessage());
//    }
//
//    @Test
//    @DisplayName("Delete product by Invalid user ID")
//    void testDeleteProduct_UserInvalid() {
//        // Mock invalid user
//        when(helper.getUserFromToken(any())).thenReturn(null);
//
//        // Call the API with an invalid user
//        ResponseEntity<ApiResponse> responseEntity = productController.deleteProduct(1L, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        assertEquals("User is invalid!", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("Delete product - Product not found")
//    void testDeleteProduct_ProductNotFound() {
//        // Mock valid user
//        User user = new User();
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock product isn't found
//        when(productService.readProduct(anyLong())).thenReturn(Optional.empty());
//
//        // Call the API with a non-existing product ID
//        ResponseEntity<ApiResponse> responseEntity = productController.deleteProduct(999L, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        assertEquals("Product is invalid!", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("Disable product")
//    void testDisableProducts() {
//        // Mock data
//        long productId = 1L;
//        int quantity = 10;
//        User user = new User();
//        Product product = new Product();
//        product.setId(productId);
//
//        // Mocking helper.getUserFromToken
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mocking productService.getProduct
//        when(productService.getProduct(productId, user.getId())).thenReturn(product);
//
//        // Mocking productService.disableProduct
//        doNothing().when(productService).disableProduct(product, quantity, user);
//
//        // Call the API
//        ResponseEntity<ApiResponse> responseEntity = productController.disableProducts(productId, quantity, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Product has been disabled", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("disabling products that doesnt belong to user")
//    void testDisableProduct_ProductNotBelongsToUser() {
//        // Mock valid user
//        User user = new User();
//        user.setId(1L);
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock product not belonging to the user
//        Product product = new Product();
//        product.setId(999L);
//        when(productService.getProduct(anyLong(), anyLong())).thenReturn(null); // The Product does not belong to the user
//
//        // Call the API with a product that doesn't belong to the user
//        ResponseEntity<ApiResponse> responseEntity = productController.disableProducts(999L, 0, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
//        assertEquals("The product does not belong to the current user", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("Enable product")
//    void testEnableProducts() {
//        // Mock data
//        long productId = 1L;
//        int quantity = 10;
//        User user = new User();
//        Product product = new Product();
//        product.setId(productId);
//
//        // Mocking helper.getUserFromToken
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mocking productService.getProduct
//        when(productService.getProduct(productId, user.getId())).thenReturn(product);
//
//        // Mocking productService.enableProduct
//        doNothing().when(productService).enableProduct(product, quantity, user);
//
//        // Call the API
//        ResponseEntity<ApiResponse> responseEntity = productController.enableProducts(productId, quantity, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Product has been enabled", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("Enable product with invalid user ")
//    void testEnableProduct_ProductNotBelongsToUser() {
//        // Mock valid user
//        User user = new User();
//        user.setId(1L);
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock product not belonging to the user
//        Product product = new Product();
//        product.setId(999L);
//        when(productService.getProduct(anyLong(), anyLong())).thenReturn(null); // The Product does not belong to the user
//
//        // Call the API with a product that doesn't belong to the user
//        ResponseEntity<ApiResponse> responseEntity = productController.enableProducts(999L, 0, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
//        assertEquals("The product does not belong to the current user", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//
//}
//

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class ProductControllerTest {

    @Mock
    private ProductServiceInterface productService;
    @Mock
    private SubCategoryServiceInterface subCategoryService;
    @Mock
    private CategoryServiceInterface categoryService;
    @Mock
    private Helper helper;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Add Product - Success")
    void addProduct_ValidProduct_ReturnsCreatedResponse() {
        // Arrange
        ProductDto productDto = new ProductDto();
        productDto.setTotalQuantity(10);
        productDto.setPrice(10);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.addProduct(productDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals("Product has been added", responseBody.getMessage());

        verify(productService).addProduct(productDto, request);
    }

    @Test
    @DisplayName("Update Product - Success")
    void updateProduct_ValidProduct_ReturnsOkResponse() {
        // Arrange
        Long productId = 1L;
        ProductDto productDto = new ProductDto();
        User user = new User();
        List<Category> categories = new ArrayList<>();
        List<SubCategory> subCategories = new ArrayList<>();
        Optional<Product> optionalProduct = Optional.of(new Product());

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(optionalProduct);
        when(categoryService.getCategoriesFromIds(anyList())).thenReturn(categories);
        when(subCategoryService.getSubCategoriesFromIds(anyList())).thenReturn(subCategories);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.updateProduct(productId, productDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals("Product has been updated", responseBody.getMessage());

        verify(productService).updateProduct(productId, productDto, request);
    }

    @Test
    @DisplayName("List Product By Subcategory Id")
    void listBySubcategoryId_ValidSubcategoryId_ReturnsListOfProducts() {
        // Arrange
        User user = new User();
        Long subcategoryId = 1L;
        Optional<SubCategory> optionalSubCategory = Optional.of(new SubCategory());
        List<ProductDto> productList = new ArrayList<>();

        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(optionalSubCategory);
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.listProductsById(subcategoryId, request)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listBySubcategoryId(subcategoryId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listProductsById(subcategoryId, request);
    }
    @Test
    @DisplayName("List Product - Category Id")
    void listByCategoryId_ValidCategoryId_ReturnsListOfProducts() {
        // Arrange
        User user = new User();
        Long categoryId = 1L;
        Optional<Category> optionalCategory = Optional.of(new Category());
        List<ProductDto> productList = new ArrayList<>();

        when(categoryService.readCategory(categoryId)).thenReturn(optionalCategory);
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.listProductsByCategoryId(categoryId, request)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listByCategoryId(categoryId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listProductsByCategoryId(categoryId, request);
    }

    @Test
    @DisplayName("Get Product - Success")
    void listByProductId_ValidProductId_ReturnsProductDto() {
        // Arrange
        Long productId = 1L;
        ProductDto productDto = new ProductDto();

        when(productService.listProductByid(productId)).thenReturn(productDto);

        // Act
        ResponseEntity<ProductDto> responseEntity = productController.listByProductId(productId);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ProductDto responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productDto, responseBody);

        verify(productService).listProductByid(productId);
    }

    @Test
    @DisplayName("List Product - Price Range")
    void getProductsByPriceRange_ValidRange_ReturnsListOfProducts() {
        // Arrange
        double minPrice = 10.0;
        double maxPrice = 100.0;
        List<ProductDto> productList = new ArrayList<>();

        when(productService.getProductsByPriceRange(minPrice, maxPrice)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.getProductsByPriceRange(minPrice, maxPrice);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).getProductsByPriceRange(minPrice, maxPrice);
    }

    @Test
    @DisplayName("Search Products")
    void searchProducts_ValidQuery_ReturnsListOfProducts() {
        // Arrange
        User user = new User();
        String query = "test";
        List<ProductDto> productList = new ArrayList<>();

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.searchProducts(query, request)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.searchProducts(query, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).searchProducts(query, request);
    }

    @Test
    @DisplayName("List Product - Desc")
    void listProductsDesc_ReturnsListOfProductsInDescendingOrder() {
        // Arrange
        User user = new User();
        List<ProductDto> productList = new ArrayList<>();

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.listProductsDesc(request)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listProductsDesc(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listProductsDesc(request);
    }

    @Test
    @DisplayName("List Product - Owner")
    void listOwnerProducts_ReturnsListOfOwnerProducts() {
        // Arrange
        User user = new User();
        List<ProductDto> productList = new ArrayList<>();

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.listOwnerProducts(request)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listOwnerProducts(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listOwnerProducts(request);
    }

    @Test
    @DisplayName("Product Filter")
    void filterProducts_ReturnsListOfFilteredProducts() {
        // Arrange
        String size = "large";
        Long subcategoryId = 1L;
        double minPrice = 10.0;
        double maxPrice = 100.0;
        List<ProductDto> productList = new ArrayList<>();

        when(productService.filterProducts(size, subcategoryId, minPrice, maxPrice)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.filterProducts(size, subcategoryId, minPrice, maxPrice);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).filterProducts(size, subcategoryId, minPrice, maxPrice);
    }

    @Test
    @DisplayName("Delete Product")
    void deleteProduct_ValidProductId_ReturnsOkResponse() {
        // Arrange
        Product product = new Product();
        Long productId = 1L;
        product.setId(productId);
        User user = new User();
        user.setId(1L);
        Optional<Product> optionalProduct = Optional.of(product);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(optionalProduct);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.deleteProduct(productId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals("Product has been deleted successfully.", responseBody.getMessage());

    }

    @Test
    @DisplayName("Disable Products - Success")
    void disableProducts_ValidProductId_ReturnsOkResponse() {
        // Arrange
        Long productId = 1L;
        int quantity = 10;
        User user = new User();
        Product product = new Product();

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.getProduct(productId, user.getId())).thenReturn(product);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.disableProducts(productId, quantity, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals("Product has been disabled", responseBody.getMessage());

        verify(productService).disableProduct(productId, quantity, request);
    }

    @Test
    @DisplayName("Enable Products - Valid Id")
    void enableProducts_ValidProductId_ReturnsOkResponse() {
        // Arrange
        Long productId = 1L;
        int quantity = 10;
        User user = new User();
        Product product = new Product();

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.getProduct(productId, user.getId())).thenReturn(product);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.enableProducts(productId, quantity, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals("Product has been enabled", responseBody.getMessage());

        verify(productService).enableProduct(productId, quantity, request);
    }

}
