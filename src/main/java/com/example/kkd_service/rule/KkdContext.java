package com.example.kkd_service.rule;

import com.example.kkd_service.entity.Goal;
import com.example.kkd_service.entity.KeyResult;
import com.example.kkd_service.entity.Unit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class KkdContext {
    private final List<Unit> units;
    private final List<Goal> activeGoals;
    private final List<KeyResult> keyResults;
}