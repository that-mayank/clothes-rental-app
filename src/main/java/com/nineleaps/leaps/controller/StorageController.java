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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/file")
@Slf4j
@AllArgsConstructor
@Api(tags = "Storage Api", description = "Contains api for uploading multiple images, downloading images, view images and delete images")
@SuppressWarnings("deprecation")
public class StorageController {
    private final StorageServiceInterface storageServiceInterface;
    private final Helper helper;


    @ApiOperation(value = "Upload image to Amazon S3")
    @PostMapping("/uploadProductImage")
    public ResponseEntity<UrlResponse> uploadFileToSpecificBucket(
            @RequestParam("file") MultipartFile[] files,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("subcategoryId") Long subcategoryId) {
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


    @ApiOperation(value = "Upload profile image to amazon s3")
    @PostMapping("/uploadProfileImage")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        try {
            String url = storageServiceInterface.uploadProfileImage(file,user);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("url", url);
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // to download the image of the product from s3
    @ApiOperation(value = "to download the image of the product from s3")
    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws IOException {
        byte[] data = storageServiceInterface.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    // to view the uploaded image
    @ApiOperation(value = "to view the uploaded image")
    @GetMapping("/view")
    public ResponseEntity<String> viewFile(@RequestParam("imageurl") String fileName, HttpServletResponse response) throws IOException {
        try {
            storageServiceInterface.viewFile(fileName, response);
            return ResponseEntity.ok("Image fetched from S3");  // Return 200 OK with a success message
        } catch (IOException e) {
            log.error("Error viewing file from S3: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error viewing file from S3");  // Return 500 Internal Server Error with an error message
        }
    }



    // to delete the image in s3 cloud storage
    @ApiOperation(value = "to delete the image in s3 cloud storage")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("fileurl") String fileName) {
        try {
            String result = storageServiceInterface.deleteFile(fileName);
            return ResponseEntity.noContent().build();  // Return 204 No Content for successful deletion
        } catch (IOException e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file from S3");  // Return 500 Internal Server Error with an error message
        }
    }

}
