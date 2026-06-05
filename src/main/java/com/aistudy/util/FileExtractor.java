package com.aistudy.util;

import jakarta.servlet.http.Part;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileExtractor {

    public static String extractText(Part filePart) throws Exception {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String fileName = filePart.getSubmittedFileName().toLowerCase();
        
        try (InputStream inputStream = filePart.getInputStream()) {
            if (fileName.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(inputStream)) {
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    return pdfStripper.getText(document);
                }
            } else if (fileName.endsWith(".txt")) {
                StringBuilder textBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textBuilder.append(line).append("\n");
                    }
                }
                return textBuilder.toString();
            } else {
                throw new Exception("Unsupported file format. Please upload a .txt or .pdf file.");
            }
        }
    }
}
