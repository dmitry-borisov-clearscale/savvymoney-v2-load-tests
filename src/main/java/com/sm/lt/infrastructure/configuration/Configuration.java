package com.sm.lt.infrastructure.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;

@Slf4j
@Builder
public class Configuration {

    private final Config environments;
    private final Config envValues;
    private final Config properties;
    private final Config defaults;
    private final Config local;
    private final Config test;

    public Config getEnvironment() {
        String environment = get("environment", Config::getString);
        log.info("Got environment: {}", environment);
        return environments.getConfig(environment);
    }

    public <T> T get(String path, BiFunction<Config, String, T> methodRef) {
        Config config = properties
                .withFallback(envValues)
                .withFallback(local)
                .withFallback(test)
                .withFallback(defaults);
        return get(config, path, methodRef);
    }

    public Map<String, String> getVariables(List<TestVariableSetting> variables) {
        Config highPriority = properties.withFallback(envValues);
        Config lowPriority = local.withFallback(test).withFallback(defaults);

        ImmutableMap.Builder<String, String> result = ImmutableMap.builder();
        for (TestVariableSetting variable : variables) {
            String specificName = variable.getTestName() + "." + variable.getVariableName();
            Optional<String> alias = variable.getAlias();

            if (get(highPriority, specificName, Config::hasPath)) {
                result.put(variable.getVariableName(), get(highPriority, specificName, Config::getString));
                continue;
            }
            if (alias.isPresent()) {
                if (get(highPriority, alias.get(), Config::hasPath)) {
                    result.put(variable.getVariableName(), get(highPriority, alias.get(), Config::getString));
                    continue;
                }
            }

            if (get(lowPriority, specificName, Config::hasPath)) {
                result.put(variable.getVariableName(), get(lowPriority, specificName, Config::getString));
                continue;
            }
            if (alias.isPresent()) {
                if (get(lowPriority, alias.get(), Config::hasPath)) {
                    result.put(variable.getVariableName(), get(lowPriority, alias.get(), Config::getString));
                    continue;
                }
            }

            log.error("Required variable missed. TestName: {}. VariableName: {}. Alias: {}. HighPriority: {}. LowPriority: {}",
                    variable.getTestName(), variable.getVariableName(), alias, highPriority, lowPriority);
            throw new IllegalStateException("Required variable missed");
        }
        return result.build();
    }

    private <T> T get(Config config, String path, BiFunction<Config, String, T> methodRef) {
        return methodRef.apply(config, path);
    }
}