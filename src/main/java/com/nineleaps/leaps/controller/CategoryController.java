package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryService;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryService;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
    //Linking Service layer using constructor injection
    private final CategoryServiceInterface categoryService;

    @Autowired
    public CategoryController(CategoryServiceInterface categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        if (Helper.notNull(categoryService.readCategory(categoryDto.getCategoryName()))) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Category already exists"), HttpStatus.CONFLICT);
        }
        Category category = new Category(categoryDto);
        categoryService.createCategory(category);
        return new ResponseEntity<ApiResponse>(new ApiResponse(true, "Created a new Category"), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Category>> listCategory() {
        List<Category> body = categoryService.listCategory();
        return new ResponseEntity<List<Category>>(body, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody Category updateCategory) {
        //Check to see if category exists
        if ((categoryService.readCategory(id)).isPresent()) {
            categoryService.updateCategory(id, updateCategory);
            return new ResponseEntity<ApiResponse>(new ApiResponse(true, "category has been updated"), HttpStatus.OK);
        }
        //Return this if category does not exist
        return new ResponseEntity<ApiResponse>(new ApiResponse(false, "category does not exist"), HttpStatus.NOT_FOUND);
    }
}
