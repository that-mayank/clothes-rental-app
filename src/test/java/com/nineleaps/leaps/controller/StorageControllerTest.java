package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.UrlResponse;
import com.nineleaps.leaps.service.StorageServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class StorageControllerTest {
    @Mock
    private StorageServiceInterface storageServiceInterface;
    @InjectMocks
    private StorageController storageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFileToSpecificBucket_Success() throws IOException {
        // Mock input parameters
        MultipartFile[] files = new MultipartFile[1]; // Mock a file
        Long categoryId = 1L;
        Long subcategoryId = 2L;

        // Mock successful upload
        String mockUrl = "https://mock-url.com";
        when(storageServiceInterface.uploadFileToBucket(any(), any(), any())).thenReturn(mockUrl);

        // Call the API
        ResponseEntity<UrlResponse> responseEntity = storageController.uploadFileToSpecificBucket(files, categoryId, subcategoryId);

        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).getUrls().size());
        assertEquals(mockUrl, responseEntity.getBody().getUrls().get(0));
    }

    @Test
    void testUploadFileToSpecificBucket_Exception() throws IOException {
        // Mock input parameters
        MultipartFile[] files = new MultipartFile[1]; // Mock a file
        Long categoryId = 1L;
        Long subcategoryId = 2L;

        // Mock an exception during upload
        doThrow(new RuntimeException("Something went wrong")).when(storageServiceInterface).uploadFileToBucket(any(), any(), any());

        // Call the API
        ResponseEntity<UrlResponse> responseEntity = storageController.uploadFileToSpecificBucket(files, categoryId, subcategoryId);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}