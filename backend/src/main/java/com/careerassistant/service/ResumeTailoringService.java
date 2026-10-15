package com.careerassistant.service;

import com.careerassistant.dto.ResumeTailorRequest;
import com.careerassistant.dto.ResumeTailorResponse;

import java.util.List;

public interface ResumeTailoringService {

    ResumeTailorResponse tailorResume(ResumeTailorRequest request);

    List<ResumeTailorResponse> getUserTailoredResumes(Long userId);
}
