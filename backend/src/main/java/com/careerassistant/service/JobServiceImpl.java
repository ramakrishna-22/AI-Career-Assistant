package com.careerassistant.service;

import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.dto.JobMatchResponse;
import com.careerassistant.dto.JobResponse;
import com.careerassistant.entity.Job;
import com.careerassistant.entity.Resume;
import com.careerassistant.exception.ResourceNotFoundException;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.JobRepository;
import com.careerassistant.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final SkillExtractionService skillExtractionService;

    public JobServiceImpl(JobRepository jobRepository,
                          ResumeRepository resumeRepository,
                          SkillExtractionService skillExtractionService) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.skillExtractionService = skillExtractionService;
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return mapToJobResponse(job);
    }

    @Override
    public List<JobMatchResponse> searchJobs(String keyword, String location, String employmentType,
                                             Integer minMatchScore, Long resumeId, Long userId) {
        List<Job> candidateJobs;

        if (keyword != null && !keyword.trim().isEmpty()) {
            candidateJobs = jobRepository.findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                    keyword.trim(), keyword.trim(), keyword.trim()
            );
        } else {
            candidateJobs = jobRepository.findAll();
        }

        // Apply Location Filter
        if (location != null && !location.trim().isEmpty() && !location.equalsIgnoreCase("All")) {
            candidateJobs = candidateJobs.stream()
                    .filter(j -> j.getLocation().toLowerCase().contains(location.trim().toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Apply Employment Type Filter
        if (employmentType != null && !employmentType.trim().isEmpty() && !employmentType.equalsIgnoreCase("All")) {
            candidateJobs = candidateJobs.stream()
                    .filter(j -> j.getEmploymentType().equalsIgnoreCase(employmentType.trim()))
                    .collect(Collectors.toList());
        }

        List<CategorizedSkillDTO> userSkills = getUserSkills(resumeId, userId);

        List<JobMatchResponse> matches = candidateJobs.stream()
                .map(job -> calculateMatch(job, userSkills))
                .collect(Collectors.toList());

        // Apply Minimum Match Score Filter
        if (minMatchScore != null && minMatchScore > 0) {
            matches = matches.stream()
                    .filter(m -> m.getMatchScore() >= minMatchScore)
                    .collect(Collectors.toList());
        }

        // Sort by match score descending
        matches.sort(Comparator.comparingInt(JobMatchResponse::getMatchScore).reversed());

        return matches;
    }

    @Override
    public List<JobMatchResponse> getRecommendedJobsForUser(Long userId, int limit) {
        List<JobMatchResponse> matches = matchJobsForResume(null, userId);
        return matches.stream().limit(limit > 0 ? limit : 5).collect(Collectors.toList());
    }

    @Override
    public List<JobMatchResponse> matchJobsForResume(Long resumeId, Long userId) {
        List<CategorizedSkillDTO> userSkills = getUserSkills(resumeId, userId);
        List<Job> allJobs = jobRepository.findAll();

        List<JobMatchResponse> matches = allJobs.stream()
                .map(job -> calculateMatch(job, userSkills))
                .collect(Collectors.toList());

        matches.sort(Comparator.comparingInt(JobMatchResponse::getMatchScore).reversed());
        return matches;
    }

    private List<CategorizedSkillDTO> getUserSkills(Long resumeId, Long userId) {
        if (userId == null && resumeId == null) {
            return Collections.emptyList();
        }

        List<Resume> userResumes;
        if (resumeId != null) {
            Resume res = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));
            if (userId != null && !res.getUser().getId().equals(userId)) {
                throw new UnauthorizedAccessException("Unauthorized access to resume id: " + resumeId);
            }
            userResumes = Collections.singletonList(res);
        } else {
            userResumes = resumeRepository.findByUserIdOrderByUploadedAtDesc(userId);
        }

        if (userResumes.isEmpty()) {
            return Collections.emptyList();
        }

        Resume targetResume = userResumes.get(0);
        return skillExtractionService.extractSkillsForResume(targetResume.getId(), targetResume.getUser().getId()).getSkills();
    }

    private JobMatchResponse calculateMatch(Job job, List<CategorizedSkillDTO> userSkills) {
        List<String> requiredList = parseSkills(job.getRequiredSkills());
        if (requiredList.isEmpty()) {
            return mapToJobMatchResponse(job, 100, Collections.emptyList(), Collections.emptyList());
        }

        Set<String> userSkillSet = userSkills.stream()
                .map(s -> s.getName().toLowerCase())
                .collect(Collectors.toSet());

        List<String> matching = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String req : requiredList) {
            if (userSkillSet.contains(req.toLowerCase())) {
                matching.add(req);
            } else {
                missing.add(req);
            }
        }

        int matchScore = (int) Math.round(((double) matching.size() / requiredList.size()) * 100);

        return mapToJobMatchResponse(job, matchScore, matching, missing);
    }

    private List<String> parseSkills(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(commaSeparated.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private JobResponse mapToJobResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getCompanyName(),
                job.getLocation(),
                job.getDescription(),
                parseSkills(job.getRequiredSkills()),
                job.getSalary(),
                job.getEmploymentType(),
                job.getExperienceRequired(),
                job.getCreatedAt()
        );
    }

    private JobMatchResponse mapToJobMatchResponse(Job job, int score, List<String> matching, List<String> missing) {
        return new JobMatchResponse(
                job.getId(),
                job.getTitle(),
                job.getCompanyName(),
                job.getLocation(),
                job.getDescription(),
                job.getSalary(),
                job.getEmploymentType(),
                job.getExperienceRequired(),
                score,
                matching,
                missing,
                job.getCreatedAt()
        );
    }
}
