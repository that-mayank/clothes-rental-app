package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.UrlResponse;
import com.nineleaps.leaps.service.StorageServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
@Slf4j
@AllArgsConstructor
@Api(tags = "Storage Api")
public class StorageController {

    //Linking layers using constructor injection
    private final StorageServiceInterface storageServiceInterface;

    // API : To upload images of the product to s3
    @ApiOperation(value = "API : To upload images of the product to s3")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('OWNER')")
    public UrlResponse uploadFile(@RequestParam(value = "file") MultipartFile[] files) {

        // Create a response object to store the URLs of the uploaded files
        UrlResponse urlResponse = new UrlResponse();
        List<String> urls = new ArrayList<>();
        try {
            // Iterate through the uploaded files
            for (MultipartFile file : files) {
                // Upload the file to Amazon S3 and get the URL
                String url = storageServiceInterface.uploadFile(file);
                // Add the URL to the list
                urls.add(url);
            }
            // Set the list of URLs in the response object
            urlResponse.setUrls(urls);
        } catch (Exception e) {
            log.error("Network Error in fetching Amazon S3");
        }
        // Return the response object containing the uploaded file URLs
        return urlResponse;
    }

    // API: To upload a profile image to Amazon S3
    @ApiOperation(value = "API : To upload a profile image to Amazon S3")
    @PostMapping("profile")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<String> uploadProfileFile(@RequestParam("file") MultipartFile file) {
        try {
            // Upload the provided profile image file to Amazon S3
            String url = storageServiceInterface.uploadFile(file);
            // Create a JSON response containing the URL of the uploaded profile image
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("url", url);
            // Return a ResponseEntity with the JSON response and HTTP status OK (200)
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // API: To download the image of the product from Amazon S3
    @ApiOperation(value = "API : To download the image of the product from Amazon S3")
    @GetMapping("{fileName}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        // Retrieve the binary data (image) from Amazon S3 based on the provided file name
        byte[] data = storageServiceInterface.downloadFile(fileName);
        // Create a ByteArrayResource to represent the binary data
        ByteArrayResource resource = new ByteArrayResource(data);
        // Return a ResponseEntity with the binary data, content length, content type, and attachment header
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    // API: To view the uploaded image
    @ApiOperation(value = "API : To view the uploaded image")
    @GetMapping("view/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    // PreAuthorize annotation restricts access to authorized users with specified authorities (OWNER, BORROWER, ADMIN)
    public void viewFile(
            @PathVariable String fileName,
            HttpServletResponse response) {
        // Delegate the task of viewing the image to the storageServiceInterface
        storageServiceInterface.viewFile(fileName, response);
    }

    // API: To delete the image in S3 cloud storage
    @ApiOperation(value = "API : To delete the image in S3 cloud storage")
    @DeleteMapping("{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    // This method deletes the specified image file in S3 storage
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        // Delegate the task of deleting the file to the storageServiceInterface and return the result
        return new ResponseEntity<>(
                storageServiceInterface.deleteFile(fileName),
                HttpStatus.NO_CONTENT);
    }
}
