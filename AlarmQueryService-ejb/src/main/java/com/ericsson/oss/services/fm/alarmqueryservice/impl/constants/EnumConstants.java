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
 * Class holds the ENUM related constants which are used in query.
 *
 */
public final class EnumConstants {

    public static final String LESS_SEVERE = "LESS_SEVERE";
    public static final String MORE_SEVERE = "MORE_SEVERE";
    public static final String UNDEFINED = "UNDEFINED";
    public static final String NO_CHANGE = "NO_CHANGE";

    public static final String ACTIVE_ACKNOWLEDGED = "ACTIVE_ACKNOWLEDGED";
    public static final String ACTIVE_UNACKNOWLEDGED = "ACTIVE_UNACKNOWLEDGED";
    public static final String CLEARED_ACKNOWLEDGED = "CLEARED_ACKNOWLEDGED";
    public static final String CLEARED_UNACKNOWLEDGED = "CLEARED_UNACKNOWLEDGED";

    public static final String ALARM = "ALARM";
    public static final String ERROR_MESSAGE = "ERROR_MESSAGE";
    public static final String NO_SYNCHABLE_ALARM = "NO_SYNCHABLE_ALARM";
    public static final String REPEATED_ALARM = "REPEATED_ALARM";
    public static final String SYNCHRONIZATION_ALARM = "SYNCHRONIZATION_ALARM";
    public static final String HEARTBEAT_ALARM = "HEARTBEAT_ALARM";
    public static final String SYNCHRONIZATION_STARTED = "SYNCHRONIZATION_STARTED";
    public static final String SYNCHRONIZATION_ENDED = "SYNCHRONIZATION_ENDED";
    public static final String SYNCHRONIZATION_ABORTED = "SYNCHRONIZATION_ABORTED";
    public static final String SYNCHRONIZATION_IGNORED = "SYNCHRONIZATION_IGNORED";
    public static final String CLEAR_LIST = "CLEAR_LIST";
    public static final String REPEATED_ERROR_MESSAGE = "REPEATED_ERROR_MESSAGE";
    public static final String REPEATED_NON_SYNCHABLE = "REPEATED_NON_SYNCHABLE";
    public static final String UPDATE = "UPDATE";
    public static final String NODE_SUSPENDED = "NODE_SUSPENDED";
    public static final String HB_FAILURE_NO_SYNCH = "HB_FAILURE_NO_SYNCH";
    public static final String SYNC_NETWORK = "SYNC_NETWORK";
    public static final String TECHNICIAN_PRESENT = "TECHNICIAN_PRESENT";
    public static final String ALARM_SUPPRESSED_ALARM = "ALARM_SUPPRESSED_ALARM";
    public static final String OSCILLATORY_HB_ALARM = "OSCILLATORY_HB_ALARM";
    public static final String UNKNOWN_RECORD_TYPE = "UNKNOWN_RECORD_TYPE";

    public static final String CRITICAL = "CRITICAL";
    public static final String MAJOR = "MAJOR";
    public static final String MINOR = "MINOR";
    public static final String WARNING = "WARNING";
    public static final String INDETERMINATE = "INDETERMINATE";
    public static final String CLEARED = "CLEARED";

    public static final String NEW = "NEW";
    public static final String CLEAR = "CLEAR";
    public static final String CHANGE = "CHANGE";
    public static final String ACKSTATE_CHANGE = "ACKSTATE_CHANGE";
    public static final String COMMENT = "COMMENT";
    public static final String CLOSED = "CLOSED";

    private EnumConstants() {
    }

}