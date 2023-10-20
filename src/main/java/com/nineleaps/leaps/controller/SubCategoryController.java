package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subcategory")
@AllArgsConstructor
@Validated
@Api(tags = "Subcategory Api")
public class SubCategoryController {

    //Linking layers using constructor injection
    private final CategoryServiceInterface categoryService;
    private final SubCategoryServiceInterface subCategoryService;
    private final Helper helper;

    // API : To add category by admin
    @ApiOperation("API : To add category by admin")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> createSubCategory(
            @Valid @RequestBody SubCategoryDto subCategoryDto,
            HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard Statement : Check user role is admin or not
        if(user.getRole() != Role.ADMIN) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Category can only be accessed by admin"),
                    HttpStatus.FORBIDDEN);
        }

        // Guard Statement : Check if category already present in DB
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Parent Category is invalid"),
                    HttpStatus.NOT_FOUND);
        }
        Category category = optionalCategory.get();

        // `Guard Statement` : Check if subcategory already exists by name in the same category
        if (Helper.notNull(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), category))) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Sub Category already exists"),
                    HttpStatus.CONFLICT);
        }

        // Calling service layer to save category
        subCategoryService.createSubCategory(subCategoryDto, category);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Category is created"),
                HttpStatus.CREATED);
    }

    // API : To List all subcategories
    @ApiOperation("API : To List all subcategories")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<SubCategory>> listSubCategories() {

        // Calling service layer to get list of categories
        List<SubCategory> body = subCategoryService.listSubCategory();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To list subcategories by category
    @ApiOperation("API : To list subcategories by category")
    @GetMapping(value = "/listByCategory/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<SubCategory>> listSubCategoriesByCategoriesId(
            @PathVariable("categoryId") Long categoryId) {

        // Guard Statement : Check if the category id is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (optionalCategory.isPresent()) {
            //the return all subcategory accordingly
            List<SubCategory> body = subCategoryService.listSubCategory(categoryId);
            return new ResponseEntity<>(body, HttpStatus.OK);
        }

        // If category is invalid
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
    }

    // API : To update subcategory
    @ApiOperation("API : To update subcategory")
    @PutMapping(value = "/update/{subcategoryId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> updateSubCategory(
            @PathVariable("subcategoryId") Long subcategoryId,
            @Valid @RequestBody SubCategoryDto subCategoryDto,
            HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard Statement : Check user role is admin or not
        if(user.getRole() != Role.ADMIN) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Category can only be accessed by admin"),
                    HttpStatus.FORBIDDEN);
        }

        // Guard Statement : Check if category is valid or not
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Category is invalid"),
                    HttpStatus.NOT_FOUND);
        }

        // Guard Statement : Check if subcategory is valid or not
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (optionalSubCategory.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Subcategory is invalid"),
                    HttpStatus.NOT_FOUND);
        }

        // Calling service layer to add subcategory
        Category category = optionalCategory.get();
        subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Subcategory updated successfully"),
                HttpStatus.OK);
    }
}
