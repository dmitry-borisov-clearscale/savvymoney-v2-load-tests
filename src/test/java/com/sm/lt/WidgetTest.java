package com.sm.lt;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.ImmutableList;
import com.sm.lt.api.Session;
import com.sm.lt.api.User;

@Slf4j
public class WidgetTest {

    private static final List<String> UNIQUE_SSNS = ImmutableList.<String>builder()
            .add("666224057")
            .add("666234390")
            .add("666561858")
            .add("666532762")
            .add("666031027")
            .add("666901244")
            .add("666407520")
            .add("666574544")
            .add("666806797")
            .add("666947001")
            .build();

    @Test
    public void test() throws Exception {
        final List<Session> sessions = UNIQUE_SSNS
                .stream()
                .map(WidgetTest::user)
                .map(Session::start)
                .collect(Collectors.toList());
        run(sessions);
    }

    private static void run(List<Session> sessions) throws Exception {
        File data = new File("test_plans/data.csv");
        if (data.exists()) {
            data.delete();
        }
        try (FileOutputStream outputStream = new FileOutputStream(data)) {
            String str = sessions.stream().map(Session::getSmToken).collect(Collectors.joining("\n"));
            byte[] strToBytes = str.getBytes();
            outputStream.write(strToBytes);
        }

        // Jmeter location
        File jmeterHome = new File("test_plans");

        // Ready to start JMX scenario location
        File testPlan = new File("test_plans/Widget.jmx");

        if (!jmeterHome.exists()) {
            System.exit(3);
            return;
        }
        if (!testPlan.exists()) {
            System.exit(2);
            return;
        }
        File jmeterProperties = new File(jmeterHome.getPath() + File.separator + "bin" + File.separator + "jmeter.properties");
        if (!jmeterProperties.exists()) {
            System.exit(1);
            return;
        }
        // JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // Initialize Properties, locale, etc.
        JMeterUtils.setJMeterHome(jmeterHome.getPath());
        JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
        JMeterUtils.initLocale();

        // Set directory for HTML report
        String repDir = jmeterHome.getPath() + File.separator + "HTMLReport";
        JMeterUtils.setProperty("jmeter.reportgenerator.exporter.html.property.output_dir", repDir);

        // Initialize JMeter SaveService
        SaveService.loadProperties();

        // Load existing .jmx Test Plan
        HashTree testPlanTree = SaveService.loadTree(testPlan);

        //add Summarizer output to get test progress in stdout like:
        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        // Store execution results into a .jtl file
        File logFile = new File(jmeterHome + File.separator + "Widget.jtl");
        //delete log file if exists
        if (logFile.exists()) {
            boolean delete = logFile.delete();
            System.out.println("Jtl deleted: " + delete);
        }
        ResultCollector logger = new ResultCollector(summer);
        ReportGenerator reportGenerator =
                new ReportGenerator(logFile.getPath(), logger); //creating ReportGenerator for creating HTML report
        logger.setFilename(logFile.getPath());
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();

        // Report Generator
        FileUtils.deleteDirectory(new File(repDir)); //delete old report
        reportGenerator.generate();

        System.out.println("Test completed. See " + jmeterHome + File.separator + "Widget.jtl file for results");

        //Open HTML report in default browser
        File htmlFile = new File(repDir + File.separator + "index.html");
        Desktop.getDesktop().browse(htmlFile.toURI());
    }

    private static User user(String ssn) {
        return User.create().toBuilder().ssn(ssn).build();
    }
}