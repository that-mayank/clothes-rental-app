package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.dto.UrlResponse;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.StorageServiceInterface;
import com.nineleaps.leaps.utils.Helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName("test case file for Storage Controller")
@ExtendWith(RuntimeBenchmarkExtension.class)
class StorageControllerTest {
    @Mock
    private StorageServiceInterface storageServiceInterface;
    @InjectMocks
    private StorageController storageController;
    @Mock
    private Helper helper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test successful file upload to specific bucket")
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
    @DisplayName("Test file upload to specific bucket with exception")
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

    @Test
    @DisplayName("Test successful profile image upload")
    void testUploadProfileImage_Success() throws IOException {
        // Mock the MultipartFile and HttpServletRequest
        MultipartFile mockFile = mock(MultipartFile.class);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        // Mock user
        User user = new User();
        user.setId(1L);  // Set appropriate user details

        // Mock successful upload
        String mockUrl = "https://example.com/profile.jpg";
        when(helper.getUserFromToken(mockRequest)).thenReturn(user);
        when(storageServiceInterface.uploadProfileImage(mockFile, user)).thenReturn(mockUrl);

        ResponseEntity<String> response = storageController.uploadProfileImage(mockFile, mockRequest);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());



    }

    @Test
    @DisplayName("Test profile image upload with exception")
    void testUploadProfileImage_Exception() throws IOException {
        // Mock the MultipartFile and HttpServletRequest
        MultipartFile mockFile = mock(MultipartFile.class);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        // Mock user
        User user = new User();
        user.setId(1L);  // Set appropriate user details

        // Mock an exception during upload
        when(helper.getUserFromToken(mockRequest)).thenReturn(user);
        doThrow(new RuntimeException("Upload failed")).when(storageServiceInterface).uploadProfileImage(mockFile, user);

        ResponseEntity<String> response = storageController.uploadProfileImage(mockFile, mockRequest);

        // Assert the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Test successful view file operation")
    void testViewFile_Success()  {
        // Mock input parameters
        String fileName = "mock-file.jpg";
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // Call the API
        ResponseEntity<String> responseEntity = storageController.viewFile(fileName, mockResponse);

        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Image fetched from S3", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test view file operation with exception")
    void testViewFile_Exception() throws IOException {
        // Mock input parameters
        String fileName = "mock-file.jpg";
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // Mock an exception during viewing file
        doThrow(new IOException("Unable to view file")).when(storageServiceInterface).viewFile(fileName, mockResponse);

        // Call the API
        ResponseEntity<String> responseEntity = storageController.viewFile(fileName, mockResponse);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error viewing file from S3", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test successful file deletion")
    void testDeleteFile_Success() throws IOException {
        // Mock input parameters
        String fileName = "mock-file.jpg";

        // Call the API
        ResponseEntity<String> responseEntity = storageController.deleteFile(fileName);

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());  // Empty body for 204 No Content

        // Verify that the storageServiceInterface.deleteFile was called with the correct argument
        verify(storageServiceInterface).deleteFile(fileName);
    }

    @Test
    @DisplayName("Test file deletion with exception")
    void testDeleteFile_Exception() throws IOException {
        // Mock input parameters
        String fileName = "mock-file.jpg";

        // Mock an exception during file deletion
        doThrow(new IOException("Unable to delete file")).when(storageServiceInterface).deleteFile(fileName);

        // Call the API
        ResponseEntity<String> responseEntity = storageController.deleteFile(fileName);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error deleting file from S3", responseEntity.getBody());
    }

}