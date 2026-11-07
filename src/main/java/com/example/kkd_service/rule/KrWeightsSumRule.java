package com.example.kkd_service.rule;

import com.example.kkd_service.entity.KeyResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KrWeightsSumRule implements KkdRule {

    @Override
    public String getCode() {
        return "KR_WEIGHTS_SUM";
    }

    @Override
    public String getSeverity() {
        return "critical";
    }

    @Override
    public List<FoundIssue> evaluate(KkdContext context) {
        // Group key results by goal ID
        Map<UUID, List<KeyResult>> krsByGoal = context.getKeyResults().stream()
                .collect(Collectors.groupingBy(kr -> kr.getGoal().getId()));

        return krsByGoal.entrySet().stream()
                .filter(entry -> {
                    BigDecimal sum = entry.getValue().stream()
                            .map(KeyResult::getWeight)
                            .filter(w -> w != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return sum.compareTo(BigDecimal.valueOf(100)) != 0;
                })
                .flatMap(entry -> entry.getValue().stream()
                        .map(kr -> FoundIssue.builder()
                                .ruleCode(getCode())
                                .severity(getSeverity())
                                .unitId(kr.getGoal().getUnit().getId())
                                .goalId(kr.getGoal().getId())
                                .krId(kr.getId())
                                .build()))
                .collect(Collectors.toList());
    }
}