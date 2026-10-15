package com.careerassistant.service;

import com.careerassistant.dto.JobMatchResponse;
import com.careerassistant.dto.JobResponse;

import java.util.List;

public interface JobService {

    List<JobResponse> getAllJobs();

    JobResponse getJobById(Long id);

    List<JobMatchResponse> searchJobs(String keyword, String location, String employmentType, Integer minMatchScore, Long resumeId, Long userId);

    List<JobMatchResponse> getRecommendedJobsForUser(Long userId, int limit);

    List<JobMatchResponse> matchJobsForResume(Long resumeId, Long userId);
}
