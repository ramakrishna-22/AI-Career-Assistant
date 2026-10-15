package com.careerassistant.service;

import com.careerassistant.dto.ApplicationStatsResponse;
import com.careerassistant.dto.JobApplicationRequest;
import com.careerassistant.dto.JobApplicationResponse;
import com.careerassistant.entity.ApplicationStatus;

import java.util.List;

public interface JobApplicationService {

    JobApplicationResponse applyForJob(JobApplicationRequest request);

    List<JobApplicationResponse> getUserApplications(Long userId);

    JobApplicationResponse getApplicationById(Long applicationId, Long userId);

    JobApplicationResponse updateApplicationStatus(Long applicationId, Long userId, ApplicationStatus status);

    void deleteApplication(Long applicationId, Long userId);

    ApplicationStatsResponse getApplicationStats(Long userId);
}
