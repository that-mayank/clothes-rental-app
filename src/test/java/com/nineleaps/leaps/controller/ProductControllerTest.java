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

        when(helper.getUser(request)).thenReturn(user);
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
        when(helper.getUser(request)).thenReturn(user);
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
        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);
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