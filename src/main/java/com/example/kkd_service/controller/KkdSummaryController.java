package com.example.kkd_service.controller;

import com.example.kkd_service.dto.KkdSummaryResponse;
import com.example.kkd_service.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kkd")
@RequiredArgsConstructor
public class KkdSummaryController {

    private final SummaryService summaryService;

    @GetMapping("/summary")
    public ResponseEntity<KkdSummaryResponse> getSummary(
            @RequestParam("run_id") String runId,
            @RequestParam("unit_id") String unitId) {

        KkdSummaryResponse response = summaryService.getSummary(runId, unitId);
        return ResponseEntity.ok(response);
    }
}