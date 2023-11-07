package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subcategory")
@AllArgsConstructor
@Validated
@Api(tags = "Subcategory Api")
public class SubCategoryController {

    //Linking layers using constructor injection

    private final SubCategoryServiceInterface subCategoryService;

    // API : To add category by admin
    @ApiOperation("API : To add category by admin")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> createSubCategory(@Valid @RequestBody SubCategoryDto subCategoryDto) {
        // Calling service layer to save category
        subCategoryService.createSubCategory(subCategoryDto);
        return new ResponseEntity<>(new ApiResponse(true, "Category is created"), HttpStatus.CREATED);
    }

    // API : To List all subcategories
    @ApiOperation("API : To List all subcategories")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubCategory>> listSubCategories() {
        // Calling service layer to get list of categories
        List<SubCategory> body = subCategoryService.listSubCategory();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To list subcategories by category
    @ApiOperation("API : To list subcategories by category")
    @GetMapping(value = "{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubCategory>> listSubCategoriesByCategoriesId(@PathVariable("categoryId") Long categoryId) {
        //the return all subcategory accordingly
        List<SubCategory> body = subCategoryService.listSubCategory(categoryId);
        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    // API : To update subcategory
    @ApiOperation("API : To update subcategory")
    @PutMapping(value = "update/{subcategoryId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> updateSubCategory(@PathVariable("subcategoryId") Long subcategoryId, @Valid @RequestBody SubCategoryDto subCategoryDto) {

        subCategoryService.updateSubCategory(subcategoryId, subCategoryDto);
        return new ResponseEntity<>(new ApiResponse(true, "Subcategory updated successfully"), HttpStatus.OK);
    }
}
