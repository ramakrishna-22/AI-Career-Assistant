package com.careerassistant.service;

import com.careerassistant.dto.CategorizedSkillDTO;
import com.careerassistant.dto.SkillExtractionResponse;
import com.careerassistant.entity.Resume;

import java.util.List;

public interface SkillExtractionService {

    List<CategorizedSkillDTO> extractAndCategorizeSkills(String text);

    SkillExtractionResponse extractSkillsForResume(Long resumeId, Long userId);

    void saveExtractedSkills(Resume resume, List<CategorizedSkillDTO> skills);
}
