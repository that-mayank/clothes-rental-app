package com.nineleaps.leaps.controller;


import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.enums.Role;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/category")
@AllArgsConstructor
@Validated
@Api(tags = "Category Api")

public class CategoryController {

    //Linking layers using constructor injection

    private final CategoryServiceInterface categoryService;
    private final Helper helper;

    // API : To add category by admin

    @ApiOperation(value = "API : To add category")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")

    // Validate the categoryDto object

    public ResponseEntity<ApiResponse> createCategory(@RequestBody @Valid CategoryDto categoryDto, HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Guard Statement : Check user role is admin or not

        if(user.getRole() != Role.ADMIN) {
            return new ResponseEntity<>(new ApiResponse(false, "Category can only be accessed by admin"), HttpStatus.FORBIDDEN);
        }

        // Guard Statement : Check if category already present in DB

        if (Helper.notNull(categoryService.readCategory(categoryDto.getCategoryName()))) {
            return new ResponseEntity<>(new ApiResponse(false, "Category already exists"), HttpStatus.CONFLICT);
        }

        // Calling service layer to save category

        Category category = new Category(categoryDto);
        categoryService.createCategory(category);
        return new ResponseEntity<>(new ApiResponse(true, "Created a new Category"), HttpStatus.CREATED);
    }

    // API : To get list of categories

    @ApiOperation(value = "API : To get list of categories")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity<List<Category>> listCategory() {

        // Calling service layer to get list of categories

        List<Category> body = categoryService.listCategory();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : To update category

    @ApiOperation(value = "API : To update category")
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")

    // Validate the categoryDto object

    public ResponseEntity<ApiResponse> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryDto updateCategory, HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Guard Statement : Check user role is admin or not

        if(user.getRole() != Role.ADMIN) {
            return new ResponseEntity<>(new ApiResponse(false, "Category can only be accessed by admin"), HttpStatus.FORBIDDEN);
        }

        // Guard Statement : Check to see if category exists

        if ((categoryService.readCategory(id)).isPresent()) {
            categoryService.updateCategory(id, updateCategory);
            return new ResponseEntity<>(new ApiResponse(true, "category has been updated"), HttpStatus.OK);
        }

        //Return if category does not exist

        return new ResponseEntity<>(new ApiResponse(false, "category does not exist"), HttpStatus.NOT_FOUND);
    }
}
