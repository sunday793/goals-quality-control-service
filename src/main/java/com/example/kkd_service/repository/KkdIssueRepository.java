package com.example.kkd_service.repository;

import com.example.kkd_service.entity.KkdIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KkdIssueRepository extends JpaRepository<KkdIssue, Long> {
    List<KkdIssue> findByRun_Id(String runId);
}