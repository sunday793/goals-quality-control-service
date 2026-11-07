package com.example.kkd_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "goal")
@Data
@NoArgsConstructor
public class Goal {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "owner_id")
    private String ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id")
    private Goal parentGoal;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "due_date")
    private LocalDate dueDate;
}