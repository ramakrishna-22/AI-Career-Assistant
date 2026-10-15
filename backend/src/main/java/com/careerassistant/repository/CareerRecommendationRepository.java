package com.careerassistant.repository;

import com.careerassistant.entity.CareerRecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRecommendationRepository extends JpaRepository<CareerRecommendationEntity, Long> {

    List<CareerRecommendationEntity> findByResumeIdOrderByMatchScoreDesc(Long resumeId);

    void deleteByResumeId(Long resumeId);
}
