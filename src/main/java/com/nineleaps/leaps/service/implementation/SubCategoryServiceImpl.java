package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubCategoryServiceImpl implements SubCategoryServiceInterface {
    private final SubCategoryRepository categoryRepository;

    @Autowired
    public SubCategoryServiceImpl(SubCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void createSubCategory(SubCategoryDto subCategoryDto, Category category) {
        SubCategory subCategory = getSubCategoryFromDto(subCategoryDto, category);
        categoryRepository.save(subCategory);
    }

    @Override
    public SubCategory readSubCategory(String subcategoryName, Category category) {
        List<SubCategory> subCategories = listSubCategory(category.getId());
        for (SubCategory subCategory : subCategories) {
            if (subcategoryName.equals(subCategory.getSubcategoryName())) {
                return subCategory;
            }
        }
        return null;
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

    @Override
    public List<SubCategory> getSubCategoriesFromIds(List<Long> subcategoryIds) throws CategoryNotExistException {
        List<SubCategory> subCategories = new ArrayList<>();
        for (Long subcategoryId : subcategoryIds) {
            Optional<SubCategory> optionalSubCategory = readSubCategory(subcategoryId);
            if (!optionalSubCategory.isPresent()) {
                throw new CategoryNotExistException("Subcategory is invalid: " + subcategoryId);
            }
            subCategories.add(optionalSubCategory.get());
        }
        return subCategories;
    }
}
