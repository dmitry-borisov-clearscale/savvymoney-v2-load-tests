package com.sm.lt.api;

import static com.google.common.collect.ImmutableMap.*;
import static com.sm.lt.infrastructure.JsonUtils.*;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.sm.lt.infrastructure.services.Services;

@Slf4j
@Getter
@AllArgsConstructor
public class Session {

    private final String smToken;
    private User user;

    public static Session start(User user) {
        final Map<String, String> parametersForCache = Services.userToParametersMapper().map(user);
        final String smToken = Services.creditSessionManagerClient().saveAttributes(parametersForCache);
        log.info("New session started. {}", jsonizer(of("smtoken", smToken, "user", jsonizer(user))));
        return new Session(smToken, user);
    }

    public void invalidate() {
        Services.creditSessionManagerClient().removeToken(smToken);
    }

    public boolean updateAttributes(User user) {
        final Map<String, String> parametersForCache = Services.userToParametersMapper().map(user);
        final boolean result = Services.creditSessionManagerClient().updateAttributes(smToken, parametersForCache);
        if (result) {
            this.user = user;
        }
        return result;
    }

    public Map<String, String> getAttributes() {
        return Services.creditSessionManagerClient().getAttributes(smToken);
    }

    public Map<String, String> getAttributes(String prefix) {
        return Services.creditSessionManagerClient().getAttributes(smToken, prefix);
    }

    public Boolean prolongToken() {
        return Services.creditSessionManagerClient().prolongToken(smToken);
    }

    public String cloneToken() {
        return Services.creditSessionManagerClient().cloneToken(smToken);
    }
}
