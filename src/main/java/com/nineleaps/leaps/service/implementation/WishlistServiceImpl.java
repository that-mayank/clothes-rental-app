package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.WishlistRepository;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.service.WishlistServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring service component
@AllArgsConstructor // Lombok's annotation to generate a constructor with all required fields
@Transactional // Marks this class as transactional for database operations
public class WishlistServiceImpl implements WishlistServiceInterface {

    private final WishlistRepository wishlistRepository;
    private final Helper helper;
    private final ProductServiceInterface productService;


    // Method to create a new wishlist item
    @Override
    public void createWishlist(Long productId, HttpServletRequest request) {
        // JWT : Extracting user info from token

        User user = helper.getUserFromToken(request);

        // Guard Statement : Check if product is valid

        Optional<Product> optionalProduct = productService.readProduct(productId);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotExistException("Product is invalid");
        }

        // Guard Statement : Check the same product cannot be added to wishlist

        List<ProductDto> userWishlist = readWishlist(request);
        if (userWishlist.stream().anyMatch(productDto -> productDto.getId().equals(productId))) {
            throw new CustomException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist(optionalProduct.get(), user);
        wishlistRepository.save(wishlist);
    }

    // Method to read the wishlist items for a given user
    @Override
    public List<ProductDto> readWishlist(HttpServletRequest request) {

        // JWT : Extracting user info from token

        User user = helper.getUserFromToken(request);

        // Retrieve wishlists for the user and sort them by creation date in descending order
        List<Wishlist> wishlists = wishlistRepository.findAllByUserIdOrderByCreateDateDesc(user.getId());

        // Filter out deleted products from the wishlist
        List<Wishlist> filteredWishlist = new ArrayList<>();
        for (Wishlist wishlist : wishlists) {
            if (!wishlist.getProduct().isDeleted()) {
                filteredWishlist.add(wishlist);
            }
        }

        // return the wishlist i.e. all products in the wishlist
        List<ProductDto> productDtos = new ArrayList<>();
        for (Wishlist wishlist : filteredWishlist) {
            productDtos.add(ProductServiceImpl.getDtoFromProduct(wishlist.getProduct()));
        }

        return productDtos;
    }

    // Method to remove an item from the wishlist
    @Override
    public void removeFromWishlist(HttpServletRequest request, Long productId) throws CustomException {

        // JWT : Extracting user info from token
        User user = helper.getUserFromToken(request);

        // Find the wishlist item by user ID and product ID
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(user.getId(), productId);

        // Check if the wishlist item exists
        if (Optional.ofNullable(wishlist).isEmpty()) {
            throw new CustomException("Item not found");
        }

        // Delete the wishlist item from the database
        wishlistRepository.deleteById(wishlist.getId());
    }
}