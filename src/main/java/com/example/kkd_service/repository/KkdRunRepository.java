package com.example.kkd_service.repository;

import com.example.kkd_service.entity.KkdRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KkdRunRepository extends JpaRepository<KkdRun, String> {
    // Find the latest run by snapshot date to compare with
    Optional<KkdRun> findFirstByOrderBySnapshotDateDesc();
}