package com.nineleaps.leaps.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.nineleaps.leaps.service.StorageServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

@Service // This class is a service component.
@Slf4j // A tool to log messages.
@Transactional // Helps manage database transactions.
public class StorageServiceImpl implements StorageServiceInterface {

    String baseUrl = NGROK; // The base URL for accessing files.

    @Value("${application.bucket.name}") // The name of the AWS S3 bucket.
    String bucketName;

    AmazonS3 s3Client; // An interface for working with AWS S3.

    // Determine the type of content based on the file extension.
    String determineContentType(String fileName) {
        String contentType;
        // Check the file extension and set the appropriate content type.
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

    // Convert a file uploaded as a MultipartFile into a regular file.
    File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            byte[] fileBytes = file.getBytes();
            fos.write(fileBytes);
        } catch (IOException e) {
            log.error("Error converting multipartFile to file");
        }
        return convertedFile;
    }

    // Upload a file to AWS S3 cloud storage.
    public String uploadFile(MultipartFile file) {
        // Convert the MultipartFile to a regular file.
        File fileObj = convertMultiPartFileToFile(file);
        // Generate a unique file name using the current timestamp and the original file name.
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // Upload the file to the specified AWS S3 bucket.
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        try {
            // Delete the temporary file created during the conversion.
            Files.delete(fileObj.toPath());
        } catch (IOException e) {
            log.error("Error uploading file");
        }
        // Return the URL for accessing the uploaded file.
        return UriComponentsBuilder.fromHttpUrl(baseUrl).path("/api/v1/file/view/").path(fileName).toUriString();
    }

    // Download a file from AWS S3.
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try {
            // Read the file content from the input stream and convert it to a byte array.
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("Error downloading file");
        }
        // Return an empty byte array if there's an error.
        return new byte[0];
    }

    // Delete a file from AWS S3.
    public String deleteFile(String fileName) {
        // Delete the specified file from the AWS S3 bucket.
        s3Client.deleteObject(bucketName, fileName);
        // Return a message indicating that the file has been removed.
        return fileName + " removed";
    }

    // View a file from AWS S3 and send it as a response.
    public void viewFile(String fileName, HttpServletResponse response) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            // Set the response headers to specify the content type.
            String contentType = determineContentType(fileName);
            response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);

            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            // Read the file content from the input stream and write it to the response output stream.
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.flush();
        } catch (IOException e) {
            log.error("Amazon S3 Network Error");
        }
    }
}
