package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySubCategoriesId(Long subcategoryId);

    List<Product> findByCategoriesId(Long categoryId);
}
