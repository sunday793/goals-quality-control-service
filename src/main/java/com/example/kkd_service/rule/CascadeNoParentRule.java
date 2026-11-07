package com.example.kkd_service.rule;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CascadeNoParentRule implements KkdRule {

    @Override
    public String getCode() {
        return "CASCADE_NO_PARENT";
    }

    @Override
    public String getSeverity() {
        return "major";
    }

    @Override
    public List<FoundIssue> evaluate(KkdContext context) {
        return context.getActiveGoals().stream()
                .filter(goal -> goal.getParentGoal() == null)
                .map(goal -> FoundIssue.builder()
                        .ruleCode(getCode())
                        .severity(getSeverity())
                        .unitId(goal.getUnit().getId())
                        .goalId(goal.getId())
                        .build())
                .collect(Collectors.toList());
    }
}