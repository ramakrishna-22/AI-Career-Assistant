package com.careerassistant.service;

import com.careerassistant.dto.CompleteAIAnalysisResponse;

public interface AIAnalysisService {

    CompleteAIAnalysisResponse performCompleteAnalysis(Long resumeId, Long userId);
}
