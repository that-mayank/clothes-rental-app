//package com.nineleaps.leaps.service.implementation;
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.dto.category.CategoryDto;
//import com.nineleaps.leaps.dto.category.SubCategoryDto;
//import com.nineleaps.leaps.dto.product.ProductDto;
//import com.nineleaps.leaps.exceptions.ProductNotExistException;
//import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.model.categories.Category;
//import com.nineleaps.leaps.model.categories.SubCategory;
//import com.nineleaps.leaps.model.product.Product;
//import com.nineleaps.leaps.repository.ProductRepository;
//import com.nineleaps.leaps.service.CategoryServiceInterface;
//import com.nineleaps.leaps.service.SubCategoryServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import org.hibernate.Filter;
//import org.hibernate.Session;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.function.Executable;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.test.util.ReflectionTestUtils;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.*;
//import static com.nineleaps.leaps.LeapsApplication.NGROK;
//import static com.nineleaps.leaps.config.MessageStrings.DELETED_PRODUCT_FILTER;
//import static com.nineleaps.leaps.config.MessageStrings.DISABLED_PRODUCT_FILTER;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import javax.persistence.EntityManager;
//import javax.servlet.http.HttpServletRequest;
//
//import static org.mockito.Mockito.*;
//@Tag("unit_tests")
//@DisplayName("Product Service Tests")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class ProductServiceImplTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @InjectMocks
//    private ProductServiceImpl productService;
//
//    @Mock
//    private EntityManager entityManager;
//    @Mock
//    private  Helper helper;
//    @Mock
//    private  SubCategoryServiceInterface subCategoryService;
//    @Mock
//    private  CategoryServiceInterface categoryService;
//    @Mock
//    private Session session;
//
//    @Mock
//    private Filter deletedProductFilter;
//
//    @Mock
//    private Filter disabledProductFilter;
//
//    @Mock
//    private HttpServletRequest request;
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("Add product should save product with image URL")
//    void addProduct_shouldSaveProductWithImageUrl() {
//        // Create a sample ProductDto
//        ProductDto productDto = new ProductDto();
//        productDto.setName("Sample Product");
//        productDto.setDescription("Sample description");
//        productDto.setPrice(100.0);
//        productDto.setImageUrl(Arrays.asList("image1.jpg", "image2.jpg"));
//
//        // Create sample SubCategories, Categories, and User
//        List<SubCategory> subCategories = createSampleSubCategories();
//        List<Category> categories = createSampleCategories();
//        User user = new User();
//
//        // Call the method to add the product
//        productService.addProduct(productDto,request);
//
//        // Verify that the productRepository.save() was called once
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    private List<SubCategory> createSampleSubCategories() {
//        SubCategoryDto subCategoryDto = new SubCategoryDto();
//        subCategoryDto.setId(1L);
//        subCategoryDto.setCategoryId(1L);
//        subCategoryDto.setDescription("test sub category");
//        subCategoryDto.setImageURL("123456789.jpg");
//        subCategoryDto.setSubcategoryName("shirts");
//
//        // Create a sample category
//        Category sampleCategory = createSampleCategories().get(0);
//
//        // Create and return sample SubCategories
//        SubCategory subCategory1 = new SubCategory(subCategoryDto, sampleCategory);
//
//        // Add more as needed
//        return List.of(subCategory1);
//    }
//
//    private List<Category> createSampleCategories() {
//        // Create and return sample Categories
//        CategoryDto categoryDto = new CategoryDto();
//        categoryDto.setCategoryName("shirts");
//        categoryDto.setId(1L);
//        categoryDto.setImageUrl("qwertyuio.jpg");
//        categoryDto.setDescription("test category");
//        Category category1 = new Category(categoryDto);
//        return List.of(category1);
//    }
//
//
//
//
//    @Test
//    @DisplayName("Update product should update product")
//    void updateProduct_shouldUpdateProduct() {
//        // Create a sample ProductDto
//        ProductDto productDto = new ProductDto();
//        productDto.setName("Updated Product");
//        productDto.setDescription("Updated description");
//        productDto.setPrice(150.0);
//        productDto.setImageUrl(Collections.singletonList("updated_image.jpg"));
//
//        // Create sample SubCategories, Categories, and User
//        List<SubCategory> subCategories = new ArrayList<>(); // Initialize with actual subcategories
//        List<Category> categories = new ArrayList<>(); // Initialize with actual categories
//        User user = new User();
//        user.setId(1L);
//
//        // Create a sample existing product
//        Product existingProduct = new Product();
//        existingProduct.setId(1L);
//        existingProduct.setName("Original Product");
//        existingProduct.setDescription("Original description");
//        existingProduct.setPrice(100.0);
//
//        // Mock the behavior of productRepository.findByUserIdAndId
//        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(existingProduct);
//
//        // Execute the method
//        productService.updateProduct(1L, productDto, request);
//
//        // Verify that the product is updated with the correct values
//        verify(productRepository, times(1)).save(any(Product.class));
//        verify(productRepository).save(argThat(product -> {
//            // Verify that the product is updated with the correct values
//            assertEquals(productDto.getName(), product.getName());
//            assertEquals(productDto.getDescription(), product.getDescription());
//            assertEquals(productDto.getPrice(), product.getPrice(), 0.001); // Ensure double equality
//            return true;
//        }));
//    }
//
//
//    @Test
//    @DisplayName("Read product should return product")
//    void readProduct_shouldReturnProduct() {
//        // Create a sample product
//        Product product = new Product();
//        product.setId(1L);
//        product.setName("Sample Product");
//        product.setDescription("Sample description");
//        product.setPrice(100.0);
//
//        // Mock the behavior of productRepository.findById
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
//
//        // Execute the method
//        Optional<Product> result = productService.readProduct(1L);
//
//        // Verify that the correct product is returned
//        assertTrue(result.isPresent());
//        assertEquals(product, result.get());
//    }
//
//    @Test
//    @DisplayName("Read product should return empty optional for nonexistent product")
//    void readProduct_shouldReturnEmptyOptionalForNonexistentProduct() {
//        // Mock the behavior of productRepository.findById for a nonexistent product
//        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        // Execute the method
//        Optional<Product> result = productService.readProduct(100L); // Assuming product with ID 100 doesn't exist
//
//        // Verify that an empty Optional is returned
//        assertFalse(result.isPresent());
//    }
//
//    @Test
//    @DisplayName("List product by ID should return product DTO")
//    void listProductByid_shouldReturnProductDto() throws ProductNotExistException {
//        // Create a sample product and productDto
//        Product product = new Product();
//        product.setId(1L);
//        product.setName("Sample Product");
//        product.setDescription("Sample description");
//        product.setPrice(100.0);
//
//        // Mock the behavior of productRepository.findById
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
//
//        // Execute the method
//        ProductDto result = productService.listProductByid(1L);
//        // Verify that the correct ProductDto is returned
//        assertEquals(product.getName(), result.getName());
//        assertEquals(product.getDescription(), result.getDescription());
//    }
//
//    @Test
//    @DisplayName("List product by ID should throw ProductNotExistException for nonexistent product")
//    void listProductByid_shouldThrowProductNotExistExceptionForNonexistentProduct() {
//        // Mock the behavior of productRepository.findById for a nonexistent product
//        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        // Execute the method and verify that it throws ProductNotExistException
//        assertThrows(ProductNotExistException.class, () -> productService.listProductByid(100L)); // Assuming product with ID 100 doesn't exist
//    }
//
//
//    @Test
//    @DisplayName("Get product by ID should return product")
//    void getProductById_shouldReturnProduct() throws ProductNotExistException {
//        // Create a sample product
//        Product product = new Product();
//        product.setId(1L);
//        product.setName("Sample Product");
//        product.setDescription("Sample description");
//        product.setPrice(100.0);
//
//        // Mock the behavior of productRepository.findById
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
//
//        // Execute the method
//        Product result = productService.getProductById(1L);
//
//        // Verify that the correct Product is returned
//        assertEquals(product, result);
//    }
//
//    @Test
//    @DisplayName("Get product by ID should throw ProductNotExistException for nonexistent product")
//    void getProductById_shouldThrowProductNotExistExceptionForNonexistentProduct() {
//        // Mock the behavior of productRepository.findById for a nonexistent product
//        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        // Execute the method and verify that it throws ProductNotExistException
//        assertThrows(ProductNotExistException.class, () -> productService.getProductById(100L)); // Assuming product with ID 100 doesn't exist
//    }
//
//    @Test
//    @DisplayName("Delete product should mark product as deleted")
//    void deleteProduct_shouldMarkProductAsDeleted() {
//        // Create a sample User and Product
//        User user = new User();
//        user.setId(1L);
//        Product product = new Product();
//        product.setId(1L);
//
//        // Mock the behavior of productRepository.findByUserIdAndId
//        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(product);
//
//        // Execute the method
//        productService.deleteProduct(1L, request);
//
//        // Verify that the product is marked as deleted
//        assertTrue(product.isDeleted());
//        verify(productRepository, times(1)).save(product);
//    }
//
////    @Test
////    @DisplayName("Delete product should throw ProductNotExistException for nonexistent product")
////    void deleteProduct_shouldThrowProductNotExistExceptionForNonexistentProduct() {
////        // Mock the behavior of productRepository.findByUserIdAndId for a nonexistent product
////        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(null);
////
////        // Execute the method and verify that it throws ProductNotExistException
////        Executable executable = () -> productService.deleteProduct(100L,request); // Assuming product with ID 100 doesn't exist
////        assertThrows(ProductNotExistException.class, executable);
////    }
//
//
//    @Test
//    @DisplayName("Get product should return product")
//    void getProduct_shouldReturnProduct() {
//        // Create a sample User and Product
//        User user = new User();
//        user.setId(1L);
//        Product product = new Product();
//        product.setId(1L);
//
//        // Mock the behavior of productRepository.findByUserIdAndId
//        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(product);
//
//        // Execute the method
//        Product result = productService.getProduct(1L, 1L);
//
//        // Verify that the method returned the expected product
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    @DisplayName("Get product should return null for nonexistent product")
//    void getProduct_shouldReturnNullForNonexistentProduct() {
//        // Mock the behavior of productRepository.findByUserIdAndId for a nonexistent product
//        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(null);
//
//        // Execute the method
//        Product result = productService.getProduct(100L, 1L); // Assuming product with ID 100 doesn't exist
//
//        // Verify that the method returned null
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Disable product should disable product and update quantities")
//    void disableProduct_shouldDisableProductAndUpdateQuantities() {
//        // Create a sample User and Product
//        User user = new User();
//        Product product = new Product();
//        product.setAvailableQuantities(10);
//        product.setDisabledQuantities(0);
//        product.setAuditColumnsUpdate(user.getId());
//        product.setAuditColumnsCreate(user);
//        product.setDisabled(true);
//
//        // Execute the method
//        productService.disableProduct(product.getId(), 5, request);
//
//        // Verify that the product is disabled and quantities are updated
//        assertTrue(product.isDisabled());
//        assertEquals(5, product.getDisabledQuantities());
//        assertEquals(5, product.getAvailableQuantities());
//
//
//        // Verify that the productRepository.save() was called once
//        verify(productRepository, times(1)).save(product);
//    }
//
//    @Test
//    @DisplayName("Disable product should throw exception for invalid quantity")
//    void disableProduct_shouldThrowExceptionForInvalidQuantity() {
//        // Create a sample User and Product
//        User user = new User();
//        Product product = new Product();
//        product.setAvailableQuantities(10);
//        product.setDisabledQuantities(0);
//
//        // Try to disable more quantities than available
//        assertThrows(QuantityOutOfBoundException.class, () -> productService.disableProduct(product.getId(), 15, request));
//
//        // Verify that the product is not disabled and quantities are unchanged
//        assertFalse(product.isDisabled());
//        assertEquals(0, product.getDisabledQuantities());
//        assertEquals(10, product.getAvailableQuantities());
//
//        // Verify that the productRepository.save() was not called
//        verify(productRepository, never()).save(product);
//    }
//
//    @Test
//    void enableProduct() {
//        Long productId = 1L;
//        int quantity = 2;
//
//        User user = new User();
//        user.setId(1L);
//
//        Product product = new Product();
//        product.setId(1L);
//        product.setName("Product");
//        product.setUser(new User());
//        product.setQuantity(10);
//        product.setAvailableQuantities(4);
//        product.setDisabledQuantities(6);
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(product);
//
//        //Act
//        productService.enableProduct(productId, quantity, request);
//
//        //Assert
//        assertEquals(6, product.getAvailableQuantities());
//        assertEquals(4, product.getDisabledQuantities());
//    }
//
//    @Test
//    @DisplayName("Should remove ngrok prefix from image URL")
//    void ngrokLinkRemove_shouldRemoveNgrokPrefix() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        // Input image URL with ngrok prefix
//        String imageUrl = NGROK+"/image.jpg";
//
//        // Use reflection to access the private method
//        Method method = ProductServiceImpl.class.getDeclaredMethod("ngrokLinkRemove", String.class);
//        method.setAccessible(true);
//
//        // Execute the method
//        String result = (String) method.invoke(productService,imageUrl);
//
//        // Verify the result
//        assertEquals("/image.jpg", result); // The ngrok prefix should not be removed
//    }
//
//    @Test
//    @DisplayName("Should not remove prefix if not present in image URL")
//    void ngrokLinkRemove_shouldNotRemovePrefixIfNotPresent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
//        // Input image URL without ngrok prefix
//        String imageUrl = NGROK+"/image.jpg";
//
//        // Use reflection to access the private method
//        Method method = ProductServiceImpl.class.getDeclaredMethod("ngrokLinkRemove", String.class);
//        method.setAccessible(true);
//
//        // Execute the method
//        String result = (String) method.invoke(productService,imageUrl);
//
//
//        // Verify the result
//        assertEquals("/image.jpg", result); // The input should remain unchanged
//    }
//
//    private List<Product> createDummyProducts() {
//        User user = new User();
//
//        SubCategory subCategory = new SubCategory();
//        subCategory.setId(1L);
//
//        Category category = new Category();
//        category.setId(1L);
//
//        List<Product> products = new ArrayList<>();
//
//        Product product1 = new Product();
//        product1.setId(1L);
//        product1.setName("Product 1");
//        product1.setPrice(10.0);
//        product1.setBrand("Brand A");
//        product1.setSize("M");
//        product1.setUser(user);
//        product1.setSubCategories((List.of(subCategory)));
//        product1.setCategories(List.of(category));
//
//
//        Product product2 = new Product();
//        product2.setId(2L);
//        product2.setName("Product 2");
//        product2.setBrand("Brand B");
//        product2.setPrice(20.0);
//        product2.setSize("S");
//        product2.setUser(user);
//        product2.setSubCategories((List.of(subCategory)));
//        product2.setCategories(List.of(category));
//
//        products.add(product1);
//        products.add(product2);
//
//        return products;
//    }
//
//
//    @Test
//    @DisplayName("Should list products")
//    void listProducts() {
//
//        int pageNumber = 0;
//        int pageSize = 10;
//        User user = new User();
//       List<Product> products = createDummyProducts();
//
//        Page<Product> page = new PageImpl<>(products);
//
//        // Mock the behavior of the productRepository
//        when(productRepository.findAllByUserNot(any(Pageable.class), any(User.class))).thenReturn(page);
//
//        when(entityManager.unwrap(Session.class)).thenReturn(session);
//        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(mock(Filter.class));
//        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(mock(Filter.class));
//
//        // Set the EntityManager for the ProductService
//        ReflectionTestUtils.setField(productService, "entityManager", entityManager);
//        // When
//        List<ProductDto> productDtos = productService.listProducts(pageNumber, pageSize, request);
//
//        // Then
//        assertEquals(products.size(), productDtos.size());
//        verify(productRepository, times(1)).findAllByUserNot(any(Pageable.class), any(User.class));
//        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);
//    }
//
//
//
//    @Test
//    void listProductsById_specificSubcategoryId() {
//        //Arrange
//        Session session = mock(Session.class);
//        Filter deletedProductFilter = mock(Filter.class);
//        Filter disabledProductFilter = mock(Filter.class);
//
//        Long subcategoryId = 1L;
//
//        User user = new User();
//        user.setId(1L);
//
//        Product product1 = new Product();
//        product1.setId(1L);
//        product1.setName("Product 1");
//        product1.setUser(new User());
//
//        Product product2 = new Product();
//        product2.setId(2L);
//        product2.setName("Product 2");
//        product2.setUser(new User());
//
//        SubCategory subCategory = new SubCategory();
//        subCategory.setId(1L);
//        subCategory.setSubcategoryName("Subcategory");
//        subCategory.setDescription("Description");
//        subCategory.setImageUrl("/image-url.jpeg");
//
//        when(entityManager.unwrap(Session.class)).thenReturn(session);
//        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
//        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);
//
//        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(Optional.of(subCategory));
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productRepository.findBySubCategoriesId(anyLong())).thenReturn(List.of(product1, product2));
//
//        //Act
//        List<ProductDto> productDtoList = productService.listProductsById(subcategoryId, request);
//
//        //Assert
//        assertEquals(2, productDtoList.size());
//        assertEquals(product1.getName(), productDtoList.get(0).getName());
//        assertEquals(product2.getName(), productDtoList.get(1).getName());
//    }
//
//
//    @Test
//    @DisplayName("Should list products by category ID")
//    void listProductsByCategoryId() {
//        // Given
//        List<Product> products = createDummyProducts();
//        User user1 = new User();
//
//        when(productRepository.findByCategoriesId(1L)).thenReturn(products);
//
//
//
//        when(entityManager.unwrap(Session.class)).thenReturn(session);
//        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
//        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);
//        // Set the EntityManager for the ProductService
//        ReflectionTestUtils.setField(productService, "entityManager", entityManager);
//
//        // When
//        List<ProductDto> productDtos = productService.listProductsByCategoryId(1L, request);
//
//        // Then
//        assertNotNull(productDtos);
//        assertEquals(products.size(), productDtos.size());
//        verify(productRepository, times(1)).findByCategoriesId(1L);
//        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);
//
//    }
//
//    @Test
//    void testListProductsDesc() {
//        // Create a list of dummy products
//        User user = new User();
//        List<Product> products = new ArrayList<>();
//        Product product1 = new Product();
//        product1.setId(1L);
//        product1.setName("Product 1");
//        product1.setPrice(10.0);
//        product1.setUser(user);
//
//        Product product2 = new Product();
//        product2.setId(2L);
//        product2.setName("Product 2");
//        product2.setPrice(20.0);
//        product2.setUser(user);
//
//        products.add(product1);
//        products.add(product2);
//
//        Session session = mock(Session.class);
//        Filter deletedProductFilter = mock(Filter.class);
//        when(entityManager.unwrap(Session.class)).thenReturn(session);
//        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//
//        // Mock the behavior of the productRepository.findAllByUser()
//        when(productRepository.findAllByUser(user, Sort.by(Sort.Direction.DESC, "id"))).thenReturn(products);
//
//
//        // Call the listProductsDesc() method
//        List<ProductDto> productList = productService.listProductsDesc(request);
//
//        // Verify the result
//        assertEquals(2, productList.size());
//        assertEquals(1L, productList.get(0).getId());
//        assertEquals("Product 1", productList.get(0).getName());
//        assertEquals(10.0, productList.get(0).getPrice());
//        assertEquals(2L, productList.get(1).getId());
//        assertEquals("Product 2", productList.get(1).getName());
//        assertEquals(20.0, productList.get(1).getPrice());
//
//        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
//    }
//
//
//    @Test
//    @DisplayName("Should list owner products")
//    void testListOwnerProducts() {
//        // Create a user
//        User user = new User();
//        user.setId(1L);
//
//        List<Product> products =createDummyProducts();
//        when(entityManager.unwrap(Session.class)).thenReturn(session);
//        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
//
//
//        // Mock the behavior of the productRepository.findAllByUser()
//        when(productRepository.findAllByUser(user)).thenReturn(products);
//
//        // Create an instance of ProductServiceImpl and pass the mocked productRepository
//        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager,helper,subCategoryService,categoryService);
//
//        // Call the listOwnerProducts() method
//        List<ProductDto> productList = productService.listOwnerProducts(request);
//
//        // Verify the result
//        assertEquals(2, productList.size());
//        assertEquals(1L, productList.get(0).getId());
//        assertEquals("Product 1", productList.get(0).getName());
//        assertEquals(10.0, productList.get(0).getPrice());
//        assertEquals(2L, productList.get(1).getId());
//        assertEquals("Product 2", productList.get(1).getName());
//        assertEquals(20.0, productList.get(1).getPrice());
//
//        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
//    }
//
//
//    @Test
//    @DisplayName("Should get products by price range")
//    void testGetProductsByPriceRange() {
//
//        List<Product> products = createDummyProducts();
//
//        when(entityManager.unwrap(Session.class)).thenReturn(session);
//        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
//        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);
//
//        // Mock the behavior of the productRepository.findProductByPriceRange()
//        when(productRepository.findProductByPriceRange(0.0, 15.0)).thenReturn(products);
//
//        // Create an instance of ProductServiceImpl and pass the mocked productRepository
//        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager,helper,subCategoryService,categoryService);
//
//        // Call the getProductsByPriceRange() method
//        List<ProductDto> productList = productService.getProductsByPriceRange(0.0, 15.0);
//
//        // Verify the result
//        assertEquals(2, productList.size());
//        assertEquals(1L, productList.get(0).getId());
//        assertEquals("Product 1", productList.get(0).getName());
//        assertEquals(10.0, productList.get(0).getPrice());
//        assertEquals(2L, productList.get(1).getId());
//        assertEquals("Product 2", productList.get(1).getName());
//        assertEquals(20.0, productList.get(1).getPrice());
//
//        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);
//
//    }
//
//    @Test
//    @DisplayName("Should search products")
//    void testSearchProducts() {
//
//        User user1 = new User();
//        List<Product> products =createDummyProducts();
//
//        when(entityManager.unwrap(Session.class)).thenReturn(session);
//        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
//        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);
//
//        // Mock the behavior of the productRepository.searchProducts()
//        when(productRepository.searchProducts("Product")).thenReturn(products);
//
//        // Create an instance of ProductServiceImpl and pass the mocked productRepository
//        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager,helper,subCategoryService,categoryService);
//
//        // Call the searchProducts() method
//        List<ProductDto> productList = productService.searchProducts("Product", request);
//
//        // Verify the result
//        assertEquals(2, productList.size());
//        assertEquals(1L, productList.get(0).getId());
//        assertEquals("Product 1", productList.get(0).getName());
//        assertEquals("Brand A", productList.get(0).getBrand());
//        assertEquals(2L, productList.get(1).getId());
//        assertEquals("Product 2", productList.get(1).getName());
//        assertEquals("Brand B", productList.get(1).getBrand());
//
//        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);
//    }
//
//    @Test
//    @DisplayName("Should filter products")
//    void testFilterProducts() {
//        List<Product> products = createDummyProducts();
//
//        when(entityManager.unwrap(Session.class)).thenReturn(session);
//        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
//        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);
//
//        // Mock the behavior of the productRepository.findBySubCategoriesId()
//        when(productRepository.findBySubCategoriesId(1L)).thenReturn(products);
//
//        // Create an instance of ProductServiceImpl and pass the mocked productRepository
//        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager,helper,subCategoryService,categoryService);
//
//        // Call the filterProducts() method
//        List<ProductDto> productList = productService.filterProducts("S", 1L, 0.0, 20.0);
//
//        // Verify the result
//        assertEquals(1, productList.size());
//        assertEquals(2L, productList.get(0).getId());
//        assertEquals("Product 2", productList.get(0).getName());
//        assertEquals("Brand B", productList.get(0).getBrand());
//        assertEquals(20.0, productList.get(0).getPrice());
//        assertEquals("S", productList.get(0).getSize());
//
//        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
//        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);
//    }
//
//
//
//
//
//}


package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nineleaps.leaps.config.MessageStrings.DELETED_PRODUCT_FILTER;
import static com.nineleaps.leaps.config.MessageStrings.DISABLED_PRODUCT_FILTER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Helper helper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CategoryServiceInterface categoryService;

    @Mock
    private SubCategoryServiceInterface subCategoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDtoFromProduct() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setBrand("Test Brand");

        // When
        ProductDto productDto = ProductServiceImpl.getDtoFromProduct(product);

        // Then
        assertNotNull(productDto);
        assertEquals(product.getId(), productDto.getId());
        assertEquals(product.getName(), productDto.getName());
        assertEquals(product.getBrand(), productDto.getBrand());
    }

    @Test
    void addProduct() {
        //Arrange
        User user = new User();
        user.setId(1L);

        ProductDto productDto = new ProductDto();
        productDto.setBrand("Brand");
        productDto.setName("Product");
        productDto.setDescription("Description");
        productDto.setPrice(100.0);
        productDto.setSize("Size");
        productDto.setImageUrl(List.of("/image-url.jpeg"));
        productDto.setColor("Color");
        productDto.setCategoryIds(List.of(1L));
        productDto.setSubcategoryIds(List.of(1L));

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Category");
        category.setDescription("Description");
        category.setImageUrl("/image-url.jpeg");

        SubCategory subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setCategory(category);
        subCategory.setSubcategoryName("Subcategory");
        subCategory.setDescription("Description");
        subCategory.setImageUrl("/image-url.jpeg");

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(categoryService.getCategoriesFromIds(anyList())).thenReturn(List.of(category));
        when(subCategoryService.getSubCategoriesFromIds(anyList())).thenReturn(List.of(subCategory));

        //Act
        productService.addProduct(productDto, request);

        //Assert
        verify(productRepository, times(2)).save(any(Product.class));

    }

    @Test
    void listProducts() {
        // Create a list of dummy products
        int pageNumber = 0;
        int pageSize = 10;

        User user = new User();

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(10.0);
        product1.setUser(user);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(20.0);
        product2.setUser(user);

        products.add(product1);
        products.add(product2);

        Page<Product> page = new PageImpl<>(products);

        // Mock the behavior of the productRepository
        when(productRepository.findAllByUserNot(any(Pageable.class), any(User.class))).thenReturn(page);

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        when(helper.getUserFromToken(request)).thenReturn(user);


        // When
        List<ProductDto> productDtos = productService.listProducts(pageNumber, pageSize, request);

        // Then
        assertEquals(products.size(), productDtos.size());
        verify(productRepository, times(1)).findAllByUserNot(any(Pageable.class), any(User.class));
        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);
    }


    @Test
    void listSuggestions() {
        String suggestion1 = "blue shirt";
        String suggestion2 = "blue shirt";
        assertSame(suggestion1, suggestion2);
    }

    @Test
    void updateProduct_existingProduct() {
        //Arrange
        User user = new User();
        user.setId(1L);

        ProductDto productDto = new ProductDto();
        productDto.setBrand("Brand");
        productDto.setName("Product");
        productDto.setDescription("Description");
        productDto.setPrice(100.0);
        productDto.setSize("Size");
        productDto.setImageUrl(List.of("/image-url.jpeg"));
        productDto.setColor("Color");
        productDto.setCategoryIds(List.of(1L));
        productDto.setSubcategoryIds(List.of(1L));

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Category");
        category.setDescription("Description");
        category.setImageUrl("/image-url.jpeg");

        SubCategory subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setCategory(category);
        subCategory.setSubcategoryName("Subcategory");
        subCategory.setDescription("Description");
        subCategory.setImageUrl("/image-url.jpeg");

        Long productId = 1L;
        Product product = new Product(productDto, List.of(subCategory), List.of(category), user);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(categoryService.getCategoriesFromIds(anyList())).thenReturn(List.of(category));
        when(subCategoryService.getSubCategoriesFromIds(anyList())).thenReturn(List.of(subCategory));

        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(product);

        //Act
        productService.updateProduct(productId, productDto, request);

        //Assert
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void readProduct_existingProduct() {
        // Given
        Long productId = 1L;
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        Optional<Product> result = productService.readProduct(productId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void readProduct_nonExistingProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productService.readProduct(productId);

        // Then
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void listProductsById_specificSubcategoryId() {
        //Arrange
        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);

        Long subcategoryId = 1L;

        User user = new User();
        user.setId(1L);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setUser(new User());

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setUser(new User());

        SubCategory subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setSubcategoryName("Subcategory");
        subCategory.setDescription("Description");
        subCategory.setImageUrl("/image-url.jpeg");

        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(Optional.of(subCategory));
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productRepository.findBySubCategoriesId(anyLong())).thenReturn(List.of(product1, product2));

        //Act
        List<ProductDto> productDtoList = productService.listProductsById(subcategoryId, request);

        //Assert
        assertEquals(2, productDtoList.size());
        assertEquals(product1.getName(), productDtoList.get(0).getName());
        assertEquals(product2.getName(), productDtoList.get(1).getName());
    }

    @Test
    void listProductsByCategoryId() {
        //Arrange
        Long categoryId = 1L;

        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Category");
        category.setDescription("Description");
        category.setImageUrl("/image-url.jpeg");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setUser(new User());

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setUser(new User());

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);

        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        when(categoryService.readCategory(anyLong())).thenReturn(Optional.of(category));
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productRepository.findByCategoriesId(anyLong())).thenReturn(List.of(product1, product2));

        //Act
        List<ProductDto> productDtoList = productService.listProductsByCategoryId(categoryId, request);

        //Assert
        assertEquals(2, productDtoList.size());
        assertEquals(product1.getName(), productDtoList.get(0).getName());
        assertEquals(product2.getName(), productDtoList.get(1).getName());
    }

    @Test
    void testListProductByid() throws ProductNotExistException {
        // Create a dummy product
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setPrice(10.0);

        // Mock the behavior of productRepository.findById()
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Call the listProductByid() method
        ProductDto productDto = productService.listProductByid(1L);

        // Verify the result
        assertEquals(1L, productDto.getId());
        assertEquals("Product 1", productDto.getName());
        assertEquals(10.0, productDto.getPrice());
    }

    @Test
    void testListProductByid_ProductNotExist() {
        // Mock the behavior of productRepository.findById() with an empty Optional
        when(productRepository.findById(1L)).thenReturn(Optional.empty());


        // Call the listProductByid() method and expect a ProductNotExistException to be thrown
        assertThrows(ProductNotExistException.class, () -> {
            productService.listProductByid(1L);
        });
    }


    @Test
    void testGetProductById() throws ProductNotExistException {
        // Create a dummy product
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setPrice(10.0);

        // Mock the behavior of productRepository.findById()
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));


        // Call the getProductById() method
        Product resultProduct = productService.getProductById(1L);

        // Verify the result
        assertEquals(1L, resultProduct.getId());
        assertEquals("Product 1", resultProduct.getName());
        assertEquals(10.0, resultProduct.getPrice());
    }

    @Test
    void testListProductsDesc() {
        // Create a list of dummy products
        User user = new User();
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10.0);
        product1.setUser(user);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(20.0);
        product2.setUser(user);

        products.add(product1);
        products.add(product2);

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);

        when(helper.getUserFromToken(request)).thenReturn(user);


        // Mock the behavior of the productRepository.findAllByUser()
        when(productRepository.findAllByUser(user, Sort.by(Sort.Direction.DESC, "id"))).thenReturn(products);


        // Call the listProductsDesc() method
        List<ProductDto> productList = productService.listProductsDesc(request);

        // Verify the result
        assertEquals(2, productList.size());
        assertEquals(1L, productList.get(0).getId());
        assertEquals("Product 1", productList.get(0).getName());
        assertEquals(10.0, productList.get(0).getPrice());
        assertEquals(2L, productList.get(1).getId());
        assertEquals("Product 2", productList.get(1).getName());
        assertEquals(20.0, productList.get(1).getPrice());

        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
    }

    @Test
    void testListOwnerProducts() {
        // Create a user
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        // Create a list of dummy products
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10.0);
        product1.setUser(user);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(20.0);
        product2.setUser(user);

        products.add(product1);
        products.add(product2);

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);

        when(helper.getUserFromToken(request)).thenReturn(user);


        // Mock the behavior of the productRepository.findAllByUser()
        when(productRepository.findAllByUser(user)).thenReturn(products);

        // Call the listOwnerProducts() method
        List<ProductDto> productList = productService.listOwnerProducts(request);

        // Verify the result
        assertEquals(2, productList.size());
        assertEquals(1L, productList.get(0).getId());
        assertEquals("Product 1", productList.get(0).getName());
        assertEquals(10.0, productList.get(0).getPrice());
        assertEquals(2L, productList.get(1).getId());
        assertEquals("Product 2", productList.get(1).getName());
        assertEquals(20.0, productList.get(1).getPrice());

        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
    }

    @Test
    void testGetProductsByPriceRange() {
        // Create a list of dummy products
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10.0);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(20.0);

        products.add(product1);
        products.add(product2);

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        // Mock the behavior of the productRepository.findProductByPriceRange()
        when(productRepository.findProductByPriceRange(0.0, 15.0)).thenReturn(products);

        // Call the getProductsByPriceRange() method
        List<ProductDto> productList = productService.getProductsByPriceRange(0.0, 15.0);

        // Verify the result
        assertEquals(2, productList.size());
        assertEquals(1L, productList.get(0).getId());
        assertEquals("Product 1", productList.get(0).getName());
        assertEquals(10.0, productList.get(0).getPrice());
        assertEquals(2L, productList.get(1).getId());
        assertEquals("Product 2", productList.get(1).getName());
        assertEquals(20.0, productList.get(1).getPrice());

        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);

    }

    @Test
    void testSearchProducts() {
        String query = "Product";

        User user = new User();
        user.setId(1L);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setUser(new User());

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setUser(new User());

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);

        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productRepository.searchProducts(query)).thenReturn(List.of(product1, product2));

        doNothing().when(session).disableFilter(DELETED_PRODUCT_FILTER);
        doNothing().when(session).disableFilter(DISABLED_PRODUCT_FILTER);

        //Act
        List<ProductDto> productDtoList = productService.searchProducts(query, request);

        //Assert
        assertEquals(2, productDtoList.size());
        assertEquals(product1.getName(), productDtoList.get(0).getName());
        assertEquals(product2.getName(), productDtoList.get(1).getName());
    }


    @Test
    void testFilterProducts() {
        // Create a list of dummy products
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setBrand("Brand A");
        product1.setPrice(10.0);
        product1.setSize("S");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setBrand("Brand B");
        product2.setPrice(20.0);
        product2.setSize("M");

        products.add(product1);
        products.add(product2);

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        // Mock the behavior of the productRepository.findBySubCategoriesId()
        when(productRepository.findBySubCategoriesId(1L)).thenReturn(products);

        // Call the filterProducts() method
        List<ProductDto> productList = productService.filterProducts("S", 1L, 0.0, 15.0);

        // Verify the result
        assertEquals(1, productList.size());
        assertEquals(1L, productList.get(0).getId());
        assertEquals("Product 1", productList.get(0).getName());
        assertEquals("Brand A", productList.get(0).getBrand());
        assertEquals(10.0, productList.get(0).getPrice());
        assertEquals("S", productList.get(0).getSize());

        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);
    }

    @Test
    void testDeleteProduct() {

        // Create a mock object
        Product product = Mockito.mock(Product.class);

        // Create a dummy product
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        product.setId(1L);
        product.setName("Product 1");
        product.setUser(user);

        when(helper.getUserFromToken(request)).thenReturn(user);


        // Configure the mock repository to return the test product when findById() is called
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Mock the behavior of the productRepository.findByUserIdAndId()
        when(productRepository.findByUserIdAndId(1L, 1L)).thenReturn(product);

        // Call the deleteProduct() method
        productService.deleteProduct(1L, request);

        // Verify that the product is marked as deleted and saved
        verify(product).setDeleted(true);
        verify(productRepository).save(product);
    }

    @Test
    void testDeleteProduct_ProductNotExistException() {
        User user = new User();
        user.setId(1L);

        when(helper.getUserFromToken(request)).thenReturn(user);
        // Mock the behavior of the productRepository.findByUserIdAndId() to return null
        when(productRepository.findByUserIdAndId(1L, 1L)).thenReturn(null);


        // Call the deleteProduct() method and assert that it throws ProductNotExistException
        assertThrows(ProductNotExistException.class, () -> productService.deleteProduct(1L, request));
    }


    @Test
    void getProduct() {
        // Create a dummy user
        User user = new User();
        user.setId(1L);

        // Create a dummy product
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Product 1");
        product.setUser(user);

        // Configure the mock repository to return the test product when findByUserIdAndId() is called
        when(productRepository.findByUserIdAndId(user.getId(), productId)).thenReturn(product);

        // Call the getProduct() method
        Product result = productService.getProduct(productId, user.getId());

        // Verify the product is retrieved correctly
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Product 1", result.getName());

        // Verify that the findByUserIdAndId() method is called on the productRepository
        verify(productRepository).findByUserIdAndId(user.getId(), productId);
    }

    @Test
    void disableProduct() {
        Long productId = 1L;
        int quantity = 2;

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setUser(new User());
        product.setQuantity(10);
        product.setAvailableQuantities(4);
        product.setDisabledQuantities(6);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(product);

        //Act
        productService.disableProduct(productId, quantity, request);

        //Assert
        assertEquals(2, product.getAvailableQuantities());
        assertEquals(8, product.getDisabledQuantities());
    }


    @Test
    void enableProduct() {
        Long productId = 1L;
        int quantity = 2;

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setUser(new User());
        product.setQuantity(10);
        product.setAvailableQuantities(4);
        product.setDisabledQuantities(6);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productRepository.findByUserIdAndId(anyLong(), anyLong())).thenReturn(product);

        //Act
        productService.enableProduct(productId, quantity, request);

        //Assert
        assertEquals(6, product.getAvailableQuantities());
        assertEquals(4, product.getDisabledQuantities());
    }
}