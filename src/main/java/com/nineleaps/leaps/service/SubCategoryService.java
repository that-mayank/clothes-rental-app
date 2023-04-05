package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubCategoryService implements SubCategoryServiceInterface {
    private final SubCategoryRepository categoryRepository;

    @Autowired
    public SubCategoryService(SubCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void createSubCategory(SubCategoryDto subCategoryDto, Category category) {
        SubCategory subCategory = getSubCategoryFromDto(subCategoryDto, category);
        categoryRepository.save(subCategory);
    }

    @Override
    public SubCategory readSubCategory(String subcategoryName) {
        return categoryRepository.findBySubcategoryName(subcategoryName);
    }

    private SubCategory getSubCategoryFromDto(SubCategoryDto subCategoryDto, Category category) {
        return new SubCategory(subCategoryDto, category);
    }

    @Override
    public Optional<SubCategory> readSubCategory(Long subcategoryId) {
        return categoryRepository.findById(subcategoryId);
    }

    @Override
    public List<SubCategory> listSubCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public List<SubCategory> listSubCategory(Long categoryId) {
        return categoryRepository.findByCategory_Id(categoryId);
    }

    @Override
    public void updateSubCategory(Long subcategoryId, SubCategoryDto subCategoryDto, Category category) {
        SubCategory updatedSubCategory = getSubCategoryFromDto(subCategoryDto, category);
        if (Helper.notNull(updatedSubCategory)) {
            updatedSubCategory.setId(subcategoryId);
            categoryRepository.save(updatedSubCategory);
        }
    }

}
