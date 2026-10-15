package com.careerassistant.service;

import com.careerassistant.dto.ResumeAnalysisResponse;

public interface ResumeAnalysisService {

    ResumeAnalysisResponse analyzeResume(Long resumeId, Long userId);
}
