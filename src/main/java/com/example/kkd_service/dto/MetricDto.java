package com.example.kkd_service.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class MetricDto {
    private String key;
    private String title;
    private String unit;
    private BigDecimal value;
    private BigDecimal previous;
    private BigDecimal delta;
    private String trend;
}