package com.sm.lt.api;

import static com.sm.lt.api.ApplicationType.*;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;

import com.sm.lt.infrastructure.services.iframe.IFrameUtils;

public enum Page {

    SSO_ENDPOINT                     /**/("/ui/sso?smtoken={SMTOKEN}", IFrameUtils.Endpoint.SSO),

    WIDGET                           /**/("/ui/page/{SMTOKEN}", UNIVERSAL_WIDGET.app()),

    //on-boarding pages
    ONBOARDING_DISCLOSURE            /**/("/ui/page/{SMTOKEN}", ONBOARDING.app("disclosure")),
    ONBOARDING_OOW                   /**/("/ui/page/{SMTOKEN}", ONBOARDING.app("oow")),

    //dashboard pages
    WEB_CREDIT_SCORE_DASHBOARD       /**/("/ui/page/{SMTOKEN}", WEB.app("credit-score/dashboard")),
    WEB_CREDIT_SCORE_AGE             /**/("/ui/page/{SMTOKEN}", WEB.app("credit-score/age")),
    WEB_CREDIT_SCORE_BALANCE         /**/("/ui/page/{SMTOKEN}", WEB.app("credit-score/balance")),
    WEB_CREDIT_SCORE_HISTORY         /**/("/ui/page/{SMTOKEN}", WEB.app("credit-score/history")),
    WEB_CREDIT_SCORE_MONITORING      /**/("/ui/page/{SMTOKEN}", WEB.app("credit-score/monitoring")),
    WEB_CREDIT_SCORE_RECENT          /**/("/ui/page/{SMTOKEN}", WEB.app("credit-score/recent")),
    WEB_CREDIT_SCORE_USAGE           /**/("/ui/page/{SMTOKEN}", WEB.app("credit-score/usage")),

    //credit-report pages
    WEB_CREDIT_REPORT_ACCOUNTS       /**/("/ui/page/{SMTOKEN}", WEB.app("credit-record/accounts")),
    WEB_CREDIT_REPORT_INQUIRIES      /**/("/ui/page/{SMTOKEN}", WEB.app("credit-record/inquiries")),
    WEB_CREDIT_REPORT_PUBLIC_RECORDS /**/("/ui/page/{SMTOKEN}", WEB.app("credit-record/public-records")),
    WEB_CREDIT_REPORT_SUMMARY        /**/("/ui/page/{SMTOKEN}", WEB.app("credit-record/summary")),

    //offer pages
    WEB_CREDIT_CARDS                 /**/("/ui/page/{SMTOKEN}", WEB.app("credit-cards")),
    WEB_CREDIT_CARDS_BALANCE_TRANSFER/**/("/ui/page/{SMTOKEN}", WEB.app("credit-cards-balance-transfer")),
    WEB_AUTO_LOANS_NEW               /**/("/ui/page/{SMTOKEN}", WEB.app("auto-loans-new")),
    WEB_AUTO_LOANS_REFINANCE         /**/("/ui/page/{SMTOKEN}", WEB.app("auto-loans-refinance")),
    WEB_HOME_LOANS                   /**/("/ui/page/{SMTOKEN}", WEB.app("home-loans")),
    WEB_OTHER_LOANS                  /**/("/ui/page/{SMTOKEN}", WEB.app("other-loans")),
    WEB_PERSONAL_LOANS_NEW           /**/("/ui/page/{SMTOKEN}", WEB.app("personal-loans-new")),
    WEB_PERSONAL_LOANS_REFINANCE     /**/("/ui/page/{SMTOKEN}", WEB.app("personal-loans-refinance")),
    WEB_PROFILE                      /**/("/ui/page/{SMTOKEN}", WEB.app("profile")),
    WEB_STUDENT_LOANS                /**/("/ui/page/{SMTOKEN}", WEB.app("student-loans"));

    private final String base;
    private final String app;
    private final IFrameUtils.Endpoint endpoint;

    Page(String base, String app) {
        this.base = base;
        this.app = app;
        this.endpoint = null;
    }

    Page(String base, IFrameUtils.Endpoint endpoint) {
        this.base = base;
        this.app = null;
        this.endpoint = endpoint;
    }

    public String build(String baseUrl, String smToken) {
        final String s = baseUrl + StringUtils.replace(base, "{SMTOKEN}", smToken);
        return (app == null) ? s : s + "/" + app;
    }

    public URI build(IFrameUtils.Builder builder) {
        return (endpoint != null)
                ? builder.endpoint(endpoint).build()
                : builder.link(app).build();
    }
}