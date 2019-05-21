package com.sm.lt.infrastructure.services.credit_session_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDTO {
    private String code;
    private String message;
    private String subCode;
    private String type;
}