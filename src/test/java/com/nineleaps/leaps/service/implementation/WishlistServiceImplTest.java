//package com.nineleaps.leaps.service.implementation;
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.dto.product.ProductDto;
//import com.nineleaps.leaps.exceptions.CustomException;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.model.Wishlist;
//import com.nineleaps.leaps.model.product.Product;
//import com.nineleaps.leaps.repository.WishlistRepository;
//import com.nineleaps.leaps.utils.Helper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//@Tag("unit_tests")
//@DisplayName("Wishlist Service Tests")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class WishlistServiceImplTest {
//
//    @InjectMocks
//    private WishlistServiceImpl wishlistService;
//
//    @Mock
//    private WishlistRepository wishlistRepository;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private ProductServiceImpl productService;
//
//    @Mock
//    private Helper helper;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("Create Wishlist - Wishlist Created Successfully")
//    void createWishlist_WishlistCreatedSuccessfully() {
//        // Arrange
//        Wishlist wishlist = new Wishlist();
//        Product product = new Product();
//        User user = new User();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.readProduct(product.getId())).thenReturn(Optional.of(product));
//        // Act
//        wishlistService.createWishlist(product.getId(),request);
//
//        // Assert
//        verify(wishlistRepository, times(1)).save(wishlist);
//    }
//
//    @Test
//    @DisplayName("Read Wishlist - Return Wishlist For User Id")
//    void readWishlist_ReturnWishlistForUserId() {
//        // Arrange
//        Long userId = 123L;
//        User user = new User();
//        Wishlist wishlist1 = new Wishlist();
//        Product product1 = new Product();
//        product1.setDeleted(false);
//        wishlist1.setProduct(product1);
//
//        Wishlist wishlist2 = new Wishlist();
//        Product product2 = new Product();
//        product2.setDeleted(true);  // Set product as deleted
//        wishlist2.setProduct(product2);
//
//        List<Wishlist> wishlistList = new ArrayList<>();
//        wishlistList.add(wishlist1);
//        wishlistList.add(wishlist2);
//
//        // Mock repository method
//        when(wishlistRepository.findAllByUserIdOrderByCreateDateDesc(userId)).thenReturn(wishlistList);
//    when(helper.getUserFromToken(request)).thenReturn(user);
//        // Act
//        List<ProductDto> result = wishlistService.readWishlist(request);
//
//        // Assert
//        assertEquals(1, result.size());
//        assertTrue(result.contains(wishlist1));
//        assertFalse(result.contains(wishlist2));
//    }
//
//
//    @Test
//    @DisplayName("Remove From Wishlist - Item Removed Successfully")
//    void removeFromWishlist_ItemRemovedSuccessfully() throws CustomException {
//        // Arrange
//        Long userId = 123L;
//        Long productId = 456L;
//        Wishlist wishlist = new Wishlist();
//        wishlist.setId(1L);
//        User user = new User();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock repository method
//        when(wishlistRepository.findByUserIdAndProductId(userId, productId)).thenReturn(wishlist);
//
//        // Act
//        wishlistService.removeFromWishlist(request, productId);
//
//        // Assert
//        verify(wishlistRepository, times(1)).deleteById(wishlist.getId());
//    }
//
//    @Test
//    @DisplayName("Remove From Wishlist - Wishlist Item Not Found - Throws CustomException")
//    void removeFromWishlist_WishlistItemNotFound_ThrowsCustomException() {
//        // Arrange
//        Long userId = 123L;
//        Long productId = 456L;
//        User user = new User();
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock repository method to return null (item not found)
//        when(wishlistRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null);
//
//        // Act and Assert
//        assertThrows(CustomException.class, () -> wishlistService.removeFromWishlist(request, productId));
//    }
//}


package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.WishlistRepository;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Tag("unit")
class WishlistServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private Helper helper;

    @Mock
    private ProductServiceInterface productService;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createWishlist_ProductExistsAndNotInWishlist_ShouldCreateWishlistItem() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(Optional.of(product));
        when(wishlistRepository.findAllByUserIdOrderByCreateDateDesc(user.getId())).thenReturn(new ArrayList<>());

        // Act
        assertDoesNotThrow(() -> wishlistService.createWishlist(productId, request));

        // Assert
        verify(wishlistRepository, times(1)).save(any());
    }

    @Test
    void createWishlist_ProductDoesNotExist_ShouldThrowProductNotExistException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        Long productId = 1L;

        when(productService.readProduct(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotExistException.class, () -> wishlistService.createWishlist(productId, request));

        // Assert
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void readWishlist_ShouldReturnListOfProductsInWishlist() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<Wishlist> wishlists = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        wishlists.add(new Wishlist(product, user));

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(wishlistRepository.findAllByUserIdOrderByCreateDateDesc(user.getId())).thenReturn(wishlists);

        // Act
        List<ProductDto> result = wishlistService.readWishlist(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void removeFromWishlist_WishlistItemExists_ShouldRemoveFromWishlist() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long productId = 1L;
        Wishlist wishlist = new Wishlist(new Product(), user);
        wishlist.setId(1L);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(wishlistRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(wishlist);

        // Act
        assertDoesNotThrow(() -> wishlistService.removeFromWishlist(request, productId));

        // Assert
        verify(wishlistRepository, times(1)).deleteById(wishlist.getId());
    }

    @Test
    void removeFromWishlist_WishlistItemDoesNotExist_ShouldThrowCustomException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long productId = 1L;

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(wishlistRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(null);

        // Act & Assert
        assertThrows(CustomException.class, () -> wishlistService.removeFromWishlist(request, productId));

        // Assert
        verify(wishlistRepository, never()).deleteById(anyLong());
    }
}