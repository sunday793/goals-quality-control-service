package com.example.kkd_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueSeverityDto {
    private String code;
    private int current;
    private int previous;
    private int delta;
}