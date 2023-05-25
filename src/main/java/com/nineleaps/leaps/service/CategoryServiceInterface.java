package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.categories.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryServiceInterface {
    public void createCategory(Category category);
    public List<Category> listCategory();
    public void updateCategory(Long id, Category updateCategory);
    public Category readCategory(String categoryName);
    public Optional<Category> readCategory(Long id);
    public List<Category> getCategoriesFromIds(List<Long> categoryIds);
}
