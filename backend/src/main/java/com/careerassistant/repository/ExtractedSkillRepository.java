package com.careerassistant.repository;

import com.careerassistant.entity.ExtractedSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtractedSkillRepository extends JpaRepository<ExtractedSkillEntity, Long> {

    List<ExtractedSkillEntity> findByResumeId(Long resumeId);

    void deleteByResumeId(Long resumeId);
}
