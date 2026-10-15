package com.careerassistant.service;

import com.careerassistant.dto.CareerRecommendationDTO;
import com.careerassistant.dto.CareerRecommendationListResponse;
import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.entity.Resume;

import java.util.List;

public interface CareerRecommendationService {

    List<CareerRecommendationDTO> generateRecommendations(List<CategorizedSkillDTO> skills);

    CareerRecommendationListResponse getCareerRecommendationsForResume(Long resumeId, Long userId);

    void saveCareerRecommendations(Resume resume, List<CareerRecommendationDTO> recommendations);
}
