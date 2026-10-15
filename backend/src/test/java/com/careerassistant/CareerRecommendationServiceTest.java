package com.careerassistant;

import com.careerassistant.dto.CareerRecommendationDTO;
import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.entity.SkillCategory;
import com.careerassistant.service.CareerRecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CareerRecommendationServiceTest {

    private CareerRecommendationServiceImpl careerRecommendationService;

    @BeforeEach
    public void setUp() {
        careerRecommendationService = new CareerRecommendationServiceImpl(null, null, null);
    }

    @Test
    public void testCareerRecommendationMatchingAndSorting() {
        List<CategorizedSkillDTO> skills = Arrays.asList(
                new CategorizedSkillDTO("Java", SkillCategory.PROGRAMMING),
                new CategorizedSkillDTO("Spring Boot", SkillCategory.BACKEND),
                new CategorizedSkillDTO("REST API", SkillCategory.BACKEND),
                new CategorizedSkillDTO("React", SkillCategory.FRONTEND),
                new CategorizedSkillDTO("HTML", SkillCategory.FRONTEND),
                new CategorizedSkillDTO("CSS", SkillCategory.FRONTEND),
                new CategorizedSkillDTO("MySQL", SkillCategory.DATABASE),
                new CategorizedSkillDTO("Git", SkillCategory.TOOLS)
        );

        List<CareerRecommendationDTO> recommendations = careerRecommendationService.generateRecommendations(skills);

        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty(), "Recommendations list should not be empty");

        // Top recommendation should be Java Full Stack Developer
        CareerRecommendationDTO topRec = recommendations.get(0);
        assertEquals("Java Full Stack Developer", topRec.getCareer());
        assertTrue(topRec.getMatchScore() >= 70, "Match score for Java Full Stack Developer should be high");

        // Verify sorting by matchScore descending
        for (int i = 0; i < recommendations.size() - 1; i++) {
            assertTrue(recommendations.get(i).getMatchScore() >= recommendations.get(i + 1).getMatchScore(),
                    "Recommendations must be sorted by matchScore in descending order");
        }
    }
}
