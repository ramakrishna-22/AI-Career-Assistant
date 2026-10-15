package com.careerassistant.dto;

import jakarta.validation.constraints.NotNull;

public class JobApplicationRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "jobId is required")
    private Long jobId;

    private Long resumeId;
    private String notes;

    public JobApplicationRequest() {
    }

    public JobApplicationRequest(Long userId, Long jobId, Long resumeId, String notes) {
        this.userId = userId;
        this.jobId = jobId;
        this.resumeId = resumeId;
        this.notes = notes;
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

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
