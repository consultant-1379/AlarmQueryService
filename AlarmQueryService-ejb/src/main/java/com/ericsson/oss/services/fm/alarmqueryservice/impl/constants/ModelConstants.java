/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.constants;

/**
 * Class holds the Configuration constants which are used in query.
 *
 */
public final class ModelConstants {

    public static final String OPEN_ALARM_URN = "/dps_primarytype/FM/OpenAlarm/*";

    public static final String STRING_TYPE = "STRING";
    public static final String TIMESTAMP_TYPE = "TIMESTAMP";
    public static final String BOOLEAN_TYPE = "BOOLEAN";
    public static final String LONG_TYPE = "LONG";
    public static final String ENUM_TYPE = "ENUM_REF";
    public static final String INTEGER_TYPE = "INTEGER";

    public static final String EMPTY_STRING = "";

    private ModelConstants() {
    }
}
