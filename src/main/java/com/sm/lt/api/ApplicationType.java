package com.sm.lt.api;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum ApplicationType {
    WEB("web"),
    ONBOARDING("onboarding"),
//        MOBILE_WEB("mobile-web"),
//        MOBILE_ONBOARDING("mobile-onboarding"),
    UNIVERSAL_WIDGET("universal-widget")
    ;

    private final String type;

    ApplicationType(String type) {
        this.type = type;
    }

    public static Optional<ApplicationType> fromType(String type) {
        for (ApplicationType e : values()) {
            if (e.type.equalsIgnoreCase(type)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public String app(String app) {
        return type + "/" + app;
    }

    public String app() {
        return type;
    }

}