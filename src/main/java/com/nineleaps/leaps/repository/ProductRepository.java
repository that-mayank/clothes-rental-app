package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import org.hibernate.sql.Select;
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


//    @Query("select product from Product product where product.name like %:query% or product.size like %:query% or product.description like %:query% or product.brand like %:query% or product.color like %:query% or product.material like %:query%")
//    List<Product> searchProducts(String query);

    List<Product> findAllByUser(User user);

    List<Product> findAllByUser(User user, Sort id);

    @Query("SELECT product FROM Product product WHERE (:brandQuery IS NULL OR product.brand LIKE %:brandQuery%) AND (:nameQuery IS NULL OR product.name LIKE %:nameQuery%) AND (:colorQuery IS NULL OR product.color LIKE %:colorQuery%) AND (:materialQuery IS NULL OR product.material LIKE %:materialQuery%)")
    List<Product> searchProducts(@Param("brandQuery") String brandQuery, @Param("nameQuery") String nameQuery, @Param("colorQuery") String colorQuery, @Param("materialQuery") String materialQuery);



}
