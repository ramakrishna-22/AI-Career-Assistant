package com.careerassistant;

import com.careerassistant.dto.ResumeTailorRequest;
import com.careerassistant.dto.ResumeTailorResponse;
import com.careerassistant.entity.User;
import com.careerassistant.repository.UserRepository;
import com.careerassistant.service.ResumeTailoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ResumeTailoringServiceTest {

    @Autowired
    private ResumeTailoringService resumeTailoringService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        if (!userRepository.existsByEmail("tailor@example.com")) {
            User u = new User();
            u.setName("Tailor User");
            u.setEmail("tailor@example.com");
            u.setPassword("Password123");
            testUser = userRepository.save(u);
        } else {
            testUser = userRepository.findByEmail("tailor@example.com").get();
        }
    }

    @Test
    public void testTailorResumeAndAtsMatchScore() {
        ResumeTailorRequest req = new ResumeTailorRequest(testUser.getId(), null, null, "Targeting Java Microservices Role");
        ResumeTailorResponse response = resumeTailoringService.tailorResume(req);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertTrue(response.getAtsMatchScore() > 50);
        assertNotNull(response.getTailoredSummary());
        assertNotNull(response.getSuggestedKeywords());

        List<ResumeTailorResponse> userTailored = resumeTailoringService.getUserTailoredResumes(testUser.getId());
        assertFalse(userTailored.isEmpty());
    }
}
