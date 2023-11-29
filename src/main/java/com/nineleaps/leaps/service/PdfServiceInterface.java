package com.nineleaps.leaps.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.model.User;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface PdfServiceInterface {

    Document getPdf(User user) throws FileNotFoundException, DocumentException;

    void addContent(Document document, HttpServletRequest request) throws DocumentException, IOException;


}
