package com.sm.lt.api;

import static com.sm.lt.infrastructure.JsonUtils.*;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import com.sm.lt.infrastructure.services.Services;
import com.sm.lt.infrastructure.services.credit_service.CreditServiceClient;
import com.sm.lt.infrastructure.services.credit_service.dto.SetCreditReportDTO;
import com.sm.lt.infrastructure.services.credit_service.dto.UserRequestDTO;
import com.sm.lt.infrastructure.services.credit_service.dto.UserResponseDTO;

@Slf4j
@Value
@Builder(toBuilder = true)
public class User {

    /**
     *  This restriction is based on Credit Service behavior. It breaks down if email is too long.
     */
    public static final int MAX_UNIQUE_SUFFIX_LENGTH = 28;

    String firstName;
    String lastName;
    long partnerId;
    String partnerMemberID;
    String address1;
    String address2;
    String city;
    String state;
    String zip;
    String email;
    LocalDate birthday;
    String ssn;

    // suplemental attributes
    String suplementalAttr1;
    String suplementalAttr2;
    String suplementalAttr3;

    // the user status
    String status;

    // check attributes set
    String password;
    String contactEmail;
    boolean sso;

    String report;

    public User configureAsRegisteredUser() {
        final CreditServiceClient client = Services.creditServiceClient();
        final UserRequestDTO userRequestDTO = UserRequestDTO.fromUser(this);
        final UserResponseDTO response = client.createOrUpdateRegisteredUser(userRequestDTO);
        if ((response == null) || !response.isAuthenticated()) {
            log.error("Error while configuring user as registered. PMI: {}. REQUEST: {}. RESPONSE: {}.",
                    this.partnerMemberID, jsonizer(userRequestDTO), jsonizer(response));
            throw new AssertionError("Error while configuring user as registered");
        }
        log.info("User was configured as registered. PMI: {}", this.partnerMemberID);
        return this;
    }

    public User configureAsUserWhoHasAcceptedDisclosure() {
        final CreditServiceClient client = Services.creditServiceClient();
        final UserRequestDTO userRequestDTO = UserRequestDTO.fromUser(this);
        final UserResponseDTO response = client.createOrUpdateAuthProgressUser(userRequestDTO);
        if ((response == null) || !response.isAuthenticated()) {
            log.error("Error while configuring user as one who accepted disclosure. PMI: {}. REQUEST: {}. RESPONSE: {}.",
                    this.partnerMemberID, jsonizer(userRequestDTO), jsonizer(response));
            throw new AssertionError("Error while configuring user as one who accepted disclosure");
        }
        log.info("User was configured as one who accepted disclosure. PMI: {}", this.partnerMemberID);
        return this;
    }

    /**
     * Internally API implementation uses {@code javax.validation.constraints.Email}. In practice it has length
     * restriction.
     */
    public static boolean isValidUniqueSuffix(String uniqueSuffix) {
        final int length = uniqueSuffix.length();
        return length >= 1 && length <= MAX_UNIQUE_SUFFIX_LENGTH;
    }

    private void sendCreditReport(String reportDateTime, String reportXml) {
        final CreditServiceClient client = Services.creditServiceClient();
        final SetCreditReportDTO setCreditReportDTO = SetCreditReportDTO
                .builder()
                .partnerId(partnerId)
                .partnerMemberId(partnerMemberID)
                .reportDt(reportDateTime)
                .reportXml(reportXml)
                .build();

        final boolean success = client.uploadCreditReport(setCreditReportDTO);
        if (!success) {
            log.error("Error while uploading credit report. PMI: {}", this.partnerMemberID);
            throw new AssertionError("Error while uploading credit report");
        }
    }
}