package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.UrlResponse;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.StorageServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/api/v1/file")
@Slf4j
@AllArgsConstructor
@Api(tags = "Storage Api", description = "Contains APIs for uploading multiple images, downloading images, viewing images, and deleting images")
@SuppressWarnings("deprecation")
public class StorageController {


    // Storage service for handling file-related operations
    private final StorageServiceInterface storageServiceInterface;
    private final Helper helper;



    // API to upload an image to Amazon S3
    @ApiOperation(value = "Upload image to Amazon S3")
    @PostMapping(value = "/uploadProductImage")
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ResponseEntity<UrlResponse> uploadFileToSpecificBucket(
            @RequestParam("file") MultipartFile[] files,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("subcategoryId") Long subcategoryId) {

        // Create a response object to hold URLs
        UrlResponse urlResponse = new UrlResponse();
        List<String> urls = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                // Construct the bucket path based on category and subcategory
                String url = storageServiceInterface.uploadFileToBucket(file, categoryId, subcategoryId);
                urls.add(url);
            }
            urlResponse.setUrls(urls);
            return ResponseEntity.ok(urlResponse);  // Return 200 OK with the UrlResponse
        } catch (Exception e) {
            log.error("Network Error in fetching Amazon S3");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Return 500 Internal Server Error
        }
    }

    // API to upload a profile image to Amazon S3
    @ApiOperation(value = "Upload profile image to Amazon S3")
    @PostMapping(value = "/uploadProfileImage")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        try {
            // Upload the profile image and obtain the URL
            String url = storageServiceInterface.uploadProfileImage(file, user);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("url", url);
            return ResponseEntity.ok(jsonResponse.toString());  // Return 200 OK with the UrlResponse
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Return 500 Internal Server Error with an error message
        }
    }

    // API to download an image from S3
    @ApiOperation(value = "Download an image from S3")
    @GetMapping(value = "/download/{fileName}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws IOException {
        // Download the file data as a byte array
        byte[] data = storageServiceInterface.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()  // Return 200 OK with the UrlResponse
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }



    // API to view an uploaded image
    @ApiOperation(value = "View an uploaded image")
    @GetMapping(value = "/view")
    public ResponseEntity<String> viewFile(@RequestParam("image") String fileName, HttpServletResponse response) {
        try {
            // View the file in the response
            storageServiceInterface.viewFile(fileName, response);
            return ResponseEntity.ok("Image fetched from S3");  // Return 200 OK with a success message
        } catch (IOException e) {
            log.error("Error viewing file from S3: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error viewing file from S3");  // Return 500 Internal Server Error with an error message
        }
    }



    // API to delete an image from S3 cloud storage
    @ApiOperation(value = "Delete an image from S3 cloud storage")
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<String> deleteFile(@RequestParam("file") String fileName) {
        try {
            // Delete the file from S3
            storageServiceInterface.deleteFile(fileName);
            return ResponseEntity.noContent().build();  // Return 204 No Content for successful deletion
        } catch (IOException e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file from S3");  // Return 500 Internal Server Error with an error message
        }
    }
}

