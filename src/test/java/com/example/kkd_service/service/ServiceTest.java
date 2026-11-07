package com.example.kkd_service.service;

import com.example.kkd_service.entity.*;
import com.example.kkd_service.repository.*;
import com.example.kkd_service.rule.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private KeyResultRepository keyResultRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private KkdIssueRepository kkdIssueRepository;

    @InjectMocks
    private MetricsService metricsService;

    @InjectMocks
    private DeltaService deltaService;

    // --- TEST 1: Boundary data for GOAL_NO_OWNER ---
    @Test
    void testGoalNoOwnerRule() {
        GoalNoOwnerRule rule = new GoalNoOwnerRule();

        // Case 1: Goal with owner (no issues)
        Goal goalWithOwner = new Goal();
        goalWithOwner.setId(UUID.randomUUID());
        goalWithOwner.setOwnerId("user1");
        goalWithOwner.setUnit(createUnit("UNIT-1"));

        KkdContext context1 = new KkdContext(List.of(), List.of(goalWithOwner), List.of());
        assertTrue(rule.evaluate(context1).isEmpty(), "Goal with owner should not have issues");

        // Case 2: Goal with null owner (1 issue)
        Goal goalWithoutOwner = new Goal();
        goalWithoutOwner.setId(UUID.randomUUID());
        goalWithoutOwner.setOwnerId(null);
        goalWithoutOwner.setUnit(createUnit("UNIT-1"));

        KkdContext context2 = new KkdContext(List.of(), List.of(goalWithoutOwner), List.of());
        assertEquals(1, rule.evaluate(context2).size(), "Goal without owner should have 1 issue");
    }

    // --- TEST 2: Boundary data for KR_WEIGHTS_SUM ---
    @Test
    void testKrWeightsSumRule() {
        KrWeightsSumRule rule = new KrWeightsSumRule();
        Goal goal = new Goal();
        goal.setId(UUID.randomUUID());
        goal.setUnit(createUnit("UNIT-1"));

        // Case 1: Sum = 100 (no issues)
        KeyResult kr1 = createKr(goal, 60);
        KeyResult kr2 = createKr(goal, 40);

        KkdContext context1 = new KkdContext(List.of(), List.of(goal), List.of(kr1, kr2));
        assertTrue(rule.evaluate(context1).isEmpty(), "Sum of weights = 100 should not have issues");

        // Case 2: Sum = 90 (issues for both KRs)
        kr1.setWeight(BigDecimal.valueOf(50));
        kr2.setWeight(BigDecimal.valueOf(40));

        KkdContext context2 = new KkdContext(List.of(), List.of(goal), List.of(kr1, kr2));
        assertEquals(2, rule.evaluate(context2).size(), "Sum of weights != 100 should have 2 issues");
    }

    // --- TEST 3: Metrics calculation (Clean Goals Share) ---
    @Test
    void testCalculateCleanGoalsShare() {
        Unit unit = createUnit("UNIT-1");
        Goal goal1 = createGoal(unit);
        Goal goal2 = createGoal(unit);

        // Mock repositories
        when(unitRepository.findAll()).thenReturn(List.of(unit));
        when(goalRepository.findByUnit_IdIn(List.of("UNIT-1"))).thenReturn(List.of(goal1, goal2));

        // Mock issues: goal1 has a critical issue, goal2 is clean
        KkdIssue criticalIssue = new KkdIssue();
        criticalIssue.setGoalId(goal1.getId());
        criticalIssue.setSeverity("critical");
        criticalIssue.setUnitId("UNIT-1");
        when(kkdIssueRepository.findByRun_Id("RUN-1")).thenReturn(List.of(criticalIssue));

        // Calculate
        BigDecimal result = metricsService.calculateCleanGoalsShare("RUN-1", "UNIT-1");

        // Expected: 1 clean goal out of 2 = 50%
        assertEquals(0, result.compareTo(BigDecimal.valueOf(50.0)), "Expected 50% clean goals");
    }

    // --- TEST 4: Delta calculation ---
    @Test
    void testCalculateDelta() {
        String currentRunId = "RUN-CURRENT";
        String prevRunId = "RUN-PREV";
        String unitId = "UNIT-1";
        UUID goalId = UUID.randomUUID();

        // Previous run had 1 issue
        KkdRuleEntity ruleEntity = new KkdRuleEntity();
        ruleEntity.setCode("TEST_RULE");

        KkdIssue prevIssue = new KkdIssue();
        prevIssue.setRun(createRun(prevRunId));
        prevIssue.setRule(ruleEntity);
        prevIssue.setGoalId(goalId);
        prevIssue.setUnitId(unitId);
        prevIssue.setSeverity("major");

        // Current run has 1 NEW issue (different rule) and 1 PERSISTING (same as prev)
        KkdIssue persistIssue = new KkdIssue();
        persistIssue.setRun(createRun(currentRunId));
        persistIssue.setRule(ruleEntity);
        persistIssue.setGoalId(goalId);
        persistIssue.setUnitId(unitId);
        persistIssue.setSeverity("major");

        KkdRuleEntity newRule = new KkdRuleEntity();
        newRule.setCode("NEW_RULE");
        KkdIssue newIssue = new KkdIssue();
        newIssue.setRun(createRun(currentRunId));
        newIssue.setRule(newRule);
        newIssue.setGoalId(goalId);
        newIssue.setUnitId(unitId);
        newIssue.setSeverity("critical");

        when(kkdIssueRepository.findByRun_Id(currentRunId)).thenReturn(List.of(persistIssue, newIssue));
        when(kkdIssueRepository.findByRun_Id(prevRunId)).thenReturn(List.of(prevIssue));

        DeltaService.DeltaResult result = deltaService.calculateDelta(currentRunId, prevRunId, unitId, List.of(unitId));

        assertEquals(1, result.getNewIssues(), "Expected 1 new issue");
        assertEquals(1, result.getPersistingIssues(), "Expected 1 persisting issue");
        assertEquals(0, result.getResolvedIssues(), "Expected 0 resolved issues");
    }

    // --- Helper methods ---
    private Unit createUnit(String id) {
        Unit unit = new Unit();
        unit.setId(id);
        unit.setName("Test Unit");
        return unit;
    }

    private Goal createGoal(Unit unit) {
        Goal goal = new Goal();
        goal.setId(UUID.randomUUID());
        goal.setUnit(unit);
        goal.setStatus("active");
        return goal;
    }

    private KeyResult createKr(Goal goal, double weight) {
        KeyResult kr = new KeyResult();
        kr.setId(UUID.randomUUID());
        kr.setGoal(goal);
        kr.setWeight(BigDecimal.valueOf(weight));
        return kr;
    }

    private KkdRun createRun(String id) {
        KkdRun run = new KkdRun();
        run.setId(id);
        return run;
    }
}