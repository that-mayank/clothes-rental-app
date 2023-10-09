package com.nineleaps.leaps.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@Tag("unit_tests")
@DisplayName(" PdfResponse Tests")
class PdfResponseTest {

    @Test
    @DisplayName(" get PDF Bytes")
    void testGetPdfBytes() {
        // Create a sample byte array for testing
        byte[] sampleBytes = {1, 2, 3, 4, 5};

        // Create a PdfResponse object with the sample byte array
        PdfResponse pdfResponse = new PdfResponse(sampleBytes);

        // Call the getPdfBytes() method and assert that it returns the same byte array
        assertArrayEquals(sampleBytes, pdfResponse.getPdfBytes());
    }

    @Test
    @DisplayName(" set PDF Bytes")
    void testSetPdfBytes() {
        // Create a sample byte array for testing
        byte[] sampleBytes = {1, 2, 3, 4, 5};

        // Create a PdfResponse object
        PdfResponse pdfResponse = new PdfResponse(null);

        // Set the byte array using the setPdfBytes() method
        pdfResponse.setPdfBytes(sampleBytes);

        // Call the getPdfBytes() method and assert that it returns the same byte array
        assertArrayEquals(sampleBytes, pdfResponse.getPdfBytes());
    }
}