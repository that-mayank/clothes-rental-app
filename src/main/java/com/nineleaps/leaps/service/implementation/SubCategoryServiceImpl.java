package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nineleaps.leaps.config.MessageStrings.CATEGORY_INVALID;

@Service // Marks this class as a Spring service component.
@AllArgsConstructor // Lombok's annotation to generate a constructor with all required fields.
@Transactional // Marks this class as transactional for database operations.
public class SubCategoryServiceImpl implements SubCategoryServiceInterface {

    private final SubCategoryRepository categoryRepository;
    private final CategoryServiceInterface categoryService;

    // Create a new subcategory.
    @Override
    public void createSubCategory(SubCategoryDto subCategoryDto) {
        // Guard Statement : Check if category already present in DB
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        if (optionalCategory.isEmpty()) {
            throw new CategoryNotExistException(CATEGORY_INVALID);
        }
        Category category = optionalCategory.get();

        // `Guard Statement` : Check if subcategory already exists by name in the same category
        if (Optional.ofNullable(readSubCategory(subCategoryDto.getSubcategoryName(), category)).isPresent()) {
            throw new CategoryNotExistException(CATEGORY_INVALID);
        }
        SubCategory subCategory = getSubCategoryFromDto(subCategoryDto, category);
        categoryRepository.save(subCategory);
    }

    // Read a subcategory by its name within a category.
    @Override
    public SubCategory readSubCategory(String subcategoryName, Category category) {
        // Get a list of all subcategories within the provided category.
        List<SubCategory> subCategories = listSubCategory(category.getId());

        // Iterate through the list of subcategories.
        for (SubCategory subCategory : subCategories) {
            // Check if the subcategory's name matches the provided subcategoryName.
            if (subcategoryName.equals(subCategory.getSubcategoryName())) {
                // If a match is found, return the subcategory.
                return subCategory;
            }
        }

        // If no matching subcategory is found, return null.
        return null;
    }


    // Helper method to create a SubCategory object from a SubCategoryDto and Category.
    SubCategory getSubCategoryFromDto(SubCategoryDto subCategoryDto, Category category) {
        return new SubCategory(subCategoryDto, category);
    }

    // Read a subcategory by its ID.
    @Override
    public Optional<SubCategory> readSubCategory(Long subcategoryId) {
        return categoryRepository.findById(subcategoryId);
    }

    // List all subcategories.
    @Override
    public List<SubCategory> listSubCategory() {
        return categoryRepository.findAll();
    }

    // List subcategories within a specific category.
    @Override
    public List<SubCategory> listSubCategory(Long categoryId) {
        return categoryRepository.findByCategoryId(categoryId);
    }

    // Update an existing subcategory.
    @Override
    public void updateSubCategory(Long subcategoryId, SubCategoryDto subCategoryDto) {
        // Guard Statement : Check if category is valid or not
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        if (optionalCategory.isEmpty()) {
            throw new CategoryNotExistException(CATEGORY_INVALID);
        }

        // Guard Statement : Check if subcategory is valid or not
        Optional<SubCategory> optionalSubCategory = readSubCategory(subcategoryId);
        if (optionalSubCategory.isEmpty()) {
            throw new CategoryNotExistException("Category is invalid");
        }
        // Create a new SubCategory instance using the provided SubCategoryDto and Category.
        SubCategory updatedSubCategory = getSubCategoryFromDto(subCategoryDto, optionalCategory.get());

        // Set the ID of the updated SubCategory to the provided subcategoryId.
        updatedSubCategory.setId(subcategoryId);

        // Save the updated SubCategory to the repository.
        categoryRepository.save(updatedSubCategory);
    }


    // Get a list of SubCategories from their IDs.
    @Override
    public List<SubCategory> getSubCategoriesFromIds(List<Long> subcategoryIds) throws CategoryNotExistException {
        List<SubCategory> subCategories = new ArrayList<>();

        // Iterate through the list of subcategory IDs.
        for (Long subcategoryId : subcategoryIds) {
            // Attempt to read a SubCategory by its ID.
            Optional<SubCategory> optionalSubCategory = readSubCategory(subcategoryId);

            // Check if a SubCategory with the given ID exists.
            if (optionalSubCategory.isEmpty()) {
                // If not, throw a CategoryNotExistException with a descriptive message.
                throw new CategoryNotExistException("Subcategory is invalid: " + subcategoryId);
            }

            // Add the found SubCategory to the list.
            subCategories.add(optionalSubCategory.get());
        }

        // Return the list of SubCategories.
        return subCategories;
    }
}

