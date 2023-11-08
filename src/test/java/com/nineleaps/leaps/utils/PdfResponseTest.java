package com.nineleaps.leaps.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfResponseTest {

    @Test
    void testConstructorAndGetters() {
        byte[] pdfBytes = new byte[]{1, 2, 3, 4, 5};
        PdfResponse pdfResponse = new PdfResponse(pdfBytes);

        assertArrayEquals(pdfBytes, pdfResponse.getPdfBytes());
    }

    @Test
    void testSetPdfBytes() {
        byte[] pdfBytes = new byte[]{1, 2, 3, 4, 5};
        PdfResponse pdfResponse = new PdfResponse(new byte[]{});

        pdfResponse.setPdfBytes(pdfBytes);
        assertArrayEquals(pdfBytes, pdfResponse.getPdfBytes());
    }

    @Test
    void testNotEquals() {
        byte[] pdfBytes1 = new byte[]{1, 2, 3, 4, 5};
        byte[] pdfBytes2 = new byte[]{5, 4, 3, 2, 1};
        PdfResponse pdfResponse1 = new PdfResponse(pdfBytes1);
        PdfResponse pdfResponse2 = new PdfResponse(pdfBytes2);

        assertNotEquals(pdfResponse1, pdfResponse2);
    }

    @Test
    void testHashCode() {
        byte[] pdfBytes = new byte[]{1, 2, 3, 4, 5};
        PdfResponse pdfResponse = new PdfResponse(pdfBytes);

        int actualHashCode = pdfResponse.hashCode();

        // Ensure that the actual hash code is not the same as any specific expected value
        assertNotSame(0, actualHashCode); // Hash code is non-zero
    }
}
