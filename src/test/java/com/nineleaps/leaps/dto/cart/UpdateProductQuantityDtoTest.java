package com.nineleaps.leaps.dto.cart;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateProductQuantityDtoTest {

    @Test
    void getProductId() {
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setProductId(100L);

        assertEquals(100L, updateProductQuantityDto.getProductId());
    }

    @Test
    void getQuantity() {
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setQuantity(5);

        assertEquals(5, updateProductQuantityDto.getQuantity());
    }

    @Test
    void setProductId() {
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setProductId(100L);

        assertEquals(100L, updateProductQuantityDto.getProductId());
    }

    @Test
    void setQuantity() {
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setQuantity(5);

        assertEquals(5, updateProductQuantityDto.getQuantity());
    }
}
