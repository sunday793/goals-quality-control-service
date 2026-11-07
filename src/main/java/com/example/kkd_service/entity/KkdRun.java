package com.example.kkd_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kkd_run")
@Data
@NoArgsConstructor
public class KkdRun {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}