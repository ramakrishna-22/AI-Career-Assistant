package com.careerassistant.dto;

import jakarta.validation.constraints.NotNull;

public class SkillGapRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    private Long resumeId;
    private Long jobId;
    private String targetRole;

    public SkillGapRequest() {
    }

    public SkillGapRequest(Long userId, Long resumeId, Long jobId, String targetRole) {
        this.userId = userId;
        this.resumeId = resumeId;
        this.jobId = jobId;
        this.targetRole = targetRole;
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

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }
}
