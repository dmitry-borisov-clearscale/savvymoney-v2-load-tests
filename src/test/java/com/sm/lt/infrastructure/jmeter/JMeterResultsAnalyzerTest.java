package com.sm.lt.infrastructure.jmeter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.*;
import static org.hamcrest.collection.IsEmptyCollection.*;

import java.net.URL;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;
import com.sm.lt.infrastructure.configuration.Configuration;
import com.sm.lt.infrastructure.configuration.ConfigurationUtils;
import com.typesafe.config.Config;

public class JMeterResultsAnalyzerTest {

    private static final URL REPORT = Resources.getResource("infrastructure/jmeter_results_analyzer_example.csv");
    private static final URL EMPTY_REPORT = Resources.getResource("infrastructure/jmeter_empty_report.csv");

    @Test
    public void emptyReport() throws Exception {
        Configuration configuration = ConfigurationUtils.getConfiguration("infrastructure/assertions_not_redefined.conf");
        Config config = configuration.getAssertions("ExampleTest");
        AnalysisResult analysisResult = JMeterResultsAnalyzer.analyze(Paths.get(EMPTY_REPORT.toURI()), config);
        Assert.assertTrue(analysisResult.isReportEmpty());
        Assert.assertThat(analysisResult.getBrokenAssertions(), empty());
    }

    @Test
    public void assertionsDisabled() throws Exception {
        Configuration configuration = ConfigurationUtils.getConfiguration("infrastructure/assertions_disabled.conf");
        Config config = configuration.getAssertions("ExampleTest");
        AnalysisResult analysisResult = JMeterResultsAnalyzer.analyze(Paths.get(REPORT.toURI()), config);
        Assert.assertFalse(analysisResult.isReportEmpty());
        Assert.assertThat(analysisResult.getBrokenAssertions(), empty());
    }

    @Test
    public void defaultAssertions() throws Exception {
        Configuration configuration = ConfigurationUtils.getConfiguration("infrastructure/assertions_not_redefined.conf");
        Config config = configuration.getAssertions("ExampleTest");
        AnalysisResult analysisResult = JMeterResultsAnalyzer.analyze(Paths.get(REPORT.toURI()), config);
        Assert.assertFalse(analysisResult.isReportEmpty());
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasSize(1));
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasItem("maxFailuresPercent exceeded. Expected: 0.0%. Got: 50.0%"));
    }

    @Test
    public void maxFailuresPercentRedefined() throws Exception {
        Configuration configuration = ConfigurationUtils.getConfiguration("infrastructure/assertions_maxFailuresPercent_redefined.conf");
        Config config = configuration.getAssertions("ExampleTest");
        AnalysisResult analysisResult = JMeterResultsAnalyzer.analyze(Paths.get(REPORT.toURI()), config);
        Assert.assertFalse(analysisResult.isReportEmpty());
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasSize(1));
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasItem("maxFailuresPercent exceeded. Expected: 25.5%. Got: 50.0%"));
    }

    @Test
    public void maxResponseTimeRedefined() throws Exception {
        Configuration configuration = ConfigurationUtils.getConfiguration("infrastructure/assertions_maxResponseTime_redefined.conf");
        Config config = configuration.getAssertions("ExampleTest");
        AnalysisResult analysisResult = JMeterResultsAnalyzer.analyze(Paths.get(REPORT.toURI()), config);
        Assert.assertFalse(analysisResult.isReportEmpty());
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasSize(2));
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasItem("maxResponseTime exceeded. Got 3 request(s) longer than 2000ms"));
    }

    @Test
    public void maxAverageResponseTimeRedefined() throws Exception {
        Configuration configuration =
                ConfigurationUtils.getConfiguration("infrastructure/assertions_maxAverageResponseTime_redefined.conf");
        Config config = configuration.getAssertions("ExampleTest");
        AnalysisResult analysisResult = JMeterResultsAnalyzer.analyze(Paths.get(REPORT.toURI()), config);
        Assert.assertFalse(analysisResult.isReportEmpty());
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasSize(2));
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasItem("maxAverageResponseTime exceeded. Expected: 1000. Got: 1744.5"));
    }

    @Test
    public void customRequest() throws Exception {
        Configuration configuration =
                ConfigurationUtils.getConfiguration("infrastructure/assertions_with_any_custom_request.conf");
        Config config = configuration.getAssertions("ExampleTest");
        AnalysisResult analysisResult = JMeterResultsAnalyzer.analyze(Paths.get(REPORT.toURI()), config);
        Assert.assertFalse(analysisResult.isReportEmpty());
        Assert.assertThat(analysisResult.getBrokenAssertions(), hasSize(4));
        Assert.assertThat(analysisResult.getBrokenAssertions(),
                hasItem("For regex: /presentation/register/.*. maxFailuresPercent exceeded. Expected: 15.00%. Got: 66.67%"));
        Assert.assertThat(analysisResult.getBrokenAssertions(),
                hasItem("For regex: /presentation/register/.*. maxResponseTime exceeded. Got 2 request(s) longer than 1000ms"));
        Assert.assertThat(analysisResult.getBrokenAssertions(),
                hasItem("For regex: /presentation/register/.*. maxAverageResponseTime exceeded. Expected: 300. Got: 1418.67"));
    }
}