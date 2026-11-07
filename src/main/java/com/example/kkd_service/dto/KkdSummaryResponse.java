package com.example.kkd_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class KkdSummaryResponse {
    private RunInfo run;
    private RunInfo comparedTo;
    private UnitInfo unit;
    private List<MetricDto> metrics;
    private List<IssueSeverityDto> issuesBySeverity;
    private DeltaInfo delta;

    @Data
    @Builder
    public static class RunInfo {
        private String id;
        private LocalDate snapshotDate;
    }

    @Data
    @Builder
    public static class UnitInfo {
        private String id;
        private String name;
    }

    @Data
    @Builder
    public static class DeltaInfo {
        @com.fasterxml.jackson.annotation.JsonProperty("new")
        private int newIssues;
        private int persisting;
        private int resolved;
    }
}