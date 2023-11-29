package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.repository.CategoryRepository;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryServiceInterface {
    private final CategoryRepository categoryRepository;

   

    @Override
    public void createCategory(CategoryDto categoryDto) {
        log.info("Creating a new category: {}", categoryDto.getCategoryName());
        Category category = new Category(categoryDto);
        categoryRepository.save(category);
        log.info("Category created successfully: {}", categoryDto.getCategoryName());
    }

    @Override
    public List<Category> listCategory() {
        log.info("Fetching the list of all categories.");
        List<Category> categories = categoryRepository.findAll();
        log.info("Fetched {} categories.", categories.size());
        return categories;
    }

    @Override
    public void updateCategory(Long id, CategoryDto updateCategory) {
        log.info("Updating category with ID: {}", id);
        Category category;
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            log.error("Category not found with ID: {}", id);
            throw new IllegalArgumentException("Category not found with ID: " + id);
        }
        category = optionalCategory.get();
        category.setCategoryName(updateCategory.getCategoryName());
        category.setDescription(updateCategory.getDescription());
        category.setImageUrl(updateCategory.getImageUrl());
        categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", id);
    }

    @Override
    public Category readCategory(String categoryName) {
        log.info("Fetching category by name: {}", categoryName);
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category == null) {
            log.warn("Category not found with name: {}", categoryName);
        } else {
            log.info("Fetched category by name: {}", categoryName);
        }
        return category;
    }

    @Override
    public Optional<Category> readCategory(Long id) {
        log.info("Fetching category by ID: {}", id);
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            log.warn("Category not found with ID: {}", id);
        } else {
            log.info("Fetched category by ID: {}", id);
        }
        return optionalCategory;
    }

    @Override
    public List<Category> getCategoriesFromIds(List<Long> categoryIds) throws CategoryNotExistException {
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            log.info("Fetching category by ID: {}", categoryId);
            Optional<Category> optionalCategory = readCategory(categoryId);
            if (optionalCategory.isEmpty()) {
                log.error("Category is invalid: {}", categoryId);
                throw new CategoryNotExistException("Category is invalid: " + categoryId);
            }
            categories.add(optionalCategory.get());
            log.info("Fetched category by ID: {}", categoryId);
        }
        return categories;
    }
}
