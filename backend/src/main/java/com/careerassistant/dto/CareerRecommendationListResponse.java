package com.careerassistant.dto;

import java.util.List;

public class CareerRecommendationListResponse {

    private Long resumeId;
    private List<CareerRecommendationDTO> recommendations;

    public CareerRecommendationListResponse() {
    }

    public CareerRecommendationListResponse(Long resumeId, List<CareerRecommendationDTO> recommendations) {
        this.resumeId = resumeId;
        this.recommendations = recommendations;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public List<CareerRecommendationDTO> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<CareerRecommendationDTO> recommendations) {
        this.recommendations = recommendations;
    }
}
