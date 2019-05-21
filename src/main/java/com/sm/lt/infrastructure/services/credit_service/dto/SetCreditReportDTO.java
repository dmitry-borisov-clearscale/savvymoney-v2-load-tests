package com.sm.lt.infrastructure.services.credit_service.dto;

import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SetCreditReportDTO {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private Long partnerId;
    private String partnerMemberId;
    private String reportXml;
    private String reportDt;
}