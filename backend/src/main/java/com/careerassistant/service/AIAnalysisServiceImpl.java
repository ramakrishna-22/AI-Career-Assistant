package com.careerassistant.service;

import com.careerassistant.dto.*;
import com.careerassistant.entity.Resume;
import com.careerassistant.entity.ResumeAnalysisEntity;
import com.careerassistant.exception.InvalidFileException;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.ResumeAnalysisRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIAnalysisServiceImpl implements AIAnalysisService {

    private final ResumeService resumeService;
    private final SkillExtractionService skillExtractionService;
    private final CareerRecommendationService careerRecommendationService;
    private final ResumeAnalysisRepository resumeAnalysisRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|\\+?\\d{10,13}"
    );

    public AIAnalysisServiceImpl(ResumeService resumeService,
                                 SkillExtractionService skillExtractionService,
                                 CareerRecommendationService careerRecommendationService,
                                 ResumeAnalysisRepository resumeAnalysisRepository) {
        this.resumeService = resumeService;
        this.skillExtractionService = skillExtractionService;
        this.careerRecommendationService = careerRecommendationService;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
    }

    @Override
    @Transactional
    public CompleteAIAnalysisResponse performCompleteAnalysis(Long resumeId, Long userId) {
        Resume resume = resumeService.getResumeEntity(resumeId, null);

        // Strict authorization check: resume must belong to requested userId
        if (userId != null && !resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to analyze resume id: " + resumeId);
        }

        File file = new File(resume.getFilePath());
        if (!file.exists()) {
            throw new InvalidFileException("Resume file does not exist on disk at path: " + resume.getFilePath());
        }

        String text = extractText(file, resume.getFileName());
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidFileException("Extracted resume text is empty");
        }

        // Contact details extraction
        String email = extractEmail(text);
        String phone = extractPhone(text);
        String name = extractName(text, resume.getUser().getName());

        // Section & summary extraction
        String summary = extractSummary(text);
        List<String> education = extractSectionLines(text, "Education");
        List<String> experience = extractSectionLines(text, "Experience");
        List<String> projects = extractSectionLines(text, "Projects");

        // Skill Extraction
        List<CategorizedSkillDTO> skills = skillExtractionService.extractAndCategorizeSkills(text);
        skillExtractionService.saveExtractedSkills(resume, skills);

        // Career Recommendations
        List<CareerRecommendationDTO> recommendations = careerRecommendationService.generateRecommendations(skills);
        careerRecommendationService.saveCareerRecommendations(resume, recommendations);

        // Save AI Analysis entity
        ResumeAnalysisEntity analysisEntity = new ResumeAnalysisEntity(
                resume,
                summary,
                String.join("; ", education),
                String.join("; ", experience),
                String.join("; ", projects)
        );
        resumeAnalysisRepository.save(analysisEntity);

        return new CompleteAIAnalysisResponse(
                resume.getId(),
                name,
                email,
                phone,
                summary,
                education,
                experience,
                projects,
                skills,
                recommendations
        );
    }

    private String extractText(File file, String fileName) {
        try {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                try (PDDocument doc = Loader.loadPDF(file)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String t = stripper.getText(doc);
                    return t != null ? t : "";
                }
            } else {
                return new String(Files.readAllBytes(file.toPath()));
            }
        } catch (IOException e) {
            throw new InvalidFileException("Could not read text from file: " + fileName);
        }
    }

    private String extractEmail(String text) {
        Matcher m = EMAIL_PATTERN.matcher(text);
        return m.find() ? m.group() : "Not Detected";
    }

    private String extractPhone(String text) {
        Matcher m = PHONE_PATTERN.matcher(text);
        return m.find() ? m.group() : "Not Detected";
    }

    private String extractName(String text, String fallbackName) {
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.contains("@") && !trimmed.contains("http") && trimmed.length() < 40) {
                return trimmed;
            }
        }
        return fallbackName;
    }

    private String extractSummary(String text) {
        if (text.contains("Summary")) {
            int idx = text.indexOf("Summary");
            int end = Math.min(text.length(), idx + 250);
            return text.substring(idx, end).replaceAll("\\r?\\n", " ").trim();
        }
        return text.length() > 200 ? text.substring(0, 200).replaceAll("\\r?\\n", " ").trim() + "..." : text.trim();
    }

    private List<String> extractSectionLines(String text, String sectionTitle) {
        List<String> result = new ArrayList<>();
        if (!text.toLowerCase().contains(sectionTitle.toLowerCase())) {
            return result;
        }

        String[] lines = text.split("\\r?\\n");
        boolean capturing = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.toLowerCase().contains(sectionTitle.toLowerCase())) {
                capturing = true;
                continue;
            }
            if (capturing) {
                if (trimmed.equalsIgnoreCase("Skills") || trimmed.equalsIgnoreCase("Projects") ||
                        trimmed.equalsIgnoreCase("Experience") || trimmed.equalsIgnoreCase("Education")) {
                    break;
                }
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
        }
        return result;
    }
}
