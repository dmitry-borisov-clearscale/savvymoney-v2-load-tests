package com.sm.lt.infrastructure.configuration;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.collect.Lists;
import com.sm.lt.api.User;
import com.sm.lt.infrastructure.DateUtils;
import com.typesafe.config.Config;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConfigurationParser {

    public static List<User> getUsersWithoutResolving(List<? extends Config> users) {
        return Lists.transform(users, ConfigurationParser::parseUser);
    }

    public static List<User> getUsersWithResolving(Config users) {
        return Lists.transform(users.resolve().getConfigList("list"), ConfigurationParser::parseUser);
    }

    public static List<User> getUsersWithResolvingAndPmiCreation(Config users) {
        return Lists.transform(users.resolve().getConfigList("list"), ConfigurationParser::parseUserAndCreatePmi);
    }

    private static User parseUser(Config config) {
        String pmi = config.getString("pmi");
        if (!User.isValidUniqueSuffix(pmi)) {
            throw new IllegalStateException("invalid pmi");
        }
        return User
                .builder()
                .partnerId(CurrentEnvironment.PID)
                .partnerMemberID("pmi-lt-" + CurrentEnvironment.PID + "-" + pmi)
                .firstName(config.getString("firstName"))
                .lastName(config.getString("lastName"))
                .birthday(DateUtils.parseDateFromConfig(config.getString("birthday")))
                .ssn(config.getString("ssn"))
                .email("qa+lt-" + CurrentEnvironment.PID + "-" + pmi + "@example.com")
                .address1(config.getString("address1"))
                .address2(config.getString("address2"))
                .state(config.getString("state"))
                .city(config.getString("city"))
                .zip(config.getString("zip"))
                .build();
    }

    private static User parseUserAndCreatePmi(Config config) {
        String pmi = config.getString("pmi").replaceAll("<GENERATED>", String.valueOf(System.currentTimeMillis()).substring(2));
        if (!User.isValidUniqueSuffix(pmi)) {
            throw new IllegalStateException("invalid pmi");
        }
        return User
                .builder()
                .partnerId(CurrentEnvironment.PID)
                .partnerMemberID("pmi-lt-" + CurrentEnvironment.PID + "-" + pmi)
                .firstName(config.getString("firstName"))
                .lastName(config.getString("lastName"))
                .birthday(DateUtils.parseDateFromConfig(config.getString("birthday")))
                .ssn(config.getString("ssn"))
                .email("qa+lt-" + CurrentEnvironment.PID + "-" + pmi + "@example.com")
                .address1(config.getString("address1"))
                .address2(config.getString("address2"))
                .state(config.getString("state"))
                .city(config.getString("city"))
                .zip(config.getString("zip"))
                .build();
    }
}