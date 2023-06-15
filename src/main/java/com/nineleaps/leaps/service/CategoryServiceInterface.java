package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.categories.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryServiceInterface {
    void createCategory(Category category);

    List<Category> listCategory();

    void updateCategory(Long id, Category updateCategory);

    Category readCategory(String categoryName);

    Optional<Category> readCategory(Long id);

    List<Category> getCategoriesFromIds(List<Long> categoryIds);
}
