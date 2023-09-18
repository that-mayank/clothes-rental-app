package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.StorageServiceInterface;
import com.nineleaps.leaps.utils.StorageUtility;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import static com.nineleaps.leaps.LeapsApplication.bucketName;

@Slf4j
@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageServiceInterface {

    private final S3Client s3Client;
    private final StorageUtility storageUtility;
    private final Retry retry;


    public String uploadProfileImage(MultipartFile file, User user){
        String nameFirst = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String userName = user.getId() + "_" + user.getEmail() + "_";
        String fileName = userName + nameFirst;
        String folderPath = "ProfileImages";
        return storageUtility.uploadFile(file, folderPath, fileName);
    }

    public String uploadFileToBucket(MultipartFile file, Long categoryId, Long subcategoryId){
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String categoryName = storageUtility.getCategoryNameById(categoryId);
        String subCategoryName = storageUtility.getSubCategoryNameById(subcategoryId);
        String folderPath = categoryName + "/" + subCategoryName;
        return storageUtility.uploadFile(file, folderPath, fileName);
    }



    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        try {
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());

            return responseBytes.asByteArray();
        } catch (S3Exception e) {
            log.error("Error downloading file from S3: {}", e.getMessage(), e);
            throw new IOException("Error downloading file from S3", e);
        }
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());
        } catch (S3Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            throw new IOException("Error deleting file from S3", e);
        }
    }

    @Override
    public void viewFile(String fileName, HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());

            String contentType = storageUtility.determineContentType(fileName);
            response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBytes.asByteArray().length));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");

            inputStream = new ByteArrayInputStream(responseBytes.asByteArray());
            outputStream = response.getOutputStream();

            // Copy the input stream to the output stream using Spring's utility
            StreamUtils.copy(inputStream, outputStream);

        } catch (ClientAbortException e) {
            // Client disconnected; log this as info or handle as needed
            log.info("Client disconnected: {}", e.getMessage());
        } catch (S3Exception e) {
            log.error("Error viewing file from S3: {}", e.getMessage(), e);
            throw new IOException("Error viewing file from S3", e);
        } finally {
            // Close the streams in a finally block to ensure they're always closed
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Error closing input stream: {}", e.getMessage(), e);
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    log.error("Error closing output stream: {}", e.getMessage(), e);
                }
            }
        }
    }


}
