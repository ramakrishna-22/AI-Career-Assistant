package com.careerassistant.service;

import com.careerassistant.dto.CareerRecommendationDTO;
import com.careerassistant.dto.CareerRecommendationListResponse;
import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.entity.CareerRecommendationEntity;
import com.careerassistant.entity.Resume;
import com.careerassistant.exception.UnauthorizedAccessException;
import com.careerassistant.repository.CareerRecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CareerRecommendationServiceImpl implements CareerRecommendationService {

    private final ResumeService resumeService;
    private final SkillExtractionService skillExtractionService;
    private final CareerRecommendationRepository careerRecommendationRepository;

    private static class RoleProfile {
        String roleName;
        List<String> requiredSkills;

        RoleProfile(String roleName, List<String> requiredSkills) {
            this.roleName = roleName;
            this.requiredSkills = requiredSkills;
        }
    }

    private static final List<RoleProfile> ROLE_PROFILES = Arrays.asList(
            new RoleProfile("Java Full Stack Developer", Arrays.asList("Java", "Spring Boot", "REST API", "React", "HTML", "CSS", "JavaScript", "MySQL")),
            new RoleProfile("Python Full Stack Developer", Arrays.asList("Python", "Django", "Flask", "REST API", "SQL", "React", "HTML", "CSS", "Git")),
            new RoleProfile("Backend Developer", Arrays.asList("Java", "Spring Boot", "Node.js", "REST API", "SQL", "MySQL", "PostgreSQL")),
            new RoleProfile("Frontend Developer", Arrays.asList("JavaScript", "React", "HTML", "CSS", "Bootstrap", "TypeScript", "Angular")),
            new RoleProfile("Software Engineer", Arrays.asList("Java", "Python", "JavaScript", "SQL", "Git", "REST API")),
            new RoleProfile("Cloud Engineer", Arrays.asList("AWS", "Azure", "GCP", "EC2", "S3", "IAM", "Docker", "Kubernetes")),
            new RoleProfile("DevOps Engineer", Arrays.asList("Docker", "Kubernetes", "Jenkins", "CI/CD", "Git", "AWS")),
            new RoleProfile("Data Analyst", Arrays.asList("Python", "SQL", "MySQL", "PostgreSQL")),
            new RoleProfile("Data Scientist", Arrays.asList("Python", "Machine Learning", "Deep Learning", "SQL", "TensorFlow", "PyTorch")),
            new RoleProfile("AI/ML Engineer", Arrays.asList("Python", "Machine Learning", "Deep Learning", "TensorFlow", "PyTorch", "SQL", "NLP", "Computer Vision", "OpenCV"))
    );

    public CareerRecommendationServiceImpl(ResumeService resumeService,
                                            SkillExtractionService skillExtractionService,
                                            CareerRecommendationRepository careerRecommendationRepository) {
        this.resumeService = resumeService;
        this.skillExtractionService = skillExtractionService;
        this.careerRecommendationRepository = careerRecommendationRepository;
    }

    @Override
    public List<CareerRecommendationDTO> generateRecommendations(List<CategorizedSkillDTO> userSkills) {
        if (userSkills == null || userSkills.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> userSkillSet = userSkills.stream()
                .map(s -> s.getName().toLowerCase())
                .collect(Collectors.toSet());

        List<CareerRecommendationDTO> recommendations = new ArrayList<>();

        for (RoleProfile role : ROLE_PROFILES) {
            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            for (String req : role.requiredSkills) {
                if (userSkillSet.contains(req.toLowerCase())) {
                    matched.add(req);
                } else {
                    missing.add(req);
                }
            }

            int matchScore = (int) Math.round(((double) matched.size() / role.requiredSkills.size()) * 100);

            if (matchScore > 0) {
                String reason = buildReasonText(role.roleName, matched, missing);
                recommendations.add(new CareerRecommendationDTO(role.roleName, matchScore, reason));
            }
        }

        // Sort descending by match score
        recommendations.sort(Comparator.comparingInt(CareerRecommendationDTO::getMatchScore).reversed());

        // Return top recommendations
        return recommendations.stream().limit(5).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CareerRecommendationListResponse getCareerRecommendationsForResume(Long resumeId, Long userId) {
        Resume resume = resumeService.getResumeEntity(resumeId, userId);
        if (userId != null && !resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to access resume id: " + resumeId);
        }

        List<CareerRecommendationEntity> existing = careerRecommendationRepository.findByResumeIdOrderByMatchScoreDesc(resumeId);
        if (!existing.isEmpty()) {
            List<CareerRecommendationDTO> dtoList = existing.stream()
                    .map(e -> new CareerRecommendationDTO(e.getCareerName(), e.getMatchScore(), e.getReason()))
                    .collect(Collectors.toList());
            return new CareerRecommendationListResponse(resumeId, dtoList);
        }

        // Extract skills and generate recommendations
        List<CategorizedSkillDTO> userSkills = skillExtractionService.extractSkillsForResume(resumeId, userId).getSkills();
        List<CareerRecommendationDTO> generated = generateRecommendations(userSkills);

        saveCareerRecommendations(resume, generated);

        return new CareerRecommendationListResponse(resumeId, generated);
    }

    @Override
    @Transactional
    public void saveCareerRecommendations(Resume resume, List<CareerRecommendationDTO> recommendations) {
        careerRecommendationRepository.deleteByResumeId(resume.getId());
        for (CareerRecommendationDTO dto : recommendations) {
            CareerRecommendationEntity entity = new CareerRecommendationEntity(
                    resume,
                    dto.getCareer(),
                    dto.getMatchScore(),
                    dto.getReason()
            );
            careerRecommendationRepository.save(entity);
        }
    }

    private String buildReasonText(String roleName, List<String> matched, List<String> missing) {
        StringBuilder sb = new StringBuilder("Strong alignment in ");
        if (!matched.isEmpty()) {
            sb.append(String.join(", ", matched));
        } else {
            sb.append("foundational domain concepts");
        }
        if (!missing.isEmpty() && missing.size() <= 3) {
            sb.append(". Additional growth area: ").append(String.join(", ", missing)).append(".");
        } else {
            sb.append(".");
        }
        return sb.toString();
    }
}
