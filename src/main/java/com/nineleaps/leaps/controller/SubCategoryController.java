package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/v1/subcategory")
@AllArgsConstructor
@Api(tags = "Subcategory Api", description = "Contains APIs for adding subcategory, updating subcategory, and listing subcategories")
@SuppressWarnings("deprecation")
public class SubCategoryController {

    /**
     * Status Code: 200 - HttpStatus.OK
     * Description: The request was successful, and the response contains the requested data.

     * Status Code: 201 - HttpStatus.CREATED
     * Description: The request was successful, and a new resource has been created as a result.

     * Status Code: 404 - HttpStatus.NOT_FOUND
     * Description: The requested resource could not be found but may be available in the future.

     */

    // Category service for category-related operations
    private final CategoryServiceInterface categoryService;

    // Subcategory service for subcategory-related operations
    private final SubCategoryServiceInterface subCategoryService;



    // API to create a new subcategory
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> createSubCategory(@Valid @RequestBody SubCategoryDto subCategoryDto) {
        // Check if the parent category exists
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Parent Category is invalid"), HttpStatus.NOT_FOUND);
        }
        Category category = optionalCategory.get();

        // Check if subcategory with the same name exists in the same category
        if (Helper.notNull(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), category))) {
            return new ResponseEntity<>(new ApiResponse(false, "Sub Category already exists"), HttpStatus.CONFLICT);
        }

        // Create the subcategory
        subCategoryService.createSubCategory(subCategoryDto, category);
        return new ResponseEntity<>(new ApiResponse(true, "Category is created"), HttpStatus.CREATED);
    }

    // API to list all subcategories
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<SubCategory>> listSubCategories() {
        // Get the list of all subcategories
        List<SubCategory> body = subCategoryService.listSubCategory();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API to list subcategories by category id
    @GetMapping("/listbyid/{categoryId}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<SubCategory>> listSubCategoriesByCategoriesId(@PathVariable("categoryId") Long categoryId) {
        // Check if the category id is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (optionalCategory.isPresent()) {
            // Return all subcategories for the specified category
            List<SubCategory> body = subCategoryService.listSubCategory(categoryId);
            return new ResponseEntity<>(body, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
    }


    // API to update a subcategory
    @PutMapping("/update/{subcategoryId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateSubCategory(@PathVariable("subcategoryId") Long subcategoryId, @Valid @RequestBody SubCategoryDto subCategoryDto) {
        // Check if the category is valid
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Category is invalid"), HttpStatus.NOT_FOUND);
        }
        // Check if subcategory is valid
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (optionalSubCategory.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Subcategory is invalid"), HttpStatus.NOT_FOUND);
        }

        Category category = optionalCategory.get();

        // Update the subcategory
        subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category);

        return new ResponseEntity<>(new ApiResponse(true, "Subcategory updated successfully"), HttpStatus.OK);
    }
}
