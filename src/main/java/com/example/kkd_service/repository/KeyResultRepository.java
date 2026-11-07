package com.example.kkd_service.repository;

import com.example.kkd_service.entity.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KeyResultRepository extends JpaRepository<KeyResult, UUID> {
    List<KeyResult> findByGoal_IdIn(List<UUID> goalIds);
}