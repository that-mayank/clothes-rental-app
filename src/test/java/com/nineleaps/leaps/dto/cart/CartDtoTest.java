package com.nineleaps.leaps.dto.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
@Tag("unit_tests")
@DisplayName("CartDto Tests")
class CartDtoTest {

   @Test
   @DisplayName("Test getUserId")
   void testUserId() {
      // Sample data for the test
      List<CartItemDto> cartItems = new ArrayList<>();
      double totalCost = 100.0;
      double tax = 10.0;
      double finalPrice = 120.0;
      long userId = 123L;

      // Create an instance of CartDto
      CartDto cartDto = new CartDto(cartItems, totalCost, tax, finalPrice, userId);

      // Check if userId is set correctly
      assertEquals(userId, cartDto.getUserId());

      // Update userId and check again
      long newUserId = 456L;
      cartDto.setUserId(newUserId);
      assertEquals(newUserId, cartDto.getUserId());
   }
}
