package com.nineleaps.leaps.model.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductUrlTest {

    private ProductUrl productUrl;

    @BeforeEach
    void setUp() {
        productUrl = new ProductUrl();
    }

    @Test
    void getId() {
        productUrl.setId(1L);
        assertEquals(1L, productUrl.getId());
    }

    @Test
    void getUrl() {
        productUrl.setUrl("https://example.com/product1");
        assertEquals("https://example.com/product1", productUrl.getUrl());
    }

    @Test
    void getProduct() {
        Product product = new Product();
        productUrl.setProduct(product);
        assertEquals(product, productUrl.getProduct());
    }

    @Test
    void setId() {
        productUrl.setId(2L);
        assertEquals(2L, productUrl.getId());
    }

    @Test
    void setUrl() {
        productUrl.setUrl("https://example.com/product2");
        assertEquals("https://example.com/product2", productUrl.getUrl());
    }

    @Test
    void setProduct() {
        Product product = new Product();
        productUrl.setProduct(product);
        assertEquals(product, productUrl.getProduct());
    }
}
