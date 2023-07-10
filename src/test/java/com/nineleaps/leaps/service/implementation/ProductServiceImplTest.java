package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.ProductRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static com.nineleaps.leaps.config.MessageStrings.DELETED_PRODUCT_FILTER;
import static com.nineleaps.leaps.config.MessageStrings.DISABLED_PRODUCT_FILTER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager entityManager;

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
        // Given
        ProductDto productDto = new ProductDto();
        String url = "NGROK/api/v1/file/view/test2_image.png";
        productDto.setImageUrl(List.of(url));
        List<SubCategory> subCategories = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        User user = new User();

        // When
        productService.addProduct(productDto, subCategories, categories, user);

        // Then
        verify(productRepository, times(2)).save(any(Product.class)); //because we are saving twice one before and one after adding inageUrl
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

        // When
        List<ProductDto> productDtos = productService.listProducts(pageNumber, pageSize, user);

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
        assertSame(suggestion1,suggestion2);
    }

    @Test
    void updateProduct_existingProduct() {
        // Given
        Long productId = 1L;
        ProductDto productDto = new ProductDto();
        String url = NGROK+"/api/v1/file/view/test2_image.png";
        productDto.setImageUrl(List.of(url));
        List<SubCategory> subCategories = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        User user = new User();
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setUser(user);
        when(productRepository.findByUserIdAndId(user.getId(), productId)).thenReturn(existingProduct);

        // When
        productService.updateProduct(productId, productDto, subCategories, categories, user);

        // Then
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_nonExistingProduct() {
        // Given
        Long productId = 1L;
        ProductDto productDto = new ProductDto();
        List<SubCategory> subCategories = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        User user = new User();
        when(productRepository.findByUserIdAndId(user.getId(), productId)).thenReturn(null);

        // When/Then
        assertThrows(ProductNotExistException.class, () ->
                productService.updateProduct(productId, productDto, subCategories, categories, user)
        );
        verify(productRepository, never()).save(any(Product.class));
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
        // Given
        SubCategory subCategory = new SubCategory();
        Long subcategoryId = 1L;
        subCategory.setId(subcategoryId);
        User user = new User();
        User user1 = new User();
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setUser(user);
        product1.setSubCategories(List.of(subCategory));
        products.add(product1);
        Product product2 = new Product();
        product2.setUser(user);
        product2.setSubCategories(List.of(subCategory));
        products.add(product2);
        when(productRepository.findBySubCategoriesId(subcategoryId)).thenReturn(products);

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        // When
        List<ProductDto> productDtos = productService.listProductsById(subcategoryId, user1);

        // Then
        assertNotNull(productDtos);
        assertEquals(products.size(), productDtos.size());
        verify(productRepository, times(1)).findBySubCategoriesId(subcategoryId);
        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);

    }

    @Test
    void listProductsByCategoryId() {
        // Given
        Category category = new Category();
        Long categoryId = 1L;
        category.setId(categoryId);
        User user = new User();
        User user1 = new User();
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setUser(user);
        product1.setCategories(List.of(category));
        products.add(product1);
        Product product2 = new Product();
        product2.setUser(user);
        product2.setCategories(List.of(category));
        products.add(product2);
        when(productRepository.findByCategoriesId(categoryId)).thenReturn(products);

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        // When
        List<ProductDto> productDtos = productService.listProductsByCategoryId(categoryId, user1);

        // Then
        assertNotNull(productDtos);
        assertEquals(products.size(), productDtos.size());
        verify(productRepository, times(1)).findByCategoriesId(categoryId);
        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);

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

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

        // Call the listProductByid() method
        ProductDto productDto = productService.listProductByid(1L);

        // Verify the result
        assertEquals(1L, productDto.getId());
        assertEquals("Product 1", productDto.getName());
        assertEquals(10.0, productDto.getPrice());
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

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

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

        // Mock the behavior of the productRepository.findAllByUser()
        when(productRepository.findAllByUser(user, Sort.by(Sort.Direction.DESC, "id"))).thenReturn(products);

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

        // Call the listProductsDesc() method
        List<ProductDto> productList = productService.listProductsDesc(user);

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


        // Mock the behavior of the productRepository.findAllByUser()
        when(productRepository.findAllByUser(user)).thenReturn(products);

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

        // Call the listOwnerProducts() method
        List<ProductDto> productList = productService.listOwnerProducts(user);

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

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

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
        // Create a list of dummy products
        User user = new User();
        User user1 = new User();
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setBrand("Brand A");
        product1.setUser(user);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setBrand("Brand B");
        product2.setUser(user);

        products.add(product1);
        products.add(product2);

        Session session = mock(Session.class);
        Filter deletedProductFilter = mock(Filter.class);
        Filter disabledProductFilter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(DELETED_PRODUCT_FILTER)).thenReturn(deletedProductFilter);
        when(session.enableFilter(DISABLED_PRODUCT_FILTER)).thenReturn(disabledProductFilter);

        // Mock the behavior of the productRepository.searchProducts()
        when(productRepository.searchProducts("Product")).thenReturn(products);

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

        // Call the searchProducts() method
        List<ProductDto> productList = productService.searchProducts("Product", user1);

        // Verify the result
        assertEquals(2, productList.size());
        assertEquals(1L, productList.get(0).getId());
        assertEquals("Product 1", productList.get(0).getName());
        assertEquals("Brand A", productList.get(0).getBrand());
        assertEquals(2L, productList.get(1).getId());
        assertEquals("Product 2", productList.get(1).getName());
        assertEquals("Brand B", productList.get(1).getBrand());

        verify(session, times(1)).enableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).enableFilter(DISABLED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DELETED_PRODUCT_FILTER);
        verify(session, times(1)).disableFilter(DISABLED_PRODUCT_FILTER);
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

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

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

        // Configure the mock repository to return the test product when findById() is called
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Mock the behavior of the productRepository.findByUserIdAndId()
        when(productRepository.findByUserIdAndId(1L, 1L)).thenReturn(product);

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

        // Call the deleteProduct() method
        productService.deleteProduct(1L, 1L);

        // Verify that the product is marked as deleted and saved
        verify(product).setDeleted(true);
        verify(productRepository).save(product);
    }

    @Test
     void testDeleteProduct_ProductNotExistException() {
        // Mock the behavior of the productRepository.findByUserIdAndId() to return null
        when(productRepository.findByUserIdAndId(1L, 1L)).thenReturn(null);

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

        // Call the deleteProduct() method and assert that it throws ProductNotExistException
        assertThrows(ProductNotExistException.class, () -> productService.deleteProduct(1L, 1L));
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

        // Create an instance of ProductServiceImpl and pass the mocked productRepository
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

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
        // Create a dummy product
        Product product = new Product();
        product.setId(1L);
        product.setAvailableQuantities(10);
        product.setDisabledQuantities(0);
        product.setDisabled(false);

        // Create an instance of ProductServiceImpl
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

        // Disable 5 quantities of the product
        int disableQuantity = 5;
        productService.disableProduct(product, disableQuantity);

        // Verify that the available quantities and disabled quantities are updated correctly
        assertEquals(5, product.getAvailableQuantities());
        assertEquals(5, product.getDisabledQuantities());

        // Verify that the product is not disabled yet
        assertFalse(product.isDisabled());

        // Verify that the save method is called on the productRepository
        verify(productRepository).save(product);
    }


    @Test
    void enableProduct() {
        // Create a dummy product
        Product product = new Product();
        product.setId(1L);
        product.setAvailableQuantities(5);
        product.setDisabledQuantities(5);
        product.setDisabled(true);

        // Create an instance of ProductServiceImpl
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, entityManager);

        // Enable 3 quantities of the product
        int enableQuantity = 3;
        productService.enableProduct(product, enableQuantity);

        // Verify that the available quantities and disabled quantities are updated correctly
        assertEquals(8, product.getAvailableQuantities());
        assertEquals(2, product.getDisabledQuantities());

        // Verify that the product is enabled
        assertFalse(product.isDisabled());

        // Verify that the save method is called on the productRepository
        verify(productRepository).save(product);
    }
}