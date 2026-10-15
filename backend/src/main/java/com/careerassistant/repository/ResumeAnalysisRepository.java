package com.careerassistant.repository;

import com.careerassistant.entity.ResumeAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysisEntity, Long> {

    Optional<ResumeAnalysisEntity> findTopByResumeIdOrderByCreatedAtDesc(Long resumeId);
}
