package com.nineleaps.leaps.controller;


import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@AllArgsConstructor
@Api(tags = "Category Api", description = "Contains api for adding category, updating category, and list categories")
@SuppressWarnings("deprecation")
public class CategoryController {
    //Linking Service layer using constructor injection
    private final CategoryServiceInterface categoryService;


    @ApiOperation(value = "Add new category")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        if (Helper.notNull(categoryService.readCategory(categoryDto.getCategoryName()))) {
            return new ResponseEntity<>(new ApiResponse(false, "Category already exists"), HttpStatus.CONFLICT);
        }
        Category category = new Category(categoryDto);
        categoryService.createCategory(category);
        return new ResponseEntity<>(new ApiResponse(true, "Created a new Category"), HttpStatus.CREATED);
    }

    @ApiOperation(value = "List categories")
    @GetMapping("/list")
    public ResponseEntity<List<Category>> listCategory() {
        List<Category> body = categoryService.listCategory();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ApiOperation(value = "update category")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryDto updateCategory) {
        //Check to see if category exists
        if ((categoryService.readCategory(id)).isPresent()) {
            categoryService.updateCategory(id, updateCategory);
            return new ResponseEntity<>(new ApiResponse(true, "category has been updated"), HttpStatus.OK);
        }
        //Return this if category does not exist
        return new ResponseEntity<>(new ApiResponse(false, "category does not exist"), HttpStatus.NOT_FOUND);
    }
}
