package com.example.kkd_service.service;

import com.example.kkd_service.entity.Goal;
import com.example.kkd_service.entity.KkdIssue;
import com.example.kkd_service.entity.Unit;
import com.example.kkd_service.repository.GoalRepository;
import com.example.kkd_service.repository.KkdIssueRepository;
import com.example.kkd_service.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final UnitRepository unitRepository;
    private final GoalRepository goalRepository;
    private final KkdIssueRepository kkdIssueRepository;

    @Transactional(readOnly = true)
    public BigDecimal calculateCleanGoalsShare(String runId, String unitId) {
        // 1. Get all units in the subtree
        List<String> unitIds = getUnitSubtreeIds(unitId);

        // 2. Get all active goals in these units
        List<Goal> activeGoals = goalRepository.findByUnit_IdIn(unitIds).stream()
                .filter(goal -> "active".equals(goal.getStatus()))
                .collect(Collectors.toList());

        if (activeGoals.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 3. Get all issues for this run and these units
        List<KkdIssue> issues = kkdIssueRepository.findByRun_Id(runId).stream()
                .filter(issue -> unitIds.contains(issue.getUnitId()))
                .collect(Collectors.toList());

        // 4. Find goals with critical issues
        Set<UUID> goalsWithCriticalIssues = issues.stream()
                .filter(issue -> "critical".equals(issue.getSeverity()))
                .map(KkdIssue::getGoalId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 5. Calculate clean goals
        long totalGoals = activeGoals.size();
        long cleanGoals = activeGoals.stream()
                .filter(goal -> !goalsWithCriticalIssues.contains(goal.getId()))
                .count();

        // 6. Calculate percentage
        return BigDecimal.valueOf(cleanGoals)
                .divide(BigDecimal.valueOf(totalGoals), 1, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public List<String> getUnitSubtreeIds(String rootUnitId) {
        List<Unit> allUnits = unitRepository.findAll();
        List<String> result = new ArrayList<>();
        result.add(rootUnitId);

        Queue<String> queue = new LinkedList<>();
        queue.add(rootUnitId);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            List<Unit> children = allUnits.stream()
                    .filter(u -> u.getParent() != null && u.getParent().getId().equals(currentId))
                    .collect(Collectors.toList());

            for (Unit child : children) {
                result.add(child.getId());
                queue.add(child.getId());
            }
        }

        return result;
    }
}