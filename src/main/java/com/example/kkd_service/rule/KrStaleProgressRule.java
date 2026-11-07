package com.example.kkd_service.rule;

import com.example.kkd_service.entity.KeyResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KrStaleProgressRule implements KkdRule {

    @Override
    public String getCode() {
        return "KR_STALE_PROGRESS";
    }

    @Override
    public String getSeverity() {
        return "minor";
    }

    @Override
    public List<FoundIssue> evaluate(KkdContext context) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(45);

        return context.getKeyResults().stream()
                .filter(kr -> kr.getFactUpdatedAt() != null && kr.getFactUpdatedAt().isBefore(threshold))
                .map(kr -> FoundIssue.builder()
                        .ruleCode(getCode())
                        .severity(getSeverity())
                        .unitId(kr.getGoal().getUnit().getId())
                        .goalId(kr.getGoal().getId())
                        .krId(kr.getId())
                        .build())
                .collect(Collectors.toList());
    }
}