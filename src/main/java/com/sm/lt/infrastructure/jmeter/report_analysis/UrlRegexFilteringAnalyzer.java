package com.sm.lt.infrastructure.jmeter.report_analysis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UrlRegexFilteringAnalyzer implements ReportAnalyzer {
    private final String regex;
    private final ReportAnalyzer analyzer;

    @Override
    public void analyze(CSVRecord record) {
        URL url;
        try {
            url = new URL(record.get(ReportColumn.URL_COLUMN_INDEX));
        } catch (MalformedURLException ignored) {
            // do nothing
            return;
        }

        Pattern pattern = Pattern.compile(regex);
        if (pattern.matcher(url.getPath()).matches()) {
            analyzer.analyze(record);
        }
    }

    @Override
    public Optional<String> getResult() {
        return analyzer.getResult().map(str -> "For regex: " + regex + ". " + str);
    }
}