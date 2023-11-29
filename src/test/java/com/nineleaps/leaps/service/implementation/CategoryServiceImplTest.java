package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.repository.CategoryRepository;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Tag("unit")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Helper helper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_ShouldSaveCategoryToRepository() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto(1L,"Test Category", "Description", "image-url");

        // Act
        assertDoesNotThrow(() -> categoryService.createCategory(categoryDto));

        // Assert
        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    void listCategory_ShouldReturnListOfCategories() {
        // Arrange
        List<Category> categories = new ArrayList<>();
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.listCategory();

        // Assert
        assertNotNull(result);
        assertEquals(categories, result);
    }

    @Test
    void updateCategory_CategoryExists_ShouldUpdateCategory() {
        // Arrange
        Long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto(1L,"Updated Category", "Updated Description", "updated-image-url");
        Category existingCategory = new Category(updateCategoryDto);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        // Act
        assertDoesNotThrow(() -> categoryService.updateCategory(categoryId, updateCategoryDto));

        // Assert
        assertEquals(updateCategoryDto.getCategoryName(), existingCategory.getCategoryName());
        assertEquals(updateCategoryDto.getDescription(), existingCategory.getDescription());
        assertEquals(updateCategoryDto.getImageUrl(), existingCategory.getImageUrl());
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    // Add more test cases for readCategory, getCategoriesFromIds, and other methods.

    @Test
    void readCategory_CategoryNameExists_ShouldReturnCategory() {
        // Arrange
        String categoryName = "Test Category";
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName(categoryName);
        category.setDescription("Description");
        category.setImageUrl("image-url");
        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(category);

        // Act
        Category result = categoryService.readCategory(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(category, result);
    }

    @Test
    void readCategory_CategoryNameDoesNotExist_ShouldReturnNull() {
        // Arrange
        String categoryName = "Non-Existent Category";
        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(null);

        // Act
        Category result = categoryService.readCategory(categoryName);

        // Assert
        assertNull(result);
    }

    @Test
    void readCategory_CategoryIdExists_ShouldReturnCategory() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Category Name");
        category.setDescription("Description");
        category.setImageUrl("image-url");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryService.readCategory(categoryId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(category, result.get());
    }

    @Test
    void readCategory_CategoryIdDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.readCategory(categoryId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getCategoriesFromIds_AllCategoriesExist_ShouldReturnListOfCategories() {
        // Arrange
        List<Long> categoryIds = List.of(1L, 2L, 3L);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));

        // Act
        assertDoesNotThrow(() -> categoryService.getCategoriesFromIds(categoryIds));

        // Assert
        verify(categoryRepository, times(categoryIds.size())).findById(anyLong());
    }

    @Test
    void getCategoriesFromIds_OneCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        List<Long> categoryIds = List.of(1L, 2L, 3L);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()), Optional.empty(), Optional.of(new Category()));

        // Act & Assert
        assertThrows(CategoryNotExistException.class, () -> categoryService.getCategoriesFromIds(categoryIds));
    }
}