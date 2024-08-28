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

/**
 * Defines the possible values of Record Type of an Alarm.
 *
 */
public enum AlarmRecordType {
    UNDEFINED, ALARM, ERROR_MESSAGE, NON_SYNCHABLE_ALARM, REPEATED_ALARM, SYNCHRONIZATION_ALARM, HEARTBEAT_ALARM, SYNCHRONIZATION_STARTED, SYNCHRONIZATION_ENDED, SYNCHRONIZATION_ABORTED, SYNCHRONIZATION_IGNORED, CLEAR_LIST, REPEATED_ERROR_MESSAGE, REPEATED_NON_SYNCHABLE, UPDATE, NODE_SUSPENDED, HB_FAILURE_NO_SYNCH, SYNC_NETWORK, TECHNICIAN_PRESENT, ALARM_SUPPRESSED_ALARM, OSCILLATORY_HB_ALARM, UNKNOWN_RECORD_TYPE, OUT_OF_SYNC, CLEARALL;
}