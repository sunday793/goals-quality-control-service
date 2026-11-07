package com.example.kkd_service.service;

import com.example.kkd_service.dto.IssueSeverityDto;
import com.example.kkd_service.entity.KkdIssue;
import com.example.kkd_service.repository.KkdIssueRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeltaService {

    private final KkdIssueRepository kkdIssueRepository;

    @Data
    @Builder
    public static class DeltaResult {
        private int newIssues;
        private int persistingIssues;
        private int resolvedIssues;
    }

    /**
     * Calculates delta between current and previous runs.
     * A problem is considered "the same" if rule_code and object (goalId/krId) match.
     */
    @Transactional(readOnly = true)
    public DeltaResult calculateDelta(String currentRunId, String previousRunId, String unitId, List<String> unitSubtreeIds) {
        List<KkdIssue> currentIssues = getIssuesForRunAndUnits(currentRunId, unitSubtreeIds);
        List<KkdIssue> previousIssues = getIssuesForRunAndUnits(previousRunId, unitSubtreeIds);

        Set<String> currentKeys = currentIssues.stream()
                .map(this::getIssueKey)
                .collect(Collectors.toSet());

        Set<String> previousKeys = previousIssues.stream()
                .map(this::getIssueKey)
                .collect(Collectors.toSet());

        int newCount = 0;
        for (String key : currentKeys) {
            if (!previousKeys.contains(key)) {
                newCount++;
            }
        }

        int resolvedCount = 0;
        for (String key : previousKeys) {
            if (!currentKeys.contains(key)) {
                resolvedCount++;
            }
        }

        int persistingCount = 0;
        for (String key : currentKeys) {
            if (previousKeys.contains(key)) {
                persistingCount++;
            }
        }

        return DeltaResult.builder()
                .newIssues(newCount)
                .persistingIssues(persistingCount)
                .resolvedIssues(resolvedCount)
                .build();
    }

    /**
     * Calculates issue counts by severity for current and previous runs.
     */
    @Transactional(readOnly = true)
    public List<IssueSeverityDto> calculateSeverityStats(String currentRunId, String previousRunId, List<String> unitSubtreeIds) {
        List<KkdIssue> currentIssues = getIssuesForRunAndUnits(currentRunId, unitSubtreeIds);
        List<KkdIssue> previousIssues = getIssuesForRunAndUnits(previousRunId, unitSubtreeIds);

        Map<String, Long> currentCounts = currentIssues.stream()
                .collect(Collectors.groupingBy(KkdIssue::getSeverity, Collectors.counting()));

        Map<String, Long> previousCounts = previousIssues.stream()
                .collect(Collectors.groupingBy(KkdIssue::getSeverity, Collectors.counting()));

        // Combine all severities
        Set<String> allSeverities = new HashSet<>();
        allSeverities.addAll(currentCounts.keySet());
        allSeverities.addAll(previousCounts.keySet());

        return allSeverities.stream()
                .map(severity -> {
                    int current = currentCounts.getOrDefault(severity, 0L).intValue();
                    int previous = previousCounts.getOrDefault(severity, 0L).intValue();
                    return IssueSeverityDto.builder()
                            .code(severity)
                            .current(current)
                            .previous(previous)
                            .delta(current - previous)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<KkdIssue> getIssuesForRunAndUnits(String runId, List<String> unitIds) {
        if (runId == null) return Collections.emptyList();
        return kkdIssueRepository.findByRun_Id(runId).stream()
                .filter(issue -> unitIds.contains(issue.getUnitId()))
                .collect(Collectors.toList());
    }

    private String getIssueKey(KkdIssue issue) {
        String objectId = issue.getKrId() != null ? issue.getKrId().toString() :
                (issue.getGoalId() != null ? issue.getGoalId().toString() : "unknown");
        return issue.getRule().getCode() + ":" + objectId;
    }
}