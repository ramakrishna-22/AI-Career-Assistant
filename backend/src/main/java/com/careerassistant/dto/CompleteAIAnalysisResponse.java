package com.careerassistant.dto;

import java.util.List;

public class CompleteAIAnalysisResponse {

    private Long resumeId;
    private String name;
    private String email;
    private String phone;
    private String summary;
    private List<String> education;
    private List<String> experience;
    private List<String> projects;
    private List<CategorizedSkillDTO> skills;
    private List<CareerRecommendationDTO> careerRecommendations;

    public CompleteAIAnalysisResponse() {
    }

    public CompleteAIAnalysisResponse(Long resumeId, String name, String email, String phone, String summary,
                                      List<String> education, List<String> experience, List<String> projects,
                                      List<CategorizedSkillDTO> skills, List<CareerRecommendationDTO> careerRecommendations) {
        this.resumeId = resumeId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.summary = summary;
        this.education = education;
        this.experience = experience;
        this.projects = projects;
        this.skills = skills;
        this.careerRecommendations = careerRecommendations;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getEducation() {
        return education;
    }

    public void setEducation(List<String> education) {
        this.education = education;
    }

    public List<String> getExperience() {
        return experience;
    }

    public void setExperience(List<String> experience) {
        this.experience = experience;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public List<CategorizedSkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<CategorizedSkillDTO> skills) {
        this.skills = skills;
    }

    public List<CareerRecommendationDTO> getCareerRecommendations() {
        return careerRecommendations;
    }

    public void setCareerRecommendations(List<CareerRecommendationDTO> careerRecommendations) {
        this.careerRecommendations = careerRecommendations;
    }
}
