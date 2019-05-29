package com.sm.lt.infrastructure.jmeter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.collect.ImmutableList;
import com.sm.lt.infrastructure.jmeter.report_analysis.MaxAverageResponseTimeReportAnalyzer;
import com.sm.lt.infrastructure.jmeter.report_analysis.MaxFailuresPercentReportAnalyzer;
import com.sm.lt.infrastructure.jmeter.report_analysis.MaxResponseTimeReportAnalyzer;
import com.sm.lt.infrastructure.jmeter.report_analysis.ReportAnalyzer;
import com.sm.lt.infrastructure.jmeter.report_analysis.UrlRegexFilteringAnalyzer;
import com.typesafe.config.Config;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class JMeterResultsAnalyzer {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withFirstRecordAsHeader();

    public static AnalysisResult analyze(Path resultsReport, Config config) throws IOException {
        if (isEmptyReport(resultsReport)) {
            return AnalysisResult
                    .builder()
                    .brokenAssertions(ImmutableList.of())
                    .reportEmpty(true)
                    .build();
        }

        CSVParser csv = CSVParser.parse(resultsReport.toFile(), StandardCharsets.UTF_8, CSV_FORMAT);
        List<ReportAnalyzer> analyzers = buildAnalyzersList(config);
        for (CSVRecord record : csv) {
            for (ReportAnalyzer analyzer : analyzers) {
                analyzer.analyze(record);
            }
        }

        List<String> brokenAssertions = analyzers
                .stream()
                .map(ReportAnalyzer::getResult)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return AnalysisResult
                .builder()
                .brokenAssertions(brokenAssertions)
                .reportEmpty(false)
                .build();
    }

    private static boolean isEmptyReport(Path resultsReport) throws IOException {
        CSVParser csv = CSVParser.parse(resultsReport.toFile(), StandardCharsets.UTF_8, CSV_FORMAT);
        return !csv.iterator().hasNext();
    }

    private static List<ReportAnalyzer> buildAnalyzersList(Config config) {
        ArrayList<ReportAnalyzer> result = new ArrayList<>();

        Config maxFailuresPercent = config.getConfig("maxFailuresPercent");
        if (maxFailuresPercent.getBoolean("enabled")) {
            result.add(new MaxFailuresPercentReportAnalyzer(maxFailuresPercent.getDouble("value")));
        }

        Config maxAverageResponseTime = config.getConfig("maxAverageResponseTime");
        if (maxAverageResponseTime.getBoolean("enabled")) {
            result.add(new MaxAverageResponseTimeReportAnalyzer(maxAverageResponseTime.getLong("value")));
        }

        Config maxResponseTime = config.getConfig("maxResponseTime");
        if (maxResponseTime.getBoolean("enabled")) {
            result.add(new MaxResponseTimeReportAnalyzer(maxResponseTime.getLong("value")));
        }

        for (Config request : config.getConfigList("perRequest")) {
            if (request.hasPath("maxFailuresPercent")) {
                ReportAnalyzer analyzer = new MaxFailuresPercentReportAnalyzer(request.getDouble("maxFailuresPercent"));
                result.add(new UrlRegexFilteringAnalyzer(request.getString("regex"), analyzer));
            }
            if (request.hasPath("maxAverageResponseTime")) {
                ReportAnalyzer analyzer = new MaxAverageResponseTimeReportAnalyzer(request.getLong("maxAverageResponseTime"));
                result.add(new UrlRegexFilteringAnalyzer(request.getString("regex"), analyzer));
            }
            if (request.hasPath("maxResponseTime")) {
                ReportAnalyzer analyzer = new MaxResponseTimeReportAnalyzer(request.getLong("maxResponseTime"));
                result.add(new UrlRegexFilteringAnalyzer(request.getString("regex"), analyzer));
            }
        }
        return result;
    }

}