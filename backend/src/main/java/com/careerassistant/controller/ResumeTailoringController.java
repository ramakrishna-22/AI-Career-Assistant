package com.careerassistant.controller;

import com.careerassistant.dto.ResumeTailorRequest;
import com.careerassistant.dto.ResumeTailorResponse;
import com.careerassistant.service.ResumeTailoringService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tailoring")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeTailoringController {

    private final ResumeTailoringService resumeTailoringService;

    public ResumeTailoringController(ResumeTailoringService resumeTailoringService) {
        this.resumeTailoringService = resumeTailoringService;
    }

    @PostMapping("/optimize")
    public ResponseEntity<ResumeTailorResponse> tailorResume(@Valid @RequestBody ResumeTailorRequest request) {
        ResumeTailorResponse response = resumeTailoringService.tailorResume(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResumeTailorResponse>> getUserTailoredResumes(@PathVariable Long userId) {
        return ResponseEntity.ok(resumeTailoringService.getUserTailoredResumes(userId));
    }
}
