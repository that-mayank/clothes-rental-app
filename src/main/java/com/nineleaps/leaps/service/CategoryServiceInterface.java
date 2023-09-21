package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryServiceInterface {
    void createCategory(Category category, User user);

    List<Category> listCategory();

    void updateCategory(Long id, CategoryDto updateCategory,User user);

    Category readCategory(String categoryName);

    Optional<Category> readCategory(Long id);

    List<Category> getCategoriesFromIds(List<Long> categoryIds);
}
