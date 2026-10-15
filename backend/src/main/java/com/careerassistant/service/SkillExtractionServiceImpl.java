package com.careerassistant.service;

import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.dto.SkillExtractionResponse;
import com.careerassistant.entity.ExtractedSkillEntity;
import com.careerassistant.entity.Resume;
import com.careerassistant.entity.SkillCategory;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.ExtractedSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SkillExtractionServiceImpl implements SkillExtractionService {

    private final ResumeService resumeService;
    private final ExtractedSkillRepository extractedSkillRepository;

    private static final Map<String, SkillCategory> SKILL_DICTIONARY = new LinkedHashMap<>();

    static {
        // Programming Languages
        putSkill("Java", SkillCategory.PROGRAMMING);
        putSkill("Python", SkillCategory.PROGRAMMING);
        putSkill("JavaScript", SkillCategory.PROGRAMMING);
        putSkill("TypeScript", SkillCategory.PROGRAMMING);
        putSkill("C", SkillCategory.PROGRAMMING);
        putSkill("C++", SkillCategory.PROGRAMMING);
        putSkill("C#", SkillCategory.PROGRAMMING);
        putSkill("Go", SkillCategory.PROGRAMMING);
        putSkill("PHP", SkillCategory.PROGRAMMING);

        // Frontend
        putSkill("HTML", SkillCategory.FRONTEND);
        putSkill("CSS", SkillCategory.FRONTEND);
        putSkill("React", SkillCategory.FRONTEND);
        putSkill("Angular", SkillCategory.FRONTEND);
        putSkill("Vue", SkillCategory.FRONTEND);
        putSkill("Bootstrap", SkillCategory.FRONTEND);

        // Backend
        putSkill("Spring", SkillCategory.BACKEND);
        putSkill("Spring Boot", SkillCategory.BACKEND);
        putSkill("REST API", SkillCategory.BACKEND);
        putSkill("Node.js", SkillCategory.BACKEND);
        putSkill("Express", SkillCategory.BACKEND);
        putSkill("Django", SkillCategory.BACKEND);
        putSkill("Flask", SkillCategory.BACKEND);
        putSkill("JDBC", SkillCategory.BACKEND);
        putSkill("JPA", SkillCategory.BACKEND);
        putSkill("Hibernate", SkillCategory.BACKEND);

        // Databases
        putSkill("MySQL", SkillCategory.DATABASE);
        putSkill("PostgreSQL", SkillCategory.DATABASE);
        putSkill("MongoDB", SkillCategory.DATABASE);
        putSkill("Oracle", SkillCategory.DATABASE);
        putSkill("SQL", SkillCategory.DATABASE);
        putSkill("Redis", SkillCategory.DATABASE);

        // Cloud / DevOps
        putSkill("AWS", SkillCategory.CLOUD);
        putSkill("Azure", SkillCategory.CLOUD);
        putSkill("GCP", SkillCategory.CLOUD);
        putSkill("EC2", SkillCategory.CLOUD);
        putSkill("S3", SkillCategory.CLOUD);
        putSkill("IAM", SkillCategory.CLOUD);
        putSkill("Docker", SkillCategory.DEVOPS);
        putSkill("Kubernetes", SkillCategory.DEVOPS);
        putSkill("Jenkins", SkillCategory.DEVOPS);
        putSkill("CI/CD", SkillCategory.DEVOPS);

        // AI / ML
        putSkill("Machine Learning", SkillCategory.AI_ML);
        putSkill("Deep Learning", SkillCategory.AI_ML);
        putSkill("TensorFlow", SkillCategory.AI_ML);
        putSkill("PyTorch", SkillCategory.AI_ML);
        putSkill("NLP", SkillCategory.AI_ML);
        putSkill("Computer Vision", SkillCategory.AI_ML);
        putSkill("OpenCV", SkillCategory.AI_ML);

        // Tools
        putSkill("Git", SkillCategory.TOOLS);
        putSkill("GitHub", SkillCategory.TOOLS);
        putSkill("GitLab", SkillCategory.TOOLS);
        putSkill("Maven", SkillCategory.TOOLS);
        putSkill("Postman", SkillCategory.TOOLS);
    }

    private static void putSkill(String name, SkillCategory category) {
        SKILL_DICTIONARY.put(name, category);
    }

    public SkillExtractionServiceImpl(ResumeService resumeService, ExtractedSkillRepository extractedSkillRepository) {
        this.resumeService = resumeService;
        this.extractedSkillRepository = extractedSkillRepository;
    }

    @Override
    public List<CategorizedSkillDTO> extractAndCategorizeSkills(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();
        Set<String> matchedNames = new LinkedHashSet<>();
        List<CategorizedSkillDTO> result = new ArrayList<>();

        for (Map.Entry<String, SkillCategory> entry : SKILL_DICTIONARY.entrySet()) {
            String skillName = entry.getKey();
            SkillCategory category = entry.getValue();

            // Word boundary regex check for case-insensitive matching
            String regex = "\\b" + Pattern.quote(skillName.toLowerCase()) + "\\b";
            if (Pattern.compile(regex).matcher(lowerText).find()) {
                if (!matchedNames.contains(skillName.toLowerCase())) {
                    matchedNames.add(skillName.toLowerCase());
                    result.add(new CategorizedSkillDTO(skillName, category));
                }
            }
        }

        return result;
    }

    @Override
    @Transactional
    public SkillExtractionResponse extractSkillsForResume(Long resumeId, Long userId) {
        Resume resume = resumeService.getResumeEntity(resumeId, userId);
        if (userId != null && !resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to access resume id: " + resumeId);
        }

        List<ExtractedSkillEntity> existing = extractedSkillRepository.findByResumeId(resumeId);
        if (!existing.isEmpty()) {
            List<CategorizedSkillDTO> dtoList = existing.stream()
                    .map(e -> new CategorizedSkillDTO(e.getSkillName(), e.getCategory()))
                    .collect(Collectors.toList());
            return new SkillExtractionResponse(resumeId, dtoList);
        }

        // Extract from disk
        File file = new File(resume.getFilePath());
        String text = extractText(file, resume.getFileName());
        List<CategorizedSkillDTO> extracted = extractAndCategorizeSkills(text);

        saveExtractedSkills(resume, extracted);

        return new SkillExtractionResponse(resumeId, extracted);
    }

    @Override
    @Transactional
    public void saveExtractedSkills(Resume resume, List<CategorizedSkillDTO> skills) {
        extractedSkillRepository.deleteByResumeId(resume.getId());
        for (CategorizedSkillDTO dto : skills) {
            ExtractedSkillEntity entity = new ExtractedSkillEntity(
                    resume,
                    dto.getName(),
                    dto.getCategory(),
                    1.0
            );
            extractedSkillRepository.save(entity);
        }
    }

    private String extractText(File file, String fileName) {
        try {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.Loader.loadPDF(file)) {
                    return new org.apache.pdfbox.text.PDFTextStripper().getText(doc);
                }
            } else {
                return new String(java.nio.file.Files.readAllBytes(file.toPath()));
            }
        } catch (Exception e) {
            return "";
        }
    }
}
