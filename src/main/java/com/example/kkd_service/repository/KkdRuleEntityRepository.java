package com.example.kkd_service.repository;

import com.example.kkd_service.entity.KkdRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KkdRuleEntityRepository extends JpaRepository<KkdRuleEntity, String> {
}