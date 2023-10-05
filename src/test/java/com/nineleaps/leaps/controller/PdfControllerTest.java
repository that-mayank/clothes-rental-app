package com.nineleaps.leaps.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.controller.PdfController;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.PdfServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PdfControllerTest {

    private PdfController pdfController;

    @Mock
    private PdfServiceInterface pdfServiceInterface;

    @Mock
    private Helper helper;

    @Mock
    private MockHttpServletRequest mockHttpServletRequest;

    private MockMvc mockMvc;

    private User user;

    @BeforeEach
    void setUp() throws DocumentException, IOException {
        MockitoAnnotations.openMocks(this);
        pdfController = new PdfController(pdfServiceInterface, helper);
        mockMvc = MockMvcBuilders.standaloneSetup(pdfController).build();

        user = new User();
        user.setId(1L);
        user.setEmail("test@nineleaps.com");

        when(helper.getUserFromToken(mockHttpServletRequest)).thenReturn(user);
        Document document = new Document();
        when(pdfServiceInterface.getPdf(user)).thenReturn(document);
    }

    @Test
    void generatePdf_shouldReturnOkResponse() throws Exception {
        // Arrange
        when(helper.getUserFromToken(mockHttpServletRequest)).thenReturn(user);
        Document document = new Document();
        when(pdfServiceInterface.getPdf(user)).thenReturn(document);

        // Act
        MvcResult result = mockMvc.perform((RequestBuilder) get("/api/v1/pdf/export")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getContentType());
        verify(helper, times(1)).getUserFromToken(mockHttpServletRequest);
        verify(pdfServiceInterface, times(1)).getPdf(user);
    }

    private ResponseEntity.BodyBuilder get(String path) {
        return null;
    }

    @Test
    void generatePdf_shouldReturnErrorResponse() throws Exception {
        // Arrange
        when(helper.getUserFromToken(mockHttpServletRequest)).thenReturn(user);
        when(pdfServiceInterface.getPdf(user)).thenThrow(new DocumentException());

        // Act
        MvcResult result = mockMvc.perform((RequestBuilder) get("/api/v1/pdf/export")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        // Assert
        // TODO: Assert the response status code and any other relevant assertions
        verify(helper, times(1)).getUserFromToken(mockHttpServletRequest);
        verify(pdfServiceInterface, times(1)).getPdf(user);
    }

    // TODO: Add more test methods for other public methods in PdfController

}