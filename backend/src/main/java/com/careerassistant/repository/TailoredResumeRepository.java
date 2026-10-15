package com.careerassistant.repository;

import com.careerassistant.entity.TailoredResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TailoredResumeRepository extends JpaRepository<TailoredResumeEntity, Long> {

    List<TailoredResumeEntity> findByUserIdOrderByGeneratedAtDesc(Long userId);
}
