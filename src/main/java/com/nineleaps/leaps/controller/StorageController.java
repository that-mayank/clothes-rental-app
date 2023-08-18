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

    // used to upload images of the product to s3
    @ApiOperation(value = "Upload image to amazon s3")
    @PostMapping("/upload")
    public UrlResponse uploadFile(@RequestParam(value = "file") MultipartFile[] files) {
        UrlResponse urlResponse = new UrlResponse();
        List<String> urls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String url = storageServiceInterface.uploadFile(file);
                urls.add(url);
            }
            urlResponse.setUrls(urls);
        } catch (Exception e) {
            log.error("Network Error in fetching Amazon S3");
        }
        return urlResponse;
    }

    @ApiOperation(value = "Upload profile image to amazon s3")
    @PostMapping("/uploadProfileImage")
    public ResponseEntity<String> uploadProfileFile(@RequestParam("file") MultipartFile file) {
        try {
            String url = storageServiceInterface.uploadFile(file);
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
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
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
    @GetMapping("/view/{fileName}")
    public void viewFile(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) {

                storageServiceInterface.viewFile(fileName, response);

    }


    // to delete the image in s3 cloud storage
    @ApiOperation(value = "to delete the image in s3 cloud storage")
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(storageServiceInterface.deleteFile(fileName), HttpStatus.OK);
    }
}
