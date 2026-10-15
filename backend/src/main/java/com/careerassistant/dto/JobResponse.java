package com.careerassistant.dto;

import java.time.LocalDateTime;
import java.util.List;

public class JobResponse {

    private Long id;
    private String title;
    private String companyName;
    private String location;
    private String description;
    private List<String> requiredSkills;
    private String salary;
    private String employmentType;
    private String experienceRequired;
    private LocalDateTime createdAt;

    public JobResponse() {
    }

    public JobResponse(Long id, String title, String companyName, String location, String description,
                       List<String> requiredSkills, String salary, String employmentType,
                       String experienceRequired, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.salary = salary;
        this.employmentType = employmentType;
        this.experienceRequired = experienceRequired;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getExperienceRequired() {
        return experienceRequired;
    }

    public void setExperienceRequired(String experienceRequired) {
        this.experienceRequired = experienceRequired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
