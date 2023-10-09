package com.nineleaps.leaps.utils;


import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.CategoryRepository;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import java.io.IOException;
import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName(" StorageUtility Tests")
class StorageUtilityTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private Retry retry;

    @Mock
    private S3TransferManager transferManager;

    private StorageUtility storageUtility;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storageUtility = new StorageUtility(categoryRepository, subCategoryRepository, retry, transferManager);
    }

    @DisplayName("Test determineContentType for .pdf extension")
    @Test
    void determineContentType() {
        // Test for .pdf extension
        assertEquals(MediaType.APPLICATION_PDF_VALUE, storageUtility.determineContentType("test.pdf"));

        // Test for .jpg extension
        assertEquals(MediaType.IMAGE_JPEG_VALUE, storageUtility.determineContentType("test.jpg"));

        // Test for .png extension
        assertEquals(MediaType.IMAGE_PNG_VALUE, storageUtility.determineContentType("test.png"));

        // Test for unknown extension
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, storageUtility.determineContentType("test.txt"));
    }

    @DisplayName("Test convertMultiPartFileToFile")
    @Test
    void convertMultiPartFileToFile() throws IOException {
        // Mock MultipartFile
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.jpg");

        // Test the conversion
        assertNotNull(storageUtility.convertMultiPartFileToFile(mockMultipartFile));
    }

    @DisplayName("Test getSubCategoryNameById")
    @Test
    void getSubCategoryNameById() {
        // Mock SubCategory
        SubCategory subCategory = new SubCategory();
        subCategory.setSubcategoryName("Test Subcategory");

        // Mock SubCategoryRepository
        when(subCategoryRepository.findById(1L)).thenReturn(java.util.Optional.of(subCategory));

        // Test the retrieval of subcategory name
        assertEquals("Test Subcategory", storageUtility.getSubCategoryNameById(1L));
    }

    @DisplayName("Test getCategoryNameById")
    @Test
    void getCategoryNameById() {
        // Mock Category
        Category category = new Category();
        category.setCategoryName("Test Category");

        // Mock CategoryRepository
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));

        // Test the retrieval of category name
        assertEquals("Test Category", storageUtility.getCategoryNameById(1L));
    }

    @DisplayName("Test constructor and getters for S3UploadException")
    @Test
    void constructorAndGetters() {
        // Given
        String message = "Test S3UploadException message";
        Throwable cause = new RuntimeException("Test cause");

        // When
        StorageUtility.S3UploadException exception = new StorageUtility.S3UploadException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }



    @DisplayName("Test uploadFile with upload failure, expects S3UploadException")
    @Test
    void uploadFile_UploadFailure_ThrowsS3UploadException() throws IOException {
        // Mocking necessary dependencies
        S3TransferManager transferManager = mock(S3TransferManager.class);
        StorageUtility utility = new StorageUtility(categoryRepository, subCategoryRepository, retry, transferManager);

        // Given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("testFile.jpg");
        when(file.getBytes()).thenReturn("Test data".getBytes());

        String folderPath = "test-folder";
        String fileName = "testFile.jpg";

        // Mock the uploadFile method to simulate failure
        when(transferManager.uploadFile(any(UploadFileRequest.class)))
                .thenThrow(new RuntimeException("Simulated S3 upload failure"));

        // When
        try {
            utility.uploadFile(file, folderPath, fileName);
        } catch (StorageUtility.S3UploadException e) {
            // Then
            assertEquals("Error uploading file to S3 after retries", e.getMessage());
        }
    }


    @DisplayName("Test buildUriString for successful URI creation")
    @Test
    void uploadFile_BuildUriString_Success() {
        // Given
        String folderPath = "test-folder";
        String fileName = "testFile.jpg";

        StorageUtility storageUtility = new StorageUtility(categoryRepository, subCategoryRepository, retry, transferManager);

        // When
        String result = storageUtility.buildUriString(folderPath, fileName);

        // Then
        assertEquals(NGROK + "/api/v1/file/view?image=test-folder/testFile.jpg", result);
    }





}
