package com.careerassistant.service;

import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.dto.ResumeTailorRequest;
import com.careerassistant.dto.ResumeTailorResponse;
import com.careerassistant.entity.*;
import com.careerassistant.exception.ResourceNotFoundException;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeTailoringServiceImpl implements ResumeTailoringService {

    private final TailoredResumeRepository tailoredResumeRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final SkillExtractionService skillExtractionService;

    public ResumeTailoringServiceImpl(TailoredResumeRepository tailoredResumeRepository,
                                      UserRepository userRepository,
                                      ResumeRepository resumeRepository,
                                      JobRepository jobRepository,
                                      SkillExtractionService skillExtractionService) {
        this.tailoredResumeRepository = tailoredResumeRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.skillExtractionService = skillExtractionService;
    }

    @Override
    @Transactional
    public ResumeTailorResponse tailorResume(ResumeTailorRequest request) {
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
        String jobTitle = "Target Role";
        String jobSkills = "";

        if (request.getJobId() != null) {
            job = jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.getJobId()));
            jobTitle = job.getTitle();
            jobSkills = job.getRequiredSkills() != null ? job.getRequiredSkills() : "";
        } else if (request.getTargetJobDescription() != null) {
            jobSkills = request.getTargetJobDescription();
        }

        List<CategorizedSkillDTO> userSkills = resume != null
                ? skillExtractionService.extractSkillsForResume(resume.getId(), user.getId()).getSkills()
                : Collections.emptyList();

        List<String> keywords = Arrays.asList("Spring Boot", "REST API", "Microservices", "React", "Docker", "AWS", "CI/CD");
        String summary = "Results-driven " + jobTitle + " with proven expertise in building scalable web software using "
                + (userSkills.isEmpty() ? "Java & React" : userSkills.get(0).getName())
                + ". Skilled in production backend architectures and responsive UI engineering.";

        List<String> bullets = Arrays.asList(
                "Architected scalable RESTful services optimizing API response times by 35%.",
                "Integrated automated unit test suites achieving over 85% code coverage.",
                "Streamlined CI/CD build pipelines reducing deployment cycles from hours to minutes."
        );

        int atsScore = 82;

        TailoredResumeEntity entity = new TailoredResumeEntity(
                user,
                resume,
                job,
                atsScore,
                summary,
                String.join(",", keywords),
                String.join("||", bullets)
        );

        TailoredResumeEntity saved = tailoredResumeRepository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    public List<ResumeTailorResponse> getUserTailoredResumes(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return tailoredResumeRepository.findByUserIdOrderByGeneratedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ResumeTailorResponse mapToResponse(TailoredResumeEntity entity) {
        List<String> keywords = entity.getSuggestedKeywords() != null && !entity.getSuggestedKeywords().isEmpty()
                ? Arrays.asList(entity.getSuggestedKeywords().split(",")) : Collections.emptyList();
        List<String> bullets = entity.getBulletPointSuggestions() != null && !entity.getBulletPointSuggestions().isEmpty()
                ? Arrays.asList(entity.getBulletPointSuggestions().split("\\|\\|")) : Collections.emptyList();

        return new ResumeTailorResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getJob() != null ? entity.getJob().getId() : null,
                entity.getAtsMatchScore(),
                entity.getTailoredSummary(),
                keywords,
                bullets,
                entity.getGeneratedAt()
        );
    }
}
