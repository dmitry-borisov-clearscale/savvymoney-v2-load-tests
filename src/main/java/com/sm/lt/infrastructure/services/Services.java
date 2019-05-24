package com.sm.lt.infrastructure.services;

import com.sm.lt.infrastructure.configuration.CurrentEnvironment;
import com.sm.lt.infrastructure.services.credit_service.CreditServiceClient;
import com.sm.lt.infrastructure.services.credit_session_manager.CreditSessionManagerClient;
import com.sm.lt.infrastructure.services.credit_session_manager.UserToParametersMapper;
import com.sm.lt.infrastructure.services.iframe.IFrameUtils;

public class Services {

    public static CreditSessionManagerClient creditSessionManagerClient() {
        return new CreditSessionManagerClient(CurrentEnvironment.CREDIT_SESSION_MANAGER_CLIENT_URL);
    }

    public static UserToParametersMapper userToParametersMapper() {
        return new UserToParametersMapper(
                CurrentEnvironment.USER_TO_PARAMETERS_MAPPER_RELAY_STATE, CurrentEnvironment.USER_TO_PARAMETERS_MAPPER_RELAY_ENTITY_ID);
    }

    public static IFrameUtils iframeUtils() {
        return new IFrameUtils(CurrentEnvironment.IFRAME_UTILS_URL);
    }

    public static CreditServiceClient creditServiceClient() {
        return new CreditServiceClient(CurrentEnvironment.CREDIT_SERVICE_CLIENT_URL);
    }
}