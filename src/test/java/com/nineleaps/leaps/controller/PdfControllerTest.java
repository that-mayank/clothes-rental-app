package com.nineleaps.leaps.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.PdfServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Tag("unit")
class PdfControllerTest {

    @Mock
    private PdfServiceInterface pdfServiceInterface;
    @Mock
    private Helper helper;
    @InjectMocks
    private PdfController pdfController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Generate Pdf - Success")
    void getPdf() throws IOException, DocumentException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        when(helper.getUser(request)).thenReturn(user);
        Document document = mock(Document.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(pdfServiceInterface.getPdf(user)).thenReturn(document);

        // Act
        ResponseEntity<InputStreamResource> response = pdfController.getPdf(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("form-data; name=\"attachment\"; filename=\"report.pdf\"", response.getHeaders().getContentDisposition().toString());
        verify(pdfServiceInterface, times(1)).getPdf(user);
        verify(pdfServiceInterface, times(1)).addContent(document, request);
        byte[] pdfBytes = baos.toByteArray();
        byte[] readBytes = new byte[pdfBytes.length];
        assertArrayEquals(pdfBytes, readBytes);
    }
}
