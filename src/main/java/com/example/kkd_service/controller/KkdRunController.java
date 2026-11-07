package com.example.kkd_service.controller;

import com.example.kkd_service.entity.KkdRun;
import com.example.kkd_service.repository.KkdRunRepository;
import com.example.kkd_service.service.KkdRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/runs")
@RequiredArgsConstructor
public class KkdRunController {

    private final KkdRunService kkdRunService;
    private final KkdRunRepository kkdRunRepository;

    @PostMapping
    public ResponseEntity<KkdRun> executeRun() {
        KkdRun run = kkdRunService.executeNewRun();
        return ResponseEntity.ok(run);
    }

    @GetMapping
    public ResponseEntity<List<KkdRun>> getAllRuns() {
        return ResponseEntity.ok(kkdRunRepository.findAll());
    }
}