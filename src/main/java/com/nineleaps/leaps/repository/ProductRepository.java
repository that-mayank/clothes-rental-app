package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
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

    List<Product> findAllByUser(User user, Sort id);

    List<Product> findAllByUser(User user);

    @Query("select product from Product product where product.price between :minPrice and :maxPrice")
    List<Product> findProductByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
}
