package com.nineleaps.leaps.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.PdfServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
@ExtendWith(RuntimeBenchmarkExtension.class)
class PdfControllerTest {

    @Mock
    private Helper helper;

    @Mock
    private PdfServiceInterface pdfService;

    @InjectMocks
    private PdfController pdfController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pdfController = new PdfController(pdfService, helper);
    }

    @Test
    void generatePdfResource_shouldReturnPdfResource() throws Exception {
        // Mocking behavior for dependencies
        Document mockDocument = new Document();
        User mockUser = new User();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = PdfWriter.getInstance(mockDocument, byteArrayOutputStream);

        doAnswer(invocation -> {
            Document document = invocation.getArgument(0);
            document.open();
            document.add(new Paragraph("Mocked content for the PDF"));
            document.close();
            return null;
        }).when(pdfService).addContent(any(Document.class), any(User.class));

        // Call the method to be tested
        Resource generatedPdfResource = pdfController.generatePdfResource(mockDocument, mockUser);

        // Close the PdfWriter
        pdfWriter.close();

        // Validate the content of the generated PDF
        byte[] generatedContent = byteArrayOutputStream.toByteArray();
        ByteArrayResource expectedResource = new ByteArrayResource(generatedContent);

        // Assert the generated PDF resource by comparing their content
        assertEquals(expectedResource.contentLength(), generatedPdfResource.contentLength());

    }

//    @Test
//    void getPdf_shouldReturnPdf() throws IOException, DocumentException {
//        // Mocking behavior for dependencies
//        User mockUser = new User();
//        Document mockDocument = Mockito.mock(Document.class);  // Mock a Document instance
//        InputStreamResource mockPdfResource = createMockPdfResource();
//
//        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
//
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(mockUser);
//        when(pdfService.getPdf(any(User.class))).thenReturn(mockDocument);
//        when(generatePdfResource(any(User.class))).thenReturn(mockPdfResource);
//
//
//        ResponseEntity<InputStreamResource> responseEntity = yourController.getPdf(mockRequest);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("application/pdf", responseEntity.getHeaders().getContentType().toString());
//    }
//
//
//
//    private Resource generatePdfResource(User user) throws DocumentException, IOException {
//        // Create a new Document instance
//        Document document = new Document();
//        User user1 = new User();
//        user1.setEmail("yokes.e@nineleaps.com");
//
//        // Add content to the document
//
//
//        // Create a temporary file to store the PDF
//        File file = File.createTempFile("report", ".pdf");
//
//        // Write the content to the PDF file
//        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
//        pdfWriter.setCloseStream(false);
//
//        // Open the document and add content
//        document.open();
//        document.add(new Paragraph("This is the content of the PDF for user: " + user1.getEmail()));
//        document.close();
//
//        // Return the PDF file as a resource
//        return new FileSystemResource(file);
//    }
//
//
//
//    private InputStreamResource createMockPdfResource() {
//        // Create a sample PDF content
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        Document document = new Document();
//
//        try {
//            PdfWriter.getInstance(document, byteArrayOutputStream);
//            document.open();
//            document.add(new com.itextpdf.text.Paragraph("Mock PDF content"));
//            document.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//        return new InputStreamResource(byteArrayInputStream);
//    }
//
//
//
//
//    @Test
//    void getPdf_shouldHandleException() throws Exception {
//        // Mock behavior to throw a RuntimeException
//        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
//
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenThrow(new RuntimeException("Test error"));
//
//        ResponseEntity<InputStreamResource> responseEntity = yourController.getPdf(mockRequest);
//
//        // Assert the response for an error scenario
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }

}