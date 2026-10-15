package com.careerassistant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_gap_analyses")
public class SkillGapAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(nullable = false)
    private String targetRole;

    private int readinessScore;

    @Column(columnDefinition = "TEXT")
    private String matchingSkills;

    @Column(columnDefinition = "TEXT")
    private String criticalGaps;

    @Column(columnDefinition = "TEXT")
    private String secondaryGaps;

    @Column(columnDefinition = "TEXT")
    private String learningRoadmap;

    @Column(nullable = false, updatable = false)
    private LocalDateTime analyzedAt;

    public SkillGapAnalysisEntity() {
    }

    public SkillGapAnalysisEntity(User user, Resume resume, Job job, String targetRole, int readinessScore,
                                 String matchingSkills, String criticalGaps, String secondaryGaps,
                                 String learningRoadmap) {
        this.user = user;
        this.resume = resume;
        this.job = job;
        this.targetRole = targetRole;
        this.readinessScore = readinessScore;
        this.matchingSkills = matchingSkills;
        this.criticalGaps = criticalGaps;
        this.secondaryGaps = secondaryGaps;
        this.learningRoadmap = learningRoadmap;
    }

    @PrePersist
    protected void onCreate() {
        this.analyzedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
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

    public String getMatchingSkills() {
        return matchingSkills;
    }

    public void setMatchingSkills(String matchingSkills) {
        this.matchingSkills = matchingSkills;
    }

    public String getCriticalGaps() {
        return criticalGaps;
    }

    public void setCriticalGaps(String criticalGaps) {
        this.criticalGaps = criticalGaps;
    }

    public String getSecondaryGaps() {
        return secondaryGaps;
    }

    public void setSecondaryGaps(String secondaryGaps) {
        this.secondaryGaps = secondaryGaps;
    }

    public String getLearningRoadmap() {
        return learningRoadmap;
    }

    public void setLearningRoadmap(String learningRoadmap) {
        this.learningRoadmap = learningRoadmap;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
