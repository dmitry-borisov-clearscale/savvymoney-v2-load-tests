package com.sm.lt;

import org.junit.Rule;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import com.sm.lt.infrastructure.junit.CurrentTestFiles;

@Slf4j
public class UnregisteredWidgetTest {

    @Rule
    public CurrentTestFiles currentTestFiles = new CurrentTestFiles();

    private static final String JMETER_TEST_PLAN = "unregistered_widget/test_plan.jmx";
    private static final String TEST_PLAN_CONFIGURATION = "unregistered_widget/test_plan.conf";

    @Test
    public void test() throws Exception {
//        List<User> users = ConfigurationParser.getUsers(TEST_PLAN_CONFIGURATION, "users");
//        List<Session> sessions = Lists.transform(users, Session::start);
//
//        currentTestFiles.saveToTestFolder("data.csv", sessions.stream().map(Session::getSmToken).collect(Collectors.joining("\n")));
//        Path testPlan = currentTestFiles.copyToTestFolder("test_plan.jmx", JMETER_TEST_PLAN);
//        JMeterTestExecutor
//                .builder()
//                .testPlan(testPlan)
//                .testFolder(currentTestFiles.getTestFolder())
//                .resultsFolder(currentTestFiles.getResultsFolder())
//                .build()
//                .run();
    }
}