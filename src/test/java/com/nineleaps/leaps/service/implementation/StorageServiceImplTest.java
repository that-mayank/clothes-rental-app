package com.nineleaps.leaps.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {

    private StorageServiceImpl storageService;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storageService = new StorageServiceImpl();
        storageService.s3Client = s3Client;
        storageService.baseUrl = "http://localhost:8080";
        storageService.bucketName = "example-bucket";
    }

    @Test
    void determineContentType_pdf() {
        // Prepare
        String fileName = "example.pdf";

        // Execute
        String contentType = storageService.determineContentType(fileName);

        // Verify
        assertEquals(MediaType.APPLICATION_PDF_VALUE, contentType);
    }

    @Test
    void determineContentType_jpg() {
        // Prepare
        String fileName = "example.jpg";

        // Execute
        String contentType = storageService.determineContentType(fileName);

        // Verify
        assertEquals(MediaType.IMAGE_JPEG_VALUE, contentType);
    }

    @Test
    void determineContentType_png() {
        // Prepare
        String fileName = "example.png";

        // Execute
        String contentType = storageService.determineContentType(fileName);

        // Verify
        assertEquals(MediaType.IMAGE_PNG_VALUE, contentType);
    }

    @Test
    void determineContentType_default() {
        // Prepare
        String fileName = "example.txt";

        // Execute
        String contentType = storageService.determineContentType(fileName);

        // Verify
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, contentType);
    }

    @Test
    void convertMultiPartFileToFile() throws IOException {
        // Prepare
        byte[] fileBytes = "Example file content".getBytes();
        File tempFile = File.createTempFile("example", ".txt");
        when(multipartFile.getOriginalFilename()).thenReturn("example.txt");
        when(multipartFile.getBytes()).thenReturn(fileBytes);

        // Execute
        File convertedFile = storageService.convertMultiPartFileToFile(multipartFile);

        // Verify
        assertNotNull(convertedFile);
        assertArrayEquals(fileBytes, Files.readAllBytes(convertedFile.toPath()));

        // Clean up
        Files.deleteIfExists(convertedFile.toPath());
    }

    @Test
    void uploadFile() throws IOException {
        // Prepare
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        String fileName = "example.txt";
        byte[] fileContent = "File content 1".getBytes();
        File fileObj = new File("example.txt");
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getBytes()).thenReturn(fileContent);
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        // Execute
        String result = storageService.uploadFile(multipartFile);

        // Verify
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
        assertTrue(result.startsWith("http://localhost:8080/api/v1/file/view/"));

        // Clean up
        fileObj.delete();
    }




    @Test
    void downloadFile() throws IOException {
        // Prepare
        String fileName = "example.txt";
        byte[] fileBytes = "Example file content".getBytes();
        S3Object s3Object = new S3Object();
        S3ObjectInputStream inputStream = new S3ObjectInputStream(new ByteArrayInputStream(fileBytes), null);
        s3Object.setObjectContent(inputStream);
        when(s3Client.getObject(anyString(), anyString())).thenReturn(s3Object);

        // Execute
        byte[] result = storageService.downloadFile(fileName);

        // Verify
        assertNotNull(result);
        assertArrayEquals(fileBytes, result);
        verify(s3Client, times(1)).getObject(anyString(), anyString());
        inputStream.close(); // Close the input stream to release resources
    }

    @Test
    void deleteFile() {
        // Prepare
        String fileName = "example.txt";

        // Execute
        String result = storageService.deleteFile(fileName);

        // Verify
        assertEquals("example.txt removed ...", result);
        verify(s3Client, times(1)).deleteObject(anyString(), anyString());
    }

    @Test
    void viewFile() throws IOException {
        // Prepare
        String fileName = "example.txt";
        byte[] fileBytes = "Example file content".getBytes();
        S3Object s3Object = new S3Object();
        S3ObjectInputStream inputStream = new S3ObjectInputStream(new ByteArrayInputStream(fileBytes), null);
        s3Object.setObjectContent(inputStream);
        when(s3Client.getObject(anyString(), anyString())).thenReturn(s3Object);

        // Mock HttpServletResponse
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Execute
        storageService.viewFile(fileName, response);

        // Verify
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getHeader(HttpHeaders.CONTENT_TYPE));
        assertArrayEquals(fileBytes, response.getContentAsByteArray());
        verify(s3Client, times(1)).getObject(anyString(), anyString());
        inputStream.close(); // Close the input stream to release resources
    }
}