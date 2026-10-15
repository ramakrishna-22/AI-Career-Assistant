package com.careerassistant.dto;

public class CareerRecommendationDTO {

    private String career;
    private int matchScore;
    private String reason;

    public CareerRecommendationDTO() {
    }

    public CareerRecommendationDTO(String career, int matchScore, String reason) {
        this.career = career;
        this.matchScore = matchScore;
        this.reason = reason;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
