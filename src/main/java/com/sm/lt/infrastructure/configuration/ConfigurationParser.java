package com.sm.lt.infrastructure.configuration;

import static com.sm.lt.api.User.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sm.lt.api.User;
import com.sm.lt.infrastructure.DateUtils;
import com.typesafe.config.Config;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConfigurationParser {

    private static final Pattern UNIQUENESS_PATTERN = Pattern.compile("(.*)<GENERATED>(.*)");

    public static List<User> generateUsers(Config config, int count) {
        String uniqueTemplate = config.getString("uniqueTemplate");
        Long pid = CurrentEnvironment.PID;
        int uniquenessLength = getUniquenessLength(pid, uniqueTemplate);
        return generateUniqueness(uniquenessLength, count)
                .stream()
                .map(uniq -> user(config, uniq, pid))
                .collect(Collectors.toList());
    }

    private static int getUniquenessLength(Long pid, String uniqueTemplate) {
        Matcher matcher = UNIQUENESS_PATTERN.matcher(uniqueTemplate);
        if (!matcher.matches()) {
            log.error("Invalid unique template value. {}", uniqueTemplate);
            throw new IllegalStateException("Invalid unique template value");
        }

        String preFinal = pid + "-" + uniqueTemplate.replaceAll(UNIQUENESS_PATTERN.pattern(), "$1$2");
        if (preFinal.length() >= User.MAX_UNIQUE_SUFFIX_LENGTH) {
            log.error("Invalid unique template value. {}. preFinal: {}", uniqueTemplate, preFinal);
            throw new IllegalStateException("Invalid unique template value");
        }

        return MAX_UNIQUE_SUFFIX_LENGTH - preFinal.length();
    }

    private static Set<String> generateUniqueness(int uniquenessLong, int count) {
        Set<String> result = new HashSet<>();
        String millisStr = String.valueOf(System.currentTimeMillis());
        int index = (millisStr.length() < uniquenessLong) ? 0 : millisStr.length() - uniquenessLong;
        for (int i = 0; i < count; i++) {
            result.add(String.valueOf(System.currentTimeMillis()).substring(index));
        }
        while (result.size() < count) {
            result.add(String.valueOf(System.currentTimeMillis()).substring(index));
        }
        return result;
    }

    private static User user(Config config, String uniqueness, long pid) {
        String uniqueTemplate = config.getString("uniqueTemplate");
        String uniqueSuffix = pid + "-" + uniqueTemplate.replaceAll(UNIQUENESS_PATTERN.pattern(), "$1" + uniqueness + "$2");
        if (!User.isValidUniqueSuffix(uniqueSuffix)) {
            throw new IllegalStateException("invalid pmi");
        }
        return User
                .builder()
                .partnerId(pid)
                .partnerMemberID("pmi-lt-" + uniqueSuffix)
                .firstName(config.getString("firstName"))
                .lastName(config.getString("lastName"))
                .birthday(DateUtils.parseDateFromConfig(config.getString("birthday")))
                .ssn(config.getString("ssn"))
                .email("qa+lt-" + uniqueSuffix + "@example.com")
                .address1(config.getString("address1"))
                .address2(config.getString("address2"))
                .state(config.getString("state"))
                .city(config.getString("city"))
                .zip(config.getString("zip"))
                .build();
    }
}