package com.careerassistant;

import com.careerassistant.dto.JobMatchResponse;
import com.careerassistant.dto.JobResponse;
import com.careerassistant.entity.Job;
import com.careerassistant.repository.JobRepository;
import com.careerassistant.service.JobServiceImpl;
import com.careerassistant.service.SkillExtractionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class JobServiceTest {

    private JobRepository jobRepository;
    private SkillExtractionService skillExtractionService;
    private JobServiceImpl jobService;

    @BeforeEach
    public void setUp() {
        jobRepository = Mockito.mock(JobRepository.class);
        skillExtractionService = Mockito.mock(SkillExtractionService.class);
        jobService = new JobServiceImpl(jobRepository, null, skillExtractionService);
    }

    @Test
    public void testGetAllJobsAndSearch() {
        Job j1 = new Job("Java Developer", "Tech", "Hyderabad", "Desc", "Java, Spring Boot, MySQL", "10L", "Full-time", "2 yrs");
        j1.setId(1L);

        when(jobRepository.findAll()).thenReturn(List.of(j1));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(j1));

        List<JobResponse> jobs = jobService.getAllJobs();
        assertFalse(jobs.isEmpty());
        assertEquals("Java Developer", jobs.get(0).getTitle());

        JobResponse single = jobService.getJobById(1L);
        assertNotNull(single);
        assertEquals("Tech", single.getCompanyName());
    }

    @Test
    public void testJobSkillMatchCalculation() {
        Job j = new Job("Full Stack Dev", "Corp", "Bengaluru", "Desc", "Java, Spring Boot, MySQL, Docker, AWS", "12L", "Full-time", "3 yrs");
        j.setId(2L);
        when(jobRepository.findAll()).thenReturn(List.of(j));

        // Search with no resume skill matching
        List<JobMatchResponse> matches = jobService.searchJobs(null, null, null, null, null, null);
        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertEquals(2L, matches.get(0).getJobId());
    }
}
