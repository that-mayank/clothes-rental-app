package com.nineleaps.leaps.controller;


import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryServiceInterface;
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
@RequestMapping("/api/v1/category")
@AllArgsConstructor
@Validated
@Api(tags = "Category Api")
public class CategoryController {

    //Linking layers using constructor injection
    private final CategoryServiceInterface categoryService;

    // API : To add category by admin
    @ApiOperation(value = "API : To add category")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    // Validate the categoryDto object
    public ResponseEntity<ApiResponse> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(new ApiResponse(true, "Created a new Category"), HttpStatus.CREATED);
    }

    // API : To get list of categories
    @ApiOperation(value = "API : To get list of categories")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Category>> listCategory() {
        // Calling service layer to get list of categories
        List<Category> body = categoryService.listCategory();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To update category
    @ApiOperation(value = "API : To update category")
    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    // Validate the categoryDto object
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryDto updateCategory) {
        // Guard Statement : Check to see if category exists
        categoryService.updateCategory(id, updateCategory);
        return new ResponseEntity<>(new ApiResponse(true, "category has been updated"), HttpStatus.OK);
    }
}