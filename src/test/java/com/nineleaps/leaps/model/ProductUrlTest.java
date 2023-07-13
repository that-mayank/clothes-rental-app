package com.nineleaps.leaps.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

 class ProductUrlTest {

    private Product product;
    private ProductUrl productUrl;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
        productUrl = new ProductUrl();
        productUrl.setId(1L);
        productUrl.setUrl("https://example.com/product-url");
        productUrl.setProduct(product);
    }

    @Test
    void testConstructor() {
        assertNotNull(productUrl.getId());
        assertEquals("https://example.com/product-url", productUrl.getUrl());
        assertEquals(product, productUrl.getProduct());
    }

    @Test
    void testGettersAndSetters() {
        assertNotNull(productUrl.getId());
        assertEquals("https://example.com/product-url", productUrl.getUrl());
        assertEquals(product, productUrl.getProduct());
    }
}
