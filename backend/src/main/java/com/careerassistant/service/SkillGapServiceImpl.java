package com.careerassistant.service;

import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.dto.SkillGapRequest;
import com.careerassistant.dto.SkillGapResponse;
import com.careerassistant.entity.*;
import com.careerassistant.exception.ResourceNotFoundException;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillGapServiceImpl implements SkillGapService {

    private final SkillGapAnalysisRepository skillGapAnalysisRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final SkillExtractionService skillExtractionService;

    public SkillGapServiceImpl(SkillGapAnalysisRepository skillGapAnalysisRepository,
                               UserRepository userRepository,
                               ResumeRepository resumeRepository,
                               JobRepository jobRepository,
                               SkillExtractionService skillExtractionService) {
        this.skillGapAnalysisRepository = skillGapAnalysisRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.skillExtractionService = skillExtractionService;
    }

    @Override
    @Transactional
    public SkillGapResponse analyzeSkillGap(SkillGapRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

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

        Job job = null;
        String roleName = request.getTargetRole() != null ? request.getTargetRole() : "Software Engineer";

        if (request.getJobId() != null) {
            job = jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.getJobId()));
            roleName = job.getTitle();
        }

        List<String> userSkills = new ArrayList<>();
        if (resume != null) {
            userSkills = skillExtractionService.extractSkillsForResume(resume.getId(), user.getId())
                    .getSkills().stream()
                    .map(CategorizedSkillDTO::getName)
                    .collect(Collectors.toList());
        }

        List<String> targetSkills = getTargetSkills(job, roleName);
        Set<String> userSkillSet = userSkills.stream().map(String::toLowerCase).collect(Collectors.toSet());

        List<String> matching = new ArrayList<>();
        List<String> criticalGaps = new ArrayList<>();
        List<String> secondaryGaps = new ArrayList<>();

        for (int i = 0; i < targetSkills.size(); i++) {
            String sk = targetSkills.get(i);
            if (userSkillSet.contains(sk.toLowerCase())) {
                matching.add(sk);
            } else if (i < 3) {
                criticalGaps.add(sk);
            } else {
                secondaryGaps.add(sk);
            }
        }

        int readinessScore = targetSkills.isEmpty() ? 100 : (int) Math.round(((double) matching.size() / targetSkills.size()) * 100);

        List<String> roadmap = new ArrayList<>();
        if (!criticalGaps.isEmpty()) {
            roadmap.add("Phase 1 (Weeks 1-2): Master critical core requirements: " + String.join(", ", criticalGaps));
        }
        if (!secondaryGaps.isEmpty()) {
            roadmap.add("Phase 2 (Weeks 3-4): Build hands-on portfolio projects with " + String.join(", ", secondaryGaps));
        }
        roadmap.add("Phase 3 (Week 5): Prepare system design & technical interview practice for " + roleName);

        SkillGapAnalysisEntity entity = new SkillGapAnalysisEntity(
                user,
                resume,
                job,
                roleName,
                readinessScore,
                String.join(",", matching),
                String.join(",", criticalGaps),
                String.join(",", secondaryGaps),
                String.join("||", roadmap)
        );

        SkillGapAnalysisEntity saved = skillGapAnalysisRepository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    public List<SkillGapResponse> getUserAnalyses(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return skillGapAnalysisRepository.findByUserIdOrderByAnalyzedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private List<String> getTargetSkills(Job job, String roleName) {
        if (job != null && job.getRequiredSkills() != null && !job.getRequiredSkills().trim().isEmpty()) {
            return Arrays.stream(job.getRequiredSkills().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        // Fallback default target skills based on role name
        if (roleName.toLowerCase().contains("react") || roleName.toLowerCase().contains("frontend")) {
            return Arrays.asList("JavaScript", "React", "HTML", "CSS", "TypeScript", "Redux");
        } else if (roleName.toLowerCase().contains("python") || roleName.toLowerCase().contains("data")) {
            return Arrays.asList("Python", "SQL", "Django", "PostgreSQL", "Docker", "Git");
        }
        return Arrays.asList("Java", "Spring Boot", "REST API", "MySQL", "React", "Docker", "AWS");
    }

    private SkillGapResponse mapToResponse(SkillGapAnalysisEntity entity) {
        List<String> matching = entity.getMatchingSkills() != null && !entity.getMatchingSkills().isEmpty()
                ? Arrays.asList(entity.getMatchingSkills().split(",")) : Collections.emptyList();
        List<String> critical = entity.getCriticalGaps() != null && !entity.getCriticalGaps().isEmpty()
                ? Arrays.asList(entity.getCriticalGaps().split(",")) : Collections.emptyList();
        List<String> secondary = entity.getSecondaryGaps() != null && !entity.getSecondaryGaps().isEmpty()
                ? Arrays.asList(entity.getSecondaryGaps().split(",")) : Collections.emptyList();
        List<String> roadmap = entity.getLearningRoadmap() != null && !entity.getLearningRoadmap().isEmpty()
                ? Arrays.asList(entity.getLearningRoadmap().split("\\|\\|")) : Collections.emptyList();

        return new SkillGapResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTargetRole(),
                entity.getReadinessScore(),
                matching,
                critical,
                secondary,
                roadmap,
                entity.getAnalyzedAt()
        );
    }
}
