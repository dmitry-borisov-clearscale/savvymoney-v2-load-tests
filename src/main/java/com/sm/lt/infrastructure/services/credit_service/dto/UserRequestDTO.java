package com.sm.lt.infrastructure.services.credit_service.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.ImmutableMap;
import com.sm.lt.api.User;
import com.sm.lt.infrastructure.DateUtils;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    private Long partnerId;
    private String partnerMemberId;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String ssn;
    private String zip;
    private String dob;
    private Map<String, String> supplementalAttrs;
    private String authorizationText;
    private String phone;
    private String email;
    private String contactEmail;
    private String firstName;
    private String lastName;
    private Boolean receiveWeeklyNewsletters;

    public static UserRequestDTO fromUser(User user) {
        final ImmutableMap.Builder<String, String> supplementalAttrs = ImmutableMap.builder();
        if (user.getSuplementalAttr1() != null) {
            supplementalAttrs.put("supplemental1", user.getSuplementalAttr1());
        }
        if (user.getSuplementalAttr2() != null) {
            supplementalAttrs.put("supplemental2", user.getSuplementalAttr2());
        }
        if (user.getSuplementalAttr3() != null) {
            supplementalAttrs.put("supplemental3", user.getSuplementalAttr3());
        }

        return UserRequestDTO.builder()
                .partnerId(user.getPartnerId())
                .partnerMemberId(user.getPartnerMemberID())
                .address1(user.getAddress1())
                .address2(user.getAddress2())
                .city(user.getCity())
                .state(user.getState())
                .ssn(user.getSsn())
                .zip(user.getZip())
                .dob(DateUtils.dateForCreditService(user.getBirthday()))
                .supplementalAttrs(supplementalAttrs.build())
                .authorizationText("Any text")
                .phone(null)
                .email(user.getEmail())
                .contactEmail(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .receiveWeeklyNewsletters(null)
                .build();
    }
}