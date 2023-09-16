package com.nineleaps.leaps.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface StorageServiceInterface {
    String uploadFile(MultipartFile file) throws IOException;

    String uploadFileToBucket(MultipartFile file,Long categoryId,Long subcategoryId) throws IOException;

    byte[] downloadFile(String fileName) throws IOException;

    String deleteFile(String fileName) throws IOException;

    void viewFile(String fileName, HttpServletResponse response) throws IOException;
}
