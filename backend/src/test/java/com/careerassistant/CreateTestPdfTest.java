package com.careerassistant;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class CreateTestPdfTest {

    @Test
    public void generateAndVerifyRealPdf() throws Exception {
        File sampleDir = new File("E:/AI-Career-Assistant/sample");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        File pdfFile = new File(sampleDir, "real_sample_resume.pdf");

        // Generate real PDF using Apache PDFBox 3.x
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                contents.beginText();
                contents.setFont(fontBold, 16);
                contents.newLineAtOffset(50, 750);
                contents.showText("Ramakrishna V");

                contents.setFont(fontRegular, 11);
                contents.newLineAtOffset(0, -20);
                contents.showText("Email: ram@example.com | Phone: +1 555-123-4567");

                contents.setFont(fontBold, 14);
                contents.newLineAtOffset(0, -30);
                contents.showText("Summary");

                contents.setFont(fontRegular, 11);
                contents.newLineAtOffset(0, -18);
                contents.showText("Experienced Software Engineer specializing in Full Stack and Cloud Applications.");

                contents.setFont(fontBold, 14);
                contents.newLineAtOffset(0, -30);
                contents.showText("Education");

                contents.setFont(fontRegular, 11);
                contents.newLineAtOffset(0, -18);
                contents.showText("Bachelor of Technology in Computer Science");

                contents.setFont(fontBold, 14);
                contents.newLineAtOffset(0, -30);
                contents.showText("Skills");

                contents.setFont(fontRegular, 11);
                contents.newLineAtOffset(0, -18);
                contents.showText("Java, Python, JavaScript, HTML, CSS, React, Spring Boot, Spring, MySQL, SQL, AWS, Docker, Git, GitHub");

                contents.setFont(fontBold, 14);
                contents.newLineAtOffset(0, -30);
                contents.showText("Experience");

                contents.setFont(fontRegular, 11);
                contents.newLineAtOffset(0, -18);
                contents.showText("Software Engineer Intern at Tech Corp - Developed REST APIs and microservices.");

                contents.setFont(fontBold, 14);
                contents.newLineAtOffset(0, -30);
                contents.showText("Projects");

                contents.setFont(fontRegular, 11);
                contents.newLineAtOffset(0, -18);
                contents.showText("AI Career Assistant - Built full-stack platform using React, Spring Boot, and MySQL.");

                contents.endText();
            }

            doc.save(pdfFile);
        }

        // Verification assertions
        assertTrue(pdfFile.exists(), "PDF file should exist");
        assertTrue(pdfFile.length() > 0, "PDF file size should be greater than 0 bytes");

        try (PDDocument readDoc = Loader.loadPDF(pdfFile)) {
            assertNotNull(readDoc, "PDDocument should not be null");
            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(readDoc);

            assertNotNull(extractedText, "Extracted text should not be null");
            assertTrue(extractedText.contains("Ramakrishna V"), "Extracted text must contain name");
            assertTrue(extractedText.contains("ram@example.com"), "Extracted text must contain email");
            assertTrue(extractedText.contains("Summary"), "Extracted text must contain Summary section");
            assertTrue(extractedText.contains("Education"), "Extracted text must contain Education section");
            assertTrue(extractedText.contains("Skills"), "Extracted text must contain Skills section");
            assertTrue(extractedText.contains("Projects"), "Extracted text must contain Projects section");
            assertTrue(extractedText.contains("Experience"), "Extracted text must contain Experience section");

            System.out.println("=== REAL PDF CREATED & VERIFIED SUCCESSFULLY ===");
            System.out.println("Path: " + pdfFile.getAbsolutePath());
            System.out.println("Size: " + pdfFile.length() + " bytes");
            System.out.println("Extracted Content Length: " + extractedText.length() + " characters");
        }
    }
}
