package com.sm.lt.infrastructure.jmeter;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AnalysisResult {
    boolean reportEmpty;
    List<String> brokenAssertions;
}