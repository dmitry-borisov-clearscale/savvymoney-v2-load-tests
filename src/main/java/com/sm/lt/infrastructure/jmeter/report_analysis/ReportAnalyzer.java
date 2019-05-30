package com.sm.lt.infrastructure.jmeter.report_analysis;

import java.util.Optional;

import org.apache.commons.csv.CSVRecord;

public interface ReportAnalyzer {

    void analyze(CSVRecord record);

    Optional<String> getResult();
}