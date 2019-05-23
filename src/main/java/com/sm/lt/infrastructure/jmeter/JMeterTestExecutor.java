package com.sm.lt.infrastructure.jmeter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import lombok.Builder;

import com.google.common.io.Resources;

@Builder
public class JMeterTestExecutor {

    private static final URL JMETER_CONFIGURATION_FOLDER = Resources.getResource("jmeter_configuration");

    private final Path testPlan;
    private final Path testFolder;
    private final Path resultsFolder;

    public void run() throws URISyntaxException, IOException, ConfigurationException, GenerationException {
        // Jmeter location
        Path jmeterHome = Paths.get(JMETER_CONFIGURATION_FOLDER.toURI());
        Path jmeterProperties = jmeterHome.resolve("bin").resolve("jmeter.properties");

        // JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // Initialize Properties, locale, etc.
        JMeterUtils.setJMeterHome(jmeterHome.toString());
        JMeterUtils.loadJMeterProperties(jmeterProperties.toString());
        JMeterUtils.initLocale();

        // Set directory for HTML report
        JMeterUtils.setProperty("jmeter.reportgenerator.exporter.html.property.output_dir", resultsFolder.toString());

        // Initialize JMeter SaveService
        SaveService.loadProperties();
        FileServer.getFileServer().setBasedir(testFolder.toString());

        // Load existing .jmx Test Plan
        HashTree testPlanTree = SaveService.loadTree(testPlan.toFile());

        //add Summarizer output to get test progress in stdout like:
        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        // Store execution results into a .jtl file
        Path logFile = testFolder.resolve("result.jtl");
        ResultCollector logger = new ResultCollector(summer);
        ReportGenerator reportGenerator =
                new ReportGenerator(logFile.toString(), logger); //creating ReportGenerator for creating HTML report
        logger.setFilename(logFile.toString());
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();

        // Report Generator
        FileUtils.deleteDirectory(resultsFolder.toFile()); //delete old report
        reportGenerator.generate();
    }
}