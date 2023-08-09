package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.UrlResponse;
import com.nineleaps.leaps.service.StorageServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageControllerTest {

    @Mock
    private StorageServiceInterface storageServiceInterface;

    private StorageController storageController;
    private Helper helper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storageController = new StorageController(storageServiceInterface,helper);
    }

    @Test
    void uploadFile() {
        // Prepare
        MockMultipartFile file1 = new MockMultipartFile("file", "filename1.txt", "text/plain", "File content 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "filename2.txt", "text/plain", "File content 2".getBytes());
        MultipartFile[] files = {file1, file2};

        when(storageServiceInterface.uploadFile(any(MultipartFile.class))).thenReturn("example-url");

        // Execute
        UrlResponse response = storageController.uploadFile(files);

        // Verify
        verify(storageServiceInterface, times(files.length)).uploadFile(any(MultipartFile.class));
        assertEquals(files.length, response.getUrls().size());
    }

    @Test
    void uploadFile_withException() {
        // Prepare
        MockMultipartFile file1 = new MockMultipartFile("file", "filename1.txt", "text/plain", "File content 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "filename2.txt", "text/plain", "File content 2".getBytes());
        MultipartFile[] files = {file1, file2};
        when(storageServiceInterface.uploadFile(any(MultipartFile.class))).thenThrow(RuntimeException.class);

        // Execute
        UrlResponse response = storageController.uploadFile(files);

        // Verify
        verify(storageServiceInterface, times(1)).uploadFile(any(MultipartFile.class));
        assertFalse(Helper.notNull(response.getUrls()));
    }

        @Test
    void uploadProfileFile() {
        // Prepare
            MultipartFile file = new MockMultipartFile("file", "filename1.txt", "text/plain", "File content 1".getBytes());
        when(storageServiceInterface.uploadFile(any(MultipartFile.class))).thenReturn("example-url");

        // Execute
        ResponseEntity<String> response = storageController.uploadProfileFile(file);

        // Verify
        verify(storageServiceInterface, times(1)).uploadFile(any(MultipartFile.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void uploadProfileFile_withException() {
        // Prepare
        MultipartFile file = new MockMultipartFile("file", "filename1.txt", "text/plain", "File content 1".getBytes());        when(storageServiceInterface.uploadFile(any(MultipartFile.class))).thenThrow(RuntimeException.class);

        // Execute
        ResponseEntity<String> response = storageController.uploadProfileFile(file);

        // Verify
        verify(storageServiceInterface, times(1)).uploadFile(any(MultipartFile.class));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void downloadFile() {
        // Prepare
        String fileName = "example-file";
        byte[] data = "This is an example file content.".getBytes();
        when(storageServiceInterface.downloadFile(fileName)).thenReturn(data);

        // Execute
        ResponseEntity<ByteArrayResource> response = storageController.downloadFile(fileName);

        // Verify
        verify(storageServiceInterface, times(1)).downloadFile(fileName);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(data.length, response.getBody().contentLength());
    }

    //
    @Test
    void viewFile() {
        // Prepare
        String fileName = "example-file";
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        // Execute
        storageController.viewFile(fileName,request,response);

        // Verify
        verify(storageServiceInterface, times(1)).viewFile(fileName, response);
    }

    @Test
    void deleteFile() {
        // Prepare
        String fileName = "example-file";
        when(storageServiceInterface.deleteFile(fileName)).thenReturn("deleted");

        // Execute
        ResponseEntity<String> response = storageController.deleteFile(fileName);

        // Verify
        verify(storageServiceInterface, times(1)).deleteFile(fileName);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
