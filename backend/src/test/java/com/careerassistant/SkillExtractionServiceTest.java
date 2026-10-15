package com.careerassistant;

import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.entity.SkillCategory;
import com.careerassistant.service.SkillExtractionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SkillExtractionServiceTest {

    private SkillExtractionServiceImpl skillExtractionService;

    @BeforeEach
    public void setUp() {
        skillExtractionService = new SkillExtractionServiceImpl(null, null);
    }

    @Test
    public void testSkillExtractionAndCategorization() {
        String text = "I am skilled in Java, Python, React, Spring Boot, MySQL, AWS, Docker, and Git.";
        List<CategorizedSkillDTO> skills = skillExtractionService.extractAndCategorizeSkills(text);

        assertNotNull(skills);
        assertTrue(skills.size() >= 8, "Should extract at least 8 skills");

        List<String> names = skills.stream().map(CategorizedSkillDTO::getName).toList();
        assertTrue(names.contains("Java"));
        assertTrue(names.contains("Python"));
        assertTrue(names.contains("React"));
        assertTrue(names.contains("Spring Boot"));
        assertTrue(names.contains("MySQL"));
        assertTrue(names.contains("AWS"));
        assertTrue(names.contains("Docker"));
        assertTrue(names.contains("Git"));
    }

    @Test
    public void testCaseInsensitivityAndDeduplication() {
        String text = "java JAVA Java java";
        List<CategorizedSkillDTO> skills = skillExtractionService.extractAndCategorizeSkills(text);

        assertNotNull(skills);
        long javaCount = skills.stream().filter(s -> s.getName().equalsIgnoreCase("Java")).count();
        assertEquals(1, javaCount, "Deduplication must result in exactly 1 Java skill");
        assertEquals(SkillCategory.PROGRAMMING, skills.get(0).getCategory());
    }

    @Test
    public void testNoSkillsFound() {
        String text = "Simple text with no tech keywords.";
        List<CategorizedSkillDTO> skills = skillExtractionService.extractAndCategorizeSkills(text);
        assertNotNull(skills);
        assertTrue(skills.isEmpty());
    }
}
