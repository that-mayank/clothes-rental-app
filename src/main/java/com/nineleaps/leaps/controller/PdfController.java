package com.nineleaps.leaps.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.PdfServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;



@RestController
@RequestMapping("/api/v1/pdf")
@AllArgsConstructor
public class PdfController {


    // linking service layers
    private final PdfServiceInterface pdfService;
    private final Helper helper;

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<InputStreamResource> getPdf(HttpServletRequest request) throws DocumentException, IOException {
        User user = helper.getUserFromToken(request);

        Document document = pdfService.getPdf(user);

        byte[] pdfBytes = generatePdfBytes(document,user);

        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "report.pdf");

        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

    private byte[] generatePdfBytes(Document document, User user) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


            PdfWriter.getInstance(document, baos);
            document.open();
            // Call PdfService to add content to the document
            pdfService.addContent(document, user);
            document.close();


        return baos.toByteArray();
    }




}
