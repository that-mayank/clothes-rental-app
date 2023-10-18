package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubCategoryServiceImplTest {

    @Mock
    private SubCategoryRepository categoryRepository;

    private SubCategoryServiceImpl subCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subCategoryService = new SubCategoryServiceImpl(categoryRepository);
    }

    @Test
    void createSubCategory() {
        // Prepare test data
        Category category = new Category();
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Test Subcategory");

        // Perform createSubCategory method
        subCategoryService.createSubCategory(subCategoryDto, category);

        // Verify that the save method is called on the categoryRepository
        verify(categoryRepository).save(any(SubCategory.class));
    }

    @Test
    void readSubCategory() {
        // Prepare test data
        Category category = new Category();
        category.setId(1L);
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setSubcategoryName("Subcategory 1");
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setSubcategoryName("Subcategory 2");
        List<SubCategory> subCategories = new ArrayList<>();
        subCategories.add(subCategory1);
        subCategories.add(subCategory2);

        // Mock the behavior of categoryRepository
        when(categoryRepository.findByCategoryId(category.getId())).thenReturn(subCategories);

        // Perform readSubCategory method
        SubCategory result1 = subCategoryService.readSubCategory("Subcategory 1", category);
        SubCategory result2 = subCategoryService.readSubCategory("Subcategory 2", category);
        SubCategory result3 = subCategoryService.readSubCategory("Non-existent Subcategory", category);

        // Verify that the correct subcategory is returned
        assertEquals(subCategory1, result1);
        assertEquals(subCategory2, result2);
        assertNull(result3);
    }


    @Test
    void readSubCategory_WithInvalidSubcategoryName_ReturnsNull() {
        // Arrange
        Category validCategory = new Category();
        validCategory.setId(1L);
        validCategory.setCategoryName("Category 1");

        SubCategory subCategory1 = new SubCategory();
        subCategory1.setId(1L);
        subCategory1.setSubcategoryName("Subcategory 1");

        SubCategory subCategory2 = new SubCategory();
        subCategory2.setId(2L);
        subCategory2.setSubcategoryName("Subcategory 2");

        when(categoryRepository.findById(validCategory.getId())).thenReturn(Optional.empty());

        // Act
        SubCategory result = subCategoryService.readSubCategory("Invalid Subcategory", validCategory);

        // Assert
        assertNull(result);
    }

    @Test
    void testReadSubCategory() {
        // Prepare test data
        Long subcategoryId = 1L;
        SubCategory subCategory = new SubCategory();
        subCategory.setId(subcategoryId);

        // Mock the behavior of categoryRepository
        when(categoryRepository.findById(subcategoryId)).thenReturn(Optional.of(subCategory));

        // Perform readSubCategory method
        Optional<SubCategory> result = subCategoryService.readSubCategory(subcategoryId);

        // Verify that the correct subcategory is returned
        assertTrue(result.isPresent());
        assertEquals(subCategory, result.get());
    }

    @Test
    void listSubCategory() {
        // Prepare test data
        List<SubCategory> subCategories = new ArrayList<>();
        subCategories.add(new SubCategory());
        subCategories.add(new SubCategory());

        // Mock the behavior of categoryRepository
        when(categoryRepository.findAll()).thenReturn(subCategories);

        // Perform listSubCategory method
        List<SubCategory> result = subCategoryService.listSubCategory();

        // Verify that the correct list of subcategories is returned
        assertEquals(subCategories, result);
    }

    @Test
    void testListSubCategory() {
        // Prepare test data
        Long categoryId = 1L;
        List<SubCategory> subCategories = new ArrayList<>();
        subCategories.add(new SubCategory());
        subCategories.add(new SubCategory());

        // Mock the behavior of categoryRepository
        when(categoryRepository.findByCategoryId(categoryId)).thenReturn(subCategories);

        // Perform listSubCategory method
        List<SubCategory> result = subCategoryService.listSubCategory(categoryId);

        // Verify that the correct list of subcategories is returned
        assertEquals(subCategories, result);
    }

    @Test
    void updateSubCategory_SubCategoryNotNull() {
        // Prepare test data
        Long subcategoryId = 1L;
        Category category = new Category();
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Updated Subcategory");

        // Create a mock updatedSubCategory
        SubCategory updatedSubCategory = new SubCategory();
        updatedSubCategory.setId(subcategoryId);

        // Mock the behavior of categoryRepository
        when(categoryRepository.save(any(SubCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Perform updateSubCategory method
        subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category);

        // Verify that the save method is called on the categoryRepository with the updated subcategory
        verify(categoryRepository).save(argThat(subCategory -> subCategory.getId().equals(subcategoryId)
                && subCategory.getSubcategoryName().equals(subCategoryDto.getSubcategoryName())));

        // Additional assertion to verify that the ID is set on the updated subcategory
        assertNotNull(updatedSubCategory);
        assertNotNull(updatedSubCategory.getId());
        assertEquals(subcategoryId, updatedSubCategory.getId());
    }


    @Test
    void getSubCategoriesFromIds() {
        // Prepare test data
        List<Long> subcategoryIds = new ArrayList<>();
        subcategoryIds.add(1L);
        subcategoryIds.add(2L);
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setId(1L);
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setId(2L);
        List<SubCategory> subCategories = new ArrayList<>();
        subCategories.add(subCategory1);
        subCategories.add(subCategory2);

        // Mock the behavior of categoryRepository
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(subCategory1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(subCategory2));

        // Perform getSubCategoriesFromIds method
        List<SubCategory> result = subCategoryService.getSubCategoriesFromIds(subcategoryIds);

        // Verify that the correct list of subcategories is returned
        assertEquals(subCategories, result);
    }

    @Test
    void getSubCategoriesFromIds_SubCategoryNotExist() {
        // Prepare test data
        List<Long> subcategoryIds = new ArrayList<>();
        subcategoryIds.add(1L);
        subcategoryIds.add(2L);
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setId(1L);


        // Mock the behavior of categoryRepository
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(subCategory1));
        // Mocking the scenario where the subcategory with ID 2 does not exist
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        // Perform getSubCategoriesFromIds method and assert an exception is thrown
        assertThrows(CategoryNotExistException.class, () -> subCategoryService.getSubCategoriesFromIds(subcategoryIds));
    }
}