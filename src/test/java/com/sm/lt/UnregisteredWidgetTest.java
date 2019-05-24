package com.sm.lt;

import static com.sm.lt.infrastructure.configuration.TestVariableSetting.var;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.sm.lt.infrastructure.jmeter.JMeterTestExecutor;
import com.sm.lt.infrastructure.junit.CurrentEnvironmentSetter;
import com.sm.lt.infrastructure.junit.CurrentTestFiles;
import com.typesafe.config.Config;

@Slf4j
public class UnregisteredWidgetTest {

    private static final String JMETER_TEST_PLAN = "unregistered_widget/test_plan.jmx";
    private static final String TEST_PLAN_CONFIGURATION = "unregistered_widget/test_plan.conf";

    private static final Configuration CONFIGURATION = ConfigurationUtils.getConfiguration(TEST_PLAN_CONFIGURATION);
    private static final Map<String, String> VARIABLES = CONFIGURATION.getVariables(ImmutableList.of(
            var("UnregisteredWidget", "numberOfThreads"),
            var("UnregisteredWidget", "rumpUpPeriod"),
            var("UnregisteredWidget", "loopCount"),
            var("UnregisteredWidget", "thinkTime")));

    @ClassRule
    public static final CurrentEnvironmentSetter currentEnvironmentSetter = new CurrentEnvironmentSetter(CONFIGURATION);

    @Rule
    public CurrentTestFiles currentTestFiles = new CurrentTestFiles();

    @Test
    public void test() throws Exception {
        List<User> users = ConfigurationParser.getUsersWithResolving(CONFIGURATION.get("users", Config::getConfig));
        List<Session> sessions = Lists.transform(users, Session::start);

        currentTestFiles.saveToTestFolder("data.csv", sessions.stream().map(Session::getSmToken).collect(Collectors.joining("\n")));
        Path testPlan = currentTestFiles.copyToTestFolder("test_plan.jmx", JMETER_TEST_PLAN);
        JMeterTestExecutor
                .builder()
                .variables(VARIABLES)
                .testPlan(testPlan)
                .testFolder(currentTestFiles.getTestFolder())
                .resultsFolder(currentTestFiles.getResultsFolder())
                .build()
                .run();
    }
}