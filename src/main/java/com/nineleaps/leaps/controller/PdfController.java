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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/pdf")
@AllArgsConstructor
public class PdfController {


    // linking service layers
    private final PdfServiceInterface pdfService;
    private final Helper helper;

    @GetMapping(value = "/export",produces = MediaType.APPLICATION_JSON_VALUE) // Handler method to export PDF
    @PreAuthorize("hasAuthority('OWNER')") // Requires 'OWNER' authority to access
    public ResponseEntity<InputStreamResource> getPdf(HttpServletRequest request) throws IOException, DocumentException {
        // Extracting the token from the Authorization header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

        // Retrieve user information from the token
        User user = helper.getUser(token);

        // Create a new PDF document
        Document document = pdfService.getPdf(user);

        // Convert the Document into a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        // Open the document
        document.open();

        // Add content to the document
        pdfService.addContent(document, user);

        // Close the document
        document.close();

        // Convert the PDF content to a byte array
        byte[] pdfBytes = baos.toByteArray();

        // Create the InputStreamResource from the byte array
        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        // Set the Content-Disposition header to force download the PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "report.pdf");

        // Return the ResponseEntity with the PDF content and headers
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }
}
