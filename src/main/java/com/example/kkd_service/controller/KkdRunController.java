package com.example.kkd_service.controller;

import com.example.kkd_service.entity.KkdRun;
import com.example.kkd_service.service.KkdRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runs")
@RequiredArgsConstructor
public class KkdRunController {

    private final KkdRunService kkdRunService;

    @PostMapping
    public ResponseEntity<KkdRun> executeRun() {
        KkdRun run = kkdRunService.executeNewRun();
        return ResponseEntity.ok(run);
    }
}