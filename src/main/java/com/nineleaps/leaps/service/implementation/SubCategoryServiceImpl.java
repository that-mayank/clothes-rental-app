package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.exceptions.CategoryNotExistException;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nineleaps.leaps.config.MessageStrings.CATEGORY_INVALID;

@Service
@AllArgsConstructor
@Transactional
@Slf4j // Add the SLF4J annotation to enable logging
public class SubCategoryServiceImpl implements SubCategoryServiceInterface {

    private final SubCategoryRepository categoryRepository;
    private final CategoryServiceInterface categoryService;

    @Override
    public void createSubCategory(SubCategoryDto subCategoryDto) {
        log.info("Creating a new subcategory: {}", subCategoryDto.getSubcategoryName());

        // Guard Statement: Check if the category already present in DB
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
        Category category = optionalCategory.get();

        // `Guard Statement`: Check if subcategory already exists by name in the same category
        if (Optional.ofNullable(readSubCategory(subCategoryDto.getSubcategoryName(), category)).isEmpty()) {
            log.error("Subcategory with the same name already exists in the category: {}", subCategoryDto.getSubcategoryName());
            throw new CategoryNotExistException(CATEGORY_INVALID);
        }
        SubCategory subCategory = getSubCategoryFromDto(subCategoryDto, category);
        categoryRepository.save(subCategory);

        log.info("Subcategory created successfully: {}", subCategoryDto.getSubcategoryName());
    }

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

    private SubCategory getSubCategoryFromDto(SubCategoryDto subCategoryDto, Category category) {
        return new SubCategory(subCategoryDto, category);
    }

    @Override
    public Optional<SubCategory> readSubCategory(Long subcategoryId) {
        log.info("Fetching subcategory by ID: {}", subcategoryId);
        return categoryRepository.findById(subcategoryId);
    }

    @Override
    public List<SubCategory> listSubCategory() {
        log.info("Fetching the list of all subcategories.");
        return categoryRepository.findAll();
    }

    @Override
    public List<SubCategory> listSubCategory(Long categoryId) {
        log.info("Fetching subcategories for category with ID: {}", categoryId);
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (optionalCategory.isEmpty()) {
            log.error("Category does not exist with ID: {}", categoryId);
            throw new CategoryNotExistException(CATEGORY_INVALID);
        }
        List<SubCategory> subCategories = categoryRepository.findByCategoryId(categoryId);
        log.info("Fetched {} subcategories for category with ID: {}", subCategories.size(), categoryId);
        return subCategories;
    }

    @Override
    public void updateSubCategory(Long subcategoryId, SubCategoryDto subCategoryDto) {
        log.info("Updating subcategory with ID: {}", subcategoryId);

        // Guard Statement: Check if category is valid or not
        Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());


        // Guard Statement: Check if subcategory is valid or not
        Optional<SubCategory> optionalSubCategory = readSubCategory(subcategoryId);
       
        // Create a new SubCategory instance using the provided SubCategoryDto and Category.
        SubCategory updatedSubCategory = getSubCategoryFromDto(subCategoryDto, optionalCategory.get());

        // Set the ID of the updated SubCategory to the provided subcategoryId.
        updatedSubCategory.setId(subcategoryId);

        // Save the updated SubCategory to the repository.
        categoryRepository.save(updatedSubCategory);

        log.info("Subcategory updated successfully with ID: {}", subcategoryId);
    }

    @Override
    public List<SubCategory> getSubCategoriesFromIds(List<Long> subcategoryIds) throws CategoryNotExistException {
        List<SubCategory> subCategories = new ArrayList<>();

        // Iterate through the list of subcategory IDs.
        for (Long subcategoryId : subcategoryIds) {
            log.info("Fetching subcategory by ID: {}", subcategoryId);
            // Attempt to read a SubCategory by its ID.
            Optional<SubCategory> optionalSubCategory = readSubCategory(subcategoryId);

            // Check if a SubCategory with the given ID exists.
            if (optionalSubCategory.isEmpty()) {
                log.error("Subcategory is invalid: {}", subcategoryId);
                throw new CategoryNotExistException("Subcategory is invalid: " + subcategoryId);
            }

            // Add the found SubCategory to the list.
            subCategories.add(optionalSubCategory.get());
            log.info("Fetched subcategory by ID: {}", subcategoryId);
        }

        // Return the list of SubCategories.
        return subCategories;
    }
}
