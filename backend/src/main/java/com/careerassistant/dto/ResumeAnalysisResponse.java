package com.careerassistant.dto;

import java.util.List;

public class ResumeAnalysisResponse {

    private Long resumeId;
    private int wordCount;
    private int characterCount;
    private List<String> skills;
    private List<String> detectedSections;
    private boolean emailDetected;
    private boolean phoneDetected;
    private int basicScore;
    private List<String> suggestions;

    public ResumeAnalysisResponse() {
    }

    public ResumeAnalysisResponse(Long resumeId, int wordCount, int characterCount, List<String> skills,
                                  List<String> detectedSections, boolean emailDetected, boolean phoneDetected,
                                  int basicScore, List<String> suggestions) {
        this.resumeId = resumeId;
        this.wordCount = wordCount;
        this.characterCount = characterCount;
        this.skills = skills;
        this.detectedSections = detectedSections;
        this.emailDetected = emailDetected;
        this.phoneDetected = phoneDetected;
        this.basicScore = basicScore;
        this.suggestions = suggestions;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(int characterCount) {
        this.characterCount = characterCount;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getDetectedSections() {
        return detectedSections;
    }

    public void setDetectedSections(List<String> detectedSections) {
        this.detectedSections = detectedSections;
    }

    public boolean isEmailDetected() {
        return emailDetected;
    }

    public void setEmailDetected(boolean emailDetected) {
        this.emailDetected = emailDetected;
    }

    public boolean isPhoneDetected() {
        return phoneDetected;
    }

    public void setPhoneDetected(boolean phoneDetected) {
        this.phoneDetected = phoneDetected;
    }

    public int getBasicScore() {
        return basicScore;
    }

    public void setBasicScore(int basicScore) {
        this.basicScore = basicScore;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
