package com.sm.lt.infrastructure.jmeter.report_analysis;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVRecord;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class MaxAverageResponseTimeReportAnalyzer implements ReportAnalyzer {

    private final long maxAverageResponseTime;
    private final List<Long> times = new ArrayList<>();

    @Override
    public void analyze(CSVRecord record) {
        times.add(Long.parseLong(record.get(ReportColumn.ELAPSED_COLUMN_INDEX)));
    }

    @Override
    public Optional<String> getResult() {
        double average = times.stream().mapToLong(i -> i).average().orElse(0);
        return (average > maxAverageResponseTime)
                ? Optional.of(format("maxAverageResponseTime exceeded. Expected: %s. Got: %.2f", maxAverageResponseTime, average))
                : Optional.empty();
    }
}