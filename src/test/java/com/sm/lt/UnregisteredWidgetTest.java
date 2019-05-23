package com.sm.lt;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Lists;
import com.sm.lt.api.Session;
import com.sm.lt.api.User;
import com.sm.lt.infrastructure.configuration.ConfigurationParser;
import com.sm.lt.infrastructure.jmeter.JMeterTestExecutor;
import com.sm.lt.infrastructure.junit.CurrentTest;

@Slf4j
public class UnregisteredWidgetTest {

    @Rule
    public CurrentTest currentTest = new CurrentTest();

    private static final String JMETER_TEST_PLAN = "unregistered_widget/test_plan.jmx";
    private static final String TEST_PLAN_CONFIGURATION = "unregistered_widget/test_plan.conf";

    @Test
    public void test() throws Exception {
        List<User> users = ConfigurationParser.getUsers(TEST_PLAN_CONFIGURATION, "users");
        List<Session> sessions = Lists.transform(users, Session::start);

        currentTest.saveToTestFolder("data.csv", sessions.stream().map(Session::getSmToken).collect(Collectors.joining("\n")));
        Path testPlan = currentTest.copyToTestFolder("test_plan.jmx", JMETER_TEST_PLAN);
        JMeterTestExecutor
                .builder()
                .testPlan(testPlan)
                .testFolder(currentTest.getTestFolder())
                .resultsFolder(currentTest.getResultsFolder())
                .build()
                .run();
    }
}