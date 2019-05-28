package com.sm.lt.infrastructure.jmeter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import lombok.Builder;

import com.google.common.io.Resources;
import com.sm.lt.infrastructure.configuration.CurrentEnvironment;

@Builder
public class JMeterTestExecutor {

    private static final URL JMETER_CONFIGURATION_FOLDER = Resources.getResource("jmeter_configuration");

    private final Path testPlan;
    private final Path testFolder;
    private final Path resultsFolder;
    private final Map<String, String> variables;

    /**
     * Configures and executes JMeter scenario.
     *
     * @return Path to results file. It is jtl-file, but internally it has CSV format.
     */
    public Path run() throws URISyntaxException, IOException, ConfigurationException, GenerationException {
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

        // this is required in order to use relative file paths, as example for CSV Data Set Config
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
        TestPlan testPlan = getTestPlan(testPlanTree);
        testPlanTree.add(testPlan, logger);

        // Set variables and save final test plan
        testPlan.setUserDefinedVariables(constructArguments());
        try (FileOutputStream out = new FileOutputStream(testFolder.resolve("test_plan_final.jmx").toFile())) {
            SaveService.saveTree(testPlanTree, out);
        }

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();

        // Report Generator
        FileUtils.deleteDirectory(resultsFolder.toFile()); //delete old report
        reportGenerator.generate();

        return logFile;
    }

    private Arguments constructArguments() throws MalformedURLException {
        Arguments result = new Arguments();

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result.addArgument(argument(entry.getKey(), entry.getValue()));
        }
        URL api = new URL(CurrentEnvironment.PRESENTATION_API_URL);
        result.addArgument(argument("apiProtocol", api.getProtocol()));
        result.addArgument(argument("apiHost", api.getHost()));
        result.addArgument(argument("apiPort", String.valueOf(getPort(api))));

        return result;
    }

    private static int getPort(URL api) {
        int port = api.getPort();
        if (port != -1) {
            return port;
        }
        return "http".equals(api.getProtocol()) ? 80 : 443;
    }

    private static TestPlan getTestPlan(HashTree testPlanTree) {
        return (TestPlan) testPlanTree.getArray()[0];
    }

    private static Argument argument(String name, String value) {
        Argument result = new Argument(name, value);
        result.setMetaData("=");
        return result;
    }
}