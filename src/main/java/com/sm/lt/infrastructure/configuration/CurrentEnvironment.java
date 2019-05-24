package com.sm.lt.infrastructure.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.sm.lt.infrastructure.services.iframe.IFrameUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class CurrentEnvironment {

    public static String CREDIT_SESSION_MANAGER_CLIENT_URL = null;
    public static String BASE_UI_URL = null;
    public static String USER_TO_PARAMETERS_MAPPER_RELAY_STATE = null;
    public static String USER_TO_PARAMETERS_MAPPER_RELAY_ENTITY_ID = null;
    public static String IFRAME_UTILS_URL = null;
    public static String CREDIT_SERVICE_CLIENT_URL = null;
    public static String PRESENTATION_API_URL = null;
    public static IFrameUtils.Environment ENV = null;
    public static Long PID = null;
}