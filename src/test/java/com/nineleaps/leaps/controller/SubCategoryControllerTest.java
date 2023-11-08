package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Tag("unit")
class SubCategoryControllerTest {

    @Mock
    private CategoryServiceInterface categoryService;
    @Mock
    private SubCategoryServiceInterface subCategoryService;
    @Mock
    private Helper helper;
    @InjectMocks
    private SubCategoryController subCategoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Subcategory - Success")
    void createSubCategory_AdminRole_ShouldReturnCreatedApiResponse() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        Category category = new Category();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.ADMIN);
        when(helper.getUser(request)).thenReturn(user);
        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.of(category));
        when(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), category)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.createSubCategory(subCategoryDto);

        // Assert
        assertEquals(HttpStatus.CREATED, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
        assertEquals("Category is created", apiResponse.getBody().getMessage());
        assertTrue(apiResponse.getBody().isSuccess());
    }

    @Test
    @DisplayName("List Subcategories")
    void listSubCategories_ShouldReturnListOfSubCategories() {
        // Arrange
        List<SubCategory> expectedSubCategories = new ArrayList<>();
        when(subCategoryService.listSubCategory()).thenReturn(expectedSubCategories);

        // Act
        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSubCategories, response.getBody());
    }

    @Test
    @DisplayName("List Subcategories By Category")
    void listSubCategoriesByCategoriesId_ValidCategoryId_ShouldReturnListOfSubCategories() {
        // Arrange
        Long categoryId = 1L;
        List<SubCategory> expectedSubCategories = new ArrayList<>();
        when(categoryService.readCategory(categoryId)).thenReturn(Optional.of(new Category()));
        when(subCategoryService.listSubCategory(categoryId)).thenReturn(expectedSubCategories);

        // Act
        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategoriesByCategoriesId(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSubCategories, response.getBody());
    }

    @Test
    @DisplayName("Update Subcategory")
    void updateSubCategory_AdminRole_ShouldReturnOkApiResponse() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        Category category = new Category();

        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.ADMIN);
        when(helper.getUser(request)).thenReturn(user);

        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.of(category));
        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(Optional.of(new SubCategory(subCategoryDto, category)));

        // Act
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto);

        // Assert
        verify(subCategoryService, times(1)).updateSubCategory(subcategoryId, subCategoryDto);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }
}
