package com.nineleaps.leaps.utils;

import lombok.Getter;

@Getter
public class PdfResponse {
    private byte[] pdfBytes;

    public PdfResponse(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
    }

    public void setPdfBytes(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
    }
}

