package com.swp391.edrive.service;

import com.swp391.edrive.entity.Quotation;

import java.io.ByteArrayOutputStream;

public interface QuotationPdfService {

    /**
     * Generate PDF cho quotation
     * @param quotationId ID của quotation
     * @return PDF content as byte array
     */
    ByteArrayOutputStream generateQuotationPdf(Long quotationId);

    /**
     * Generate PDF từ Quotation entity
     * @param quotation Quotation entity
     * @return PDF content as byte array
     */
    ByteArrayOutputStream generateQuotationPdf(Quotation quotation);
}

