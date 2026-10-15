package com.careerassistant.controller;

import com.careerassistant.dto.CareerRecommendationListResponse;
import com.careerassistant.dto.CompleteAIAnalysisResponse;
import com.careerassistant.dto.SkillExtractionResponse;
import com.careerassistant.service.AIAnalysisService;
import com.careerassistant.service.CareerRecommendationService;
import com.careerassistant.service.SkillExtractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/resumes")
@CrossOrigin(origins = "http://localhost:5173")
public class AIAnalysisController {

    private final AIAnalysisService aiAnalysisService;
    private final SkillExtractionService skillExtractionService;
    private final CareerRecommendationService careerRecommendationService;

    public AIAnalysisController(AIAnalysisService aiAnalysisService,
                                SkillExtractionService skillExtractionService,
                                CareerRecommendationService careerRecommendationService) {
        this.aiAnalysisService = aiAnalysisService;
        this.skillExtractionService = skillExtractionService;
        this.careerRecommendationService = careerRecommendationService;
    }

    @PostMapping("/{resumeId}/analyze")
    public ResponseEntity<CompleteAIAnalysisResponse> analyzeResume(
            @PathVariable Long resumeId,
            @RequestParam(value = "userId", required = false) Long userId) {
        CompleteAIAnalysisResponse response = aiAnalysisService.performCompleteAnalysis(resumeId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{resumeId}/skills")
    public ResponseEntity<SkillExtractionResponse> extractSkills(
            @PathVariable Long resumeId,
            @RequestParam(value = "userId", required = false) Long userId) {
        SkillExtractionResponse response = skillExtractionService.extractSkillsForResume(resumeId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{resumeId}/careers")
    public ResponseEntity<CareerRecommendationListResponse> getCareerRecommendations(
            @PathVariable Long resumeId,
            @RequestParam(value = "userId", required = false) Long userId) {
        CareerRecommendationListResponse response = careerRecommendationService.getCareerRecommendationsForResume(resumeId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{resumeId}/complete-analysis")
    public ResponseEntity<CompleteAIAnalysisResponse> completeAnalysis(
            @PathVariable Long resumeId,
            @RequestParam(value = "userId", required = false) Long userId) {
        CompleteAIAnalysisResponse response = aiAnalysisService.performCompleteAnalysis(resumeId, userId);
        return ResponseEntity.ok(response);
    }
}
