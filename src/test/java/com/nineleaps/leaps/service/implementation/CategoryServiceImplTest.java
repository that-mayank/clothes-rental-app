package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import org.mockito.ArgumentCaptor;




@Tag("unit_tests")
@DisplayName("Category Service Tests")
class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Create Category: Category Created Successfully")
     void createCategory_CategoryCreatedSuccessfully() {
        // Arrange
        Category category = new Category();
        User user = new User();
        LocalDateTime beforeCall = LocalDateTime.now();

        // Mock the save method of categoryRepository
        when(categoryRepository.save(any())).thenReturn(new Category());

        // Act
        categoryService.createCategory(category, user);

        // Assert
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());

        Category savedCategory = categoryCaptor.getValue();
        assertNotNull(savedCategory);
        assertEquals(category.getCategoryCreatedAt(), savedCategory.getCategoryCreatedAt());
        assertEquals(category.getCategoryCreatedBy(), savedCategory.getCategoryCreatedBy());
        assertTrue(savedCategory.getCategoryCreatedAt().isAfter(beforeCall));
    }
    @Test
    @DisplayName("Create Category: Category Created Successfully 2")
    void createCategory_CategoryCreatedSuccessfully2() {
        // Arrange
        Category category = new Category();
        User user = new User();

        // Act
        categoryService.createCategory(category, user);

        // Assert
        assertEquals(user.getId(), category.getCategoryCreatedBy());
        assertEquals(user.getId(), category.getCategoryUpdatedBy());
        verify(categoryRepository, times(1)).save(category);
    }




    @Test
    @DisplayName("List Category: Return List Of Categories")
    void listCategory_ReturnListOfCategories() {
        // Arrange
        List<Category> expectedCategories = new ArrayList<>();
        expectedCategories.add(new Category());
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        // Act
        List<Category> actualCategories = categoryService.listCategory();

        // Assert
        assertNotNull(actualCategories);
        assertEquals(expectedCategories.size(), actualCategories.size());
        assertTrue(actualCategories.containsAll(expectedCategories));
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update Category: Category Updated Successfully")
    void updateCategory_CategoryUpdatedSuccessfully() {
        // Arrange
        Long id = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        updateCategoryDto.setCategoryName("Updated Category");
        updateCategoryDto.setDescription("Updated description");
        updateCategoryDto.setImageUrl("https://example.com/image.jpg");
        User user = new User();

        Category existingCategory = new Category();
        existingCategory.setId(id);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));

        // Act
        categoryService.updateCategory(id, updateCategoryDto, user);

        // Assert
        assertEquals(updateCategoryDto.getCategoryName(), existingCategory.getCategoryName());
        assertEquals(updateCategoryDto.getDescription(), existingCategory.getDescription());
        assertEquals(updateCategoryDto.getImageUrl(), existingCategory.getImageUrl());
        assertEquals(user.getId(), existingCategory.getCategoryCreatedBy());
        assertEquals(user.getId(), existingCategory.getCategoryUpdatedBy());
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    @DisplayName("Update Category: Category Not Found")
    void updateCategory_CategoryNotFound() {
        // Arrange
        Long id = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        User user = new User();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(id, updateCategoryDto, user));
        assertEquals("Category not found with ID: " + id, thrown.getMessage());
    }

    @Test
    @DisplayName("Read Category: Category Found")
    void readCategory_CategoryFound() {
        // Arrange
        String categoryName = "Test Category";
        Category expectedCategory = new Category();
        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(expectedCategory);

        // Act
        Category result = categoryService.readCategory(categoryName);

        // Assert
        assertEquals(expectedCategory, result);
    }

    @Test
    @DisplayName("Read Category: Category Not Found")
    void readCategory_CategoryNotFound() {
        // Arrange
        String categoryName = "Nonexistent Category";
        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(null);

        // Act
        Category result = categoryService.readCategory(categoryName);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Read Category By Id: Category Found")
    void readCategoryById_CategoryFound() {
        // Arrange
        Long categoryId = 1L;
        Category expectedCategory = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        // Act
        Optional<Category> result = categoryService.readCategory(categoryId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedCategory, result.get());
    }

    @Test
    @DisplayName("Read Category By Id: Category Not Found")
    void readCategoryById_CategoryNotFound() {
        // Arrange
        Long categoryId = 2L;  // Assuming this ID does not exist in the repository
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.readCategory(categoryId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Get Categories From Ids: Categories Found")
    void getCategoriesFromIds_CategoriesFound() throws CategoryNotExistException {
        // Arrange
        List<Long> categoryIds = Arrays.asList(1L, 2L);
        Category category1 = new Category();
        Category category2 = new Category();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));

        // Act
        List<Category> result = categoryService.getCategoriesFromIds(categoryIds);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(category1));
        assertTrue(result.contains(category2));
    }

    @Test
    @DisplayName("Get Categories From Ids: Category Not Found")
    void getCategoriesFromIds_CategoryNotFound() {
        // Arrange
        List<Long> categoryIds = Collections.singletonList(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CategoryNotExistException.class, () -> categoryService.getCategoriesFromIds(categoryIds));
    }

}