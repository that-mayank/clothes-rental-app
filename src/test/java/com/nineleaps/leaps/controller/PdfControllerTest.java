//package com.nineleaps.leaps.controller;
//
//import com.google.api.client.util.IOUtils;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.PdfDocument;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.text.pdf.parser.PdfTextExtractor;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.service.PdfServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//class PdfControllerTest {
//    @Mock
//    private PdfServiceInterface pdfService;
//
//    @Mock
//    private HttpServletRequest request;
//    @Mock
//    private Helper helper;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @InjectMocks
//    private PdfController pdfController;
//
//    @Test
//    void testGetPdf() throws IOException, DocumentException {
//        // Mock the request header
//        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
//
//        // Mock the user
//        User user = new User();
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Create a new PDF document
//        Document document = new Document();
//
//        // Convert the Document into a byte array
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
//
//        // Open the document
//        document.open();
//
//        // Add content to the document
//        document.add(new Paragraph("Hello, this is a test PDF content."));
//
//        // Close the document
//        document.close();
//
//        // Manually set the PdfWriter in the PdfService
//        when(pdfService.getPdf(user)).thenReturn(document);
//        // Assuming you have created the PDF content and converted it to a byte array
//        byte[] pdfBytes = createPdfContent();
//
//        // Create InputStreamResource from the byte array
//        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));
//
//        // Set the Content-Disposition header to force download the PDF
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("attachment", "report.pdf");
//
//        // Create the ResponseEntity with the InputStreamResource and headers
//        ResponseEntity<InputStreamResource> responseEntity = new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
//
//
//        // Check status code
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//
//        // Check headers
//        HttpHeaders headers1 = responseEntity.getHeaders();
//        assertEquals(MediaType.APPLICATION_PDF, headers.getContentType());
//        assertEquals("form-data; name=\"attachment\"; filename=\"report.pdf\"", headers.getContentDisposition().toString());
//
//        // Check InputStreamResource
//        InputStreamResource inputStreamResource1 = responseEntity.getBody();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        IOUtils.copy(inputStreamResource.getInputStream(), byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//        // Check the content of the PDF using iText
//        PdfReader pdfReader = new PdfReader(byteArray);
//        String text = PdfTextExtractor.getTextFromPage(pdfReader, 1); // Extract text from the first page
//        pdfReader.close();
//
//        // Ensure the PDF content contains the expected text
//        assertEquals("Hello, this is a test PDF content.", text.trim());
//    }
//
//    private byte[] createPdfContent() throws DocumentException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Document document = new Document();
//        PdfWriter.getInstance(document, baos);
//
//        document.open();
//        document.add(new Paragraph("Hello, this is a test PDF content."));
//        document.close();
//
//        return baos.toByteArray();
//    }
//
//
//
//
//
//}
//
