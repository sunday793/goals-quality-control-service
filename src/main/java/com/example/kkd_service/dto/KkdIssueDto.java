package com.example.kkd_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class KkdIssueDto {
    private Long id;
    private String unitId;
    private UUID goalId;
    private UUID krId;
    private String severity;
    private String ruleCode;
    private String ruleTitle;
}