package com.nineleaps.leaps.model.product;

import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    void getId() {
        product.setId(1L);
        assertEquals(1L, product.getId());
    }

    @Test
    void getBrand() {
        product.setBrand("Sample Brand");
        assertEquals("Sample Brand", product.getBrand());
    }

    @Test
    void getName() {
        product.setName("Product Name");
        assertEquals("Product Name", product.getName());
    }

    @Test
    void getPrice() {
        product.setPrice(99.99);
        assertEquals(99.99, product.getPrice(), 0.001);
    }

    @Test
    void getDescription() {
        product.setDescription("Product Description");
        assertEquals("Product Description", product.getDescription());
    }

    @Test
    void getColor() {
        product.setColor("Red");
        assertEquals("Red", product.getColor());
    }

    @Test
    void getMaterial() {
        product.setMaterial("Cotton");
        assertEquals("Cotton", product.getMaterial());
    }

    @Test
    void getQuantity() {
        product.setQuantity(10);
        assertEquals(10, product.getQuantity());
    }

    @Test
    void getAvailableQuantities() {
        product.setAvailableQuantities(5);
        assertEquals(5, product.getAvailableQuantities());
    }

    @Test
    void getDisabledQuantities() {
        product.setDisabledQuantities(2);
        assertEquals(2, product.getDisabledQuantities());
    }

    @Test
    void getRentedQuantities() {
        product.setRentedQuantities(3);
        assertEquals(3, product.getRentedQuantities());
    }

    @Test
    void getSize() {
        product.setSize("Large");
        assertEquals("Large", product.getSize());
    }

    @Test
    void isDeleted() {
        product.setDeleted(true);
        assertTrue(product.isDeleted());
    }

    @Test
    void isDisabled() {
        product.setDisabled(true);
        assertTrue(product.isDisabled());
    }

    @Test
    void getSubCategories() {
        List<SubCategory> subCategories = new ArrayList<>();
        product.setSubCategories(subCategories);
        assertEquals(subCategories, product.getSubCategories());
    }

    @Test
    void getCategories() {
        List<Category> categories = new ArrayList<>();
        product.setCategories(categories);
        assertEquals(categories, product.getCategories());
    }

    @Test
    void getWishlists() {
        List<Wishlist> wishlists = new ArrayList<>();
        product.setWishlists(wishlists);
        assertEquals(wishlists, product.getWishlists());
    }

    @Test
    void getCarts() {
        List<Cart> carts = new ArrayList<>();
        product.setCarts(carts);
        assertEquals(carts, product.getCarts());
    }

    @Test
    void getUser() {
        User user = new User();
        product.setUser(user);
        assertEquals(user, product.getUser());
    }

    @Test
    void getImageURL() {
        List<ProductUrl> imageURLs = new ArrayList<>();
        product.setImageURL(imageURLs);
        assertEquals(imageURLs, product.getImageURL());
    }

    @Test
    void setId() {
        product.setId(2L);
        assertEquals(2L, product.getId());
    }

    @Test
    void setBrand() {
        product.setBrand("New Brand");
        assertEquals("New Brand", product.getBrand());
    }

    @Test
    void setName() {
        product.setName("New Product Name");
        assertEquals("New Product Name", product.getName());
    }

    @Test
    void setPrice() {
        product.setPrice(49.99);
        assertEquals(49.99, product.getPrice(), 0.001);
    }

    @Test
    void setDescription() {
        product.setDescription("New Product Description");
        assertEquals("New Product Description", product.getDescription());
    }

    @Test
    void setColor() {
        product.setColor("Blue");
        assertEquals("Blue", product.getColor());
    }

    @Test
    void setMaterial() {
        product.setMaterial("Polyester");
        assertEquals("Polyester", product.getMaterial());
    }

    @Test
    void setQuantity() {
        product.setQuantity(15);
        assertEquals(15, product.getQuantity());
    }

    @Test
    void setAvailableQuantities() {
        product.setAvailableQuantities(7);
        assertEquals(7, product.getAvailableQuantities());
    }

    @Test
    void setDisabledQuantities() {
        product.setDisabledQuantities(4);
        assertEquals(4, product.getDisabledQuantities());
    }

    @Test
    void setRentedQuantities() {
        product.setRentedQuantities(5);
        assertEquals(5, product.getRentedQuantities());
    }

    @Test
    void setSize() {
        product.setSize("Medium");
        assertEquals("Medium", product.getSize());
    }

    @Test
    void setDeleted() {
        product.setDeleted(true);
        assertTrue(product.isDeleted());
    }
}
