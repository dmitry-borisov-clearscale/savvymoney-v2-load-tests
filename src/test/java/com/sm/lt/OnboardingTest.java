package com.sm.lt;

import static com.sm.lt.infrastructure.configuration.TestVariableSetting.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sm.lt.api.Session;
import com.sm.lt.api.User;
import com.sm.lt.infrastructure.configuration.Configuration;
import com.sm.lt.infrastructure.configuration.ConfigurationParser;
import com.sm.lt.infrastructure.configuration.ConfigurationUtils;
import com.sm.lt.infrastructure.jmeter.JMeterResultsAnalyzer;
import com.sm.lt.infrastructure.jmeter.JMeterTestExecutor;
import com.sm.lt.infrastructure.junit.CurrentEnvironmentSetter;
import com.sm.lt.infrastructure.junit.CurrentTestFiles;
import com.typesafe.config.Config;

@Slf4j
public class OnboardingTest {

    private static final String JMETER_TEST_PLAN = "onboarding/test_plan.jmx";
    private static final String TEST_PLAN_CONFIGURATION = "onboarding/test_plan.conf";

    private static final Configuration CONFIGURATION = ConfigurationUtils.getConfiguration(TEST_PLAN_CONFIGURATION);
    private static final Map<String, String> VARIABLES = CONFIGURATION.getVariables(ImmutableList.of(
            var("OnboardingTest", "numberOfThreads"),
            var("OnboardingTest", "rumpUpPeriod"),
            var("OnboardingTest", "loopCount"),
            var("OnboardingTest", "thinkTime"),
            var("OnboardingTest", "skipAuth")));

    @ClassRule
    public static final CurrentEnvironmentSetter currentEnvironmentSetter = new CurrentEnvironmentSetter(CONFIGURATION);

    @Rule
    public CurrentTestFiles currentTestFiles = new CurrentTestFiles();

    @Test
    public void test() throws Exception {
        List<User> users = ConfigurationParser.getUsersWithResolvingAndPmiCreation(CONFIGURATION.get("users", Config::getConfig));
        List<Session> sessions = Lists.transform(users, Session::start);

        currentTestFiles.saveToTestFolder("data.csv", sessions
                .stream()
                .map(OnboardingTest::constructUserRow)
                .collect(Collectors.joining("\n")));
        Path testPlan = currentTestFiles.copyToTestFolder("test_plan.jmx", JMETER_TEST_PLAN);
        Path result = JMeterTestExecutor
                .builder()
                .variables(VARIABLES)
                .testPlan(testPlan)
                .testFolder(currentTestFiles.getTestFolder())
                .resultsFolder(currentTestFiles.getResultsFolder())
                .build()
                .run();

        Assert.assertTrue("Report is empty: " + result, JMeterResultsAnalyzer.notEmptyReport(result));
        Assert.assertTrue("There are errors logged in final report: " + result, JMeterResultsAnalyzer.noErrorsInReport(result));
    }

    private static String constructUserRow(Session session) {
        return session.getSmToken()
                + ',' + session.getUser().getAddress1()
                + ',' + session.getUser().getCity()
                + ',' + session.getUser().getEmail()
                + ',' + session.getUser().getFirstName()
                + ',' + session.getUser().getLastName()
                + ',' + session.getUser().getState()
                + ',' + session.getUser().getZip();
    }
}