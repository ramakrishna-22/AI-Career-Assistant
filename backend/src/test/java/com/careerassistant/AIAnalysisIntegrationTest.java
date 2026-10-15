package com.careerassistant;

import com.careerassistant.dto.CompleteAIAnalysisResponse;
import com.careerassistant.entity.Resume;
import com.careerassistant.entity.User;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.CareerRecommendationRepository;
import com.careerassistant.repository.ExtractedSkillRepository;
import com.careerassistant.repository.ResumeAnalysisRepository;
import com.careerassistant.repository.UserRepository;
import com.careerassistant.service.AIAnalysisService;
import com.careerassistant.service.ResumeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AIAnalysisIntegrationTest {

    @Autowired
    private AIAnalysisService aiAnalysisService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @Autowired
    private ExtractedSkillRepository extractedSkillRepository;

    @Autowired
    private CareerRecommendationRepository careerRecommendationRepository;

    private User ownerUser;
    private User otherUser;
    private Long uploadedResumeId;

    @BeforeEach
    public void setUp() throws Exception {
        if (!userRepository.existsByEmail("aiowner@example.com")) {
            User u = new User();
            u.setName("AI Owner User");
            u.setEmail("aiowner@example.com");
            u.setPassword("Password123");
            ownerUser = userRepository.save(u);
        } else {
            ownerUser = userRepository.findByEmail("aiowner@example.com").get();
        }

        if (!userRepository.existsByEmail("aiother@example.com")) {
            User u2 = new User();
            u2.setName("AI Other User");
            u2.setEmail("aiother@example.com");
            u2.setPassword("Password123");
            otherUser = userRepository.save(u2);
        } else {
            otherUser = userRepository.findByEmail("aiother@example.com").get();
        }

        Path pdfPath = Paths.get("E:/AI-Career-Assistant/sample/real_sample_resume.pdf");
        assertTrue(Files.exists(pdfPath));
        byte[] bytes = Files.readAllBytes(pdfPath);

        MockMultipartFile file = new MockMultipartFile("file", "real_sample_resume.pdf", "application/pdf", bytes);
        uploadedResumeId = resumeService.uploadResume(file, ownerUser.getId()).getId();
    }

    @Test
    public void testCompleteAIAnalysisPipelineAndPersistence() {
        // Owner performs complete analysis
        CompleteAIAnalysisResponse response = aiAnalysisService.performCompleteAnalysis(uploadedResumeId, ownerUser.getId());

        assertNotNull(response);
        assertEquals(uploadedResumeId, response.getResumeId());
        assertNotNull(response.getName());
        assertEquals("ram@example.com", response.getEmail());

        // Verify skills extracted & categorized
        assertNotNull(response.getSkills());
        assertFalse(response.getSkills().isEmpty(), "Skills list should not be empty");

        // Verify career recommendations generated
        assertNotNull(response.getCareerRecommendations());
        assertFalse(response.getCareerRecommendations().isEmpty(), "Career recommendations should not be empty");

        // Verify database persistence across the 3 new tables
        assertTrue(resumeAnalysisRepository.findTopByResumeIdOrderByCreatedAtDesc(uploadedResumeId).isPresent());
        assertFalse(extractedSkillRepository.findByResumeId(uploadedResumeId).isEmpty());
        assertFalse(careerRecommendationRepository.findByResumeIdOrderByMatchScoreDesc(uploadedResumeId).isEmpty());
    }

    @Test
    public void testUnauthorizedUserAccessReturnsForbidden() {
        // Unauthorized user attempts to analyze owner's resume -> HTTP 403 / UnauthorizedAccessException
        assertThrows(UnauthorizedAccessException.class, () -> {
            aiAnalysisService.performCompleteAnalysis(uploadedResumeId, otherUser.getId());
        }, "Unauthorized user must be rejected with UnauthorizedAccessException (HTTP 403)");
    }
}
