package com.careerassistant.service;

import com.careerassistant.dto.ResumeResponse;
import com.careerassistant.entity.Resume;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    ResumeResponse uploadResume(MultipartFile file, Long userId);

    List<ResumeResponse> getUserResumes(Long userId);

    Resume getResumeEntity(Long resumeId, Long userId);

    Resource loadResumeAsResource(Long resumeId, Long userId);
}
