package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.repository.CategoryRepository;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring service component
@AllArgsConstructor // Lombok's annotation to generate a constructor with all required fields
@Transactional // Marks this class as transactional for database operations
public class CategoryServiceImpl implements CategoryServiceInterface {
    // Linking Repository using constructor injection
    private final CategoryRepository categoryRepository;
    private final Helper helper;

    @Override
    public void createCategory(CategoryDto categoryDto) {
        // Calling service layer to save category

        Category category = new Category(categoryDto);
        categoryRepository.save(category); // Save the category to the database
    }

    @Override
    public List<Category> listCategory() {
        return categoryRepository.findAll(); // Retrieve a list of all categories from the database
    }

    @Override
    public void updateCategory(Long id, CategoryDto updateCategory) {
        Category category;
        Optional<Category> optionalCategory = categoryRepository.findById(id); // Find the category by its ID
        if (optionalCategory.isEmpty()) { // Check if the category doesn't exist
            throw new IllegalArgumentException("Category not found with ID: " + id);
        }
        category = optionalCategory.get(); // Get the category if it exists
        // Update the category fields with the values from the DTO (Data Transfer Object)
        category.setCategoryName(updateCategory.getCategoryName());
        category.setDescription(updateCategory.getDescription());
        category.setImageUrl(updateCategory.getImageUrl());
        categoryRepository.save(category); // Save the updated category to the database
    }

    @Override
    public Category readCategory(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName); // Find and return a category by its name
    }

    @Override
    public Optional<Category> readCategory(Long id) {
        return categoryRepository.findById(id); // Find and return a category by its ID
    }

    @Override
    public List<Category> getCategoriesFromIds(List<Long> categoryIds) throws CategoryNotExistException {
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            Optional<Category> optionalCategory = readCategory(categoryId); // Find a category by its ID
            if (optionalCategory.isEmpty()) { // Check if the category doesn't exist
                throw new CategoryNotExistException("Category is invalid: " + categoryId);
            }
            categories.add(optionalCategory.get()); // Add the found category to the list
        }
        return categories; // Return the list of categories
    }
}
