package com.nineleaps.leaps.controller;


import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private final Helper helper;

    // API - Allows the admin to create a new category
    @ApiOperation(value = "Add new category")
    @PostMapping(value = "/create",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryDto categoryDto, HttpServletRequest request) {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // calling the service layer to check if the category is already present or not
        if (Helper.notNull(categoryService.readCategory(categoryDto.getCategoryName()))) {

            // Status Code: 409-HttpStatus.CONFLICT
            return new ResponseEntity<>(new ApiResponse(false, "Category already exists"), HttpStatus.CONFLICT);
        }
        // Creating a new category object
        Category category = new Category(categoryDto);

        // Calling service layer to create new category
        categoryService.createCategory(category,user);

        // Status Code : 201-HttpStatus.CREATED
        return new ResponseEntity<>(new ApiResponse(true, "Created a new Category"), HttpStatus.CREATED);
    }

    // API - Allows the user to list categories
    @ApiOperation(value = "List categories")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Category>> listCategory() {

        // Calling service layer to list categories
        List<Category> body = categoryService.listCategory();

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API - Allows admin to update category
    @ApiOperation(value = "update category")
    @PutMapping(value = "/update/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryDto updateCategory,HttpServletRequest request ) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        //Check to see if category exists
        if ((categoryService.readCategory(id)).isPresent()) {

            // Calling service layer to update category
            categoryService.updateCategory(id, updateCategory,user);

            // Status Code : 200-HttpStatus.OK
            return new ResponseEntity<>(new ApiResponse(true, "category has been updated"), HttpStatus.OK);
        }

        //Return this if category does not exist
        // Status Code: 404-HttpStatus.NOT_FOUND
        return new ResponseEntity<>(new ApiResponse(false, "category does not exist"), HttpStatus.NOT_FOUND);
    }
}
