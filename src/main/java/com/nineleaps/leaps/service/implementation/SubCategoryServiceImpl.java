package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class SubCategoryServiceImpl implements SubCategoryServiceInterface {
    private final SubCategoryRepository subCategoryRepository;

    @Override
    public SubCategory createSubCategory(SubCategoryDto subCategoryDto, Category category, User user) {
        SubCategory subCategory = getSubCategoryFromDto(subCategoryDto, category);
        subCategory.setSubCategoryCreatedAt(LocalDateTime.now());
        subCategory.setSubCategoryCreatedBy(user.getId());
        subCategory.setAuditColumnsUpdate(user.getId());
        subCategoryRepository.save(subCategory);
        return subCategory;
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


    SubCategory getSubCategoryFromDto(SubCategoryDto subCategoryDto, Category category) {
        return new SubCategory(subCategoryDto, category);
    }

    @Override
    public Optional<SubCategory> readSubCategory(Long subcategoryId) {
        return subCategoryRepository.findById(subcategoryId);
    }

    @Override
    public List<SubCategory> listSubCategory() {
        return subCategoryRepository.findAll();
    }

    @Override
    public List<SubCategory> listSubCategory(Long categoryId) {
        return subCategoryRepository.findByCategoryId(categoryId);
    }

    @Override
    public void updateSubCategory(Long subcategoryId, SubCategoryDto subCategoryDto, Category category,User user) {
        SubCategory updatedSubCategory = getSubCategoryFromDto(subCategoryDto, category);
        if (Helper.notNull(updatedSubCategory)) {
            updatedSubCategory.setId(subcategoryId);
            updatedSubCategory.setAuditColumnsCreate(user);
            updatedSubCategory.setAuditColumnsUpdate(user.getId());
            subCategoryRepository.save(updatedSubCategory);
        }
    }

    @Override
    public List<SubCategory> getSubCategoriesFromIds(List<Long> subcategoryIds) throws CategoryNotExistException {
        List<SubCategory> subCategories = new ArrayList<>();
        for (Long subcategoryId : subcategoryIds) {
            Optional<SubCategory> optionalSubCategory = readSubCategory(subcategoryId);
            if (optionalSubCategory.isEmpty()) {
                throw new CategoryNotExistException("Subcategory is invalid: " + subcategoryId);
            }
            subCategories.add(optionalSubCategory.get());
        }
        return subCategories;
    }
}
