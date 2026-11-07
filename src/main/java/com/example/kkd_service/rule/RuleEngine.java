package com.example.kkd_service.rule;

import com.example.kkd_service.entity.Goal;
import com.example.kkd_service.entity.KeyResult;
import com.example.kkd_service.entity.Unit;
import com.example.kkd_service.repository.GoalRepository;
import com.example.kkd_service.repository.KeyResultRepository;
import com.example.kkd_service.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleEngine {

    private final List<KkdRule> rules;
    private final UnitRepository unitRepository;
    private final GoalRepository goalRepository;
    private final KeyResultRepository keyResultRepository;

    @Transactional(readOnly = true)
    public List<FoundIssue> runAllRules() {
        // 1. Load all data from DB
        List<Unit> units = unitRepository.findAll();
        List<Goal> activeGoals = goalRepository.findByStatus("active");

        List<UUID> goalIds = activeGoals.stream()
                .map(Goal::getId)
                .collect(Collectors.toList());

        List<KeyResult> keyResults = goalIds.isEmpty()
                ? List.of()
                : keyResultRepository.findByGoal_IdIn(goalIds);

        // 2. Build context
        KkdContext context = new KkdContext(units, activeGoals, keyResults);

        // 3. Run all rules and collect issues
        return rules.stream()
                .flatMap(rule -> rule.evaluate(context).stream())
                .collect(Collectors.toList());
    }
}