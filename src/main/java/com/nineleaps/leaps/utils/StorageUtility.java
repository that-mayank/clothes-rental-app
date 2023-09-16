package com.nineleaps.leaps.utils;

import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.CategoryRepository;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
@Component
@AllArgsConstructor
public class StorageUtility {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;


    public String determineContentType(String fileName) {
        String contentType;
        if (fileName.endsWith(".pdf")) {
            contentType = MediaType.APPLICATION_PDF_VALUE;
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        } else if (fileName.endsWith(".png")) {
            contentType = MediaType.IMAGE_PNG_VALUE;
        } else {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return contentType;
    }

    public File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = File.createTempFile("temp_", "_" + file.getOriginalFilename());
        file.transferTo(convertedFile);
        return convertedFile;
    }
    public String getSubCategoryNameById(Long subCategoryId) {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId).orElse(null);
        return (subCategory != null) ? subCategory.getSubcategoryName() : null;
    }

    public String getCategoryNameById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        return (category != null) ? category.getCategoryName() : null;
    }

    // Method to get subcategory name based on ID

}
