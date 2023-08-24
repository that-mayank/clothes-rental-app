package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySubCategoriesId(Long subcategoryId);

    List<Product> findByCategoriesId(Long categoryId);

    Product findByUserIdAndId(Long userId, Long productId);

    @Query("select product from Product product where product.price between :minPrice and :maxPrice")
    List<Product> findProductByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);


    @Query("select product from Product product where product.name like %:query% or product.size like %:query% or product.description like %:query% or product.brand like %:query% or product.color like %:query% or product.material like %:query%")
    List<Product> searchProducts(String query);

    List<Product> findAllByUser(User user);

    List<Product> findAllByUser(User user, Sort id);

    Page<Product> findAllByUserNot(Pageable pageable, User user);
}
