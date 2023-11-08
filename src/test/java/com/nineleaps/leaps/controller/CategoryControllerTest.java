package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;
    @Mock
    private CategoryServiceInterface categoryService;
    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Category - Success")
    void createCategory_ValidCategoryDto_ReturnsCreatedResponse() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();

        doNothing().when(categoryService).createCategory(any(CategoryDto.class));

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto);

        // Assert
        assertNotNull(responseEntity);
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
    }

    @Test
    @DisplayName("List Category - Success")
    void listCategory_ReturnsCategoryList() {
        // Arrange
        List<Category> categoryList = new ArrayList<>();

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
    @DisplayName("Update Category - Success")
    void updateCategory_ValidCategoryId_ReturnsOkResponse() {
        // Arrange
        Long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();

        doNothing().when(categoryService).updateCategory(categoryId, updateCategoryDto);

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.updateCategory(categoryId, updateCategoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("category has been updated", response.getMessage());
    }
}
