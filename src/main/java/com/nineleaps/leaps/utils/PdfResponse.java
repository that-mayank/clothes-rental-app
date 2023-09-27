package com.nineleaps.leaps.utils;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PdfResponse {
    private byte[] pdfBytes;

    public PdfResponse(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
    }

}

