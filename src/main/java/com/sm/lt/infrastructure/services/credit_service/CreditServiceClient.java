package com.sm.lt.infrastructure.services.credit_service;

import static com.sm.lt.infrastructure.HttpUtils.*;

import java.util.Objects;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.sm.lt.infrastructure.HttpUtils;
import com.sm.lt.infrastructure.JsonUtils;
import com.sm.lt.infrastructure.services.credit_service.dto.SetCreditReportDTO;
import com.sm.lt.infrastructure.services.credit_service.dto.UserRequestDTO;
import com.sm.lt.infrastructure.services.credit_service.dto.UserResponseDTO;

@Slf4j
@RequiredArgsConstructor
public class CreditServiceClient {

    private static final int MAX_EMAIL_LOCAL_PART_LENGTH = 34; // package private is only for tests

    /**
     * Mandatory placeholder in xml. Used on server side of Credit Service to set memberId because valid response
     * should be linked to specific user/member.
     */
    private static final String MEMBER_ID_PLACEHOLDER = "${USER_MEMBER_ID}"; // package private is only for tests

    private static final CloseableHttpClient CLIENT = HttpUtils.defaultClient();

    private final String baseUrl;

    /**
     * Makes request with empty body and expects that HTTP Server receives it, analyze and answers that body is invalid.
     */
    public boolean doesServerPreventInvalidRequest() {
        final HttpPost request = new HttpPost(baseUrl + "/testing/member-sso/registered");
        request.setEntity(EntityBuilder.create()
                                       .setContentType(ContentType.APPLICATION_JSON)
                                       .setText(JsonUtils.toJson(UserRequestDTO.builder().build()))
                                       .build());

        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_BAD_REQUEST);
        return makeRequestReturnResult(CLIENT, request, statuses, response -> true);
    }

    public UserResponseDTO createOrUpdateRegisteredUser(UserRequestDTO userRequestDTO) {
        checkUserCreationPreconditions(userRequestDTO);

        final HttpPost request = new HttpPost(baseUrl + "/testing/member-sso/registered");
        request.setEntity(EntityBuilder.create()
                                       .setContentType(ContentType.APPLICATION_JSON)
                                       .setText(JsonUtils.toJson(userRequestDTO))
                                       .build());

        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        return makeRequestReturnResult(CLIENT, request, statuses, UserResponseDTO.class);
    }

    public UserResponseDTO createOrUpdateAuthProgressUser(UserRequestDTO userRequestDTO) {
        checkUserCreationPreconditions(userRequestDTO);

        final HttpPost request = new HttpPost(baseUrl + "/testing/member-sso/auth-progress");
        request.setEntity(EntityBuilder.create()
                                       .setContentType(ContentType.APPLICATION_JSON)
                                       .setText(JsonUtils.toJson(userRequestDTO))
                                       .build());

        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        return makeRequestReturnResult(CLIENT, request, statuses, UserResponseDTO.class);
    }

    public boolean uploadCreditReport(SetCreditReportDTO setCreditReportDTO) {
        final long partnerId = setCreditReportDTO.getPartnerId();
        Preconditions.checkArgument(
                setCreditReportDTO.getReportXml().contains(MEMBER_ID_PLACEHOLDER),
                "Invalid usage. Credit Report should contain placeholder: " + MEMBER_ID_PLACEHOLDER);

        final HttpPost request = new HttpPost(baseUrl + "/testing/credit-report");
        request.setEntity(EntityBuilder.create()
                                       .setContentType(ContentType.APPLICATION_JSON)
                                       .setText(JsonUtils.toJson(setCreditReportDTO))
                                       .build());

        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        return makeRequestReturnResult(CLIENT, request, statuses, response -> true);
    }

    private static void checkUserCreationPreconditions(UserRequestDTO userRequestDTO) {
        final long partnerId = userRequestDTO.getPartnerId();

        final String email = userRequestDTO.getEmail();
        Preconditions.checkArgument(
                !Objects.isNull(email),
                "Invalid usage. User email should exist");
        Preconditions.checkArgument(
                email.split("@")[0].length() <= MAX_EMAIL_LOCAL_PART_LENGTH,
                "Invalid usage. User email local-part is too long: " + email);

        final String contactEmail = userRequestDTO.getContactEmail();
        Preconditions.checkArgument(
                !Objects.isNull(contactEmail),
                "Invalid usage. User contact email should exist");
        Preconditions.checkArgument(
                contactEmail.split("@")[0].length() <= MAX_EMAIL_LOCAL_PART_LENGTH,
                "Invalid usage. User contact email local-part is too long: " + contactEmail);
    }
}