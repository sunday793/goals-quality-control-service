package com.example.kkd_service.service;

import com.example.kkd_service.dto.IssueSeverityDto;
import com.example.kkd_service.dto.KkdSummaryResponse;
import com.example.kkd_service.dto.MetricDto;
import com.example.kkd_service.entity.KkdRun;
import com.example.kkd_service.entity.Unit;
import com.example.kkd_service.repository.KkdRunRepository;
import com.example.kkd_service.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final KkdRunRepository kkdRunRepository;
    private final UnitRepository unitRepository;
    private final MetricsService metricsService;
    private final DeltaService deltaService;

    @Transactional(readOnly = true)
    public KkdSummaryResponse getSummary(String runId, String unitId) {
        // 1. Validate run and unit
        KkdRun currentRun = kkdRunRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("Unit not found: " + unitId));

        // 2. Find previous run (the one with the largest snapshotDate strictly before current)
        List<KkdRun> allRuns = kkdRunRepository.findAll();
        Optional<KkdRun> previousRunOpt = allRuns.stream()
                .filter(r -> r.getSnapshotDate().isBefore(currentRun.getSnapshotDate()))
                .max((r1, r2) -> r1.getSnapshotDate().compareTo(r2.getSnapshotDate()));

        KkdRun previousRun = previousRunOpt.orElse(null);

        // 3. Get subtree units for cascade calculation
        List<String> unitSubtreeIds = metricsService.getUnitSubtreeIds(unitId);

        // 4. Calculate current and previous metrics
        BigDecimal currentCleanShare = metricsService.calculateCleanGoalsShare(runId, unitId);
        BigDecimal previousCleanShare = previousRun != null
                ? metricsService.calculateCleanGoalsShare(previousRun.getId(), unitId)
                : BigDecimal.ZERO;

        // 5. Build metric DTO
        BigDecimal delta = currentCleanShare.subtract(previousCleanShare);
        String trend = delta.compareTo(BigDecimal.ZERO) > 0 ? "better" :
                (delta.compareTo(BigDecimal.ZERO) < 0 ? "worse" : "same");

        MetricDto metric = MetricDto.builder()
                .key("clean_goals_share")
                .title("Доля корректных целей")
                .unit("%")
                .value(currentCleanShare)
                .previous(previousCleanShare)
                .delta(delta)
                .trend(trend)
                .build();

        // 6. Calculate severity stats
        List<IssueSeverityDto> severityStats = previousRun != null
                ? deltaService.calculateSeverityStats(runId, previousRun.getId(), unitSubtreeIds)
                : deltaService.calculateSeverityStats(runId, null, unitSubtreeIds);

        // 7. Calculate delta (new/persisting/resolved)
        DeltaService.DeltaResult deltaResult = previousRun != null
                ? deltaService.calculateDelta(runId, previousRun.getId(), unitId, unitSubtreeIds)
                : DeltaService.DeltaResult.builder().newIssues(0).persistingIssues(0).resolvedIssues(0).build();

        // 8. Build full response
        return KkdSummaryResponse.builder()
                .run(KkdSummaryResponse.RunInfo.builder()
                        .id(currentRun.getId())
                        .snapshotDate(currentRun.getSnapshotDate())
                        .build())
                .comparedTo(previousRun != null ? KkdSummaryResponse.RunInfo.builder()
                        .id(previousRun.getId())
                        .snapshotDate(previousRun.getSnapshotDate())
                        .build() : null)
                .unit(KkdSummaryResponse.UnitInfo.builder()
                        .id(unit.getId())
                        .name(unit.getName())
                        .build())
                .metrics(List.of(metric))
                .issuesBySeverity(severityStats)
                .delta(KkdSummaryResponse.DeltaInfo.builder()
                        .newIssues(deltaResult.getNewIssues())
                        .persisting(deltaResult.getPersistingIssues())
                        .resolved(deltaResult.getResolvedIssues())
                        .build())
                .build();
    }
}