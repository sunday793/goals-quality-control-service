package com.example.kkd_service.rule;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FoundIssue {
    private String ruleCode;
    private String severity;
    private String unitId;
    private UUID goalId;
    private UUID krId;
}