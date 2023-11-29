//package com.nineleaps.leaps.service.implementation;
//
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.dto.category.SubCategoryDto;
//import com.nineleaps.leaps.exceptions.CategoryNotExistException;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.model.categories.Category;
//import com.nineleaps.leaps.model.categories.SubCategory;
//
//import com.nineleaps.leaps.repository.SubCategoryRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//@Tag("unit_tests")
//@DisplayName("Subcategory Service Tests")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//
//class SubCategoryServiceImplTest {
//
//    @InjectMocks
//    private SubCategoryServiceImpl subCategoryService;
//
//
//    @Mock
//    private SubCategoryRepository subCategoryRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("Create SubCategory - SubCategory Created Successfully")
//    void createSubCategory_SubCategoryCreatedSuccessfully() {
//        // Arrange
//        SubCategoryDto subCategoryDto = new SubCategoryDto();
//        Category category = new Category();
//        User user = new User();
//        when(subCategoryRepository.save(any())).thenReturn(new SubCategory());
//
//        // Act
//        SubCategory subCategory = subCategoryService.createSubCategory(subCategoryDto);
//
//        // Assert
//        // Ensure that subCategoryRepository.save() is called once
//        verify(subCategoryRepository, times(1)).save(any());
//    }
//    @Test
//    @DisplayName("Create SubCategory - Correct Attributes Set")
//    void createSubCategory_CorrectAttributesSet() {
//        // Arrange
//        SubCategoryDto subCategoryDto = new SubCategoryDto();
//        subCategoryDto.setSubcategoryName("Test Subcategory");
//        Category category = new Category();
//        User user = new User();
//
//        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(new SubCategory());
//
//        // Act
//        SubCategory subCategory = subCategoryService.createSubCategory(subCategoryDto, category, user);
//
//        // Assert
//        ArgumentCaptor<SubCategory> subCategoryCaptor = ArgumentCaptor.forClass(SubCategory.class);
//        verify(subCategoryRepository).save(subCategoryCaptor.capture());
//
//        SubCategory savedSubCategory = subCategoryCaptor.getValue();
//        assertNotNull(savedSubCategory);
//        assertEquals(subCategoryDto.getSubcategoryName(), savedSubCategory.getSubcategoryName());
//        assertEquals(user.getId(), savedSubCategory.getSubCategoryCreatedBy());
//        assertNotNull(savedSubCategory.getSubCategoryCreatedAt());
//    }
//
//
//    @Test
//    @DisplayName("Read SubCategory - SubCategory Found")
//    void readSubCategory_SubCategoryFound() {
//        // Arrange
//        Category category = new Category();
//        category.setId(1L);
//        String subcategoryName = "Test Subcategory";
//
//        // Mock listSubCategory to return a subcategory with the specified name
//        List<SubCategory> subCategories = new ArrayList<>();
//        SubCategory subCategory = new SubCategory();
//        subCategory.setSubcategoryName(subcategoryName);
//        subCategories.add(subCategory);
//        when(subCategoryService.listSubCategory(category.getId())).thenReturn(subCategories);
//
//        // Act
//        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(subcategoryName, result.getSubcategoryName());
//    }
//
//    @Test
//    @DisplayName("Read SubCategory - SubCategory Not Found")
//    void readSubCategory_SubCategoryNotFound() {
//        // Arrange
//        Category category = new Category();
//        category.setId(1L);
//        String subcategoryName = "Nonexistent Subcategory";
//
//        // Mock listSubCategory to return an empty list
//        when(subCategoryService.listSubCategory(category.getId())).thenReturn(Collections.emptyList());
//
//        // Act
//        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);
//
//        // Assert
//        assertNull(result);
//    }
//
////    @Test
////    void readSubCategory_ValidSubcategoryName_ReturnsSubCategory() {
////        // Arrange
////        String subcategoryName = "TestSubcategory";
////        Category category = new Category();
////        category.setId(1L); // Set a valid category ID for testing
////
////        SubCategory expectedSubCategory = new SubCategory();
////        expectedSubCategory.setSubcategoryName(subcategoryName);
////
////        // Mock the behavior of listSubCategory to return a list containing the expected subcategory
////        when(subCategoryService.listSubCategory(category.getId())).thenReturn(Collections.singletonList(expectedSubCategory));
////
////        // Act
////        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);
////
////        // Assert
////        assertEquals(expectedSubCategory, result);
////    }
////
////    @Test
////    void readSubCategory_InvalidSubcategoryName_ReturnsNull() {
////        // Arrange
////        String subcategoryName = "NonExistentSubcategory";
////        Category category = new Category();
////        category.setId(1L); // Set a valid category ID for testing
////
////        // Mock the behavior of listSubCategory to return an empty list
////        when(subCategoryService.listSubCategory(category.getId())).thenReturn(Collections.emptyList());
////
////        // Act
////        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);
////
////        // Assert
////        assertNull(result);
////    }
//
//    @Test
//    @DisplayName("Read SubCategory - Subcategory Name Matches - Returns SubCategory")
//    void readSubCategory_SubcategoryNameMatches_ReturnsSubCategory() {
//        // Arrange
//        String subcategoryName = "TestSubcategory";
//        Category category = new Category();
//
//        SubCategory subCategory = new SubCategory();
//        subCategory.setSubcategoryName(subcategoryName);
//
//        // Mock the behavior of listSubCategory to return a list containing the subCategory
//        when(subCategoryService.listSubCategory(category.getId())).thenReturn(List.of(subCategory));
//
//        // Act
//        SubCategory result = subCategoryService.readSubCategory(subcategoryName, category);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(subCategory, result);
//    }
//
//    @Test
//    @DisplayName("Get SubCategory from DTO - Correct SubCategory Returned")
//    void getSubCategoryFromDto_CorrectSubCategoryReturned() {
//        // Arrange
//        SubCategoryDto subCategoryDto = new SubCategoryDto();
//        subCategoryDto.setSubcategoryName("Test Subcategory");
//        subCategoryDto.setImageURL("test-image-url");
//        subCategoryDto.setDescription("Test description");
//        subCategoryDto.setCategoryId(1L);  // Assuming category id
//
//        Category category = new Category();
//        category.setId(1L);
//
//        // Act
//        SubCategory result = subCategoryService.getSubCategoryFromDto(subCategoryDto, category);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(subCategoryDto.getSubcategoryName(), result.getSubcategoryName());
//        assertEquals(subCategoryDto.getImageURL(), result.getImageUrl());
//        assertEquals(subCategoryDto.getDescription(), result.getDescription());
//        assertEquals(category, result.getCategory());
//    }
//
//
//    @Test
//    @DisplayName("Read SubCategory by SubCategoryId - SubCategory Found")
//    void readSubCategoryBySubCategoryId_SubCategoryFound() {
//        // Arrange
//        Long subcategoryId = 1L;
//        SubCategory expectedSubCategory = new SubCategory();
//        when(subCategoryRepository.findById(subcategoryId)).thenReturn(Optional.of(expectedSubCategory));
//
//        // Act
//        Optional<SubCategory> result = subCategoryService.readSubCategory(subcategoryId);
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(expectedSubCategory, result.get());
//    }
//
//    @Test
//    @DisplayName("Read SubCategory by SubCategoryId - SubCategory Not Found")
//    void  readSubCategoryBySubCategoryId_SubCategoryNotFound() {
//        // Arrange
//        Long subcategoryId = 1L;
//        when(subCategoryRepository.findById(subcategoryId)).thenReturn(Optional.empty());
//
//        // Act
//        Optional<SubCategory> result = subCategoryService.readSubCategory(subcategoryId);
//
//        // Assert
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("List SubCategory - Return List of SubCategories")
//    void listSubCategory_ReturnListOfSubCategories() {
//        // Arrange
//        List<SubCategory> expectedSubCategories = new ArrayList<>();
//        expectedSubCategories.add(new SubCategory());
//        expectedSubCategories.add(new SubCategory());
//        when(subCategoryRepository.findAll()).thenReturn(expectedSubCategories);
//
//        // Act
//        List<SubCategory> result = subCategoryService.listSubCategory();
//
//        // Assert
//        assertEquals(expectedSubCategories.size(), result.size());
//        assertTrue(result.containsAll(expectedSubCategories));
//    }
//
//    @Test
//    @DisplayName("List SubCategory for Category - Return List of SubCategories")
//    void listSubCategoryForCategory_ReturnListOfSubCategories() {
//        // Arrange
//        Long categoryId = 1L;
//        List<SubCategory> expectedSubCategories = new ArrayList<>();
//        expectedSubCategories.add(new SubCategory());
//        expectedSubCategories.add(new SubCategory());
//        when(subCategoryRepository.findByCategoryId(categoryId)).thenReturn(expectedSubCategories);
//
//        // Act
//        List<SubCategory> result = subCategoryService.listSubCategory(categoryId);
//
//        // Assert
//        assertEquals(expectedSubCategories.size(), result.size());
//        assertTrue(result.containsAll(expectedSubCategories));
//    }
//
//    @Test
//    @DisplayName("List SubCategory for Category - Category Not Found - Return Empty List")
//    void listSubCategoryForCategory_CategoryNotFound_ReturnEmptyList() {
//        // Arrange
//        Long categoryId = 1L;
//        when(subCategoryRepository.findByCategoryId(categoryId)).thenReturn(Collections.emptyList());
//
//        // Act
//        List<SubCategory> result = subCategoryService.listSubCategory(categoryId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(0, result.size());
//    }
//
//    @Test
//    @DisplayName("Update SubCategory - SubCategory Updated Successfully")
//    void updateSubCategory_SubCategoryUpdatedSuccessfully() {
//        // Arrange
//        Long subcategoryId = 1L;
//        SubCategoryDto subCategoryDto = new SubCategoryDto();
//        subCategoryDto.setSubcategoryName("Updated Subcategory");
//        Category category = new Category();
//        User user = new User();
//
//        SubCategory existingSubCategory = new SubCategory();
//        existingSubCategory.setId(subcategoryId);
//        existingSubCategory.setSubCategoryCreatedBy(user.getId());
//        existingSubCategory.setSubCategoryUpdatedBy(user.getId());
//
//        when(subCategoryRepository.save(any())).thenReturn(existingSubCategory);
//        existingSubCategory = subCategoryService.getSubCategoryFromDto(subCategoryDto, category);
//
//        // Act
//        subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category, user);
//
//        // Assert
//        assertNotNull(subCategoryDto);  // Ensure subCategoryDto is not null
//        assertEquals("Updated Subcategory", subCategoryDto.getSubcategoryName());
//        assertEquals(user.getId(), existingSubCategory.getSubCategoryCreatedBy());
//        assertEquals(user.getId(), existingSubCategory.getSubCategoryUpdatedBy());
//    }
//
//    @Test
//    @DisplayName("Update SubCategory - SubCategory Not Found")
//    void updateSubCategory_SubCategoryNotFound() {
//        // Arrange
//        Long subcategoryId = 1L;
//        SubCategoryDto subCategoryDto = new SubCategoryDto();
//        Category category = new Category();
//        User user = new User();
//
//        // Mock the behavior of getSubCategoryFromDto to return null for the provided arguments
//        subCategoryService.getSubCategoryFromDto(subCategoryDto, category);
//
//        // Stub the save method of subCategoryRepository
//        when(subCategoryRepository.save(any())).thenReturn(null);
//
//        // Act & Assert
//        assertDoesNotThrow(() -> subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category, user));
//    }
//
//
//    @Test
//    @DisplayName("Get SubCategories from Ids - Valid Ids - Returns SubCategories")
//    void getSubCategoriesFromIds_ValidIds_ReturnsSubCategories() {
//        // Arrange
//        List<Long> subcategoryIds = Arrays.asList(1L, 2L, 3L);
//        List<SubCategory> expectedSubCategories = Arrays.asList(
//                new SubCategory(), new SubCategory(), new SubCategory());
//
//        // Mock the behavior of readSubCategory to return subcategories for each ID
//        when(subCategoryService.readSubCategory(anyLong())).thenReturn(Optional.of(new SubCategory()));
//
//        // Act
//        List<SubCategory> result = subCategoryService.getSubCategoriesFromIds(subcategoryIds);
//
//        // Assert
//        assertEquals(expectedSubCategories.size(), result.size());
//    }
//
//    @Test
//    @DisplayName("Get SubCategories from Ids - Invalid Ids - Throws Exception")
//    void getSubCategoriesFromIds_InvalidIds_ThrowsException() {
//        // Arrange
//        List<Long> subcategoryIds = Arrays.asList(1L, 2L, 3L);
//
//        // Mock the behavior of readSubCategory to return empty optional (indicating invalid ID)
//        when(subCategoryService.readSubCategory(anyLong())).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(CategoryNotExistException.class, () -> {
//            subCategoryService.getSubCategoriesFromIds(subcategoryIds);
//        });
//    }
//
//
//}


package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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

    @Mock
    private CategoryServiceInterface categoryService;

    @InjectMocks
    private SubCategoryServiceImpl subCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSubCategory() {
        // Prepare test data
        Category category = new Category();
        category.setCategoryName("Category");
        category.setId(1L);

        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Test Subcategory");
        subCategoryDto.setCategoryId(1L);

        SubCategory subCategory1 = new SubCategory();
        SubCategory subCategory2 = new SubCategory();
        SubCategory subCategory3 = new SubCategory();

        List<SubCategory> subcategoryList = new ArrayList<>();
        subcategoryList.add(subCategory1);
        subcategoryList.add(subCategory2);
        subcategoryList.add(subCategory3);


        when(categoryService.readCategory(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.findByCategoryId(anyLong())).thenReturn(subcategoryList);

        // Perform createSubCategory method
        subCategoryService.createSubCategory(subCategoryDto);

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
        //Arrange
        Long subCategoryId = 1L;

        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);
        subCategoryDto.setSubcategoryName("Updated SubCategory");
        subCategoryDto.setDescription("Description");
        subCategoryDto.setImageURL("/image-url.jpeg");

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Category");
        category.setDescription("Description");
        category.setImageUrl("/image-url.jpeg");

        SubCategory subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setCategory(category);
        subCategory.setSubcategoryName("Subcategory");
        subCategory.setDescription("Description");
        subCategory.setImageUrl("/image-url.jpeg");

        when(categoryService.readCategory(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(subCategory));

        //Act
        subCategoryService.updateSubCategory(subCategoryId, subCategoryDto);

        //Assert
        verify(categoryRepository).save(any(SubCategory.class));
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