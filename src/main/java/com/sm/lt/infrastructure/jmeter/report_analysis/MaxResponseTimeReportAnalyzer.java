package com.sm.lt.infrastructure.jmeter.report_analysis;

import java.util.Optional;

import org.apache.commons.csv.CSVRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class MaxResponseTimeReportAnalyzer implements ReportAnalyzer {

    private final long maxResponseTime;
    private int counter;

    @Override
    public void analyze(CSVRecord record) {
        if (Long.parseLong(record.get(ReportColumn.ELAPSED_COLUMN_INDEX)) > maxResponseTime) {
            counter++;
        }
    }

    @Override
    public Optional<String> getResult() {
        log.debug("Counter: {}. maxResponseTime: {}", counter, maxResponseTime);
        return (counter > 0)
                ? Optional.of("maxResponseTime exceeded. Got " + counter + " request(s) longer than " + maxResponseTime + "ms")
                : Optional.empty();
    }
}