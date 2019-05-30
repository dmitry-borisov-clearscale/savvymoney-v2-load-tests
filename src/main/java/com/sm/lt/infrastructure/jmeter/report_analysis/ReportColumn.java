package com.sm.lt.infrastructure.jmeter.report_analysis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
abstract class ReportColumn {

    static final int ELAPSED_COLUMN_INDEX = 1;
    static final int SUCCESS_COLUMN_INDEX = 7;
    static final int URL_COLUMN_INDEX = 13;
}