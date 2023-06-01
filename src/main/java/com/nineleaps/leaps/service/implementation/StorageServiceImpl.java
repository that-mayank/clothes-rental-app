package com.nineleaps.leaps.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import com.nineleaps.leaps.service.StorageServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static com.nineleaps.leaps.LeapsProductionApplication.ngrok_url;


@Service
@Slf4j
public class StorageServiceImpl implements StorageServiceInterface {

    private String baseUrl = ngrok_url;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    // private method to determine Content type
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


    // private method to convert multipart file to file
    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }


    // upload file to s3 cloud AWS storage
    public String uploadFile(MultipartFile file) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();
        String URL = ServletUriComponentsBuilder.fromHttpUrl(baseUrl).path("/api/v1/file/view/").path(fileName).toUriString();
        return URL;
    }

    // download the image from s3
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //delete the file from s3
    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }


    //view the file from s3
    public void viewFile(String fileName, HttpServletResponse response) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        // Set the response headers
        String contentType = determineContentType(fileName);
        response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);

        try {
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.flush();
        } catch (IOException e) {
//            log.error("Error viewing file", e);
            e.printStackTrace();
        }
    }
//    public void viewFile(String fileName, HttpServletResponse response) {
//        S3Object s3Object = s3Client.getObject(bucketName, fileName);
//        S3ObjectInputStream inputStream = s3Object.getObjectContent();
//        // Set the response headers
//        String contentType = determineContentType(fileName);
//        response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
//        try (OutputStream outputStream = response.getOutputStream()) {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//        } catch (IOException e) {
//            log.error("Error viewing file", e);
//        } finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                log.error("Error closing S3ObjectInputStream", e);
//            }
//        }
//    }
//    public void viewFile(String fileName, HttpServletResponse response) {
//        S3Object s3Object = s3Client.getObject(bucketName, fileName);
//        S3ObjectInputStream inputStream = s3Object.getObjectContent();
//
//        // Set the response headers
//        String contentType = determineContentType(fileName);
//        response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
//        response.setHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
//
//        try (OutputStream outputStream = response.getOutputStream()) {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//                outputStream.flush(); // Flush the output stream after each chunk
//            }
//        } catch (IOException e) {
//            log.error("Error viewing file", e);
//        } finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                log.error("Error closing S3ObjectInputStream", e);
//            }
//        }
//    }
//
//
//

//    public void viewFile(String fileName, HttpServletResponse response) {
//        S3Object s3Object = s3Client.getObject(bucketName, fileName);
//        S3ObjectInputStream inputStream = s3Object.getObjectContent();
//
//        // Set the response headers
//        String contentType = determineContentType(fileName);
//        response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
//        response.setHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
//
//        try (OutputStream outputStream = response.getOutputStream()) {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//            outputStream.flush(); // Flush the output stream after all chunks are written
//        } catch (IOException e) {
//            log.error("Error viewing file", e);
//        } finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                log.error("Error closing S3ObjectInputStream", e);
//            }
//        }
//    }


}

