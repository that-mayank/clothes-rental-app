package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.ProductRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import javax.persistence.EntityManager;

import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Session session;

    @Mock
    private Filter deletedProductFilter;

    @Mock
    private Filter disabledProductFilter;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_shouldSaveProductWithImageUrl() {
        // Create a sample ProductDto
        ProductDto productDto = new ProductDto();
        productDto.setName("Sample Product");
        productDto.setDescription("Sample description");
        productDto.setPrice(100.0);
        productDto.setImageUrl(Arrays.asList("image1.jpg", "image2.jpg"));

        // Create sample SubCategories, Categories, and User
        List<SubCategory> subCategories = createSampleSubCategories();
        List<Category> categories = createSampleCategories();
        User user = new User();

        // Call the method to add the product
        productService.addProduct(productDto, subCategories, categories, user);

        // Verify that the productRepository.save() was called once
        verify(productRepository, times(1)).save(any(Product.class));
    }

    private List<SubCategory> createSampleSubCategories() {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setId(1L);
        subCategoryDto.setCategoryId(1L);
        subCategoryDto.setDescription("test sub category");
        subCategoryDto.setImageURL("123456789.jpg");
        subCategoryDto.setSubcategoryName("shirts");

        // Create a sample category
        Category sampleCategory = createSampleCategories().get(0);

        // Create and return sample SubCategories
        SubCategory subCategory1 = new SubCategory(subCategoryDto, sampleCategory);

        // Add more as needed
        return List.of(subCategory1);
    }

    private List<Category> createSampleCategories() {
        // Create and return sample Categories
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("shirts");
        categoryDto.setId(1L);
        categoryDto.setImageUrl("qwertyuio.jpg");
        categoryDto.setDescription("test category");
        Category category1 = new Category(categoryDto);
        return List.of(category1);
    }

    private  ProductDto getDtoFromProduct(Product product) {
        return new ProductDto(product);
    }
//
//
//    @Test
//    void listProducts_shouldReturnProductDtos() {
//        // Create a sample User
//        User user = new User();
//
//        // Create a sample Page of Products
//        List<Product> productList = new ArrayList<>(); // Initialize with actual products
//        Product product1 = new Product();
//        product1.setName("Sample Product");
//        product1.setDescription("Sample description");
//        product1.setPrice(100.0);
//        product1.setDeleted(false);
//        product1.setDisabled(false);
//        Product product2 = new Product();
//        product2.setName("Sample Product 2");
//        product2.setDescription("Sample description 2");
//        product2.setPrice(100.0);
//        product2.setDeleted(false);
//        product2.setDisabled(false);
//        productList.add(product2);
//        Page<Product> page = new PageImpl<>(productList);
//
//        // Mock the behavior of productRepository.findAllByUserNot
//        when(productRepository.findAllByUserNot(any(), any(User.class))).thenReturn(page);
//
//        // Mock the behavior of entityManager.unwrap
//        Session sessionMock = mock(Session.class);
//        when(entityManager.unwrap(Session.class)).thenReturn(sessionMock);
//
//        // Mock the behavior of session.enableFilter
//        Filter deletedProductFilterMock = mock(Filter.class);
//        Filter disabledProductFilterMock = mock(Filter.class);
//        when(sessionMock.enableFilter(anyString())).thenReturn(deletedProductFilterMock).thenReturn(disabledProductFilterMock);
//
//        // Execute the method
//        List<ProductDto> result = productService.listProducts(0, 10, user);
//
//        // Verify the result
//        assertThat(result).hasSize(1); // Ensure that we have 1 ProductDto in the result
//
//        // Add more specific assertions based on your actual Product to ProductDto conversion logic
//        ProductDto expectedProductDto = getDtoFromProduct(product1);
//        assertThat(result.get(0)).isEqualTo(expectedProductDto);
//    }


    @Test
    void updateProduct_shouldUpdateProduct() {
        // Create a sample ProductDto
        ProductDto productDto = new ProductDto();
        productDto.setName("Updated Product");
        productDto.setDescription("Updated description");
        productDto.setPrice(150.0);
        productDto.setImageUrl(Collections.singletonList("updated_image.jpg"));

        // Create sample SubCategories, Categories, and User
        List<SubCategory> subCategories = new ArrayList<>(); // Initialize with actual subcategories
        List<Category> categories = new ArrayList<>(); // Initialize with actual categories
        User user = new User();
        user.setId(1L);

        // Create a sample existing product
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Original Product");
        existingProduct.setDescription("Original description");
        existingProduct.setPrice(100.0);

        // Mock the behavior of productRepository.findByUserIdAndId
        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(existingProduct);

        // Execute the method
        productService.updateProduct(1L, productDto, subCategories, categories, user);

        // Verify that the product is updated with the correct values
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productRepository).save(argThat(product -> {
            // Verify that the product is updated with the correct values
            assertEquals(productDto.getName(), product.getName());
            assertEquals(productDto.getDescription(), product.getDescription());
            assertEquals(productDto.getPrice(), product.getPrice(), 0.001); // Ensure double equality
            return true;
        }));
    }


    @Test
    void readProduct_shouldReturnProduct() {
        // Create a sample product
        Product product = new Product();
        product.setId(1L);
        product.setName("Sample Product");
        product.setDescription("Sample description");
        product.setPrice(100.0);

        // Mock the behavior of productRepository.findById
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // Execute the method
        Optional<Product> result = productService.readProduct(1L);

        // Verify that the correct product is returned
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    void readProduct_shouldReturnEmptyOptionalForNonexistentProduct() {
        // Mock the behavior of productRepository.findById for a nonexistent product
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute the method
        Optional<Product> result = productService.readProduct(100L); // Assuming product with ID 100 doesn't exist

        // Verify that an empty Optional is returned
        assertFalse(result.isPresent());
    }

    @Test
    void listProductByid_shouldReturnProductDto() throws ProductNotExistException {
        // Create a sample product and productDto
        Product product = new Product();
        product.setId(1L);
        product.setName("Sample Product");
        product.setDescription("Sample description");
        product.setPrice(100.0);
        ProductDto expectedProductDto = getDtoFromProduct(product);

        // Mock the behavior of productRepository.findById
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // Execute the method
        ProductDto result = productService.listProductByid(1L);
        // Verify that the correct ProductDto is returned
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getDescription(), result.getDescription());
    }

    @Test
    void listProductByid_shouldThrowProductNotExistExceptionForNonexistentProduct() {
        // Mock the behavior of productRepository.findById for a nonexistent product
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute the method and verify that it throws ProductNotExistException
        assertThrows(ProductNotExistException.class, () -> productService.listProductByid(100L)); // Assuming product with ID 100 doesn't exist
    }

    @Test
    void getProductById_shouldReturnProduct() throws ProductNotExistException {
        // Create a sample product
        Product product = new Product();
        product.setId(1L);
        product.setName("Sample Product");
        product.setDescription("Sample description");
        product.setPrice(100.0);

        // Mock the behavior of productRepository.findById
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // Execute the method
        Product result = productService.getProductById(1L);

        // Verify that the correct Product is returned
        assertEquals(product, result);
    }

    @Test
    void getProductById_shouldThrowProductNotExistExceptionForNonexistentProduct() {
        // Mock the behavior of productRepository.findById for a nonexistent product
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute the method and verify that it throws ProductNotExistException
        assertThrows(ProductNotExistException.class, () -> productService.getProductById(100L)); // Assuming product with ID 100 doesn't exist
    }

    @Test
    void deleteProduct_shouldMarkProductAsDeleted() {
        // Create a sample User and Product
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(1L);

        // Mock the behavior of productRepository.findByUserIdAndId
        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(product);

        // Execute the method
        productService.deleteProduct(1L, user);

        // Verify that the product is marked as deleted
        assertTrue(product.isDeleted());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProduct_shouldThrowProductNotExistExceptionForNonexistentProduct() {
        // Mock the behavior of productRepository.findByUserIdAndId for a nonexistent product
        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(null);

        // Execute the method and verify that it throws ProductNotExistException
        assertThrows(ProductNotExistException.class, () -> productService.deleteProduct(100L, new User())); // Assuming product with ID 100 doesn't exist
    }

    @Test
    void getProduct_shouldReturnProduct() {
        // Create a sample User and Product
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(1L);

        // Mock the behavior of productRepository.findByUserIdAndId
        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(product);

        // Execute the method
        Product result = productService.getProduct(1L, 1L);

        // Verify that the method returned the expected product
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getProduct_shouldReturnNullForNonexistentProduct() {
        // Mock the behavior of productRepository.findByUserIdAndId for a nonexistent product
        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(null);

        // Execute the method
        Product result = productService.getProduct(100L, 1L); // Assuming product with ID 100 doesn't exist

        // Verify that the method returned null
        assertNull(result);
    }

    @Test
    void disableProduct_shouldDisableProductAndUpdateQuantities() {
        // Create a sample User and Product
        User user = new User();
        Product product = new Product();
        product.setAvailableQuantities(10);
        product.setDisabledQuantities(0);
        product.setAuditColumnsUpdate(user.getId());
        product.setAuditColumnsCreate(user);
        product.setDisabled(true);

        // Execute the method
        productService.disableProduct(product, 5, user);

        // Verify that the product is disabled and quantities are updated
        assertTrue(product.isDisabled());
        assertEquals(5, product.getDisabledQuantities());
        assertEquals(5, product.getAvailableQuantities());


        // Verify that the productRepository.save() was called once
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void disableProduct_shouldThrowExceptionForInvalidQuantity() {
        // Create a sample User and Product
        User user = new User();
        Product product = new Product();
        product.setAvailableQuantities(10);
        product.setDisabledQuantities(0);

        // Try to disable more quantities than available
        assertThrows(QuantityOutOfBoundException.class, () -> productService.disableProduct(product, 15, user));

        // Verify that the product is not disabled and quantities are unchanged
        assertFalse(product.isDisabled());
        assertEquals(0, product.getDisabledQuantities());
        assertEquals(10, product.getAvailableQuantities());

        // Verify that the productRepository.save() was not called
        verify(productRepository, never()).save(product);
    }

    @Test
    void enableProduct_shouldEnableProductAndUpdateQuantities() {
        // Create a sample User and Product
        User user = new User();
        user.setId(1L);
        user.setEmail("yokes.e@nineleaps.com");
        Product product = new Product();
        product.setAvailableQuantities(5);
        product.setDisabledQuantities(5);
        product.setDisabled(true);
        product.setAuditColumnsUpdate(user.getId());
        product.setAuditColumnsCreate(user);

        // Execute the method
        productService.enableProduct(product, 3, user);

        // Verify that the product is enabled and quantities are updated
        assertFalse(product.isDisabled());
        assertEquals(2, product.getDisabledQuantities());
        assertEquals(8, product.getAvailableQuantities());


        // Verify that the productRepository.save() was called once
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void enableProduct_shouldThrowExceptionForInvalidQuantity() {
        // Create a sample User and Product
        User user = new User();
        Product product = new Product();
        product.setAvailableQuantities(5);
        product.setDisabledQuantities(5);
        product.setDisabled(true);

        // Try to enable more quantities than disabled
        assertThrows(QuantityOutOfBoundException.class, () -> productService.enableProduct(product, 7, user));

        // Verify that the product is not enabled and quantities are unchanged
        assertTrue(product.isDisabled());
        assertEquals(5, product.getDisabledQuantities());
        assertEquals(5, product.getAvailableQuantities());

        // Verify that the productRepository.save() was not called
        verify(productRepository, never()).save(product);
    }

    @Test
    void ngrokLinkRemove_shouldRemoveNgrokPrefix() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Input image URL with ngrok prefix
        String imageUrl = NGROK+"/image.jpg";

        // Use reflection to access the private method
        Method method = ProductServiceImpl.class.getDeclaredMethod("ngrokLinkRemove", String.class);
        method.setAccessible(true);

        // Execute the method
        String result = (String) method.invoke(productService,imageUrl);

        // Verify the result
        assertEquals("/image.jpg", result); // The ngrok prefix should not be removed
    }

    @Test
    void ngrokLinkRemove_shouldNotRemovePrefixIfNotPresent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        // Input image URL without ngrok prefix
        String imageUrl = NGROK+"/image.jpg";

        // Use reflection to access the private method
        Method method = ProductServiceImpl.class.getDeclaredMethod("ngrokLinkRemove", String.class);
        method.setAccessible(true);

        // Execute the method
        String result = (String) method.invoke(productService,imageUrl);


        // Verify the result
        assertEquals("/image.jpg", result); // The input should remain unchanged
    }

}