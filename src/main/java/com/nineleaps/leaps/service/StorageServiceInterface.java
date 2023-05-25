package com.nineleaps.leaps.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface StorageServiceInterface {
    public String uploadFile(MultipartFile file);
    public byte[] downloadFile(String fileName);
    public String deleteFile(String fileName);
    public void viewFile(String fileName, HttpServletResponse response);

}
