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
 * Class holds the string constants which are used in query.
 *
 */
public final class QueryConstants {

    public static final String FM = "FM";

    public static final String OPEN_ALARM = "OpenAlarm";
    public static final String OPEN_ALARMS = "open_alarms";

    public static final String RECORD_TYPE = "recordType";

    public static final String SUCCESS = "Success";
    public static final String NO_ALARMS = "No Alarms";

    // Will be made as configuration parameter in future sprints
    public static final int BATCH_SIZE = 400;
    public static final int NUMBER_OF_ADDITIONAL_RECORDS_TO_AVOID_DUPLICATES = 2000;

    public static final String LOG_ERROR_MESSAGE = "Error while retrieving alarms from DB {}";
    public static final String ERROR_CODE_UNEXPECTED_ERROR = "Error while retrieving alarms from DB {}, please check the error log for more details ";
    public static final String FAILED_TO_READ_FROM_DB = "Failed to read alarms from DB. Exception details are:";
    public static final String SERVER_REFUSED_CONNECTION_ERROR = "Server refused connection";

    public static final String DATE_FORMATER = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String UTC = "UTC";
    public static final String EMPTY_STRING = "";

    public static final String HASH_DELIMITER = "#";
    public static final String COMMA_DELIMITER = ",";
    public static final String STAR = "*";
    public static final String EQUAL_DELIMITER = "=";

    public static final String FALSE_STRING = "false";
    public static final String TRUE_STRING = "true";

    public static final String IMPROPER_INPUT = "Improper Input";

    public static final String ALARM_SEARH = "alarms_search";
    public static final String QUERY = "query";

    public static final String DYNAMIC_ALARM_ATTRIBUTE_MODEL = "DynamicAlarmAttributeInformation";
    public static final String ADDITIONAL_INFORMATION = "additionalInformation";
    public static final String INSERTTIME = "insertTime";
    public static final String DYNAMIC_ALARM_ATTRIBUTE = "dynamicAlarmAttribute";
    public static final String DYNAMIC_ALARM_ATTRIBUTE_VALUE = "dynamicAlarmAttributeValue";

    public static final String POIDS = "poIds";

    public static final String PSUEDO_PRESENT_SEVERITY = "psuedoPresentSeverity";
    public static final String PSUEDO_PREVIOUS_SEVERITY = "psuedoPreviousSeverity";

    private QueryConstants() {
    }

}
