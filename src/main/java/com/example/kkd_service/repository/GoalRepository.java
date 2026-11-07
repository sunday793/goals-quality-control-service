package com.example.kkd_service.repository;

import com.example.kkd_service.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByStatus(String status);
    List<Goal> findByUnit_IdIn(List<String> unitIds);
}