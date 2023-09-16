package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface StorageServiceInterface {
    String uploadProfileImage(MultipartFile file, User user) throws IOException;

    String uploadFileToBucket(MultipartFile file, Long categoryId, Long subcategoryId) throws IOException;

    byte[] downloadFile(String fileName) throws IOException;

    void deleteFile(String fileName) throws IOException;

    void viewFile(String fileName, HttpServletResponse response) throws IOException;
}


