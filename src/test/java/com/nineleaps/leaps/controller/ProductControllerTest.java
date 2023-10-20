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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        User user = new User();
        List<Category> categories = new ArrayList<>();
        List<SubCategory> subCategories = new ArrayList<>();

        when(helper.getUser(request)).thenReturn(user);
        when(categoryService.getCategoriesFromIds(anyList())).thenReturn(categories);
        when(subCategoryService.getSubCategoriesFromIds(anyList())).thenReturn(subCategories);
        doNothing().when(productService).addProduct(any(ProductDto.class), anyList(), anyList(), any(User.class));

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.addProduct(productDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals("Product has been added", responseBody.getMessage());

        verify(productService).addProduct(productDto, subCategories, categories, user);
    }

    @Test
    @DisplayName("Add Product - Invalid Quantity")
    void addProduct_InvalidQuantity_ReturnsBadRequestResponse() {
        // Arrange
        User user = new User();
        ProductDto productDto = new ProductDto();
        productDto.setTotalQuantity(0);
        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.addProduct(productDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("Quantity cannot be zero", responseBody.getMessage());

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Add Product - Invalid Price")
    void testAddProductWithPriceLessThanZero() {
        // Prepare a ProductDto with price less than zero
        ProductDto productDto = new ProductDto();
        productDto.setPrice(-10.0); // Set a negative price
        productDto.setTotalQuantity(10);

        // Prepare a mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Prepare a mock User
        User user = new User();
        user.setId(1L);

        // Mock the behavior of helper.getUser(request)
        when(helper.getUser(request)).thenReturn(user);

        // Call the addProduct method with the prepared ProductDto
        ResponseEntity<ApiResponse> response = productController.addProduct(productDto, request);

        // Verify that the response contains a "Bad Request" status
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Price cannot be zero", response.getBody().getMessage());
    }


    @Test
    @DisplayName("List Product - Success")
    void listProducts_ReturnsListOfProducts() {
        // Arrange
        User user = new User();
        List<ProductDto> productList = new ArrayList<>();
        when(helper.getUser(request)).thenReturn(user);
        when(productService.listProducts(anyInt(), anyInt(), any(User.class))).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listProducts(0, 1000, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listProducts(0, 1000, user);
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

        verify(productService).updateProduct(productId, productDto, subCategories, categories, user);
    }

    @Test
    @DisplayName("Update Product - Invalid Product")
    void updateProduct_InvalidProduct_ReturnsNotFoundResponse() {
        // Arrange
        Long productId = 1L;
        ProductDto productDto = new ProductDto();
        User user = new User();


        Optional<Product> optionalProduct = Optional.empty();

        when(helper.getUser(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(optionalProduct);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.updateProduct(productId, productDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("Product is invalid", responseBody.getMessage());

        verify(productService, never()).updateProduct(anyLong(), any(ProductDto.class), anyList(), anyList(), any(User.class));
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
        when(productService.listProductsById(subcategoryId, user)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listBySubcategoryId(subcategoryId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listProductsById(subcategoryId, user);
    }

    @Test
    @DisplayName("List Product - Invalid Subcategory Id")
    void listBySubcategoryId_InvalidSubcategoryId_ReturnsNotFoundResponse() {
        // Arrange
        Long subcategoryId = 1L;
        Optional<SubCategory> optionalSubCategory = Optional.empty();

        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(optionalSubCategory);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listBySubcategoryId(subcategoryId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());

        verifyNoInteractions(productService);
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
        when(productService.listProductsByCategoryId(categoryId, user)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listByCategoryId(categoryId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listProductsByCategoryId(categoryId, user);
    }

    @Test
    @DisplayName("List Product - Invalid Category Id")
    void listByCategoryId_InvalidCategoryId_ReturnsNotFoundResponse() {
        // Arrange
        Long categoryId = 1L;
        Optional<Category> optionalCategory = Optional.empty();

        when(categoryService.readCategory(categoryId)).thenReturn(optionalCategory);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listByCategoryId(categoryId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());

        verifyNoInteractions(productService);
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
        when(productService.searchProducts(query, user)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.searchProducts(query, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).searchProducts(query, user);
    }

    @Test
    @DisplayName("List Product - Desc")
    void listProductsDesc_ReturnsListOfProductsInDescendingOrder() {
        // Arrange
        User user = new User();
        List<ProductDto> productList = new ArrayList<>();

        when(helper.getUser(request)).thenReturn(user);
        when(productService.listProductsDesc(user)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listProductsDesc(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listProductsDesc(user);
    }

    @Test
    @DisplayName("List Product - Owner")
    void listOwnerProducts_ReturnsListOfOwnerProducts() {
        // Arrange
        User user = new User();
        List<ProductDto> productList = new ArrayList<>();

        when(helper.getUser(request)).thenReturn(user);
        when(productService.listOwnerProducts(user)).thenReturn(productList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.listOwnerProducts(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(productList, responseBody);

        verify(productService).listOwnerProducts(user);
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

        verify(productService).deleteProduct(productId, user.getId());
        verify(productService).readProduct(productId);
    }


    @Test
    @DisplayName("Delete Product - Invalid ProductId")
    void deleteProduct_InvalidProductId_ReturnsNotFoundResponse() {
        // Arrange
        User user = new User();
        Product product = new Product();
        Long productId = 1L;
        product.setId(productId);
        Optional<Product> optionalProduct = Optional.empty();

        when(helper.getUser(request)).thenReturn(user);

        when(productService.readProduct(productId)).thenReturn(optionalProduct);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.deleteProduct(productId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("Product is invalid!", responseBody.getMessage());

        verify(productService).readProduct(productId);
        verifyNoMoreInteractions(productService);
    }

    @Test
    @DisplayName("Delete Product - Invalid User")
    void testDeleteProductWithNullUser() {
        // Prepare a productId
        Long productId = 123L;

        // Prepare a mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Mock the behavior of helper.getUser(request) to return null
        when(helper.getUser(request)).thenReturn(null);

        // Call the deleteProduct method with the prepared productId and request
        ResponseEntity<ApiResponse> response = productController.deleteProduct(productId, request);

        // Verify that the response contains a "Not Found" status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("User is invalid!", response.getBody().getMessage());
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

        verify(productService).disableProduct(product, quantity);
    }

    @Test
    @DisplayName("Disable Products - Invalid Id")
    void disableProducts_InvalidProductId_ReturnsForbiddenResponse() {
        // Arrange
        Long productId = 1L;
        int quantity = 10;
        User user = new User();
        user.setId(1L);

        when(helper.getUser(request)).thenReturn(user);
        when(productService.getProduct(productId, user.getId())).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.disableProducts(productId, quantity, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("The product does not belong to current user", responseBody.getMessage());

        verify(productService).getProduct(productId, user.getId());
        verifyNoMoreInteractions(productService);
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

        verify(productService).enableProduct(product, quantity);
    }

    @Test
    @DisplayName("Enable Products - Invalid Id")
    void enableProducts_InvalidProductId_ReturnsForbiddenResponse() {
        // Arrange
        Long productId = 1L;
        int quantity = 10;
        User user = new User();

        when(helper.getUser(request)).thenReturn(user);
        when(productService.getProduct(productId, user.getId())).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> responseEntity = productController.enableProducts(productId, quantity, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ApiResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("The product does not belong to current user", responseBody.getMessage());

        verify(productService).getProduct(productId, user.getId());
        verifyNoMoreInteractions(productService);
    }
}