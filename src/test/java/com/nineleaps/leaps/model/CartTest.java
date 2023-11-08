package com.nineleaps.leaps.model;

import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    void getId() {
        cart.setId(1L);
        assertEquals(1L, cart.getId());
    }

    @Test
    void getCreateDate() {
        Date createDate = new Date();
        cart.setCreateDate(createDate);
        assertEquals(createDate, cart.getCreateDate());
    }

    @Test
    void getProduct() {
        Product product = new Product();
        cart.setProduct(product);
        assertEquals(product, cart.getProduct());
    }

    @Test
    void getUser() {
        User user = new User();
        cart.setUser(user);
        assertEquals(user, cart.getUser());
    }

    @Test
    void getQuantity() {
        cart.setQuantity(5);
        assertEquals(5, cart.getQuantity());
    }

    @Test
    void getRentalStartDate() {
        LocalDateTime rentalStartDate = LocalDateTime.now();
        cart.setRentalStartDate(rentalStartDate);
        assertEquals(rentalStartDate, cart.getRentalStartDate());
    }

    @Test
    void getRentalEndDate() {
        LocalDateTime rentalEndDate = LocalDateTime.now().plusDays(7);
        cart.setRentalEndDate(rentalEndDate);
        assertEquals(rentalEndDate, cart.getRentalEndDate());
    }

    @Test
    void getImageUrl() {
        String imageUrl = "https://example.com/image.jpg";
        cart.setImageUrl(imageUrl);
        assertEquals(imageUrl, cart.getImageUrl());
    }

    @Test
    void setId() {
        cart.setId(2L);
        assertEquals(2L, cart.getId());
    }

    @Test
    void setCreateDate() {
        Date createDate = new Date();
        cart.setCreateDate(createDate);
        assertEquals(createDate, cart.getCreateDate());
    }

    @Test
    void setProduct() {
        Product product = new Product();
        cart.setProduct(product);
        assertEquals(product, cart.getProduct());
    }

    @Test
    void setUser() {
        User user = new User();
        cart.setUser(user);
        assertEquals(user, cart.getUser());
    }

    @Test
    void setQuantity() {
        cart.setQuantity(3);
        assertEquals(3, cart.getQuantity());
    }

    @Test
    void setRentalStartDate() {
        LocalDateTime rentalStartDate = LocalDateTime.now();
        cart.setRentalStartDate(rentalStartDate);
        assertEquals(rentalStartDate, cart.getRentalStartDate());
    }

    @Test
    void setRentalEndDate() {
        LocalDateTime rentalEndDate = LocalDateTime.now().plusDays(14);
        cart.setRentalEndDate(rentalEndDate);
        assertEquals(rentalEndDate, cart.getRentalEndDate());
    }

    @Test
    void setImageUrl() {
        String imageUrl = "https://example.com/product.jpg";
        cart.setImageUrl(imageUrl);
        assertEquals(imageUrl, cart.getImageUrl());
    }

    @Test
    void constructorWithParameters() {
        Product product = new Product();
        User user = new User();
        int quantity = 4;
        LocalDateTime rentalStartDate = LocalDateTime.now();
        LocalDateTime rentalEndDate = rentalStartDate.plusDays(7);
        List<ProductUrl> productUrls = new ArrayList<>();
        productUrls.add(new ProductUrl(1L,"https://example.com/image1.jpg", product));

        cart = new Cart(product, user, quantity, rentalStartDate, rentalEndDate, productUrls);

        assertEquals(product, cart.getProduct());
        assertEquals(user, cart.getUser());
        assertEquals(quantity, cart.getQuantity());
        assertEquals(rentalStartDate, cart.getRentalStartDate());
        assertEquals(rentalEndDate, cart.getRentalEndDate());
        assertEquals("https://example.com/image1.jpg", cart.getImageUrl());
    }

    @Test
    void testImageUrlSetWhenImageUrlListNotEmpty() {
        // Create mock objects for Product, User, and ProductUrl
        Product product = mock(Product.class);
        User user = mock(User.class);
        ProductUrl productUrl = mock(ProductUrl.class);

        // Define the input values
        int quantity = 5;
        LocalDateTime rentalStartDate = LocalDateTime.now();
        LocalDateTime rentalEndDate = LocalDateTime.now().plusDays(7);

        // Create a list of ProductUrl containing the mock ProductUrl
        List<ProductUrl> imageUrlList = new ArrayList<>();
        imageUrlList.add(productUrl);

        // Mock the behavior of the ProductUrl to return a URL
        when(productUrl.getUrl()).thenReturn("https://example.com/image.jpg");

        // Create a Cart instance using the constructor
        Cart cart = new Cart(product, user, quantity, rentalStartDate, rentalEndDate, imageUrlList);

        // Assert that the imageUrl is set to the expected value
        assertEquals("https://example.com/image.jpg", cart.getImageUrl());
    }

    @Test
    void testImageUrlSetWhenImageUrlListEmpty() {
        // Create mock objects for Product and User
        Product product = mock(Product.class);
        User user = mock(User.class);

        // Define the input values
        int quantity = 5;
        LocalDateTime rentalStartDate = LocalDateTime.now();
        LocalDateTime rentalEndDate = LocalDateTime.now().plusDays(7);

        // Create an empty list of ProductUrl
        List<ProductUrl> imageUrlList = new ArrayList<>();

        // Create a Cart instance using the constructor with an empty imageUrlList
        Cart cart = new Cart(product, user, quantity, rentalStartDate, rentalEndDate, imageUrlList);

        // Assert that the imageUrl is set to null when the imageUrlList is empty
        assertNull(cart.getImageUrl());
    }
}
