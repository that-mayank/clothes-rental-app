package com.nineleaps.leaps.utils;

import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.CategoryRepository;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static com.nineleaps.leaps.LeapsApplication.bucketName;

@Component
@AllArgsConstructor
@Slf4j
public class StorageUtility {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final Retry retry;
    private final S3TransferManager transferManager;

    public static class S3UploadException extends RuntimeException {
        public S3UploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public String uploadFile(MultipartFile file, String folderPath, String fileName){
        try {
            return Retry.decorateFunction(retry, (MultipartFile f) -> {
                File fileObj;
                try {
                    fileObj = convertMultiPartFileToFile(f);
                } catch (IOException e) {
                    throw new S3UploadException("Error converting multipart file to file", e);
                }

                try {
                    UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                            .putObjectRequest(b -> b.bucket(bucketName).key(folderPath + "/" + fileName))
                            .addTransferListener(LoggingTransferListener.create())
                            .source(fileObj.toPath())
                            .build();

                    transferManager.uploadFile(uploadFileRequest).completionFuture().join();

                    Files.delete(fileObj.toPath());

                    return UriComponentsBuilder.fromHttpUrl(NGROK)
                            .path("/api/v1/file/view")
                            .queryParam("image", folderPath + "/" + fileName)
                            .toUriString();

                } catch (Exception e) {
                    log.error("Error during file upload: {}", e.getMessage(), e);
                    throw new S3UploadException("Error uploading file to S3", e);
                }
            }).apply(file);
        } catch (RuntimeException e) {
            log.error("Retry failed for uploading file: {}", e.getMessage(), e);
            throw new S3UploadException("Error uploading file to S3 after retries", e);
        }
    }


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



}
