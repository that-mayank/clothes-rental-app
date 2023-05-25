package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.UrlResponse;
import com.nineleaps.leaps.model.Banner;
import com.nineleaps.leaps.repository.BannerRepository;
import com.nineleaps.leaps.service.StorageServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
@AllArgsConstructor
@Api(tags = "Storage Api", description = "Contains api for uploading multiple images, downloading images, view images and delete images")
public class StorageController {
    private final StorageServiceInterface storageServiceInterface;
    private final BannerRepository bannerRepository;


    // used to upload images of the product to s3
    @ApiOperation(value = "Upload image to amazon s3")
    @PostMapping("/upload")
    public UrlResponse uploadFile(@RequestParam(value = "file") MultipartFile[] files) throws IOException {
        UrlResponse urlResponse = new UrlResponse();
        List<String> urls = new ArrayList<>();
        try{
            for(MultipartFile file:files){
                String url = storageServiceInterface.uploadFile(file);
                urls.add(url);
            }
            urlResponse.setUrls(urls);
        }catch (Exception e){
            e.printStackTrace();
        }
        return urlResponse;
    }

    // to upload banner images for category and subcategory
    @PostMapping("/uploadbannerimage")
    public void uploadbannerimage(@RequestParam(value = "file")MultipartFile file,@RequestParam(value="name") String bannername)throws IOException{
        String url = storageServiceInterface.uploadFile(file);
        Banner banner = new Banner();
        banner.setBannerName(bannername);
        banner.setBannerURL(url);
        bannerRepository.save(banner);
        System.out.println("banner image uploaded !");

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
    public void viewFile(@PathVariable String fileName, HttpServletResponse response) {
        storageServiceInterface.viewFile(fileName, response);
    }


    // to delete the image in s3 cloud storage
    @ApiOperation(value = "to delete the image in s3 cloud storage")
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(storageServiceInterface.deleteFile(fileName), HttpStatus.OK);
    }
}
