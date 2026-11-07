package com.example.kkd_service.service;

import com.example.kkd_service.entity.KkdIssue;
import com.example.kkd_service.entity.KkdRuleEntity;
import com.example.kkd_service.entity.KkdRun;
import com.example.kkd_service.repository.KkdIssueRepository;
import com.example.kkd_service.repository.KkdRuleEntityRepository;
import com.example.kkd_service.repository.KkdRunRepository;
import com.example.kkd_service.rule.FoundIssue;
import com.example.kkd_service.rule.RuleEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KkdRunService {

    private final RuleEngine ruleEngine;
    private final KkdRunRepository kkdRunRepository;
    private final KkdIssueRepository kkdIssueRepository;
    private final KkdRuleEntityRepository kkdRuleEntityRepository;

    @Transactional
    public KkdRun executeNewRun() {
        // 1. Run all rules to get found issues
        List<FoundIssue> foundIssues = ruleEngine.runAllRules();

        // 2. Create a new run
        KkdRun run = new KkdRun();
        run.setId("RUN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        run.setSnapshotDate(LocalDate.now());
        run.setCreatedAt(LocalDateTime.now());
        kkdRunRepository.save(run);

        // 3. Convert FoundIssue to KkdIssue and save
        if (!foundIssues.isEmpty()) {
            List<KkdIssue> issues = foundIssues.stream()
                    .map(found -> {
                        KkdIssue issue = new KkdIssue();
                        issue.setRun(run);

                        // Fetch rule entity (we can optimize this later, but for now it's fine)
                        KkdRuleEntity ruleEntity = kkdRuleEntityRepository.findById(found.getRuleCode())
                                .orElseThrow(() -> new RuntimeException("Rule not found: " + found.getRuleCode()));

                        issue.setRule(ruleEntity);
                        issue.setUnitId(found.getUnitId());
                        issue.setGoalId(found.getGoalId());
                        issue.setKrId(found.getKrId());
                        issue.setSeverity(found.getSeverity());
                        return issue;
                    })
                    .collect(Collectors.toList());

            kkdIssueRepository.saveAll(issues);
        }

        return run;
    }
}