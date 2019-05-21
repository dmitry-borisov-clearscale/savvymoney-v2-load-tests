package com.sm.lt.infrastructure;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter HOCON_CONFIG_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter LOCAL_INFO_DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final DateTimeFormatter PARAMETERS_DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final DateTimeFormatter CREDIT_SERVICE_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy");

    public static LocalDate parseDateFromConfig(String date) {
        if (date == null) {
            return null;
        }
        return LocalDate.parse(date, HOCON_CONFIG_DATE_FORMATTER);
    }

    public static String dateForConfig(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(HOCON_CONFIG_DATE_FORMATTER);
    }

    public static String dateForCreditService(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(CREDIT_SERVICE_DATE_FORMATTER);
    }

    public static String dateForPersonalInformation(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(LOCAL_INFO_DATE_FORMATTER);
    }

    public static String dateForParameters(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(PARAMETERS_DATE_FORMATTER);
    }

    public static String asString(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(formatter);
    }
}