package com.careerassistant;

import com.careerassistant.dto.SkillGapRequest;
import com.careerassistant.dto.SkillGapResponse;
import com.careerassistant.entity.User;
import com.careerassistant.repository.UserRepository;
import com.careerassistant.service.SkillGapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SkillGapServiceTest {

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        if (!userRepository.existsByEmail("skillgap@example.com")) {
            User u = new User();
            u.setName("Skill Gap User");
            u.setEmail("skillgap@example.com");
            u.setPassword("Password123");
            testUser = userRepository.save(u);
        } else {
            testUser = userRepository.findByEmail("skillgap@example.com").get();
        }
    }

    @Test
    public void testAnalyzeSkillGapAndLearningRoadmap() {
        SkillGapRequest req = new SkillGapRequest(testUser.getId(), null, null, "Senior Java Developer");
        SkillGapResponse response = skillGapService.analyzeSkillGap(req);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals("Senior Java Developer", response.getTargetRole());
        assertTrue(response.getReadinessScore() >= 0 && response.getReadinessScore() <= 100);
        assertNotNull(response.getLearningRoadmap());
        assertFalse(response.getLearningRoadmap().isEmpty());

        List<SkillGapResponse> userAnalyses = skillGapService.getUserAnalyses(testUser.getId());
        assertFalse(userAnalyses.isEmpty());
    }
}
