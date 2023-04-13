package com.nineleaps.leaps.service;


import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;

import java.util.List;
import java.util.Optional;

public interface SubCategoryServiceInterface {
    public void createSubCategory(SubCategoryDto subCategoryDto, Category category);

    public SubCategory readSubCategory(String subcategoryName);

    public Optional<SubCategory> readSubCategory(Long subcategoryId);

    public List<SubCategory> listSubCategory();

    public List<SubCategory> listSubCategory(Long categoryId);

    public void updateSubCategory(Long subcategoryId, SubCategoryDto subCategoryDto, Category category);

    public List<SubCategory> getSubCategoriesFromIds(List<Long> subcategoryIds);
}
