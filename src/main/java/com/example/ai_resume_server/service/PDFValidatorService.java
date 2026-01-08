package com.example.ai_resume_server.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PDFValidatorService {
    public boolean isValidSize(MultipartFile file,int size){
        long maxMb= (long) size *1024*1024;
        return file.getSize()<=maxMb;
    }
    public boolean isPdf(MultipartFile file) {
        return file.getContentType() != null &&
                file.getContentType().equalsIgnoreCase("application/pdf");
    }

    // Validate file extension
    public boolean isPdfExtension(MultipartFile file) {
        return file.getOriginalFilename() != null &&
                file.getOriginalFilename().toLowerCase().endsWith(".pdf");
    }

    // Validate using PDFBox → checks if file is corrupted or fake
    public boolean isValidPdfContent(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public String validate(MultipartFile file) {

        // 1. Check size (max 2MB)
        if (!isValidSize(file, 2)) {
            return "Error: File size must be less than 2MB";
        }

        // 2. Check PDF MIME type
        if (!isPdf(file)) {
            return "Error: Only PDF files are allowed";
        }

        // 3. Check extension
        if (!isPdfExtension(file)) {
            return "Error: File extension must be .pdf";
        }

        // 4. Validate PDF content using PDFBox
        if (!isValidPdfContent(file)) {
            return "Error: Corrupted or invalid PDF file";
        }

        return "VALID";
    }
}
