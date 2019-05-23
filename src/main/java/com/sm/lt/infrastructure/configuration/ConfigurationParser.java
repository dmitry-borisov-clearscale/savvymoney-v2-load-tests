package com.sm.lt.infrastructure.configuration;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.collect.Lists;
import com.sm.lt.api.User;
import com.sm.lt.infrastructure.DateUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConfigurationParser {

    public static List<User> getUsers(String resourceBasename, String usersListElement) {
        Config config = ConfigFactory.parseResourcesAnySyntax(resourceBasename);
        List<? extends Config> users = config.getConfigList(usersListElement);
        return Lists.transform(users, ConfigurationParser::parseUser);
    }

    private static User parseUser(Config config) {
        return User
                .builder()
                .partnerId(900005001L)
                .partnerMemberID("pmi-lt-900005001-" + config.getString("pmi"))
                .firstName(config.getString("firstName"))
                .lastName(config.getString("lastName"))
                .birthday(DateUtils.parseDateFromConfig(config.getString("birthday")))
                .ssn(config.getString("ssn"))
                .email("qa+lt-900005001-" + config.getString("pmi") + "@example.com")
                .address1(config.getString("address1"))
                .address2(config.getString("address2"))
                .state(config.getString("state"))
                .city(config.getString("city"))
                .zip(config.getString("zip"))
                .build();
    }
}