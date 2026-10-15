package com.careerassistant.service;

import com.careerassistant.dto.SkillGapRequest;
import com.careerassistant.dto.SkillGapResponse;

import java.util.List;

public interface SkillGapService {

    SkillGapResponse analyzeSkillGap(SkillGapRequest request);

    List<SkillGapResponse> getUserAnalyses(Long userId);
}
