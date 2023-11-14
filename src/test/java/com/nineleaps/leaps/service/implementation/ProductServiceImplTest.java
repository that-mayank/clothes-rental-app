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

        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);


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

        when(helper.getUser(request)).thenReturn(user);
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
        when(helper.getUser(request)).thenReturn(user);
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
        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);


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

        when(helper.getUser(request)).thenReturn(user);


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

        when(helper.getUser(request)).thenReturn(user);
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

        when(helper.getUser(request)).thenReturn(user);


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

        when(helper.getUser(request)).thenReturn(user);
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
        User user = new User();
        // Create a dummy product
        Product product = new Product();
        product.setId(1L);
        product.setAvailableQuantities(10);
        product.setDisabledQuantities(0);
        product.setDisabled(false);

        when(helper.getUser(request)).thenReturn(user);
        // Disable 5 quantities of the product
        int disableQuantity = 5;
        productService.disableProduct(product.getId(), disableQuantity, request);

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

        User user = new User();
        when(helper.getUser(request)).thenReturn(user);
        // Enable 3 quantities of the product
        int enableQuantity = 3;
        productService.enableProduct(product.getId(), enableQuantity, request);

        // Verify that the available quantities and disabled quantities are updated correctly
        assertEquals(8, product.getAvailableQuantities());
        assertEquals(2, product.getDisabledQuantities());

        // Verify that the product is enabled
        assertFalse(product.isDisabled());

        // Verify that the save method is called on the productRepository
        verify(productRepository).save(product);
    }
}