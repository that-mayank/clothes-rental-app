package com.nineleaps.leaps.service.implementation;


import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;

import com.nineleaps.leaps.repository.SubCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubCategoryServiceImplTest {

    @InjectMocks
    private SubCategoryServiceImpl subCategoryService;


    @Mock
    private SubCategoryRepository subCategoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSubCategory_SubCategoryCreatedSuccessfully() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        Category category = new Category();
        User user = new User();
        when(subCategoryRepository.save(any())).thenReturn(new SubCategory());

        // Act
        SubCategory subCategory = subCategoryService.createSubCategory(subCategoryDto, category, user);

        // Assert
        // Ensure that subCategoryRepository.save() is called once
        verify(subCategoryRepository, times(1)).save(any());
    }
    @Test
    void createSubCategory_CorrectAttributesSet() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Test Subcategory");
        Category category = new Category();
        User user = new User();

        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(new SubCategory());

        // Act
        SubCategory subCategory = subCategoryService.createSubCategory(subCategoryDto, category, user);

        // Assert
        ArgumentCaptor<SubCategory> subCategoryCaptor = ArgumentCaptor.forClass(SubCategory.class);
        verify(subCategoryRepository).save(subCategoryCaptor.capture());

        SubCategory savedSubCategory = subCategoryCaptor.getValue();
        assertNotNull(savedSubCategory);
        assertEquals(subCategoryDto.getSubcategoryName(), savedSubCategory.getSubcategoryName());
        assertEquals(user.getId(), savedSubCategory.getSubCategoryCreatedBy());
        assertNotNull(savedSubCategory.getSubCategoryCreatedAt());
    }


    @Test
    void readSubCategory_SubCategoryFound() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        String subcategoryName = "Test Subcategory";

        // Mock listSubCategory to return a subcategory with the specified name
        List<SubCategory> subCategories = new ArrayList<>();
        SubCategory subCategory = new SubCategory();
        subCategory.setSubcategoryName(subcategoryName);
        subCategories.add(subCategory);
        when(subCategoryService.listSubCategory(category.getId())).thenReturn(subCategories);

        // Act
        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);

        // Assert
        assertNotNull(result);
        assertEquals(subcategoryName, result.getSubcategoryName());
    }

    @Test
    void readSubCategory_SubCategoryNotFound() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        String subcategoryName = "Nonexistent Subcategory";

        // Mock listSubCategory to return an empty list
        when(subCategoryService.listSubCategory(category.getId())).thenReturn(Collections.emptyList());

        // Act
        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);

        // Assert
        assertNull(result);
    }

//    @Test
//    void readSubCategory_ValidSubcategoryName_ReturnsSubCategory() {
//        // Arrange
//        String subcategoryName = "TestSubcategory";
//        Category category = new Category();
//        category.setId(1L); // Set a valid category ID for testing
//
//        SubCategory expectedSubCategory = new SubCategory();
//        expectedSubCategory.setSubcategoryName(subcategoryName);
//
//        // Mock the behavior of listSubCategory to return a list containing the expected subcategory
//        when(subCategoryService.listSubCategory(category.getId())).thenReturn(Collections.singletonList(expectedSubCategory));
//
//        // Act
//        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);
//
//        // Assert
//        assertEquals(expectedSubCategory, result);
//    }
//
//    @Test
//    void readSubCategory_InvalidSubcategoryName_ReturnsNull() {
//        // Arrange
//        String subcategoryName = "NonExistentSubcategory";
//        Category category = new Category();
//        category.setId(1L); // Set a valid category ID for testing
//
//        // Mock the behavior of listSubCategory to return an empty list
//        when(subCategoryService.listSubCategory(category.getId())).thenReturn(Collections.emptyList());
//
//        // Act
//        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);
//
//        // Assert
//        assertNull(result);
//    }

    @Test
    void readSubCategory_SubcategoryNameMatches_ReturnsSubCategory() {
        // Arrange
        String subcategoryName = "TestSubcategory";
        Category category = new Category();

        SubCategory subCategory = new SubCategory();
        subCategory.setSubcategoryName(subcategoryName);

        // Mock the behavior of listSubCategory to return a list containing the subCategory
        when(subCategoryService.listSubCategory(category.getId())).thenReturn(List.of(subCategory));

        // Act
        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);

        // Assert
        assertNotNull(result);
        assertEquals(subCategory, result);
    }

    @Test
    void getSubCategoryFromDto_CorrectSubCategoryReturned() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Test Subcategory");
        subCategoryDto.setImageURL("test-image-url");
        subCategoryDto.setDescription("Test description");
        subCategoryDto.setCategoryId(1L);  // Assuming category id

        Category category = new Category();
        category.setId(1L);

        // Act
        SubCategory result = subCategoryService.getSubCategoryFromDto(subCategoryDto, category);

        // Assert
        assertNotNull(result);
        assertEquals(subCategoryDto.getSubcategoryName(), result.getSubcategoryName());
        assertEquals(subCategoryDto.getImageURL(), result.getImageUrl());
        assertEquals(subCategoryDto.getDescription(), result.getDescription());
        assertEquals(category, result.getCategory());
    }


    @Test
    void readSubCategoryBySubCategoryId_SubCategoryFound() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategory expectedSubCategory = new SubCategory();
        when(subCategoryRepository.findById(subcategoryId)).thenReturn(Optional.of(expectedSubCategory));

        // Act
        Optional<SubCategory> result = subCategoryService.readSubCategory(subcategoryId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedSubCategory, result.get());
    }

    @Test
    void  readSubCategoryBySubCategoryId_SubCategoryNotFound() {
        // Arrange
        Long subcategoryId = 1L;
        when(subCategoryRepository.findById(subcategoryId)).thenReturn(Optional.empty());

        // Act
        Optional<SubCategory> result = subCategoryService.readSubCategory(subcategoryId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void listSubCategory_ReturnListOfSubCategories() {
        // Arrange
        List<SubCategory> expectedSubCategories = new ArrayList<>();
        expectedSubCategories.add(new SubCategory());
        expectedSubCategories.add(new SubCategory());
        when(subCategoryRepository.findAll()).thenReturn(expectedSubCategories);

        // Act
        List<SubCategory> result = subCategoryService.listSubCategory();

        // Assert
        assertEquals(expectedSubCategories.size(), result.size());
        assertTrue(result.containsAll(expectedSubCategories));
    }

    @Test
    void listSubCategoryForCategory_ReturnListOfSubCategories() {
        // Arrange
        Long categoryId = 1L;
        List<SubCategory> expectedSubCategories = new ArrayList<>();
        expectedSubCategories.add(new SubCategory());
        expectedSubCategories.add(new SubCategory());
        when(subCategoryRepository.findByCategoryId(categoryId)).thenReturn(expectedSubCategories);

        // Act
        List<SubCategory> result = subCategoryService.listSubCategory(categoryId);

        // Assert
        assertEquals(expectedSubCategories.size(), result.size());
        assertTrue(result.containsAll(expectedSubCategories));
    }

    @Test
    void listSubCategoryForCategory_CategoryNotFound_ReturnEmptyList() {
        // Arrange
        Long categoryId = 1L;
        when(subCategoryRepository.findByCategoryId(categoryId)).thenReturn(Collections.emptyList());

        // Act
        List<SubCategory> result = subCategoryService.listSubCategory(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void updateSubCategory_SubCategoryUpdatedSuccessfully() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Updated Subcategory");
        Category category = new Category();
        User user = new User();

        SubCategory existingSubCategory = new SubCategory();
        existingSubCategory.setId(subcategoryId);
        existingSubCategory.setSubCategoryCreatedBy(user.getId());
        existingSubCategory.setSubCategoryUpdatedBy(user.getId());

        when(subCategoryRepository.save(any())).thenReturn(existingSubCategory);
        existingSubCategory = subCategoryService.getSubCategoryFromDto(subCategoryDto, category);

        // Act
        subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category, user);

        // Assert
        assertNotNull(subCategoryDto);  // Ensure subCategoryDto is not null
        assertEquals("Updated Subcategory", subCategoryDto.getSubcategoryName());
        assertEquals(user.getId(), existingSubCategory.getSubCategoryCreatedBy());
        assertEquals(user.getId(), existingSubCategory.getSubCategoryUpdatedBy());
    }

    @Test
    void updateSubCategory_SubCategoryNotFound() {
        // Arrange
        Long subcategoryId = 1L;
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        Category category = new Category();
        User user = new User();

        // Mock the behavior of getSubCategoryFromDto to return null for the provided arguments
        subCategoryService.getSubCategoryFromDto(subCategoryDto, category);

        // Stub the save method of subCategoryRepository
        when(subCategoryRepository.save(any())).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category, user));
    }


    @Test
    void getSubCategoriesFromIds_ValidIds_ReturnsSubCategories() {
        // Arrange
        List<Long> subcategoryIds = Arrays.asList(1L, 2L, 3L);
        List<SubCategory> expectedSubCategories = Arrays.asList(
                new SubCategory(), new SubCategory(), new SubCategory());

        // Mock the behavior of readSubCategory to return subcategories for each ID
        when(subCategoryService.readSubCategory(anyLong())).thenReturn(Optional.of(new SubCategory()));

        // Act
        List<SubCategory> result = subCategoryService.getSubCategoriesFromIds(subcategoryIds);

        // Assert
        assertEquals(expectedSubCategories.size(), result.size());
    }

    @Test
    void getSubCategoriesFromIds_InvalidIds_ThrowsException() {
        // Arrange
        List<Long> subcategoryIds = Arrays.asList(1L, 2L, 3L);

        // Mock the behavior of readSubCategory to return empty optional (indicating invalid ID)
        when(subCategoryService.readSubCategory(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotExistException.class, () -> {
            subCategoryService.getSubCategoriesFromIds(subcategoryIds);
        });
    }


}
