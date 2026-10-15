package com.careerassistant.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SkillGapResponse {

    private Long id;
    private Long userId;
    private String targetRole;
    private int readinessScore;
    private List<String> matchingSkills;
    private List<String> criticalGaps;
    private List<String> secondaryGaps;
    private List<String> learningRoadmap;
    private LocalDateTime analyzedAt;

    public SkillGapResponse() {
    }

    public SkillGapResponse(Long id, Long userId, String targetRole, int readinessScore,
                            List<String> matchingSkills, List<String> criticalGaps,
                            List<String> secondaryGaps, List<String> learningRoadmap,
                            LocalDateTime analyzedAt) {
        this.id = id;
        this.userId = userId;
        this.targetRole = targetRole;
        this.readinessScore = readinessScore;
        this.matchingSkills = matchingSkills;
        this.criticalGaps = criticalGaps;
        this.secondaryGaps = secondaryGaps;
        this.learningRoadmap = learningRoadmap;
        this.analyzedAt = analyzedAt;
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

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public int getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(int readinessScore) {
        this.readinessScore = readinessScore;
    }

    public List<String> getMatchingSkills() {
        return matchingSkills;
    }

    public void setMatchingSkills(List<String> matchingSkills) {
        this.matchingSkills = matchingSkills;
    }

    public List<String> getCriticalGaps() {
        return criticalGaps;
    }

    public void setCriticalGaps(List<String> criticalGaps) {
        this.criticalGaps = criticalGaps;
    }

    public List<String> getSecondaryGaps() {
        return secondaryGaps;
    }

    public void setSecondaryGaps(List<String> secondaryGaps) {
        this.secondaryGaps = secondaryGaps;
    }

    public List<String> getLearningRoadmap() {
        return learningRoadmap;
    }

    public void setLearningRoadmap(List<String> learningRoadmap) {
        this.learningRoadmap = learningRoadmap;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
