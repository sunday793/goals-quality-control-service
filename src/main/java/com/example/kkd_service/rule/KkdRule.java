package com.example.kkd_service.rule;

import java.util.List;

public interface KkdRule {
    String getCode();
    String getSeverity();
    List<FoundIssue> evaluate();
}