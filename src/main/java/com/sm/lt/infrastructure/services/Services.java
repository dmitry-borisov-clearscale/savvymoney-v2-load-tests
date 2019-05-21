package com.sm.lt.infrastructure.services;

import com.sm.lt.infrastructure.services.credit_service.CreditServiceClient;
import com.sm.lt.infrastructure.services.credit_session_manager.CreditSessionManagerClient;
import com.sm.lt.infrastructure.services.credit_session_manager.UserToParametersMapper;
import com.sm.lt.infrastructure.services.iframe.IFrameUtils;

public class Services {

    public static CreditSessionManagerClient creditSessionManagerClient() {
        return new CreditSessionManagerClient(
                "http://iproxy-sandbox.savvymoney.com/credit-session-manager/session"); // SANDBOX
    }

    public static UserToParametersMapper userToParametersMapper() {
        final String relayState = "https://sandbox.savvymoney.com/ui/sso"; // SANDBOX
        final String entityID = "savvymoney-test";
        return new UserToParametersMapper(relayState, entityID);
    }

    public static IFrameUtils iframeUtils() {
        return new IFrameUtils(
                "https://sandbox.savvymoney.com/ui/iframe"); // SANDBOX
    }

    public static CreditServiceClient creditServiceClient() {
        return new CreditServiceClient(
                "http://sandb-albui-v6wdzdcw4e5d-2012102494.us-east-1.elb.amazonaws.com/credit-service");
    }
}