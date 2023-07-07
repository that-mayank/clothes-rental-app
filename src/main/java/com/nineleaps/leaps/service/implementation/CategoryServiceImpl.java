package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.repository.CategoryRepository;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryServiceInterface {
    //Linking Repository using constructor injection
    private final CategoryRepository categoryRepository;

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public List<Category> listCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public void updateCategory(Long id, CategoryDto updateCategory) {
        Category category;
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new IllegalArgumentException("Category not found with ID: " + id);
        }
        if (optionalCategory.isPresent()) {
            category = optionalCategory.get();
            if (category != null) {
                category.setCategoryName(updateCategory.getCategoryName());
                category.setDescription(updateCategory.getDescription());
                category.setImageUrl(updateCategory.getImageUrl());
                categoryRepository.save(category);
            }
        }
    }

    @Override
    public Category readCategory(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    @Override
    public Optional<Category> readCategory(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getCategoriesFromIds(List<Long> categoryIds) throws CategoryNotExistException {
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            Optional<Category> optionalCategory = readCategory(categoryId);
            if (!optionalCategory.isPresent()) {
                throw new CategoryNotExistException("Category is invalid: " + categoryId);
            }
            categories.add(optionalCategory.get());
        }
        return categories;
    }
}

