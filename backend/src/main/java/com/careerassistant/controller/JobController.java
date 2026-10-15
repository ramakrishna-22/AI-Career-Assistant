package com.careerassistant.controller;

import com.careerassistant.dto.JobMatchResponse;
import com.careerassistant.dto.JobResponse;
import com.careerassistant.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobMatchResponse>> searchJobs(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "employmentType", required = false) String employmentType,
            @RequestParam(value = "minMatchScore", required = false) Integer minMatchScore,
            @RequestParam(value = "resumeId", required = false) Long resumeId,
            @RequestParam(value = "userId", required = false) Long userId) {
        List<JobMatchResponse> results = jobService.searchJobs(keyword, location, employmentType, minMatchScore, resumeId, userId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<JobMatchResponse>> getRecommendedJobs(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {
        return ResponseEntity.ok(jobService.getRecommendedJobsForUser(userId, limit));
    }

    @GetMapping("/match/{resumeId}")
    public ResponseEntity<List<JobMatchResponse>> matchJobsForResume(
            @PathVariable Long resumeId,
            @RequestParam(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok(jobService.matchJobsForResume(resumeId, userId));
    }
}
