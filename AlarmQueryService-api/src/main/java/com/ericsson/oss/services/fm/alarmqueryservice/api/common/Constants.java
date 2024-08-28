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

package com.ericsson.oss.services.fm.alarmqueryservice.api.common;

public final class Constants {
    public static final String OBJECT_OF_REFERENCE = "objectOfReference";
    public static final String FDN = "fdn";
    public static final String NODE_ID = "nodeId";
    public static final String EVENT_PO_ID = "eventPoId";
    public static final String HISTORY_ALARM_PO_ID = "historyAlarmPOId";
    public static final String COMMENT_TIME = "commentTime";
    public static final String COMMENT_OPERATOR = "commentOperator";
    public static final String TYPE = "type";
    public static final String TIME_ZONE = "timeZone";
    public static final String REPEAT_COUNT = "repeatCount";
    public static final String OSCILLATION_COUNT = "oscillationCount";
    public static final String EVENT_TIME = "eventTime";
    public static final String INSERT_TIME = "insertTime";
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String PRESENT_SEVERITY = "presentSeverity";
    public static final String PROBABLE_CAUSE = "probableCause";
    public static final String SPECIFIC_PROBLEM = "specificProblem";
    public static final String ALARM_NUMBER = "alarmNumber";
    public static final String BACKUP_OBJECT_INSTANCE = "backupObjectInstance";
    public static final String EVENT_TYPE = "eventType";
    public static final String RECORD_TYPE = "recordType";
    public static final String BACKUP_STATUS = "backupStatus";
    public static final String TREND_INDICATION = "trendIndication";
    public static final String PREVIOUS_SEVERITY = "previousSeverity";
    public static final String PROPOSED_REPAIR_ACTION = "proposedRepairAction";
    public static final String ALARM_ID = "alarmId";
    public static final String ALARM_STATE = "alarmState";
    public static final String CORRELATED_RECORD_NAME = "correlatedRecordName";
    public static final String CEASE_TIME = "ceaseTime";
    public static final String CEASE_OPERATOR = "ceaseOperator";
    public static final String ACK_TIME = "ackTime";
    public static final String ACK_OPERATOR = "ackOperator";
    public static final String PROBLEM_TEXT = "problemText";
    public static final String PROBLEM_DETAIL = "problemDetail";
    public static final String ADDITIONAL_INFORMATION = "additionalInformation";
    public static final String COMMENT_TEXT = "commentText";
    public static final String COMMENTS = "comments";
    public static final String LAST_ALARM_OPERATION = "lastAlarmOperation";
    public static final String VISIBILITY = "visibility";
    public static final String PROCESSING_TYPE = "processingType";
    public static final String FMX_GENERATED = "fmxGenerated";
    public static final String ADDITIONAL_INFORMATION_MAP = "additionalInformationmap";
    public static final String MANAGED_OBJECT = "managedObject";
    public static final String MANUAL_CEASE = "manualCease";
    public static final String CORRELATED_VISIBILITY = "correlatedVisibility";
    public static final String SYNC_STATE = "syncState";
    public static final String ALARMING_OBJECT = "alarmingObject";
    public static final String ROOT = "root";
    public static final String CI_GROUP_1 = "ciFirstGroup";
    public static final String CI_GROUP_2 = "ciSecondGroup";

    public static final String NETWORK_ELEMENT_DELIMITER = "NetworkElement=";
    public static final String MANAGEMENTSYSTEM_DELIMETER = "ManagementSystem=";
    public static final String EQUAL_DELIMITER = "=";
    public static final String HASH_DELIMITER = "#";
    public static final String COLON_DELIMITER = ":";
    public static final String COMMA_DELIMITER = ",";
    public static final String UNDER_SCORE = "_";

    public static final String EMPTY_STRING = "";
    public static final String CRITICAL = "CRITICAL";
    public static final String MAJOR = "MAJOR";
    public static final String MINOR = "MINOR";
    public static final String WARNING = "WARNING";
    public static final String INDETERMINATE = "INDETERMINATE";
    public static final String CLEARED = "CLEARED";
    public static final String UNDEFINED = "UNDEFINED";

    public static final String PRIMARY = "PRIMARY";
    public static final String SECONDARY = "SECONDARY";
    public static final String NOT_APPLICABLE = "NOT_APPLICABLE";

    public static final String FM = "FM";
    public static final String COMMENT_OPERATION = "CommentOperation";
    public static final String OPEN_ALARM_POID = "openAlarmPOId";

    public static final String _VERSION = "_version_";
    public static final String ID = "id";
    public static final String EVENT_POID_AS_STRING = "eventPoIdAsString";
    public static final String NON_SYNCHABLE_RECORD_TYPE_ADDITIONAL_ATTRIBUTE = "originalRecordType:NON_SYNCHABLE_ALARM#";

    private Constants() {}
}
