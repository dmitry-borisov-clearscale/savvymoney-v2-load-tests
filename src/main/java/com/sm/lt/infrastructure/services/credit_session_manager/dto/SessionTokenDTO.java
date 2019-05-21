package com.sm.lt.infrastructure.services.credit_session_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionTokenDTO extends ErrorableDTO {
    private String token;
    private int timeoutMinutes;
}