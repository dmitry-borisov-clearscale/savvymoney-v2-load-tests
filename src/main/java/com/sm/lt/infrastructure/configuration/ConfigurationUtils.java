package com.sm.lt.infrastructure.configuration;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigurationUtils {

    private static final String ENV_VAR_PREFIX = "SAVVY_";
    private static final String PROP_PREFIX = "savvy.";

    /**
     * folder to store result of tests
     * (this is set in pom.xml file)
     */
    private static final String SAVVY_JMETER_TESTS_FOLDER = "SAVVY_JMETER_TESTS_FOLDER";

    /**
     * file with local settings unique for every developer
     * (this is set in pom.xml file)
     */
    private static final String SAVVY_LOCAL_SETTING = "SAVVY_LOCAL_SETTING";

    /**
     * all environment variables that correspond to application logic
     */
    private static final Map<String, String> SAVVY_ENV_VALUES = System
            .getenv()
            .entrySet()
            .stream()
            .filter(e -> e.getKey().startsWith(ENV_VAR_PREFIX))
            .collect(Collectors.toMap(ConfigurationUtils::transformEnvVarKey, Map.Entry::getValue));

    private static final Config ENV_VALUES_CONFIG = ConfigFactory.parseMap(SAVVY_ENV_VALUES);

    /**
     * all application properties correspond to application logic
     */
    private static final Map<String, String> SAVVY_PROPERTIES = Maps
            .fromProperties(System.getProperties())
            .entrySet()
            .stream()
            .filter(e -> e.getKey().startsWith(PROP_PREFIX))
            .collect(Collectors.toMap(ConfigurationUtils::transformPropKey, Map.Entry::getValue));

    private static final Config PROPERTIES_CONFIG = ConfigFactory.parseMap(SAVVY_PROPERTIES);

    /**
     * available environments: dev, staging, sandbox, beta ...
     */
    private static final Config AVAILABLE_ENVIRONMENTS = ConfigFactory
            .parseResourcesAnySyntax("environments.conf")
            .getConfig("environments");

    /**
     * default configuration values
     */
    private static final Config DEFAULT_CONFIG = ConfigFactory.parseResourcesAnySyntax("defaults.conf");

    /**
     * local developer environment configuration values
     */
    private static final Config LOCAL_CONFIG = ConfigFactory.parseFile(new File(localSettingsFileName()));

    public static Optional<String> getTestsRootFolder() {
        String result = SAVVY_ENV_VALUES.get(SAVVY_JMETER_TESTS_FOLDER);
        return Strings.isNullOrEmpty(result)
                ? Optional.empty()
                : Optional.of(result);
    }

    public static Configuration getConfiguration(String testConfigurationResourceName) {
        return Configuration
                .builder()
                .environments(AVAILABLE_ENVIRONMENTS)
                .envValues(ENV_VALUES_CONFIG)
                .properties(PROPERTIES_CONFIG)
                .defaults(DEFAULT_CONFIG)
                .local(LOCAL_CONFIG)
                .test(ConfigFactory.parseResourcesAnySyntax(testConfigurationResourceName))
                .build();
    }

    private static String localSettingsFileName() {
        String result = SAVVY_ENV_VALUES.get(SAVVY_LOCAL_SETTING);
        return Strings.isNullOrEmpty(result)
                ? "local_settings" + File.separator + "local.conf"
                : result;
    }

    private static String transformEnvVarKey(Map.Entry<String, String> entry) {
        return entry.getKey().substring(ENV_VAR_PREFIX.length()).replaceAll("_", ".");
    }

    private static String transformPropKey(Map.Entry<String, String> entry) {
        return entry.getKey().substring(PROP_PREFIX.length());
    }

}