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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subcategory")
@AllArgsConstructor
@Api(tags = "Subcategory Api", description = "Contains api for adding subcategory, updating subcategory, and list subcategories")
@SuppressWarnings("deprecation")
public class SubCategoryController {
    private final CategoryServiceInterface categoryService;
    private final SubCategoryServiceInterface subCategoryService;


    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createSubCategory(@Valid @RequestBody SubCategoryDto subCategoryDto) {
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        if (!optionalCategory.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Parent Category is invalid"), HttpStatus.NOT_FOUND);
        }
        Category category = optionalCategory.get();
        //check if subcategory already exists by name in the same category
        if (Helper.notNull(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), category))) {
            return new ResponseEntity<>(new ApiResponse(false, "Sub Category already exists"), HttpStatus.CONFLICT);
        }
        subCategoryService.createSubCategory(subCategoryDto, category);
        return new ResponseEntity<>(new ApiResponse(true, "Category is created"), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<SubCategory>> listSubCategories() {
        List<SubCategory> body = subCategoryService.listSubCategory();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //list sub category by category id
    //use put mapping
    @GetMapping("/listbyid/{categoryId}")
    public ResponseEntity<List<SubCategory>> listSubCategoriesByCategoriesId(@PathVariable("categoryId") Long categoryId) {
        //check if the category id is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (optionalCategory.isPresent()) {
            //the return all subcategory accordingly
            List<SubCategory> body = subCategoryService.listSubCategory(categoryId);
            return new ResponseEntity<>(body, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
    }

    //update product
    @PutMapping("/update/{subcategoryId}")
    public ResponseEntity<ApiResponse> updateSubCategory(@PathVariable("subcategoryId") Long subcategoryId, @Valid @RequestBody SubCategoryDto subCategoryDto) {
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        if (!optionalCategory.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Category is invalid"), HttpStatus.NOT_FOUND);
        }
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (!optionalSubCategory.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Subcategory is invalid"), HttpStatus.NOT_FOUND);
        }
        Category category = optionalCategory.get();
        subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category);
        return new ResponseEntity<>(new ApiResponse(true, "Subcategory updated successfully"), HttpStatus.OK);
    }
}
