package com.sm.lt.infrastructure.services.credit_session_manager;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import com.sm.lt.api.User;
import com.sm.lt.infrastructure.DateUtils;

@RequiredArgsConstructor
public class UserToParametersMapper {

    private final String relayState;
    private final String entityID;

    public Map<String, String> map(User user) {
        final long partnerId = user.getPartnerId();
        final Map<String, String> result = new HashMap<>();
        result.put("SAML:partnerId", "" + partnerId);
        result.put("SAML:partnerMemberId", user.getPartnerMemberID());
        result.put("SAML:lastName", user.getLastName());
        result.put("SAML:address1", user.getAddress1());
        result.put("SAML:firstName", user.getFirstName());
        result.put("SAML:state", user.getState());
        result.put("SAML:ssn", user.getSsn());
        result.put("SAML:dob", DateUtils.dateForParameters(user.getBirthday()));
        result.put("SAML:email", user.getEmail());
        result.put("SAML:zip", user.getZip());
        result.put("SAML:city", user.getCity());
        result.put("SAML:supplemental1", user.getSuplementalAttr1());
        result.put("SAML:supplemental2", user.getSuplementalAttr2());
        result.put("SAML:supplemental3", user.getSuplementalAttr3());
        result.put("SSO:EntityID", entityID);
        result.put("SSO:RelayState", relayState);
        return result;
    }
}