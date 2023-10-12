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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.createSubCategory(subCategoryDto, request);

        // Assert
        assertEquals(HttpStatus.CREATED, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
        assertEquals("Category is created", apiResponse.getBody().getMessage());
        assertTrue(apiResponse.getBody().isSuccess());

        verify(categoryService, times(1)).readCategory(subCategoryDto.getCategoryId());
        verify(subCategoryService, times(1)).readSubCategory(subCategoryDto.getSubcategoryName(), category);
        verify(subCategoryService, times(1)).createSubCategory(subCategoryDto, category);
    }

    @Test
    void createSubCategory_NonAdminRole_ShouldReturnForbiddenApiResponse() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.BORROWER); // Non-admin role
        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.createSubCategory(subCategoryDto, request);

        // Assert
        verify(subCategoryService, never()).createSubCategory(any(), any());
        assertEquals(HttpStatus.FORBIDDEN, apiResponse.getStatusCode());
        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void createSubCategory_InvalidParentCategory_ShouldReturnNotFoundApiResponse() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.ADMIN);
        when(helper.getUser(request)).thenReturn(user);
        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.createSubCategory(subCategoryDto, request);

        // Assert
        verify(subCategoryService, never()).createSubCategory(any(), any());
        assertEquals(HttpStatus.NOT_FOUND, apiResponse.getStatusCode());
        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void createSubCategory_SubCategoryExists_ShouldReturnConflictApiResponse() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        subCategoryDto.setSubcategoryName("ExistingSubCategory");
        Category category = new Category();

        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.ADMIN);
        when(helper.getUser(request)).thenReturn(user);

        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.of(category));
        when(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), category)).thenReturn(new SubCategory());

        // Act
        ResponseEntity<ApiResponse> response = subCategoryController.createSubCategory(subCategoryDto, request);

        // Assert
        verify(subCategoryService, never()).createSubCategory(any(), any());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Sub Category already exists", Objects.requireNonNull(response.getBody()).getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
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
    void listSubCategoriesByCategoriesId_InvalidCategoryId_ShouldReturnNotFoundApiResponse() {
        // Arrange
        Long categoryId = 1L;
        when(categoryService.readCategory(categoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategoriesByCategoriesId(categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
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
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto, request);

        // Assert
        verify(subCategoryService, times(1)).updateSubCategory(subcategoryId, subCategoryDto, category);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void updateSubCategory_NonAdminRole_ShouldReturnForbiddenApiResponse() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.BORROWER); // Non-admin role
        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto, request);

        // Assert
        verify(subCategoryService, never()).updateSubCategory(any(), any(), any());
        assertEquals(HttpStatus.FORBIDDEN, apiResponse.getStatusCode());
        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void updateSubCategory_InvalidParentCategory_ShouldReturnNotFoundApiResponse() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.ADMIN);
        when(helper.getUser(request)).thenReturn(user);
        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto, request);

        // Assert
        verify(subCategoryService, never()).updateSubCategory(any(), any(), any());
        assertEquals(HttpStatus.NOT_FOUND, apiResponse.getStatusCode());
        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void updateSubCategory_InvalidSubCategory_ShouldReturnNotFoundApiResponse() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.ADMIN);
        when(helper.getUser(request)).thenReturn(user);
        when(categoryService.readCategory(subCategoryDto.getCategoryId())).thenReturn(Optional.of(new Category()));
        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> apiResponse = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto, request);

        // Assert
        verify(subCategoryService, never()).updateSubCategory(any(), any(), any());
        assertEquals(HttpStatus.NOT_FOUND, apiResponse.getStatusCode());
        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }
}
