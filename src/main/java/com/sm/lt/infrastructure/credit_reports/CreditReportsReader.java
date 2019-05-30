package com.sm.lt.infrastructure.credit_reports;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

@Slf4j
public class CreditReportsReader {

    private static final String DEFAULT_TUI_CREDIT_REPORT = "credit_reports/default-credit-report.xml";

    public static String getDefaultCreditReport() {
        final URL offerFile = Resources.getResource(DEFAULT_TUI_CREDIT_REPORT);
        try (
                InputStream is = offerFile.openStream();
                Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)
        ) {
            return CharStreams.toString(r);
        } catch (IOException e) {
            log.error("Error occurred. For default Credit Report file", e);
            throw new RuntimeException(e);
        }
    }

}