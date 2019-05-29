package com.sm.lt.infrastructure.jmeter.report_analysis;

import static java.lang.String.format;

import java.util.Optional;

import org.apache.commons.csv.CSVRecord;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MaxFailuresPercentReportAnalyzer implements ReportAnalyzer {

    private final double maxFailuresPercent;
    private int failuresCounter = 0;
    private int allCounter = 0;

    @Override
    public void analyze(CSVRecord record) {
        allCounter++;
        if (!Boolean.parseBoolean(record.get(ReportColumn.SUCCESS_COLUMN_INDEX))) {
            failuresCounter++;
        }
    }

    @Override
    public Optional<String> getResult() {
        if (allCounter == 0) {
            return Optional.empty();
        }
        double actualPercentage = ((double) failuresCounter / allCounter) * 100;

        return (actualPercentage > maxFailuresPercent)
                ? Optional.of(format("maxFailuresPercent exceeded. Expected: %.2f%%. Got: %.2f%%", maxFailuresPercent, actualPercentage))
                : Optional.empty();
    }
}