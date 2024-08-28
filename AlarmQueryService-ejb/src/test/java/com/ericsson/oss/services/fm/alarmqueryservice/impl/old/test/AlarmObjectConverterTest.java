
package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ACKOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARM_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CEASEOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CEASETIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CI_GROUP_1;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CI_GROUP_2;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CLEARED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CORRELATED_VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CRITICAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENT_POID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FMX_GENERATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LAST_ALARM_OPERATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MAJOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MANUAL_CEASE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MINOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PRESENTSEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PREVIOUSSEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROBLEMTEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROCESSING_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.RECORDTYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.REPEATCOUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ROOT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CI_GROUP_1;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CI_GROUP_2;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SPECIFICPROBLEM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SYNC_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.TRENDINDICATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.UNDEFINED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.WARNING;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord.AlarmRecordType;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord.EventTrendIndication;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.AlarmObjectConverter;
import com.ericsson.oss.services.fm.common.addinfo.CorrelationType;

@RunWith(MockitoJUnitRunner.class)
public class AlarmObjectConverterTest {

    @InjectMocks
    AlarmObjectConverter alarmObjectConverter;

    Map<String, Object> map = mock(HashMap.class);
    Set<String> sampleSet = new HashSet();

    List<String> headers = new ArrayList<>();
    Object[] objects = new Object[25];

    @Test
    public void testconvertToAlarmObjectwithPresentSeverity() {

        sampleSet.add(PRESENTSEVERITY);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(PRESENTSEVERITY)).thenReturn("INDETERMINATE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.INDETERMINATE);

        sampleSet.add(PRESENTSEVERITY);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(PRESENTSEVERITY)).thenReturn("INDETERMINATE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.INDETERMINATE);

        when(map.get(PRESENTSEVERITY)).thenReturn(CLEARED);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.CLEARED);

        when(map.get(PRESENTSEVERITY)).thenReturn(MAJOR);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.MAJOR);

        when(map.get(PRESENTSEVERITY)).thenReturn(CRITICAL);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.CRITICAL);

        when(map.get(PRESENTSEVERITY)).thenReturn(MINOR);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.MINOR);

        when(map.get(PRESENTSEVERITY)).thenReturn(WARNING);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.WARNING);

        when(map.get(PRESENTSEVERITY)).thenReturn(UNDEFINED);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.UNDEFINED);
    }

    @Test
    public void testconvertToAlarmObjectwithPreviousSeverity() {

        sampleSet.add(PREVIOUSSEVERITY);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(PREVIOUSSEVERITY)).thenReturn("INDETERMINATE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.INDETERMINATE);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(CLEARED);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.CLEARED);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(MAJOR);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.MAJOR);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(CRITICAL);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.CRITICAL);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(MINOR);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.MINOR);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(WARNING);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.WARNING);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(UNDEFINED);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.UNDEFINED);
    }

    @Test
    public void testconvertToAlarmObjectwithRecordType() {

        sampleSet.add(RECORDTYPE);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(RECORDTYPE)).thenReturn("ALARM");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.ALARM));

        when(map.get(RECORDTYPE)).thenReturn("ERROR_MESSAGE");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.ERROR_MESSAGE));

        when(map.get(RECORDTYPE)).thenReturn("NON_SYNCHABLE_ALARM");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.NON_SYNCHABLE_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("REPEATED_ALARM");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.REPEATED_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_ALARM");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("HEARTBEAT_ALARM");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.HEARTBEAT_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_ABORTED");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_ABORTED));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_IGNORED");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_IGNORED));

        when(map.get(RECORDTYPE)).thenReturn("CLEAR_LIST");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.CLEAR_LIST));

        when(map.get(RECORDTYPE)).thenReturn("REPEATED_ERROR_MESSAGE");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.REPEATED_ERROR_MESSAGE));

        when(map.get(RECORDTYPE)).thenReturn("REPEATED_NON_SYNCHABLE");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.REPEATED_NON_SYNCHABLE));

        when(map.get(RECORDTYPE)).thenReturn("UPDATE");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.UPDATE));

        when(map.get(RECORDTYPE)).thenReturn("NODE_SUSPENDED");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.NODE_SUSPENDED));

        when(map.get(RECORDTYPE)).thenReturn("HB_FAILURE_NO_SYNCH");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.HB_FAILURE_NO_SYNCH));

        when(map.get(RECORDTYPE)).thenReturn("SYNC_NETWORK");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNC_NETWORK));

        when(map.get(RECORDTYPE)).thenReturn("TECHNICIAN_PRESENT");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.TECHNICIAN_PRESENT));

        when(map.get(RECORDTYPE)).thenReturn("ALARM_SUPPRESSED_ALARM");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.ALARM_SUPPRESSED_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("OSCILLATORY_HB_ALARM");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.OSCILLATORY_HB_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_STARTED");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_STARTED));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_ENDED");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_ENDED));

        when(map.get(RECORDTYPE)).thenReturn("UNKNOWN_RECORD_TYPE");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.UNKNOWN_RECORD_TYPE));

        when(map.get(RECORDTYPE)).thenReturn(UNDEFINED);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.UNDEFINED));

        when(map.get(RECORDTYPE)).thenReturn("OUT_OF_SYNC");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRecordType().equals(AlarmRecordType.OUT_OF_SYNC));
    }

    @Test
    public void testconvertToAlarmObjectwithAlarmState() {

        sampleSet.add(ALARM_STATE);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(ALARM_STATE)).thenReturn("ACTIVE_ACKNOWLEDGED");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.ACTIVE_ACKNOWLEDGED);

        when(map.get(ALARM_STATE)).thenReturn("ACTIVE_UNACKNOWLEDGED");
        when(map.get(ALARM_STATE)).thenReturn("ACTIVE_UNACKNOWLEDGED");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.ACTIVE_UNACKNOWLEDGED);

        when(map.get(ALARM_STATE)).thenReturn("CLEARED_ACKNOWLEDGED");
        when(map.get(ALARM_STATE)).thenReturn("CLEARED_ACKNOWLEDGED");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.CLEARED_ACKNOWLEDGED);

        when(map.get(ALARM_STATE)).thenReturn("CLEARED_UNACKNOWLEDGED");
        when(map.get(ALARM_STATE)).thenReturn("CLEARED_UNACKNOWLEDGED");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.CLEARED_UNACKNOWLEDGED);

        when(map.get(ALARM_STATE)).thenReturn("CLOSED");
        when(map.get(ALARM_STATE)).thenReturn("CLOSED");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.CLOSED);

    }

    @Test
    public void testconvertToAlarmObjectwithTrendIndication() {

        sampleSet.add(TRENDINDICATION);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(TRENDINDICATION)).thenReturn("LESS_SEVERE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getTrendIndication(), (EventTrendIndication.LESS_SEVERE));

        when(map.get(TRENDINDICATION)).thenReturn("MORE_SEVERE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getTrendIndication(), (EventTrendIndication.MORE_SEVERE));

        when(map.get(TRENDINDICATION)).thenReturn("NO_CHANGE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getTrendIndication(), (EventTrendIndication.NO_CHANGE));

        when(map.get(TRENDINDICATION)).thenReturn(UNDEFINED);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getTrendIndication(), (EventTrendIndication.UNDEFINED));
    }

    @Test
    public void testConvertToAlarmObjectWithRoot() {
        sampleSet.add(ROOT);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(ROOT)).thenReturn("NOT APPLICABLE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getRoot(), CorrelationType.NOT_APPLICABLE);

        when(map.get(ROOT)).thenReturn("PRIMARY");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getRoot(), CorrelationType.PRIMARY);

        when(map.get(ROOT)).thenReturn("SECONDARY");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getRoot(), CorrelationType.SECONDARY);
    }

    @Test
    public void testConvertToAlarmObjectWithCiFirstGroup() {
        sampleSet.add(CI_GROUP_1);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(CI_GROUP_1)).thenReturn("81d4fae-7dec-11d0-a765-00a0c91e6bf6");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getCiFirstGroup(), "81d4fae-7dec-11d0-a765-00a0c91e6bf6");
    }

    @Test
    public void testConvertToAlarmObjectWithCiSecondGroup() {
        sampleSet.add(CI_GROUP_2);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(CI_GROUP_2)).thenReturn("81d4fae-7dec-11d0-a765-00a0c91e6bf6");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getCiSecondGroup(), "81d4fae-7dec-11d0-a765-00a0c91e6bf6");
    }

    @Test
    public void testConvertTolastAlarmOpetartionCoverter() {
        sampleSet.add(LAST_ALARM_OPERATION);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn(UNDEFINED);
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.UNDEFINED);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("NEW");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.NEW);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("CLEAR");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.CLEAR);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("CHANGE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.CHANGE);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("ACKSTATE_CHANGE");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.ACKSTATE_CHANGE);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("COMMENT");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.COMMENT);
    }

    @Test
    public void testConvertToAlarmRecordWithOrr() {
        headers.add("objectOfReference");
        objects[0] = "MeContext=LTE30ERBS00001";
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getObjectOfReference(), ("MeContext=LTE30ERBS00001"));

    }

    @Test
    public void testConvertToAlarmRecordWithEventPoId() {
        headers.add(EVENT_POID);
        objects[0] = 1234555L;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getEventPoId().equals(1234555L));

    }

    @Test
    public void testConvertToAlarmRecordWithRepeatCount() {
        headers.add(REPEATCOUNT);
        objects[0] = 1;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getRepeatCount().equals(1));

    }

    @Test
    public void testConvertToAlarmRecordWithOscillationCount() {
        headers.add("oscillationCount");
        objects[0] = 1;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getOscillationCount().equals(1));

    }

    @Test
    public void testConvertToAlarmRecordWithFdn() {
        headers.add("fdn");
        objects[0] = "NetworkElement=LTE35ERBS0001";
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getFdn(), "NetworkElement=LTE35ERBS0001");

    }

    @Test
    public void testConvertToAlarmRecordWithEventType() {
        headers.add("eventType");
        objects[0] = "eventType1";
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getEventType(), ("eventType1"));

    }

    @Test
    public void testConvertToAlarmRecordWithEventTime() {
        headers.add("eventTime");
        final Date date = new Date();
        objects[0] = date;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getEventTime().equals(date));

    }

    @Test
    public void testConvertToAlarmRecordWithInsertTime() {
        headers.add("insertTime");
        final Date date = new Date();
        objects[0] = date;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getInsertTime().equals(date));

    }

    @Test
    public void testConvertToAlarmRecordWithLastUpdated() {
        headers.add("lastUpdated");
        final Date date = new Date();
        objects[0] = date;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getLastUpdated().equals(date));

    }

    @Test
    public void testConvertToAlarmRecordWithProbableCause() {
        headers.add("probableCause");
        objects[0] = "probableCause1";
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getProbableCause(), ("probableCause1"));

    }

    @Test
    public void testConvertToAlarmRecordWithSpecificProblem() {
        headers.add(SPECIFICPROBLEM);
        objects[0] = "specificProblem1";
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getSpecificProblem(), ("specificProblem1"));

    }

    @Test
    public void testConvertToAlarmRecordWithBackupStatus() {
        headers.add("backupStatus");
        objects[0] = false;
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getBackupStatus(), (false));

    }

    @Test
    public void testConvertToAlarmRecordWithBackupObjectOfReference() {
        headers.add("backupObjectInstance");
        objects[0] = "backupObjectInstance";
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getBackupObjectInstance(), ("backupObjectInstance"));

    }

    @Test
    public void testConvertToAlarmRecordWithAlarmNumber() {
        headers.add("alarmNumber");
        objects[0] = 12345L;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getAlarmNumber().equals(12345L));

    }

    @Test
    public void testConvertToAlarmRecordWithAlarmId() {
        headers.add(ALARMID);
        objects[0] = 12345L;
        alarmObjectConverter.convertToAlarmObject(objects, headers);
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getAlarmId().equals(12345L));

    }

    @Test
    public void testConvertToAlarmRecordWithProposedRepairAction() {
        headers.add("proposedRepairAction");
        objects[0] = "proposedRepairAction";
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getProposedRepairAction(), ("proposedRepairAction"));

    }

    @Test
    public void testConvertToAlarmRecordProcessingType() {
        headers.add(PROCESSING_TYPE);
        objects[0] = PROCESSING_TYPE;
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getProcessingType(), (PROCESSING_TYPE));

    }

    @Test
    public void testConvertToAlarmRecordFmxGenerated() {
        headers.add(FMX_GENERATED);
        objects[0] = FMX_GENERATED;
        assertEquals(alarmObjectConverter.convertToAlarmObject(objects, headers).getFmxGenerated(), (FMX_GENERATED));

    }

    @Test
    public void testConvertToAlarmRecordCorrelatedVisibility() {
        headers.add(CORRELATED_VISIBILITY);
        objects[0] = true;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).isCorrelatedVisibility());

    }

    @Test
    public void testConvertToAlarmRecordSyncState() {
        headers.add(SYNC_STATE);
        objects[0] = false;
        assertFalse(alarmObjectConverter.convertToAlarmObject(objects, headers).isSyncState());

    }

    @Test
    public void testConvertToAlarmRecordVisibility() {
        headers.add(VISIBILITY);
        objects[0] = true;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).isVisibility());

    }

    @Test
    public void testConvertToAlarmRecordManualCease() {
        headers.add(MANUAL_CEASE);
        objects[0] = true;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).isManualCease());
    }

    @Test
    public void testConvertToAlarmRecordWithCeaseTime() {
        headers.add(CEASETIME);
        final Date date = new Date();
        objects[0] = date;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getCeaseTime().equals(date));

    }

    @Test
    public void testConvertToAlarmRecordWithCeaseOperator() {
        headers.add(CEASEOPERATOR);
        objects[0] = CEASEOPERATOR;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getCeaseOperator().equalsIgnoreCase(CEASEOPERATOR));

    }

    @Test
    public void testConvertToAlarmRecordWithAckTime() {
        headers.add("ackTime");
        final Date date = new Date();
        objects[0] = date;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getAckTime().equals(date));

    }

    @Test
    public void testConvertToAlarmRecordWithAckOperator() {
        headers.add(ACKOPERATOR);
        objects[0] = ACKOPERATOR;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getAckOperator().equalsIgnoreCase(ACKOPERATOR));

    }

    @Test
    public void testConvertToAlarmRecordWithProblemText() {
        headers.add(PROBLEMTEXT);
        objects[0] = PROBLEMTEXT;
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getProblemText().equalsIgnoreCase(PROBLEMTEXT));

    }

    @Test
    public void testConvertToAlarmRecordWithProblemDetail() {
        headers.add("problemDetail");
        objects[0] = "problemDetail";
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getProblemDetail().equalsIgnoreCase("problemDetail"));

    }

    @Test
    public void testConvertToAlarmRecordWithAdditionalInformation() {
        headers.add("additionalInformation");
        objects[0] = "additionalInformation";
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getAdditionalInformation().equalsIgnoreCase("additionalInformation"));

    }

    @Test
    public void testConvertToAlarmRecordWithCommentText() {
        headers.add("commentText");
        objects[0] = "commentText";
        assertTrue(alarmObjectConverter.convertToAlarmObject(objects, headers).getCommentText().equalsIgnoreCase("commentText"));

    }

    @Test
    public void testConvertToAlarmRecordWithPresentSeverity() {
        headers.add(PRESENTSEVERITY);
        objects[0] = PRESENTSEVERITY;
        alarmObjectConverter.convertToAlarmObject(objects, headers);

    }

    @Test
    public void testConvertToAlarmRecordWithPreviousSeverity() {
        headers.add(PREVIOUSSEVERITY);
        objects[0] = PREVIOUSSEVERITY;
        alarmObjectConverter.convertToAlarmObject(objects, headers);

    }

    @Test
    public void testConvertToAlarmRecordWithRecordType() {
        headers.add(RECORDTYPE);
        objects[0] = RECORDTYPE;
        alarmObjectConverter.convertToAlarmObject(objects, headers);

    }

    @Test
    public void testConvertToAlarmRecordWithAlarmState() {
        headers.add(ALARM_STATE);
        objects[0] = ALARM_STATE;
        alarmObjectConverter.convertToAlarmObject(objects, headers);

    }

    @Test
    public void testConvertToAlarmRecordWithTrendIndication() {
        headers.add(TRENDINDICATION);
        objects[0] = TRENDINDICATION;
        alarmObjectConverter.convertToAlarmObject(objects, headers);

    }

    @Test
    public void testConvertToAlarmRecordWithLastAlarmOperation() {
        headers.add(LAST_ALARM_OPERATION);
        objects[0] = LAST_ALARM_OPERATION;
        alarmObjectConverter.convertToAlarmObject(objects, headers);

    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithObjectOfReference() {

        sampleSet.add("objectOfReference");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("objectOfReference")).thenReturn("MeContext=LTE30ERBS00001");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getObjectOfReference(), ("MeContext=LTE30ERBS00001"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithEventPoId() {

        sampleSet.add(EVENT_POID);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(EVENT_POID)).thenReturn(123L);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getEventPoId().equals(123L));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithRepeatCount() {

        sampleSet.add(REPEATCOUNT);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(REPEATCOUNT)).thenReturn(4L);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRepeatCount().equals(4));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithOscillationCount() {

        sampleSet.add("oscillationCount");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("oscillationCount")).thenReturn(4L);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getOscillationCount().equals(4));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithFdn() {

        sampleSet.add("fdn");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("fdn")).thenReturn("NetworkElement=1");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getFdn(), ("NetworkElement=1"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithEventType() {

        sampleSet.add("eventType");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("eventType")).thenReturn("eventType");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getEventType().equals("eventType"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithEventTime() {

        sampleSet.add("eventTime");
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get("eventTime")).thenReturn(date);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getEventTime().equals(date));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithInserTime() {

        sampleSet.add("insertTime");
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get("insertTime")).thenReturn(date);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getInsertTime().equals(date));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithLastUpdated() {

        sampleSet.add("lastUpdated");
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get("lastUpdated")).thenReturn(date);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getLastUpdated().equals(date));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithSpecificProblem() {

        sampleSet.add(SPECIFICPROBLEM);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(SPECIFICPROBLEM)).thenReturn(SPECIFICPROBLEM);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getSpecificProblem().equals(SPECIFICPROBLEM));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithBackupStatus() {

        sampleSet.add("backupStatus");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("backupStatus")).thenReturn(true);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getBackupStatus().equals(true));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithProposedRepairAction() {

        sampleSet.add("proposedRepairAction");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("proposedRepairAction")).thenReturn("proposedRepairAction");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getProposedRepairAction().equals("proposedRepairAction"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithAlarmNumber() {

        sampleSet.add("alarmNumber");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("alarmNumber")).thenReturn(123L);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAlarmNumber().equals(123L));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithProblemText() {

        sampleSet.add("problemText");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("problemText")).thenReturn("problemText");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getProblemText().equals("problemText"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithProblemDetail() {

        sampleSet.add("problemDetail");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("problemDetail")).thenReturn("problemDetail");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getProblemDetail().equals("problemDetail"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithAdditionalInformation() {

        sampleSet.add("additionalInformation");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("additionalInformation")).thenReturn("additionalInformation");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAdditionalInformation().equals("additionalInformation"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithCommentText() {

        sampleSet.add("commentText");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("commentText")).thenReturn("commentText");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getCommentText().equals("commentText"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithBackupInstance() {

        sampleSet.add("backupObjectInstance");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("backupObjectInstance")).thenReturn("backupObjectInstance");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getBackupObjectInstance().equals("backupObjectInstance"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithProbableCause() {

        sampleSet.add("probableCause");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("probableCause")).thenReturn(123);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getProbableCause().equals("123"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithComments() {

        sampleSet.add("comments");
        when(map.keySet()).thenReturn(sampleSet);
        final List<Map<Object, Object>> comments = new ArrayList<Map<Object, Object>>();
        when(map.get("comments")).thenReturn(comments);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getComments().equals(comments));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithAckOperator() {

        sampleSet.add(ACKOPERATOR);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(ACKOPERATOR)).thenReturn(ACKOPERATOR);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAckOperator().equals(ACKOPERATOR));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithAckTime() {

        sampleSet.add("ackTime");
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get("ackTime")).thenReturn(date);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAckTime().equals(date));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithAlarmId() {

        sampleSet.add(ALARMID);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(ALARMID)).thenReturn(123L);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAlarmId().equals(123L));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithCeaseTime() {

        sampleSet.add(CEASETIME);
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get(CEASETIME)).thenReturn(date);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getCeaseTime().equals(date));

    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithCeaseOperator() {

        sampleSet.add(CEASEOPERATOR);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(CEASEOPERATOR)).thenReturn("AASOperator");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getCeaseOperator().equalsIgnoreCase("AASOperator"));
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectwithPresentSeverity() {

        sampleSet.add(PRESENTSEVERITY);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(PRESENTSEVERITY)).thenReturn("INDETERMINATE");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.INDETERMINATE);

        when(map.get(PRESENTSEVERITY)).thenReturn(CLEARED);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.CLEARED);

        when(map.get(PRESENTSEVERITY)).thenReturn(MAJOR);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.MAJOR);

        when(map.get(PRESENTSEVERITY)).thenReturn(CRITICAL);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.CRITICAL);

        when(map.get(PRESENTSEVERITY)).thenReturn(MINOR);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.MINOR);

        when(map.get(PRESENTSEVERITY)).thenReturn(WARNING);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.WARNING);

        when(map.get(PRESENTSEVERITY)).thenReturn(UNDEFINED);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPresentSeverity(), AlarmRecord.EventSeverity.UNDEFINED);
    }

    @Test
    public void testConvertHistoricalDataToAlarmObjectWithLastAlarmOperation() {

        sampleSet.add(LAST_ALARM_OPERATION);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn(UNDEFINED);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.UNDEFINED);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("NEW");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.NEW);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("CLEAR");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.CLEAR);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("CHANGE");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.CHANGE);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("ACKSTATE_CHANGE");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getLastAlarmOperation(),
                AlarmRecord.LastAlarmOperation.ACKSTATE_CHANGE);
        when(map.get(LAST_ALARM_OPERATION)).thenReturn("COMMENT");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getLastAlarmOperation(), AlarmRecord.LastAlarmOperation.COMMENT);

    }

    @Test
    public void testconvertHistoricalDataToAlarmObjectwithAlarmState() {
        sampleSet.add(ALARM_STATE);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(ALARM_STATE)).thenReturn("ACTIVE_ACKNOWLEDGED");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.ACTIVE_ACKNOWLEDGED);

        when(map.get(ALARM_STATE)).thenReturn("ACTIVE_UNACKNOWLEDGED");
        when(map.get(ALARM_STATE)).thenReturn("ACTIVE_UNACKNOWLEDGED");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.ACTIVE_UNACKNOWLEDGED);

        when(map.get(ALARM_STATE)).thenReturn("CLEARED_ACKNOWLEDGED");
        when(map.get(ALARM_STATE)).thenReturn("CLEARED_ACKNOWLEDGED");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.CLEARED_ACKNOWLEDGED);

        when(map.get(ALARM_STATE)).thenReturn("CLEARED_UNACKNOWLEDGED");
        when(map.get(ALARM_STATE)).thenReturn("CLEARED_UNACKNOWLEDGED");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.CLEARED_UNACKNOWLEDGED);

        when(map.get(ALARM_STATE)).thenReturn("CLOSED");
        when(map.get(ALARM_STATE)).thenReturn("CLOSED");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getAlarmState(), AlarmRecord.EventState.CLOSED);

    }

    @Test
    public void testconvertHistoricalDataToAlarmObjectwithPreviousSeverity() {

        sampleSet.add(PREVIOUSSEVERITY);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(PREVIOUSSEVERITY)).thenReturn("INDETERMINATE");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.INDETERMINATE);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(CLEARED);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.CLEARED);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(MAJOR);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.MAJOR);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(CRITICAL);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.CRITICAL);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(MINOR);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.MINOR);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(WARNING);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.WARNING);

        when(map.get(PREVIOUSSEVERITY)).thenReturn(UNDEFINED);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getPreviousSeverity(), AlarmRecord.EventSeverity.UNDEFINED);
    }

    @Test
    public void testconvertHistoricalDataToAlarmObjectwithRecordType() {

        sampleSet.add(RECORDTYPE);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(RECORDTYPE)).thenReturn("ALARM");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.ALARM));

        when(map.get(RECORDTYPE)).thenReturn("ERROR_MESSAGE");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.ERROR_MESSAGE));

        when(map.get(RECORDTYPE)).thenReturn("NON_SYNCHABLE_ALARM");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.NON_SYNCHABLE_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("REPEATED_ALARM");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.REPEATED_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_ALARM");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("HEARTBEAT_ALARM");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.HEARTBEAT_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_ABORTED");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_ABORTED));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_IGNORED");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_IGNORED));

        when(map.get(RECORDTYPE)).thenReturn("CLEAR_LIST");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.CLEAR_LIST));

        when(map.get(RECORDTYPE)).thenReturn("REPEATED_ERROR_MESSAGE");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.REPEATED_ERROR_MESSAGE));

        when(map.get(RECORDTYPE)).thenReturn("REPEATED_NON_SYNCHABLE");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.REPEATED_NON_SYNCHABLE));

        when(map.get(RECORDTYPE)).thenReturn("UPDATE");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.UPDATE));

        when(map.get(RECORDTYPE)).thenReturn("NODE_SUSPENDED");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.NODE_SUSPENDED));

        when(map.get(RECORDTYPE)).thenReturn("HB_FAILURE_NO_SYNCH");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.HB_FAILURE_NO_SYNCH));

        when(map.get(RECORDTYPE)).thenReturn("SYNC_NETWORK");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNC_NETWORK));

        when(map.get(RECORDTYPE)).thenReturn("TECHNICIAN_PRESENT");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.TECHNICIAN_PRESENT));

        when(map.get(RECORDTYPE)).thenReturn("ALARM_SUPPRESSED_ALARM");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.ALARM_SUPPRESSED_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("OSCILLATORY_HB_ALARM");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.OSCILLATORY_HB_ALARM));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_STARTED");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_STARTED));

        when(map.get(RECORDTYPE)).thenReturn("SYNCHRONIZATION_ENDED");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.SYNCHRONIZATION_ENDED));

        when(map.get(RECORDTYPE)).thenReturn("UNKNOWN_RECORD_TYPE");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.UNKNOWN_RECORD_TYPE));

        when(map.get(RECORDTYPE)).thenReturn(UNDEFINED);
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.UNDEFINED));

        when(map.get(RECORDTYPE)).thenReturn("OUT_OF_SYNC");
        assertTrue(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getRecordType().equals(AlarmRecordType.OUT_OF_SYNC));

    }

    @Test
    public void testconvertHistoricalDataToAlarmObjectwithTrendIndication() {

        sampleSet.add(TRENDINDICATION);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(TRENDINDICATION)).thenReturn("LESS_SEVERE");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getTrendIndication(), (EventTrendIndication.LESS_SEVERE));

        when(map.get(TRENDINDICATION)).thenReturn("MORE_SEVERE");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getTrendIndication(), (EventTrendIndication.MORE_SEVERE));

        when(map.get(TRENDINDICATION)).thenReturn("NO_CHANGE");
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getTrendIndication(), (EventTrendIndication.NO_CHANGE));

        when(map.get(TRENDINDICATION)).thenReturn(UNDEFINED);
        assertEquals(alarmObjectConverter.convertHistoricalDataToAlarmObject(map).getTrendIndication(), (EventTrendIndication.UNDEFINED));
    }

    @Test
    public void testConvertToAlarmObjectWithManualCease() {

        sampleSet.add(MANUAL_CEASE);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(MANUAL_CEASE)).thenReturn(true);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).isManualCease());
    }

    @Test
    public void testConvertToAlarmObjectWithSyncState() {

        sampleSet.add(SYNC_STATE);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(SYNC_STATE)).thenReturn(true);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).isSyncState());
    }

    @Test
    public void testConvertToAlarmObjectWithVisibility() {

        sampleSet.add(VISIBILITY);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(VISIBILITY)).thenReturn(true);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).isVisibility());
    }

    @Test
    public void testConvertToAlarmObjectWitCorrelatedVisibility() {

        sampleSet.add(CORRELATED_VISIBILITY);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(CORRELATED_VISIBILITY)).thenReturn(true);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).isCorrelatedVisibility());
    }

    @Test
    public void testConvertToAlarmObjectWithProcessingType() {

        sampleSet.add(PROCESSING_TYPE);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(PROCESSING_TYPE)).thenReturn(PROCESSING_TYPE);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getProcessingType().equals(PROCESSING_TYPE));
    }

    @Test
    public void testConvertToAlarmObjectWithFmxGenerated() {

        sampleSet.add(FMX_GENERATED);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(FMX_GENERATED)).thenReturn("true");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getFmxGenerated().equals("true"));
    }

    @Test
    public void testConvertToAlarmObjectWithEventPoId() {

        sampleSet.add(EVENT_POID);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(EVENT_POID)).thenReturn(123L);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getEventPoId().equals(123L));
    }

    @Test
    public void testConvertToAlarmObjectWithRepeatCount() {

        sampleSet.add(REPEATCOUNT);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(REPEATCOUNT)).thenReturn(4);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRepeatCount().equals(4));
    }

    @Test
    public void testConvertToAlarmObjectWithOscillationCount() {

        sampleSet.add("oscillationCount");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("oscillationCount")).thenReturn(4);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getRepeatCount().equals(4));
    }

    @Test
    public void testConvertToAlarmObjectWithFdn() {

        sampleSet.add("fdn");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("fdn")).thenReturn("NetworkElement=1");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getFdn(), ("NetworkElement=1"));
    }

    @Test
    public void testConvertToAlarmObjectWithEventType() {

        sampleSet.add("eventType");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("eventType")).thenReturn("eventType");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getEventType().equals("eventType"));
    }

    @Test
    public void testConvertToAlarmObjectWithEventTime() {

        sampleSet.add("eventTime");
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get("eventTime")).thenReturn(date);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getEventTime().equals(date));
    }

    @Test
    public void testConvertToAlarmObjectWithInsertTime() {

        sampleSet.add("insertTime");
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get("insertTime")).thenReturn(date);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getInsertTime().equals(date));
    }

    @Test
    public void testConvertToAlarmObjectWithLastUpdated() {

        sampleSet.add("lastUpdated");
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get("lastUpdated")).thenReturn(date);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getLastUpdated().equals(date));
    }

    @Test
    public void testConvertDataToAlarmObjectWithSpecificProblem() {

        sampleSet.add(SPECIFICPROBLEM);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(SPECIFICPROBLEM)).thenReturn(SPECIFICPROBLEM);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getSpecificProblem().equals(SPECIFICPROBLEM));
    }

    @Test
    public void testConvertToAlarmObjectWithBackupStatus() {

        sampleSet.add("backupStatus");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("backupStatus")).thenReturn(true);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getBackupStatus().equals(true));
    }

    @Test
    public void testConvertToAlarmObjectWithObjectOfReference() {

        sampleSet.add("objectOfReference");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("objectOfReference")).thenReturn("MeContext=1");
        assertEquals(alarmObjectConverter.convertToAlarmObject(map).getObjectOfReference(), ("MeContext=1"));
    }

    @Test
    public void testConvertToAlarmObjectWithProposedRepairAction() {

        sampleSet.add("proposedRepairAction");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("proposedRepairAction")).thenReturn("proposedRepairAction");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getProposedRepairAction().equals("proposedRepairAction"));
    }

    @Test
    public void testConvertToAlarmObjectWithAlarmNumber() {

        sampleSet.add("alarmNumber");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("alarmNumber")).thenReturn(123L);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getAlarmNumber().equals(123L));
    }

    @Test
    public void testConvertToAlarmObjectWithProblemText() {

        sampleSet.add("problemText");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("problemText")).thenReturn("problemText");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getProblemText().equals("problemText"));
    }

    @Test
    public void testConvertToAlarmObjectWithProblemDetail() {

        sampleSet.add("problemDetail");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("problemDetail")).thenReturn("problemDetail");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getProblemDetail().equals("problemDetail"));
    }

    @Test
    public void testConvertToAlarmObjectWithAdditionalInformation() {

        sampleSet.add("additionalInformation");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("additionalInformation")).thenReturn("additionalInformation");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getAdditionalInformation().equals("additionalInformation"));
    }

    @Test
    public void testConvertToAlarmObjectWithCommentText() {

        sampleSet.add("commentText");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("commentText")).thenReturn("commentText");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getCommentText().equals("commentText"));
    }

    @Test
    public void testConvertToAlarmObjectWithBackupInstance() {

        sampleSet.add("backupObjectInstance");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("backupObjectInstance")).thenReturn("backupObjectInstance");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getBackupObjectInstance().equals("backupObjectInstance"));
        ;
    }

    @Test
    public void testConvertToAlarmObjectWithProbableCause() {

        sampleSet.add("probableCause");
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get("probableCause")).thenReturn("123");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getProbableCause().equals("123"));
    }

    @Test
    public void testConvertToAlarmObjectWithComments() {

        sampleSet.add("comments");
        when(map.keySet()).thenReturn(sampleSet);
        final List<Map<Object, Object>> comments = new ArrayList<Map<Object, Object>>();
        when(map.get("comments")).thenReturn(comments);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getComments().equals(comments));
    }

    @Test
    public void testConvertToAlarmObjectWithAckOperator() {

        sampleSet.add(ACKOPERATOR);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(ACKOPERATOR)).thenReturn(ACKOPERATOR);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getAckOperator().equals(ACKOPERATOR));
    }

    @Test
    public void testConvertToAlarmObjectWithAckTime() {

        sampleSet.add("ackTime");
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get("ackTime")).thenReturn(date);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getAckTime().equals(date));
    }

    @Test
    public void testConvertToAlarmObjectWithAlarmId() {

        sampleSet.add(ALARMID);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(ALARMID)).thenReturn(123L);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getAlarmId().equals(123L));
    }

    @Test
    public void testConvertToAlarmObjectWithCeaseTime() {

        sampleSet.add(CEASETIME);
        when(map.keySet()).thenReturn(sampleSet);
        final Date date = new Date();
        when(map.get(CEASETIME)).thenReturn(date);
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getCeaseTime().equals(date));
    }

    @Test
    public void testConvertToAlarmObjectWithCeaseOperator() {

        sampleSet.add(CEASEOPERATOR);
        when(map.keySet()).thenReturn(sampleSet);
        when(map.get(CEASEOPERATOR)).thenReturn("AASOperator");
        assertTrue(alarmObjectConverter.convertToAlarmObject(map).getCeaseOperator().equalsIgnoreCase("AASOperator"));
    }

}