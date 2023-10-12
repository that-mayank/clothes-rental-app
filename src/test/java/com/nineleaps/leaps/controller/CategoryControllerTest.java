package com.nineleaps.leaps.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;

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
    void createCategory_ValidCategoryDto_ReturnsCreatedResponse() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);
        Category existingCategory = new Category();

        when(helper.getUser(request)).thenReturn(adminUser);
        when(categoryService.readCategory(categoryDto.getCategoryName())).thenReturn(existingCategory);
        doNothing().when(categoryService).createCategory(any(Category.class));

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto, request);

        // Assert
        assertNotNull(responseEntity);
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
    }

    @Test
    void createCategory_AdminUserInvalidRole_ReturnsForbiddenResponse() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.BORROWER);

        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
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
        ResponseEntity<ApiResponse> responseEntity = categoryController.createCategory(categoryDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }

    @Test
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
        ResponseEntity<ApiResponse> responseEntity = categoryController.updateCategory(categoryId, updateCategoryDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("category has been updated", response.getMessage());
    }

    @Test
    void updateCategory_UserInvalidRole_ReturnsForbiddenResponse() {
        // Arrange
        Long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.BORROWER);

        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = categoryController.updateCategory(categoryId, updateCategoryDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
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
        ResponseEntity<ApiResponse> responseEntity = categoryController.updateCategory(categoryId, updateCategoryDto, request);

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
