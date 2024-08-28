/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util;

public final class QueryConstants {
    public static final String ACK = "ACTIVE_ACKNOWLEDGED";
    public static final String UNACK = "ACTIVE_UNACKNOWLEDGED";
    public static final String CLEAREDUNACK = "CLEARED_UNACKNOWLEDGED";
    public static final String FM = "FM";
    public static final String OPEN_ALARM = "OpenAlarm";
    public static final String HISTORY_ALARM = "HistoryAlarm";
    public static final String IMPORPER_FDN = "Improper FDN";
    public static final String FDN_NOTFOUND = "FDN not found";
    public static final String NO_ALRMS = "No alarms found under the FDN";
    public static final String ALARM_OPERATION = "AlarmOperation";
    public static final String ALARM_STATE = "alarmState";
    public static final String OPERATION_TIME = "operationTime";
    public static final String FDN = "fdn";
    public static final String OOR = "objectOfReference";
    public static final String ALARMID = "alarmId";
    public static final String EVENT_POID = "eventPoId";
    public static final String PRESENTSEVERITY = "presentSeverity";
    public static final String PREVIOUSSEVERITY = "previousSeverity";
    public static final String INSERTTIME = "insertTime";
    public static final String LASTUPDATED = "lastUpdated";
    public static final String CRITICAL = "CRITICAL";
    public static final String MAJOR = "MAJOR";
    public static final String MINOR = "MINOR";
    public static final String WARNING = "WARNING";
    public static final String INDETERMINATE = "INDETERMINATE";
    public static final String CLEARED = "CLEARED";
    public static final String UNDEFINED = "UNDEFINED";
    public static final String PRIMARY = "PRIMARY";
    public static final String SECONDARY = "SECONDARY";
    public static final String NOT_APPLICABLE = "NOT APPLICABLE";
    public static final String VISIBILITY = "visibility";
    public static final String NEALARMCOUNTDATA = "NEAlarmCountData";
    public static final String OSS_NE_DEF = "OSS_NE_DEF";
    public static final String NETWORK_ELEMENT = "NetworkElement";
    public static final String NETWORKELEMENT_DELIMETER = "NetworkElement=";
    public static final String MANUAL_CEASE = "manualCease";
    public static final String CORRELATED_VISIBILITY = "correlatedVisibility";
    public static final String FMX_GENERATED = "fmxGenerated";
    public static final String PROCESSING_TYPE = "processingType";
    public static final String HISTORY_PO_ID = "historyAlarmPOId";
    public static final String COMMENTS = "comments";
    public static final String COMMENT = "comment";

    public static final String OBJECTOFREFERENCE = "objectOfReference";
    public static final String EVENTTIME = "eventTime";
    public static final String SPECIFICPROBLEM = "specificProblem";
    public static final String EVENTTYPE = "eventType";
    public static final String COMMENTTEXT = "commentText";
    public static final String COMMENTTIME = "commentTime";
    public static final String COMMENTOPERATOR = "commentOperator";
    public static final String ALARMSTATE = "alarmState";
    public static final String PROBABLECAUSE = "probableCause";
    public static final String ALARMNUMBER = "alarmNumber";
    public static final String BACKUPOBJECTINSTANCE = "backupObjectInstance";
    public static final String RECORDTYPE = "recordType";
    public static final String BACKUPSTATUS = "backupStatus";
    public static final String TRENDINDICATION = "trendIndication";
    public static final String PROPOSEDREPAIRACTION = "proposedRepairAction";
    public static final String CEASETIME = "ceaseTime";
    public static final String CEASEOPERATOR = "ceaseOperator";
    public static final String ACKTIME = "ackTime";
    public static final String ACKOPERATOR = "ackOperator";
    public static final String REPEATCOUNT = "repeatCount";
    public static final String OSCILLATIONCOUNT = "oscillationCount";
    public static final String ALARMING_OBJECT = "alarmingObject";
    public static final String ROOT = "root";
    public static final String CI_GROUP_1 = "ciFirstGroup";
    public static final String CI_GROUP_2 = "ciSecondGroup";
    public static final String UTC = "UTC";
    public static final String CONTAINS = "contains";
    public static final String STARTSWITH = "startsWith";
    public static final String ENDSWITH = "endsWith";
    public static final String EQUALOPERATOR = "=";
    public static final String NOTEQUALOPERATOR = "!=";
    public static final String DELIMETER = "#";
    public static final String SEMICOLON = ":";
    public static final String SUCCESS = "Success";
    public static final String OPEN = "open";
    public static final String BETWEEN = "between";
    public static final String ERBS = "ERBS";

    public static final String ADDITIONAL_INFORMATION = "additionalInformation";
    public static final String PROBLEMTEXT = "problemText";
    public static final String PROBLEMDETAIL = "problemDetail";
    public static final String SYNC_STATE = "syncState";
    public static final String TARGET_ADDITIONAL_INFORMATION = "targetAdditionalInformation";

    public static final String ELDER = "elder";
    public static final String YOUNGER = "younger";
    public static final String TRUE_AS_STRING = "true";
    public static final String FALSE_AS_STRING = "false";

    public static final int BATCH_SIZE = 750;
    public static final String GREATER_THAN_OR_EQUAL = ">=";
    public static final String GREATER_THAN = ">";
    public static final String LESS_THAN_OR_EQUAL = "<=";
    public static final String LESS_THAN = "<";
    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static final String LAST_ALARM_OPERATION = "lastAlarmOperation";

    public static final String DESC = "desc";
    public static final String ASC = "asc";

    public static final String LIMIT_EXCEEDED = "Number of Alarms for the given search criteria are greater than 5000. "
            + "Please refine search criteria and perform the serach again";
    public static final String SEARCH_LIMIT_EXCEEDED = "SEARCH_LIMIT_EXCEEDED";

    public static final String ALARM_ADDITIONAL_INFORMATION = "DynamicAlarmAttributeInformation";
    public static final String ALARM_ADDITIONAL_INFORMATION_ATTRIBUTE = "dynamicAlarmAttribute";
    public static final String ALARM_ADDITIONAL_INFORMATION_VERSION = "1.0.1";

    private QueryConstants() {
    }
}