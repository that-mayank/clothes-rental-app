package com.nineleaps.leaps.service.implementation;


import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.utils.StorageUtility;
import org.apache.catalina.connector.ClientAbortException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import static com.nineleaps.leaps.LeapsApplication.BUCKET_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class StorageServiceImplTest {

    @Mock
    private StorageUtility storageUtility;

    @InjectMocks
    private StorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadProfileImage() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        // Create a sample image file
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "some image".getBytes());

        // Adjust the file name part of the argument matcher using anyString() with a regex pattern
        when(storageUtility.uploadFile(any(), anyString(), matches("\\d+_user@example\\.com_\\d+_test\\.jpg")))
                .thenReturn("https://example.com/image.jpg");

        // Call the method to be tested
        String result = storageService.uploadProfileImage(file, user);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.startsWith("https://example.com/"));

        // Verify that storageUtility.uploadFile was called with correct parameters
        verify(storageUtility, times(1))
                .uploadFile(eq(file), eq("ProfileImages"), matches("\\d+_user@example\\.com_\\d+_test\\.jpg"));

    }

    @Test
    void testUploadFileToBucket() {
        // Mock necessary data
        Long categoryId = 1L;
        Long subcategoryId = 2L;
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Test data".getBytes());
        String expectedFileNameRegex = "\\d+_test\\.jpg";

        when(storageUtility.getCategoryNameById(categoryId)).thenReturn("TestCategory");
        when(storageUtility.getSubCategoryNameById(subcategoryId)).thenReturn("TestSubCategory");
        when(storageUtility.uploadFile(any(), anyString(), matches(expectedFileNameRegex)))
                .thenReturn("https://example.com/test.jpg");

        // Call the method to be tested
        String result = storageService.uploadFileToBucket(file, categoryId, subcategoryId);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.startsWith("https://example.com/"));

        // Verify that storageUtility methods were called with correct parameters
        verify(storageUtility).getCategoryNameById(categoryId);
        verify(storageUtility).getSubCategoryNameById(subcategoryId);
        verify(storageUtility).uploadFile(eq(file), eq("TestCategory/TestSubCategory"), matches(expectedFileNameRegex));
    }

    @Test
    void testDeleteFile() throws IOException {
        String fileName = "test-file.jpg";

        S3Client s3Client = mock(S3Client.class);
        StorageServiceImpl storageService = new StorageServiceImpl(s3Client, storageUtility);

        // Call the method to be tested
        storageService.deleteFile(fileName);

        // Verify that S3Client's deleteObject was called with the correct arguments
        ArgumentCaptor<DeleteObjectRequest> deleteRequestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(deleteRequestCaptor.capture());

        DeleteObjectRequest deleteObjectRequest = deleteRequestCaptor.getValue();
        assertEquals(BUCKET_NAME, deleteObjectRequest.bucket());
        assertEquals(fileName, deleteObjectRequest.key());
    }
    @Test
    void testDeleteFileS3Exception() {
        String fileName = "test-file.jpg";

        S3Client s3Client = mock(S3Client.class);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(S3Exception.builder().message("S3 error").build());

        storageService = new StorageServiceImpl(s3Client,storageUtility);

        // Call the method to be tested and expect an IOException
        IOException exception = assertThrows(IOException.class, () -> storageService.deleteFile(fileName));
        assertEquals("Error deleting file from S3", exception.getMessage());
    }

    @Test
    void testViewFile() throws IOException {
        // Create a mock S3Client and StorageUtility
        S3Client s3Client = mock(S3Client.class);

        // Create a StorageServiceImpl instance
        StorageServiceImpl storageService = new StorageServiceImpl(s3Client, storageUtility);

        // Mock response
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock S3Client getObjectAsBytes to return a ResponseBytes with sample bytes
        byte[] sampleBytes = "This is a sample response".getBytes(StandardCharsets.UTF_8);
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), ByteBuffer.wrap(sampleBytes).array());
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        // Call the method to be tested
        storageService.viewFile("example-file.jpg", response);

        // Verify the response header and content
        assertEquals("inline; filename=\"example-file.jpg\"", response.getHeader("Content-Disposition"));
        assertArrayEquals(sampleBytes, response.getContentAsByteArray());
        assertEquals(sampleBytes.length, response.getContentLength());
    }

    @Test
    void testHandleClientAbortException() {

        ClientAbortException clientAbortException = new ClientAbortException("Client disconnected");

        try {
            storageService.handleClientAbortException();
        } catch (ClientAbortException e) {
            // Ensure that the correct exception was thrown
            assertEquals("client left the pool", e.getMessage());
        }


    }

}

