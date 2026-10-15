package com.careerassistant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tailored_resumes")
public class TailoredResumeEntity {

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

    private int atsMatchScore;

    @Column(columnDefinition = "TEXT")
    private String tailoredSummary;

    @Column(columnDefinition = "TEXT")
    private String suggestedKeywords;

    @Column(columnDefinition = "TEXT")
    private String bulletPointSuggestions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    public TailoredResumeEntity() {
    }

    public TailoredResumeEntity(User user, Resume resume, Job job, int atsMatchScore, String tailoredSummary,
                                String suggestedKeywords, String bulletPointSuggestions) {
        this.user = user;
        this.resume = resume;
        this.job = job;
        this.atsMatchScore = atsMatchScore;
        this.tailoredSummary = tailoredSummary;
        this.suggestedKeywords = suggestedKeywords;
        this.bulletPointSuggestions = bulletPointSuggestions;
    }

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
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

    public String getSuggestedKeywords() {
        return suggestedKeywords;
    }

    public void setSuggestedKeywords(String suggestedKeywords) {
        this.suggestedKeywords = suggestedKeywords;
    }

    public String getBulletPointSuggestions() {
        return bulletPointSuggestions;
    }

    public void setBulletPointSuggestions(String bulletPointSuggestions) {
        this.bulletPointSuggestions = bulletPointSuggestions;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
