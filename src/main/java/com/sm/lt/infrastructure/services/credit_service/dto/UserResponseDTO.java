package com.sm.lt.infrastructure.services.credit_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private boolean authenticated;
    private AuthenticatedMember member;
    private AuthError error;

    @Getter
    @Setter
    public static class AuthError {
        private ErrorCode code;
        private String message;
    }

    public enum ErrorCode {
        MEMBER_NOT_FOUND,
        WRONG_CREDENTIALS,
        USER_CANCELED,
        MAX_JOINT_ACCOUNTS_REACHED,
        SSN_IN_USE,
        SSN_BLOCKED,
        MAIL_EXISTS,
        CONTACT_MAIL_EXISTS,
        INVALID_TEST_DATA
    }

    @Getter
    @Setter
    public static class AuthenticatedMember {
        private Long memberId;
        private boolean sso;
        private Boolean dormant;
        private Long partnerId;
        private String partnerMemberId;
        private String firstName;
        private String lastName;
        private String email;
        private String contactEmail;
        private CreditUser creditUser;
    }

    @Getter
    @Setter
    public static class CreditUser {
        private String firstName;
        private String lastName;
        private String address;
        private String city;
        private String state;
        private String zip;
        private String dob;
        private String ssn;
    }
}
