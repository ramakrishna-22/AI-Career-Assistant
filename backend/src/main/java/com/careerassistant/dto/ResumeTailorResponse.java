package com.careerassistant.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ResumeTailorResponse {

    private Long id;
    private Long userId;
    private Long jobId;
    private int atsMatchScore;
    private String tailoredSummary;
    private List<String> suggestedKeywords;
    private List<String> bulletPointSuggestions;
    private LocalDateTime generatedAt;

    public ResumeTailorResponse() {
    }

    public ResumeTailorResponse(Long id, Long userId, Long jobId, int atsMatchScore,
                                String tailoredSummary, List<String> suggestedKeywords,
                                List<String> bulletPointSuggestions, LocalDateTime generatedAt) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.atsMatchScore = atsMatchScore;
        this.tailoredSummary = tailoredSummary;
        this.suggestedKeywords = suggestedKeywords;
        this.bulletPointSuggestions = bulletPointSuggestions;
        this.generatedAt = generatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public int getAtsMatchScore() {
        return atsMatchScore;
    }

    public void setAtsMatchScore(int atsMatchScore) {
        this.atsMatchScore = atsMatchScore;
    }

    public String getTailoredSummary() {
        return tailoredSummary;
    }

    public void setTailoredSummary(String tailoredSummary) {
        this.tailoredSummary = tailoredSummary;
    }

    public List<String> getSuggestedKeywords() {
        return suggestedKeywords;
    }

    public void setSuggestedKeywords(List<String> suggestedKeywords) {
        this.suggestedKeywords = suggestedKeywords;
    }

    public List<String> getBulletPointSuggestions() {
        return bulletPointSuggestions;
    }

    public void setBulletPointSuggestions(List<String> bulletPointSuggestions) {
        this.bulletPointSuggestions = bulletPointSuggestions;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
