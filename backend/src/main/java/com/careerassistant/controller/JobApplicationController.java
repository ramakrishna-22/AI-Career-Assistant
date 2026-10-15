package com.careerassistant.controller;

import com.careerassistant.dto.ApplicationStatsResponse;
import com.careerassistant.dto.JobApplicationRequest;
import com.careerassistant.dto.JobApplicationResponse;
import com.careerassistant.entity.ApplicationStatus;
import com.careerassistant.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> applyForJob(@Valid @RequestBody JobApplicationRequest request) {
        JobApplicationResponse response = jobApplicationService.applyForJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobApplicationResponse>> getUserApplications(@PathVariable Long userId) {
        return ResponseEntity.ok(jobApplicationService.getUserApplications(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getApplicationById(
            @PathVariable Long id,
            @RequestParam(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok(jobApplicationService.getApplicationById(id, userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam("status") ApplicationStatus status) {
        return ResponseEntity.ok(jobApplicationService.updateApplicationStatus(id, userId, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long id,
            @RequestParam(value = "userId", required = false) Long userId) {
        jobApplicationService.deleteApplication(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<ApplicationStatsResponse> getApplicationStats(@PathVariable Long userId) {
        return ResponseEntity.ok(jobApplicationService.getApplicationStats(userId));
    }
}
