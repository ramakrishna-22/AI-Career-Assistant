package com.careerassistant.controller;

import com.careerassistant.dto.ResumeAnalysisResponse;
import com.careerassistant.dto.ResumeResponse;
import com.careerassistant.entity.Resume;
import com.careerassistant.service.ResumeAnalysisService;
import com.careerassistant.service.ResumeService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeController(ResumeService resumeService, ResumeAnalysisService resumeAnalysisService) {
        this.resumeService = resumeService;
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        ResumeResponse response = resumeService.uploadResume(file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResumeResponse>> getUserResumes(@PathVariable Long userId) {
        List<ResumeResponse> responseList = resumeService.getUserResumes(userId);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{resumeId}/download")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long resumeId,
            @RequestParam(value = "userId", required = false) Long userId) {
        Resource resource = resumeService.loadResumeAsResource(resumeId, userId);
        Resume resume = resumeService.getResumeEntity(resumeId, userId);

        String contentType = resume.getFileType();
        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFileName() + "\"")
                .body(resource);
    }

    @PostMapping("/{resumeId}/analyze")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @PathVariable Long resumeId,
            @RequestParam(value = "userId", required = false) Long userId) {
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResume(resumeId, userId);
        return ResponseEntity.ok(response);
    }
}
