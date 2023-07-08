package com.nineleaps.leaps.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    private StorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        storageService = new StorageServiceImpl();
        storageService.s3Client = s3Client;
    }

//    @Test
//    void uploadFile_ShouldReturnFileURL() throws IOException {
//        // Read the file content and convert it to byte array
//        Path filePath = Paths.get("/home/nineleaps/Downloads/Thumb_Impression.jpg");
//        byte[] fileBytes = Files.readAllBytes(filePath);
//
//        // Mock data
//        MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", MediaType.IMAGE_JPEG_VALUE, fileBytes);
//
//        File convertedFile = mock(File.class);
//
//        // Adjust the mock setup for convertMultiPartFileToFile
//        when(storageService.convertMultiPartFileToFile(file)).thenReturn(convertedFile);
//
//        String bucketName = "test-bucket";
//        storageService.bucketName = bucketName;
//        String baseUrl = "http://example.com";
//        storageService.baseUrl = baseUrl;
//        String expectedFileName = "123456789_test.jpg";
//        String expectedFileURL = "http://example.com/api/v1/file/view/123456789_test.jpg";
//
//        // Mock S3 client
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, expectedFileName, convertedFile);
//        when(s3Client.putObject(putObjectRequest)).thenReturn(null);
//
//        // Invoke the method
//        String fileURL = storageService.uploadFile(file);
//
//        // Assertions
//        assertEquals(expectedFileURL, fileURL);
//        verify(s3Client, times(1)).putObject(putObjectRequest);
//        verify(convertedFile, times(1)).delete();
//    }

//    @Test
//    void downloadFile_ShouldThrowException_WhenIOExceptionOccurs() throws IOException {
//        // Mock data
//        String fileName = "test.jpg";
//        S3Object s3Object = mock(S3Object.class);
//        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
//        when(s3Client.getObject(storageService.bucketName, fileName)).thenReturn(s3Object);
//        when(s3Object.getObjectContent()).thenReturn(inputStream);
//        IOException exception = new IOException("Failed to read file content");
//
//        // Use PowerMockito to mock the static method
//        PowerMockito.mockStatic(IOUtils.class);
//        PowerMockito.when(IOUtils.toByteArray(inputStream)).thenThrow(exception);
//
//        // Assertions
//        assertThrows(IOException.class, () -> storageService.downloadFile(fileName));
//        verify(s3Object, times(1)).getObjectContent();
//        verify(inputStream, times(1)).close();
//    }
//
//

//    @Test
//    void uploadFile_ShouldThrowException_WhenFileConversionFails() {
//        // Mock data
//        MultipartFile file = mock(MultipartFile.class);
//        when(file.getOriginalFilename()).thenReturn("test.jpg");
//        IOException exception = new IOException("File conversion failed");
//        when(storageService.convertMultiPartFileToFile(file)).thenThrow(exception);
//
//        // Assertions
//        assertThrows(RuntimeException.class, () -> storageService.uploadFile(file));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class));
//    }

//    @Test
//    void downloadFile_ShouldReturnFileContent() throws IOException {
//        // Mock data
//        String fileName = "test.jpg";
//        S3Object s3Object = mock(S3Object.class);
//        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
//        when(s3Client.getObject(storageService.bucketName, fileName)).thenReturn(s3Object);
//        when(s3Object.getObjectContent()).thenReturn(inputStream);
//        byte[] fileContent = "File content".getBytes();
//        when(IOUtils.toByteArray(inputStream)).thenReturn(fileContent);
//
//        // Invoke the method
//        byte[] downloadedContent = storageService.downloadFile(fileName);
//
//        // Assertions
//        assertArrayEquals(fileContent, downloadedContent);
//        verify(s3Client, times(1)).getObject(storageService.bucketName, fileName);
//        verify(s3Object, times(1)).getObjectContent();
//        verify(inputStream, times(1)).close();
//    }



    @Test
    void deleteFile_ShouldDeleteFileFromS3() {
        // Mock data
        String fileName = "test.jpg";

        // Invoke the method
        String result = storageService.deleteFile(fileName);

        // Assertions
        assertEquals(fileName + " removed ...", result);
        verify(s3Client, times(1)).deleteObject(storageService.bucketName, fileName);
    }

//    @Test
//    void viewFile_ShouldSetResponseHeadersAndWriteContent() throws IOException {
//        // Mock data
//        String fileName = "test.jpg";
//        S3Object s3Object = mock(S3Object.class);
//        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
//        when(s3Client.getObject(storageService.bucketName, fileName)).thenReturn(s3Object);
//        when(s3Object.getObjectContent()).thenReturn(inputStream);
//        String contentType = "image/jpeg";
//        when(storageService.determineContentType(fileName)).thenReturn(contentType);
//        MockHttpServletResponse response = mock(MockHttpServletResponse.class);
//        OutputStream outputStream = mock(OutputStream.class);
//        doThrow(new IOException("Failed to get output stream")).when(response).getOutputStream();
//        when(response.getOutputStream()).thenReturn((ServletOutputStream) outputStream);
//
//        // Invoke the method
//        storageService.viewFile(fileName, response);
//
//        // Assertions
//        assertEquals(contentType, response.getHeader(HttpHeaders.CONTENT_TYPE));
//        verify(inputStream, times(1)).read(any(byte[].class));
//        verify(inputStream, times(1)).close();
//    }

    @Test
    void determineContentType_ShouldReturnCorrectContentType_ForPdfFile() {
        // Mock data
        String fileName = "test.pdf";

        // Invoke the method
        String contentType = storageService.determineContentType(fileName);

        // Assertions
        assertEquals(MediaType.APPLICATION_PDF_VALUE, contentType);
    }

    @Test
    void determineContentType_ShouldReturnCorrectContentType_ForJpgFile() {
        // Mock data
        String fileName = "test.jpg";

        // Invoke the method
        String contentType = storageService.determineContentType(fileName);

        // Assertions
        assertEquals(MediaType.IMAGE_JPEG_VALUE, contentType);
    }

    @Test
    void determineContentType_ShouldReturnCorrectContentType_ForPngFile() {
        // Mock data
        String fileName = "test.png";

        // Invoke the method
        String contentType = storageService.determineContentType(fileName);

        // Assertions
        assertEquals(MediaType.IMAGE_PNG_VALUE, contentType);
    }

    @Test
    void determineContentType_ShouldReturnDefaultContentType_ForUnknownFile() {
        // Mock data
        String fileName = "test.xyz";

        // Invoke the method
        String contentType = storageService.determineContentType(fileName);

        // Assertions
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, contentType);
    }

    @Test
    void convertMultiPartFileToFile_ShouldConvertMultipartFileToFile() throws IOException {
        // Mock data
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        byte[] fileContent = "File content".getBytes();
        when(multipartFile.getBytes()).thenReturn(fileContent);

        // Invoke the method
        File convertedFile = storageService.convertMultiPartFileToFile(multipartFile);

        // Assertions
        assertTrue(convertedFile.exists());
        assertArrayEquals(fileContent, Files.readAllBytes(convertedFile.toPath()));

        // Clean up
        Files.deleteIfExists(convertedFile.toPath());
    }

}
