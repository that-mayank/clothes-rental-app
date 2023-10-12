package com.nineleaps.leaps.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.PdfServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/pdf")
@AllArgsConstructor
@Validated
@Api(tags = "Pdf Api")
public class PdfController {

    //Linking layers using constructor injection

    private final PdfServiceInterface pdfService;
    private final Helper helper;

    //API : To export pdf

    @ApiOperation(value = "API : To export pdf")
    @GetMapping(value = "/export", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<InputStreamResource> getPdf(HttpServletRequest request) throws IOException, DocumentException {
        User user = helper.getUser(request);

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

        byte[] pdfBytes = baos.toByteArray();

        // Create the InputStreamResource from the byte array
        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        // Set the Content-Disposition header to force download the PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "report.pdf");

        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }
}
