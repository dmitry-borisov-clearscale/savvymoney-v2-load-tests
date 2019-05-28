package com.sm.lt.infrastructure.jmeter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class JMeterResultsAnalyzer {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withFirstRecordAsHeader();
    private static final int SUCCESS_COLUMN_INDEX = 7;

    public static boolean noErrorsInReport(Path resultsReport) throws IOException {
        CSVParser csv = CSVParser.parse(resultsReport.toFile(), StandardCharsets.UTF_8, CSV_FORMAT);
        for (CSVRecord record : csv) {
            boolean success = Boolean.parseBoolean(record.get(SUCCESS_COLUMN_INDEX));
            if (!success) {
                return false;
            }
        }
        return true;
    }

    public static boolean notEmptyReport(Path resultsReport) throws IOException {
        CSVParser csv = CSVParser.parse(resultsReport.toFile(), StandardCharsets.UTF_8, CSV_FORMAT);
        return csv.iterator().hasNext();
    }
}