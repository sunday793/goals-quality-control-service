package com.example.kkd_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "kkd_issue")
@Data
@NoArgsConstructor
public class KkdIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private KkdRun run;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_code", nullable = false)
    private KkdRuleEntity rule;

    @Column(name = "unit_id", nullable = false, length = 50)
    private String unitId;

    @Column(name = "goal_id")
    private UUID goalId;

    @Column(name = "kr_id")
    private UUID krId;

    @Column(name = "severity", nullable = false)
    private String severity;
}