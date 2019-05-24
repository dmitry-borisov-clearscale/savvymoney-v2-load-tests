package com.sm.lt.infrastructure.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sm.lt.infrastructure.configuration.Configuration;
import com.sm.lt.infrastructure.configuration.CurrentEnvironment;
import com.sm.lt.infrastructure.services.iframe.IFrameUtils;
import com.typesafe.config.Config;

@Slf4j
@AllArgsConstructor
public class CurrentEnvironmentSetter implements TestRule {

    private final Configuration configuration;

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                Config environment = configuration.getEnvironment();
                CurrentEnvironment.CREDIT_SESSION_MANAGER_CLIENT_URL = environment.getString("creditSessionManager.url");
                CurrentEnvironment.BASE_UI_URL = environment.getString("base.url");
                CurrentEnvironment.USER_TO_PARAMETERS_MAPPER_RELAY_STATE = environment.getString("sso.url");
                CurrentEnvironment.USER_TO_PARAMETERS_MAPPER_RELAY_ENTITY_ID = "savvymoney-test";
                CurrentEnvironment.IFRAME_UTILS_URL = environment.getString("iframe.url");
                CurrentEnvironment.CREDIT_SERVICE_CLIENT_URL = environment.getString("creditService.url");
                CurrentEnvironment.PRESENTATION_API_URL = environment.getString("presentationAPI.url");
                CurrentEnvironment.ENV = IFrameUtils.Environment.fromString(configuration.get("environment", Config::getString));
                CurrentEnvironment.PID = configuration.get("pid", Config::getLong);
                try {
                    base.evaluate();
                } finally {
                    CurrentEnvironment.CREDIT_SESSION_MANAGER_CLIENT_URL = null;
                    CurrentEnvironment.BASE_UI_URL = null;
                    CurrentEnvironment.USER_TO_PARAMETERS_MAPPER_RELAY_STATE = null;
                    CurrentEnvironment.USER_TO_PARAMETERS_MAPPER_RELAY_ENTITY_ID = null;
                    CurrentEnvironment.IFRAME_UTILS_URL = null;
                    CurrentEnvironment.CREDIT_SERVICE_CLIENT_URL = null;
                    CurrentEnvironment.PRESENTATION_API_URL = null;
                    CurrentEnvironment.ENV = null;
                    CurrentEnvironment.PID = null;
                }
            }
        };
    }
}