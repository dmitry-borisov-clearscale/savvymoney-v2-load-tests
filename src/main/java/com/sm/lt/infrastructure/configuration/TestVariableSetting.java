package com.sm.lt.infrastructure.configuration;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TestVariableSetting {

    @Getter
    private final String testName;
    @Getter
    private final String variableName;

    /**
     * It is a short name that could be used from Env Variable or Application Property (java -Dalias)
     */
    private final String alias;

    public static TestVariableSetting var(String testName, String variableName) {
        return new TestVariableSetting(testName, variableName, null);
    }

    public static TestVariableSetting var(String testName, String variableName, String alias) {
        return new TestVariableSetting(testName, variableName, alias);
    }

    public Optional<String> getAlias() {
        return Optional.ofNullable(alias);
    }
}