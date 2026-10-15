package com.careerassistant.repository;

import com.careerassistant.entity.SkillGapAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillGapAnalysisRepository extends JpaRepository<SkillGapAnalysisEntity, Long> {

    List<SkillGapAnalysisEntity> findByUserIdOrderByAnalyzedAtDesc(Long userId);
}
