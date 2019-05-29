package com.sm.lt.infrastructure.configuration;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.*;
import static org.hamcrest.collection.IsEmptyCollection.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.typesafe.config.Config;

public class ConfigurationTest {

    @Test
    public void defaultAssertions() {
        Configuration configuration = ConfigurationUtils.getConfiguration("infrastructure/assertions_not_redefined.conf");
        Config exampleTest = configuration.getAssertions("ExampleTest");
        assertThat(exampleTest.getConfig("maxFailuresPercent").getBoolean("enabled"), is(true));
        assertThat(exampleTest.getConfig("maxFailuresPercent").getInt("value"), is(0));
        assertThat(exampleTest.getConfig("maxResponseTime").getBoolean("enabled"), is(true));
        assertThat(exampleTest.getConfig("maxResponseTime").getInt("value"), is(10000));
        assertThat(exampleTest.getConfig("maxAverageResponseTime").getBoolean("enabled"), is(true));
        assertThat(exampleTest.getConfig("maxAverageResponseTime").getInt("value"), is(5000));
        assertThat(exampleTest.getConfigList("perRequest"), empty());
    }

    @Test
    public void assertionsDisabled() {
        Configuration configuration = ConfigurationUtils.getConfiguration("infrastructure/assertions_disabled.conf");
        Config exampleTest = configuration.getAssertions("ExampleTest");
        assertThat(exampleTest.getConfig("maxFailuresPercent").getBoolean("enabled"), is(false));
        assertThat(exampleTest.getConfig("maxFailuresPercent").getInt("value"), is(0));
        assertThat(exampleTest.getConfig("maxResponseTime").getBoolean("enabled"), is(true));
        assertThat(exampleTest.getConfig("maxResponseTime").getInt("value"), is(10000));
        assertThat(exampleTest.getConfig("maxAverageResponseTime").getBoolean("enabled"), is(false));
        assertThat(exampleTest.getConfig("maxAverageResponseTime").getInt("value"), is(5000));
    }

    @Test
    public void assertionsRedefinedWithCusromRequest() {
        Configuration configuration = ConfigurationUtils.getConfiguration("infrastructure/assertions_with_any_custom_request.conf");
        Config exampleTest = configuration.getAssertions("ExampleTest");
        assertThat(exampleTest.getConfigList("perRequest"), hasSize(1));
        assertThat(exampleTest.getConfigList("perRequest").get(0).getString("regex"), is("/presentation/register/.*"));
        assertThat(exampleTest.getConfigList("perRequest").get(0).getInt("maxFailuresPercent"), is(15));
        assertThat(exampleTest.getConfigList("perRequest").get(0).getInt("maxResponseTime"), is(3000));
        assertThat(exampleTest.getConfigList("perRequest").get(0).getInt("maxAverageResponseTime"), is(1500));
    }
}