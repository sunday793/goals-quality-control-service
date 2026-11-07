package com.example.kkd_service.repository;

import com.example.kkd_service.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {
    List<Unit> findByParent_Id(String parentId);
}