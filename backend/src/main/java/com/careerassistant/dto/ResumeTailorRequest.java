package com.careerassistant.dto;

import jakarta.validation.constraints.NotNull;

public class ResumeTailorRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    private Long resumeId;
    private Long jobId;
    private String targetJobDescription;

    public ResumeTailorRequest() {
    }

    public ResumeTailorRequest(Long userId, Long resumeId, Long jobId, String targetJobDescription) {
        this.userId = userId;
        this.resumeId = resumeId;
        this.jobId = jobId;
        this.targetJobDescription = targetJobDescription;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getTargetJobDescription() {
        return targetJobDescription;
    }

    public void setTargetJobDescription(String targetJobDescription) {
        this.targetJobDescription = targetJobDescription;
    }
}
