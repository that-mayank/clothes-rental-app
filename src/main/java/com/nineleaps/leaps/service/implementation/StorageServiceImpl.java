package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.service.StorageServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.CompletionException;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

@Slf4j
@Service
public class StorageServiceImpl implements StorageServiceInterface {
    private final String baseUrl = NGROK;
    private final String bucketName;
    private final S3Client s3Client;
    private final S3TransferManager transferManager;

    public StorageServiceImpl(
            @Value("${application.bucket.name}") String bucketName,
            S3Client s3Client,
            S3TransferManager transferManager
    ) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;
        this.transferManager = transferManager;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                    .putObjectRequest(b -> b.bucket(bucketName).key(fileName))
                    .addTransferListener(LoggingTransferListener.create())
                    .source(fileObj.toPath())
                    .build();

            FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

            // Wait for the upload to complete and handle errors
            CompletedFileUpload uploadResult = fileUpload.completionFuture().join();

            Files.delete(fileObj.toPath());

            return UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/api/v1/file/view/")
                    .path(fileName)
                    .toUriString();
        } catch (CompletionException e) {
            // Handle any exceptions during upload
            throw new IOException("Error uploading file to S3", e.getCause());
        } catch (S3Exception e) {
            // Handle S3 exceptions
            throw new IOException("Error uploading file to S3: " + e.getMessage(), e);
        } finally {
            // Ensure the temp file is deleted in case of any errors
            Files.deleteIfExists(fileObj.toPath());
        }
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
    public String deleteFile(String fileName) throws IOException {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());
            return fileName + " removed ...";
        } catch (S3Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            throw new IOException("Error deleting file from S3", e);
        }
    }

    @Override
    public void viewFile(String fileName, HttpServletResponse response) throws IOException {
        try {
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());

            String contentType = determineContentType(fileName);
            response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBytes.asByteArray().length));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");

            InputStream inputStream = new ByteArrayInputStream(responseBytes.asByteArray());
            OutputStream outputStream = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            // Check if the response is already committed
            if (!response.isCommitted()) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Close the streams
            inputStream.close();
            outputStream.flush();
        } catch (ClientAbortException e) {
            // Log the exception (optional) - client aborted the request
            log.warn("ClientAbortException: The client aborted the request.");
        } catch (S3Exception e) {
            log.error("Error viewing file from S3: {}", e.getMessage(), e);
            throw new IOException("Error viewing file from S3", e);
        }
    }

    private String determineContentType(String fileName) {
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

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = File.createTempFile("temp_", "_" + file.getOriginalFilename());
        file.transferTo(convertedFile);
        return convertedFile;
    }
}
