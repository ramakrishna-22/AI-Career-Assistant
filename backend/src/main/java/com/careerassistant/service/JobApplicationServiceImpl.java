package com.careerassistant.service;

import com.careerassistant.dto.ApplicationStatsResponse;
import com.careerassistant.dto.JobApplicationRequest;
import com.careerassistant.dto.JobApplicationResponse;
import com.careerassistant.entity.*;
import com.careerassistant.exception.InvalidFileException;
import com.careerassistant.exception.ResourceNotFoundException;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.JobApplicationRepository;
import com.careerassistant.repository.JobRepository;
import com.careerassistant.repository.ResumeRepository;
import com.careerassistant.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository,
                                     JobRepository jobRepository,
                                     UserRepository userRepository,
                                     ResumeRepository resumeRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
    }

    @Override
    @Transactional
    public JobApplicationResponse applyForJob(JobApplicationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.getJobId()));

        // Prevent duplicate applications for the same job
        if (jobApplicationRepository.existsByUserIdAndJobId(user.getId(), job.getId())) {
            throw new InvalidFileException("You have already applied for this job.");
        }

        Resume resume = null;
        if (request.getResumeId() != null) {
            resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + request.getResumeId()));
            if (!resume.getUser().getId().equals(user.getId())) {
                throw new UnauthorizedAccessException("Unauthorized access to resume id: " + request.getResumeId());
            }
        } else {
            List<Resume> userResumes = resumeRepository.findByUserIdOrderByUploadedAtDesc(user.getId());
            if (!userResumes.isEmpty()) {
                resume = userResumes.get(0);
            }
        }

        JobApplication application = new JobApplication(
                user,
                job,
                resume,
                ApplicationStatus.APPLIED,
                request.getNotes()
        );

        JobApplication saved = jobApplicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Override
    public List<JobApplicationResponse> getUserApplications(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return jobApplicationRepository.findByUserIdOrderByAppliedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicationResponse getApplicationById(Long applicationId, Long userId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (userId != null && !application.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to view application id: " + applicationId);
        }

        return mapToResponse(application);
    }

    @Override
    @Transactional
    public JobApplicationResponse updateApplicationStatus(Long applicationId, Long userId, ApplicationStatus status) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (userId != null && !application.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to update application id: " + applicationId);
        }

        application.setStatus(status);
        JobApplication updated = jobApplicationRepository.save(application);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteApplication(Long applicationId, Long userId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (userId != null && !application.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete application id: " + applicationId);
        }

        jobApplicationRepository.delete(application);
    }

    @Override
    public ApplicationStatsResponse getApplicationStats(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        long total = jobApplicationRepository.countByUserId(userId);
        long applied = jobApplicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.APPLIED);
        long shortlisted = jobApplicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.SHORTLISTED);
        long interviews = jobApplicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.INTERVIEW);
        long selected = jobApplicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.SELECTED);
        long rejected = jobApplicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.REJECTED);
        long withdrawn = jobApplicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.WITHDRAWN);

        return new ApplicationStatsResponse(total, applied, shortlisted, interviews, selected, rejected, withdrawn);
    }

    private JobApplicationResponse mapToResponse(JobApplication app) {
        return new JobApplicationResponse(
                app.getId(),
                app.getUser().getId(),
                app.getJob().getId(),
                app.getResume() != null ? app.getResume().getId() : null,
                app.getJob().getTitle(),
                app.getJob().getCompanyName(),
                app.getJob().getLocation(),
                app.getJob().getSalary(),
                app.getStatus(),
                app.getNotes(),
                app.getAppliedAt(),
                app.getUpdatedAt()
        );
    }
}
