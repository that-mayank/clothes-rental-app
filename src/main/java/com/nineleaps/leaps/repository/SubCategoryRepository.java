package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.categories.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    SubCategory findBySubcategoryName(String subcategoryName);

    List<SubCategory> findByCategory_Id(Long categoryId);

}
