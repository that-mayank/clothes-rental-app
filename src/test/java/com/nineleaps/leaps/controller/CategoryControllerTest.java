package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName("Test case file for Category Controller ")
@ExtendWith(RuntimeBenchmarkExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryServiceInterface categoryService;

    @Mock
    private Helper helper;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create category")
    void createCategory() {
        // Mock data
        CategoryDto categoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(categoryService.readCategory(anyString())).thenReturn(null);

        // Call the method
        ResponseEntity<ApiResponse> response = categoryController.createCategory(categoryDto, request);

        // Assert the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Created a new Category", response.getBody().getMessage());
    }
    @Test
    @DisplayName("Create category - category doesnt exist")
    void createCategory_CategoryDoesNotExist_ReturnsCreated() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("TestCategory");

        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest

        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(categoryService.readCategory("TestCategory")).thenReturn(null); // Assuming it returns null when not found

        ResponseEntity<ApiResponse> response = categoryController.createCategory(categoryDto, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Created a new Category", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Category already exists")
    void createCategory_CategoryExists_ReturnsConflict() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("TestCategory");

        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest

        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(categoryService.readCategory("TestCategory")).thenReturn(new Category()); // Assuming it returns a category when found

        ResponseEntity<ApiResponse> response = categoryController.createCategory(categoryDto, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Category already exists", Objects.requireNonNull(response.getBody()).getMessage());
    }
    @Test
    @DisplayName("List Category")
    void listCategory() {
        // Mock data
        List<Category> categories = new ArrayList<>();

        // Mock behavior
        when(categoryService.listCategory()).thenReturn(categories);

        // Call the method
        ResponseEntity<List<Category>> response = categoryController.listCategory();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
    }

    @Test
    @DisplayName("Update Category")
    void updateCategory_CategoryExists_Success() {
        // Mock data
        long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(categoryService.readCategory(categoryId)).thenReturn(java.util.Optional.of(new Category()));

        // Call the method
        ResponseEntity<ApiResponse> response = categoryController.updateCategory(categoryId, updateCategoryDto, request);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("category has been updated", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Update category - category doesnt exist")
    void updateCategory_CategoryDoesNotExist_NotFound() {
        // Mock data
        long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(categoryService.readCategory(categoryId)).thenReturn(java.util.Optional.empty());

        // Call the method
        ResponseEntity<ApiResponse> response = categoryController.updateCategory(categoryId, updateCategoryDto, request);

        // Assert the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("category does not exist", response.getBody().getMessage());
    }
}
