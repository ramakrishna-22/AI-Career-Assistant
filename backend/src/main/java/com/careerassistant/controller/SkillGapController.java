package com.careerassistant.controller;

import com.careerassistant.dto.SkillGapRequest;
import com.careerassistant.dto.SkillGapResponse;
import com.careerassistant.service.SkillGapService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-gap")
@CrossOrigin(origins = "http://localhost:5173")
public class SkillGapController {

    private final SkillGapService skillGapService;

    public SkillGapController(SkillGapService skillGapService) {
        this.skillGapService = skillGapService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<SkillGapResponse> analyzeSkillGap(@Valid @RequestBody SkillGapRequest request) {
        SkillGapResponse response = skillGapService.analyzeSkillGap(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SkillGapResponse>> getUserAnalyses(@PathVariable Long userId) {
        return ResponseEntity.ok(skillGapService.getUserAnalyses(userId));
    }
}
