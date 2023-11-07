package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;
    @Mock
    private CategoryServiceInterface categoryService;
    @Mock
    private Helper helper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Category - Success")
    void createCategory_ValidCategoryDto_ReturnsCreatedResponse() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);
        Category existingCategory = new Category();

        when(helper.getUser(request)).thenReturn(adminUser);
        when(categoryService.readCategory(categoryDto.getCategoryName())).thenReturn(existingCategory);
        doNothing().when(categoryService).createCategory(any(CategoryDto.class));

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto);

        // Assert
        assertNotNull(responseEntity);
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
    }

    @Test
    @DisplayName("Create Category - Invalid Role")
    void createCategory_AdminUserInvalidRole_ReturnsForbiddenResponse() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.BORROWER);

        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Create Category - Category Exists Conflict")
    void createCategory_CategoryExists_ReturnsConflictResponse() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);
        Category existingCategory = new Category();

        when(helper.getUser(request)).thenReturn(adminUser);
        when(categoryService.readCategory(categoryDto.getCategoryName())).thenReturn(existingCategory);

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Create Category - Success")
    void testCreateCategorySuccess() {
        // Prepare a mock CategoryDto
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("NewCategory");
        categoryDto.setDescription("Category Description");

        // Prepare a mock HttpServletRequest
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        // Prepare a mock User
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);

        // Mock the behavior of helper.getUser(request)
        when(helper.getUser(request)).thenReturn(user);

        // Mock the behavior of categoryService.readCategory
        when(categoryService.readCategory(categoryDto.getCategoryName())).thenReturn(null);

        // Create a category entity from the CategoryDto
        Category category = new Category(categoryDto);

        // Mock the behavior of categoryService.createCategory to do nothing
        Mockito.doNothing().when(categoryService).createCategory(categoryDto);

        // Call the createCategory method
        ResponseEntity<ApiResponse> response = categoryController.createCategory(categoryDto);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Created a new Category", response.getBody().getMessage());
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
        HttpServletRequest request = mock(HttpServletRequest.class);
        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);
        Category existingCategory = new Category();

        when(helper.getUser(request)).thenReturn(adminUser);
        when(categoryService.readCategory(categoryId)).thenReturn(Optional.of(existingCategory));
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

    @Test
    @DisplayName("Update Category - Invalid Role")
    void updateCategory_UserInvalidRole_ReturnsForbiddenResponse() {
        // Arrange
        Long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.BORROWER);

        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.updateCategory(categoryId, updateCategoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Update Category - Category Not Found")
    void updateCategory_CategoryNotExists_ReturnsNotFoundResponse() {
        // Arrange
        Long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);

        when(helper.getUser(request)).thenReturn(adminUser);
        when(categoryService.readCategory(categoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.updateCategory(categoryId, updateCategoryDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("category does not exist", response.getMessage());

        verify(categoryService, never()).updateCategory(anyLong(), any(CategoryDto.class));
    }
}
