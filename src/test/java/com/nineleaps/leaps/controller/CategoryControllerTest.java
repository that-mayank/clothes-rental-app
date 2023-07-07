package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Mock
    private CategoryServiceInterface categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_ValidCategoryDto_ReturnsCreatedResponse() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        Category category = new Category();
        when(categoryService.readCategory(eq(categoryDto.getCategoryName()))).thenReturn(null);
        doNothing().when(categoryService).createCategory(any(Category.class));

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Created a new Category", response.getMessage());

        verify(categoryService).createCategory(any(Category.class));
    }

    @Test
    void createCategory_CategoryExists_ReturnsConflictResponse() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        Category existingCategory = new Category();
        when(categoryService.readCategory(eq(categoryDto.getCategoryName()))).thenReturn(existingCategory);

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Category already exists", response.getMessage());

        verify(categoryService, never()).createCategory(any(Category.class));
    }

    @Test
    void listCategory_ReturnsCategoryList() {
        // Arrange
        List<Category> categoryList = Collections.singletonList(new Category());
        when(categoryService.listCategory()).thenReturn(categoryList);

        // Act
        ResponseEntity<List<Category>> responseEntity = categoryController.listCategory();

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Category> resultCategoryList = responseEntity.getBody();
        assertNotNull(resultCategoryList);
        assertEquals(categoryList, resultCategoryList);
    }

    @Test
    void updateCategory_ValidCategoryId_ReturnsOkResponse() {
        // Arrange
        Long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        Category existingCategory = new Category();
        when(categoryService.readCategory(eq(categoryId))).thenReturn(Optional.of(existingCategory));

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.updateCategory(categoryId, updateCategoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("category has been updated", response.getMessage());

        verify(categoryService).updateCategory(eq(categoryId), eq(updateCategoryDto));
    }

    @Test
    void updateCategory_CategoryNotExists_ReturnsNotFoundResponse() {
        // Arrange
        Long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        when(categoryService.readCategory(eq(categoryId))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.updateCategory(categoryId, updateCategoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("category does not exist", response.getMessage());

        verify(categoryService, never()).updateCategory(eq(categoryId), eq(updateCategoryDto));
    }
}
