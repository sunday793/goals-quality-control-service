package com.example.kkd_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kkd_rule")
@Data
@NoArgsConstructor
public class KkdRuleEntity {

    @Id
    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "severity", nullable = false)
    private String severity;
}