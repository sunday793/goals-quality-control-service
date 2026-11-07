package com.example.kkd_service.rule;

import com.example.kkd_service.entity.Goal;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoalNoOwnerRule implements KkdRule {

    @Override
    public String getCode() {
        return "GOAL_NO_OWNER";
    }

    @Override
    public String getSeverity() {
        return "critical";
    }

    @Override
    public List<FoundIssue> evaluate(KkdContext context) {
        return context.getActiveGoals().stream()
                .filter(goal -> goal.getOwnerId() == null || goal.getOwnerId().trim().isEmpty())
                .map(goal -> FoundIssue.builder()
                        .ruleCode(getCode())
                        .severity(getSeverity())
                        .unitId(goal.getUnit().getId())
                        .goalId(goal.getId())
                        .build())
                .collect(Collectors.toList());
    }
}