package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.service.implementation.CategoryServiceImpl;
import com.nineleaps.leaps.service.implementation.SubCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubCategoryControllerTest {

    @Mock
    private CategoryServiceInterface categoryService;
    @Mock
    private SubCategoryServiceInterface subCategoryService;
    @InjectMocks
    private SubCategoryController subCategoryController;

    @BeforeEach
    void setUp() {
        subCategoryService = mock(SubCategoryServiceImpl.class);
        categoryService = mock(CategoryServiceImpl.class);
        subCategoryController = new SubCategoryController(categoryService, subCategoryService);
    }

    @Test
    void createSubCategory_WhenParentCategoryIsValid_ShouldReturnCreatedResponse() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);

        Category category = new Category();
        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.of(category));
        when(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), category)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = subCategoryController.createSubCategory(subCategoryDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Category is created", response.getBody().getMessage());

        verify(categoryService, times(1)).readCategory(subCategoryDto.getCategoryId());
        verify(subCategoryService, times(1)).readSubCategory(subCategoryDto.getSubcategoryName(), category);
        verify(subCategoryService, times(1)).createSubCategory(subCategoryDto, category);
    }

    @Test
    void createSubCategory_WhenParentCategoryIsInvalid_ShouldReturnNotFoundResponse() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);

        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> response = subCategoryController.createSubCategory(subCategoryDto);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Parent Category is invalid", response.getBody().getMessage());

        verify(categoryService, times(1)).readCategory(subCategoryDto.getCategoryId());
        verify(subCategoryService, never()).readSubCategory(anyString(), any());
        verify(subCategoryService, never()).createSubCategory(any(), any());
    }

    @Test
    void createSubCategory_WhenSubCategoryAlreadyExists_ShouldReturnConflictResponse() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        subCategoryDto.setSubcategoryName("SubCategory");

        Category category = new Category();
        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.of(category));
        when(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), category)).thenReturn(new SubCategory());

        // Act
        ResponseEntity<ApiResponse> response = subCategoryController.createSubCategory(subCategoryDto);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Sub Category already exists", response.getBody().getMessage());

        verify(categoryService, times(1)).readCategory(subCategoryDto.getCategoryId());
        verify(subCategoryService, times(1)).readSubCategory(subCategoryDto.getSubcategoryName(), category);
        verify(subCategoryService, never()).createSubCategory(any(), any());
    }

    @Test
    void listSubCategories_ShouldReturnListOfSubCategories() {
        // Arrange
        List<SubCategory> subCategories = new ArrayList<>();
        when(subCategoryService.listSubCategory()).thenReturn(subCategories);

        // Act
        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategories, response.getBody());

        verify(subCategoryService, times(1)).listSubCategory();
    }

    @Test
    void listSubCategoriesByCategoriesId_WhenCategoryIdIsValid_ShouldReturnListOfSubCategories() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        when(categoryService.readCategory(categoryId)).thenReturn(Optional.of(category));

        List<SubCategory> subCategories = new ArrayList<>();
        when(subCategoryService.listSubCategory(categoryId)).thenReturn(subCategories);

        // Act
        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategoriesByCategoriesId(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategories, response.getBody());

        verify(categoryService, times(1)).readCategory(categoryId);
        verify(subCategoryService, times(1)).listSubCategory(categoryId);
    }

    @Test
    void listSubCategoriesByCategoriesId_WhenCategoryIdIsInvalid_ShouldReturnNotFoundResponse() {
        // Arrange
        Long categoryId = 1L;
        when(categoryService.readCategory(categoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategoriesByCategoriesId(categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());

        verify(categoryService, times(1)).readCategory(categoryId);
        verify(subCategoryService, never()).listSubCategory(anyLong());
    }

    @Test
    void updateSubCategory_WhenCategoryAndSubcategoryAreValid_ShouldReturnOkResponse() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);

        Category category = new Category();
        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.of(category));

        SubCategory subCategory = new SubCategory();
        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(Optional.of(subCategory));

        // Act
        ResponseEntity<ApiResponse> response = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Subcategory updated successfully", response.getBody().getMessage());

        verify(categoryService, times(1)).readCategory(subCategoryDto.getCategoryId());
        verify(subCategoryService, times(1)).readSubCategory(subcategoryId);
        verify(subCategoryService, times(1)).updateSubCategory(subcategoryId, subCategoryDto, category);
    }

    @Test
    void updateSubCategory_WhenCategoryIsInvalid_ShouldReturnNotFoundResponse() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);

        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> response = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Category is invalid", response.getBody().getMessage());

        verify(categoryService, times(1)).readCategory(subCategoryDto.getCategoryId());
        verify(subCategoryService, never()).readSubCategory(anyLong());
        verify(subCategoryService, never()).updateSubCategory(anyLong(), any(), any());
    }

    @Test
    void updateSubCategory_WhenSubcategoryIsInvalid_ShouldReturnNotFoundResponse() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);

        Category category = new Category();
        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.of(category));
        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> response = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Subcategory is invalid", response.getBody().getMessage());

        verify(categoryService, times(1)).readCategory(subCategoryDto.getCategoryId());
        verify(subCategoryService, times(1)).readSubCategory(subcategoryId);
        verify(subCategoryService, never()).updateSubCategory(anyLong(), any(), any());
    }
}
