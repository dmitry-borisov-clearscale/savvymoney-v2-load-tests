package com.sm.lt.api;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Preconditions;
import com.sm.lt.infrastructure.services.Services;
import com.sm.lt.infrastructure.services.iframe.IFrameUtils;

@Slf4j
public class UI {

    private static final String ZERO_IFRAME_SHIFT = "0px";

    public static String pageUrl(Page page, User user) {
        final Session session = Session.start(user);
        return pageUrl(page, session);
    }

    public static String pageUrl(Page page, Session session) {
        Preconditions.checkNotNull(page);
        Preconditions.checkNotNull(session);
        Preconditions.checkNotNull(session.getSmToken());

//        return (useIFrame)
//                ? pageUrlForIframe(page, session.getSmToken())
//                : pageUrlForRegular(page, session.getSmToken());
        return pageUrlForRegular(page, session.getSmToken());
    }

    private static String pageUrlForRegular(Page page, String smToken) {
        final String baseUrl = "https://sandbox.savvymoney.com"; // SANDBOX
        final String urlToEnter = page.build(baseUrl, smToken);
        log.info("No iframe. URL: {}", urlToEnter);
        return urlToEnter;
    }

    private static String pageUrlForIframe(Page page, String smToken) {
        final IFrameUtils.Builder builder = Services
                .iframeUtils()
                .builder()
                .environment(IFrameUtils.Environment.SANDBOX)
                .width("1080px")
                .height("1145px")
                .left(ZERO_IFRAME_SHIFT)
                .top(ZERO_IFRAME_SHIFT)
                .smToken(smToken);
        final URI uri = page.build(builder);
        log.info("iframe. URL: {}", uri);
        return uri.toString();
    }
}
