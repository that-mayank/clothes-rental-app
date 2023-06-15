package com.nineleaps.leaps.service;


import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;

import java.util.List;
import java.util.Optional;

public interface SubCategoryServiceInterface {
    void createSubCategory(SubCategoryDto subCategoryDto, Category category);

    SubCategory readSubCategory(String subcategoryName, Category category);

    Optional<SubCategory> readSubCategory(Long subcategoryId);

    List<SubCategory> listSubCategory();

    List<SubCategory> listSubCategory(Long categoryId);

    void updateSubCategory(Long subcategoryId, SubCategoryDto subCategoryDto, Category category);

    List<SubCategory> getSubCategoriesFromIds(List<Long> subcategoryIds);
}
