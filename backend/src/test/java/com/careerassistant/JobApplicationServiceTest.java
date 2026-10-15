package com.careerassistant;

import com.careerassistant.dto.ApplicationStatsResponse;
import com.careerassistant.dto.JobApplicationRequest;
import com.careerassistant.dto.JobApplicationResponse;
import com.careerassistant.entity.ApplicationStatus;
import com.careerassistant.entity.Job;
import com.careerassistant.entity.User;
import com.careerassistant.exception.InvalidFileException;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.JobApplicationRepository;
import com.careerassistant.repository.JobRepository;
import com.careerassistant.repository.UserRepository;
import com.careerassistant.service.JobApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JobApplicationServiceTest {

    @Autowired
    private JobApplicationService jobApplicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    private User appUser;
    private User otherUser;
    private Job testJob;

    @BeforeEach
    public void setUp() {
        if (!userRepository.existsByEmail("appuser@example.com")) {
            User u = new User();
            u.setName("Application User");
            u.setEmail("appuser@example.com");
            u.setPassword("Password123");
            appUser = userRepository.save(u);
        } else {
            appUser = userRepository.findByEmail("appuser@example.com").get();
        }

        if (!userRepository.existsByEmail("appother@example.com")) {
            User u2 = new User();
            u2.setName("Application Other User");
            u2.setEmail("appother@example.com");
            u2.setPassword("Password123");
            otherUser = userRepository.save(u2);
        } else {
            otherUser = userRepository.findByEmail("appother@example.com").get();
        }

        List<Job> jobs = jobRepository.findAll();
        if (jobs.isEmpty()) {
            Job j = new Job("Test Job", "Test Company", "Hyderabad", "Desc", "Java, Spring Boot", "10L", "Full-time", "2 yrs");
            testJob = jobRepository.save(j);
        } else {
            testJob = jobs.get(0);
        }
    }

    @Test
    public void testJobApplicationLifecycleAndDuplicatePrevention() {
        JobApplicationRequest req = new JobApplicationRequest(appUser.getId(), testJob.getId(), null, "Excited to apply");
        JobApplicationResponse response = jobApplicationService.applyForJob(req);

        assertNotNull(response);
        assertEquals(appUser.getId(), response.getUserId());
        assertEquals(testJob.getId(), response.getJobId());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());

        // Duplicate application check -> should throw InvalidFileException with duplicate message
        assertThrows(InvalidFileException.class, () -> {
            jobApplicationService.applyForJob(req);
        }, "Duplicate application should be rejected");

        // Verify User Applications list
        List<JobApplicationResponse> userApps = jobApplicationService.getUserApplications(appUser.getId());
        assertFalse(userApps.isEmpty());
        assertTrue(userApps.stream().anyMatch(a -> a.getId().equals(response.getId())));

        // Verify Status update
        JobApplicationResponse updated = jobApplicationService.updateApplicationStatus(response.getId(), appUser.getId(), ApplicationStatus.INTERVIEW);
        assertEquals(ApplicationStatus.INTERVIEW, updated.getStatus());

        // Verify Stats response
        ApplicationStatsResponse stats = jobApplicationService.getApplicationStats(appUser.getId());
        assertNotNull(stats);
        assertTrue(stats.getTotalApplications() >= 1);
        assertTrue(stats.getInterviews() >= 1);
    }

    @Test
    public void testUnauthorizedApplicationAccessReturnsForbidden() {
        JobApplicationRequest req = new JobApplicationRequest(appUser.getId(), testJob.getId(), null, "Notes");
        JobApplicationResponse response = jobApplicationService.applyForJob(req);

        // Other user attempts to update status -> HTTP 403 / UnauthorizedAccessException
        assertThrows(UnauthorizedAccessException.class, () -> {
            jobApplicationService.updateApplicationStatus(response.getId(), otherUser.getId(), ApplicationStatus.SHORTLISTED);
        }, "Unauthorized user must be rejected with UnauthorizedAccessException (HTTP 403)");
    }
}
