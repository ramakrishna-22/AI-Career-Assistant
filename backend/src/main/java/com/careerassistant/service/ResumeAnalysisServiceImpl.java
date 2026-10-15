package com.careerassistant.service;

import com.careerassistant.dto.ResumeAnalysisResponse;
import com.careerassistant.entity.Resume;
import com.careerassistant.exception.InvalidFileException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {

    private final ResumeService resumeService;

    // Extensible dictionary of technical skills
    private static final List<String> SKILL_DICTIONARY = Arrays.asList(
            "Java", "Python", "JavaScript", "HTML", "CSS", "React", "Spring Boot", "Spring",
            "MySQL", "SQL", "AWS", "Docker", "Git", "GitHub", "Machine Learning", "AI",
            "TensorFlow", "OpenCV", "Node.js", "Express", "TypeScript", "C++", "Linux",
            "REST API", "MongoDB", "PostgreSQL", "Kubernetes", "CI/CD", "Microservices",
            "Unit Testing", "Jenkins"
    );

    // Section headers to detect
    private static final List<String> SECTION_DICTIONARY = Arrays.asList(
            "Summary", "Objective", "Education", "Skills", "Experience", "Projects", "Certifications", "Achievements"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|\\+?\\d{10,13}"
    );

    public ResumeAnalysisServiceImpl(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @Override
    public ResumeAnalysisResponse analyzeResume(Long resumeId, Long userId) {
        Resume resume = resumeService.getResumeEntity(resumeId, userId);
        File file = new File(resume.getFilePath());

        if (!file.exists()) {
            throw new InvalidFileException("Resume file does not exist on disk at path: " + resume.getFilePath());
        }

        String extractedText = extractTextFromFile(file, resume.getFileName());

        // Basic Statistics
        int characterCount = extractedText.length();
        String[] words = extractedText.trim().split("\\s+");
        int wordCount = (extractedText.trim().isEmpty() || words.length == 1 && words[0].isEmpty()) ? 0 : words.length;

        // Contact Information Detection
        boolean emailDetected = EMAIL_PATTERN.matcher(extractedText).find();
        boolean phoneDetected = PHONE_PATTERN.matcher(extractedText).find();

        // Skill Detection
        List<String> detectedSkills = detectSkills(extractedText);

        // Section Detection
        List<String> detectedSections = detectSections(extractedText);

        // Rule-Based Transparent Scoring Engine
        int score = calculateRuleBasedScore(wordCount, emailDetected, phoneDetected, detectedSkills, detectedSections);

        // Improvement Suggestions
        List<String> suggestions = generateSuggestions(wordCount, emailDetected, phoneDetected, detectedSkills, detectedSections);

        return new ResumeAnalysisResponse(
                resume.getId(),
                wordCount,
                characterCount,
                detectedSkills,
                detectedSections,
                emailDetected,
                phoneDetected,
                score,
                suggestions
        );
    }

    private String extractTextFromFile(File file, String fileName) {
        String lowerName = fileName.toLowerCase();
        try {
            if (lowerName.endsWith(".pdf")) {
                try (PDDocument document = Loader.loadPDF(file)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    return text != null ? text : "";
                }
            } else if (lowerName.endsWith(".docx")) {
                try (FileInputStream fis = new FileInputStream(file);
                     XWPFDocument docx = new XWPFDocument(fis);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
                    String text = extractor.getText();
                    return text != null ? text : "";
                }
            } else if (lowerName.endsWith(".doc")) {
                return new String(Files.readAllBytes(file.toPath()));
            } else {
                throw new InvalidFileException("Unsupported file type for text extraction: " + fileName);
            }
        } catch (IOException e) {
            throw new InvalidFileException("Failed to extract text from resume file (" + fileName + "): " + e.getMessage());
        }
    }

    private List<String> detectSkills(String text) {
        List<String> found = new ArrayList<>();
        String lowerText = text.toLowerCase();

        for (String skill : SKILL_DICTIONARY) {
            String regex = "\\b" + Pattern.quote(skill.toLowerCase()) + "\\b";
            if (Pattern.compile(regex).matcher(lowerText).find()) {
                found.add(skill);
            }
        }
        return found;
    }

    private List<String> detectSections(String text) {
        List<String> found = new ArrayList<>();
        String lowerText = text.toLowerCase();

        for (String section : SECTION_DICTIONARY) {
            String regex = "\\b" + Pattern.quote(section.toLowerCase()) + "\\b";
            if (Pattern.compile(regex).matcher(lowerText).find()) {
                found.add(section);
            }
        }
        return found;
    }

    private int calculateRuleBasedScore(int wordCount, boolean emailDetected, boolean phoneDetected,
                                       List<String> skills, List<String> sections) {
        int score = 0;

        // 1. Sections Present (Max 35 points)
        if (sections.contains("Education")) score += 10;
        if (sections.contains("Skills")) score += 10;
        if (sections.contains("Projects")) score += 10;
        if (sections.contains("Experience") || sections.contains("Work")) score += 5;

        // 2. Skills Count (Max 25 points: 5 points per skill up to 25)
        score += Math.min(25, skills.size() * 5);

        // 3. Contact Details (Max 20 points)
        if (emailDetected) score += 10;
        if (phoneDetected) score += 10;

        // 4. Word Count / Length (Max 20 points)
        if (wordCount >= 301 && wordCount <= 800) {
            score += 20;
        } else if (wordCount >= 100 && wordCount <= 300) {
            score += 10;
        } else if (wordCount > 800) {
            score += 15;
        }

        return Math.min(100, score);
    }

    private List<String> generateSuggestions(int wordCount, boolean emailDetected, boolean phoneDetected,
                                             List<String> skills, List<String> sections) {
        List<String> suggestions = new ArrayList<>();

        if (!emailDetected || !phoneDetected) {
            suggestions.add("Ensure your resume contains clear contact information (both Email and Phone Number).");
        }

        if (skills.size() < 5) {
            suggestions.add("Add relevant technical skills and framework keywords to improve ATS matching.");
        }

        if (!sections.contains("Projects")) {
            suggestions.add("Add a Projects section highlighting technologies used and problem outcomes.");
        }

        if (!sections.contains("Education")) {
            suggestions.add("Include an Education section with your degree, field of study, and institution.");
        }

        if (wordCount < 250) {
            suggestions.add("Expand your project and experience descriptions with specific technical contributions.");
        }

        suggestions.add("Use strong action verbs and quantify accomplishments with metrics where applicable.");

        return suggestions;
    }
}
