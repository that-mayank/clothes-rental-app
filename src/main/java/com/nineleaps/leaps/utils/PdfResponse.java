package com.nineleaps.leaps.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PdfResponse {
    private byte[] pdfBytes;

    public PdfResponse(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
    }

}

