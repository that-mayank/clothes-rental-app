package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_ShouldSaveCategory() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        Category category = new Category(categoryDto);
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        categoryService.createCategory(categoryDto);

        // Assert
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void listCategory_ShouldReturnListOfCategories() {
        // Arrange
        List<Category> categories = new ArrayList<>();
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.listCategory();

        // Assert
        assertEquals(categories, result);
    }

    @Test
    void updateCategory_WhenCategoryExists_ShouldUpdateCategory() {
        // Arrange
        Long id = 1L;
        CategoryDto updateCategory = new CategoryDto();
        updateCategory.setCategoryName("Updated Category");
        updateCategory.setDescription("Updated Description");
        updateCategory.setImageUrl("Updated Image URL");

        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setCategoryName("Original Category");
        existingCategory.setDescription("Original Description");
        existingCategory.setImageUrl("Original Image URL");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        // Act
        categoryService.updateCategory(id, updateCategory);

        // Assert
        assertEquals(updateCategory.getCategoryName(), existingCategory.getCategoryName());
        assertEquals(updateCategory.getDescription(), existingCategory.getDescription());
        assertEquals(updateCategory.getImageUrl(), existingCategory.getImageUrl());
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        CategoryDto updateCategory = new CategoryDto();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(id, updateCategory));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void readCategory_ShouldReturnCategoryByCategoryName() {
        // Arrange
        String categoryName = "Category";
        Category expectedCategory = new Category();

        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(expectedCategory);

        // Act
        Category result = categoryService.readCategory(categoryName);

        // Assert
        assertEquals(expectedCategory, result);
    }

    @Test
    void testReadCategory_ShouldReturnCategoryById() {
        // Arrange
        Long id = 1L;
        Category expectedCategory = new Category();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(expectedCategory));

        // Act
        Optional<Category> result = categoryService.readCategory(id);

        // Assert
        assertEquals(Optional.of(expectedCategory), result);
    }

    @Test
    void getCategoriesFromIds_WhenAllCategoriesExist_ShouldReturnListOfCategories() throws CategoryNotExistException {
        // Arrange
        List<Long> categoryIds = List.of(1L, 2L, 3L);

        Category category1 = new Category();
        category1.setId(1L);

        Category category2 = new Category();
        category2.setId(2L);

        Category category3 = new Category();
        category3.setId(3L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category3));

        // Act
        List<Category> result = categoryService.getCategoriesFromIds(categoryIds);

        // Assert
        assertEquals(3, result.size());
        assertEquals(category1, result.get(0));
        assertEquals(category2, result.get(1));
        assertEquals(category3, result.get(2));
    }

    @Test
    void getCategoriesFromIds_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        List<Long> categoryIds = List.of(1L, 2L, 3L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotExistException.class, () -> categoryService.getCategoriesFromIds(categoryIds));
        verify(categoryRepository, never()).findById(2L);
        verify(categoryRepository, never()).findById(3L);
    }
}