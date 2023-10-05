package com.nineleaps.leaps.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.PdfServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;


@RestController
@RequestMapping("/api/v1/pdf")
public class PdfController {

    private final PdfServiceInterface pdfService;
    private final Helper helper;

    public PdfController(PdfServiceInterface pdfService, Helper helper) {
        this.pdfService = pdfService;
        this.helper = helper;
    }

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<InputStreamResource> getPdf(HttpServletRequest request) {
        try {
            User user = helper.getUserFromToken(request);

            Document document = pdfService.getPdf(user);

            Resource pdfResource = generatePdfResource(document, user);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "report.pdf");

            return new ResponseEntity<>(new InputStreamResource(pdfResource.getInputStream()), headers, HttpStatus.OK);
        } catch (DocumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Resource generatePdfResource(Document document, User user) throws DocumentException, IOException {
        File file = File.createTempFile("report", ".pdf");
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        pdfService.addContent(document, user);
        document.close();
        pdfWriter.close();

        return new FileSystemResource(file);
    }
}
