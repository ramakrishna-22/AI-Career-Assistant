package com.careerassistant.dto;

import java.util.List;

public class SkillExtractionResponse {

    private Long resumeId;
    private List<CategorizedSkillDTO> skills;
    private String message;

    public SkillExtractionResponse() {
    }

    public SkillExtractionResponse(Long resumeId, List<CategorizedSkillDTO> skills) {
        this.resumeId = resumeId;
        this.skills = skills;
        if (skills == null || skills.isEmpty()) {
            this.message = "No recognized skills were found in the resume.";
        }
    }

    public SkillExtractionResponse(Long resumeId, List<CategorizedSkillDTO> skills, String message) {
        this.resumeId = resumeId;
        this.skills = skills;
        this.message = message;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public List<CategorizedSkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<CategorizedSkillDTO> skills) {
        this.skills = skills;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
