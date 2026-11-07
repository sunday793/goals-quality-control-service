package com.example.kkd_service.controller;

import com.example.kkd_service.dto.KkdIssueDto;
import com.example.kkd_service.entity.KkdIssue;
import com.example.kkd_service.repository.KkdIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kkd")
@RequiredArgsConstructor
public class KkdIssueController {

    private final KkdIssueRepository kkdIssueRepository;

    @GetMapping("/issues")
    public ResponseEntity<Map<String, Object>> getIssues(
            @RequestParam("run_id") String runId,
            @RequestParam(value = "unit_id", required = false) String unitId,
            @RequestParam(value = "rule", required = false) String rule,
            @RequestParam(value = "severity", required = false) String severity,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset) {

        if (limit < 0 || offset < 0) {
            return ResponseEntity.unprocessableEntity().build();
        }

        List<KkdIssue> allIssues = kkdIssueRepository.findByRun_Id(runId);

        // Filtering
        List<KkdIssue> filtered = allIssues.stream()
                .filter(i -> unitId == null || i.getUnitId().equals(unitId))
                .filter(i -> rule == null || i.getRule().getCode().equals(rule))
                .filter(i -> severity == null || i.getSeverity().equals(severity))
                .toList();

        // Pagination
        int total = filtered.size();
        int start = Math.min(offset, total);
        int end = Math.min(start + limit, total);

        // Converting into DTO
        List<KkdIssueDto> pageContent = filtered.subList(start, end).stream()
                .map(issue -> KkdIssueDto.builder()
                        .id(issue.getId())
                        .unitId(issue.getUnitId())
                        .goalId(issue.getGoalId())
                        .krId(issue.getKrId())
                        .severity(issue.getSeverity())
                        .ruleCode(issue.getRule().getCode())
                        .ruleTitle(issue.getRule().getTitle())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("items", pageContent);
        response.put("total", total);
        response.put("limit", limit);
        response.put("offset", offset);

        return ResponseEntity.ok(response);
    }
}