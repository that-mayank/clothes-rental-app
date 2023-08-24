package com.nineleaps.leaps.utils;

import lombok.Data;

@Data
public class PdfResponse {
    private byte[] pdfBytes;

    public PdfResponse(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
    }

    public byte[] getPdfBytes() {
        return pdfBytes;
    }

    public void setPdfBytes(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
    }
}

