package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void createSubCategory_CategoryDoesNotExist_ReturnsCreated() {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L); // Assuming valid category ID
        subCategoryDto.setSubcategoryName("TestSubCategory");
        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest

        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(categoryService.readCategory(1L)).thenReturn(Optional.of(new Category())); // Assuming category exists
        when(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), new Category())).thenReturn(null);

        ResponseEntity<ApiResponse> response = subCategoryController.createSubCategory(subCategoryDto, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Category is created", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void createSubCategory_CategoryExists_ReturnsConflict() {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L); // Assuming valid category ID
        subCategoryDto.setSubcategoryName("TestSubCategory"); // An existing subcategory name

        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest

        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(categoryService.readCategory(1L)).thenReturn(Optional.of(new Category())); // Assuming category exists
        when(subCategoryService.readSubCategory(eq("TestSubCategory"), any(Category.class)))
                .thenReturn(new SubCategory()); // This should trigger the conflict scenario

        ResponseEntity<ApiResponse> response = subCategoryController.createSubCategory(subCategoryDto, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Sub Category already exists", Objects.requireNonNull(response.getBody()).getMessage());
    }




    @Test
    void listSubCategories_ReturnsListOfSubCategories() {
        List<SubCategory> subCategoryList = new ArrayList<>();
        subCategoryList.add(new SubCategory());
        subCategoryList.add(new SubCategory());

        when(subCategoryService.listSubCategory()).thenReturn(subCategoryList);

        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategoryList, response.getBody());
    }

    @Test
    void listSubCategoriesByCategoriesId_ValidCategoryId_ReturnsListOfSubCategories() {
        Long categoryId = 1L; // Assuming valid category ID
        List<SubCategory> subCategoryList = new ArrayList<>();
        subCategoryList.add(new SubCategory());
        subCategoryList.add(new SubCategory());

        when(categoryService.readCategory(categoryId)).thenReturn(Optional.of(new Category())); // Assuming category exists
        when(subCategoryService.listSubCategory(categoryId)).thenReturn(subCategoryList);

        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategoriesByCategoriesId(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategoryList, response.getBody());
    }

    @Test
    void createSubCategory_ParentCategoryNotFound_ReturnsNotFound() {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L); // Assuming invalid category ID
        subCategoryDto.setSubcategoryName("TestSubCategory");

        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest

        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(categoryService.readCategory(1L)).thenReturn(Optional.empty()); // Parent category isn't found

        ResponseEntity<ApiResponse> response = subCategoryController.createSubCategory(subCategoryDto, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Parent Category is invalid", Objects.requireNonNull(response.getBody()).getMessage());
    }


    @Test
    void listSubCategoriesByCategoriesId_InvalidCategoryId_ReturnsNotFound() {
        Long categoryId = 1L; // Assuming invalid category ID

        when(categoryService.readCategory(categoryId)).thenReturn(Optional.empty());

        ResponseEntity<List<SubCategory>> response = subCategoryController.listSubCategoriesByCategoriesId(categoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    void updateSubCategory_ValidData_ReturnsOk() {
        Long subcategoryId = 1L; // Assuming valid subcategory ID
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L); // Assuming valid category ID

        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest

        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(categoryService.readCategory(1L)).thenReturn(Optional.of(new Category())); // Assuming category exists
        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(Optional.of(new SubCategory()));

        ResponseEntity<ApiResponse> response = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Subcategory updated successfully", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void updateSubCategory_InvalidCategoryId_ReturnsNotFound() {
        Long subcategoryId = 1L; // Assuming valid subcategory ID
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L); // Assuming invalid category ID

        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest

        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(categoryService.readCategory(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category is invalid", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void updateSubCategory_InvalidSubcategoryId_ReturnsNotFound() {
        Long subcategoryId = 1L; // Assuming invalid subcategory ID
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L); // Assuming valid category ID

        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest

        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(categoryService.readCategory(1L)).thenReturn(Optional.of(new Category())); // Assuming category exists
        when(subCategoryService.readSubCategory(subcategoryId)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = subCategoryController.updateSubCategory(subcategoryId, subCategoryDto, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Subcategory is invalid", Objects.requireNonNull(response.getBody()).getMessage());
    }
}
