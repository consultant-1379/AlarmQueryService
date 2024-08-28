/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ACK_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.BACKUP_STATUS;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CEASE_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CLEARED;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CORRELATED_RECORD_NAME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CORRELATED_VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CRITICAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.INDETERMINATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.INSERT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.LAST_UPDATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.MAJOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.MANUAL_CEASE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.MINOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OSCILLATION_COUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PREVIOUS_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.REPEAT_COUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SYNC_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.UNDEFINED;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.WARNING;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.NO_ALARMS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.ComparatorHelper;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;

@RunWith(MockitoJUnitRunner.class)
public class AlarmAttributeResponseBuilderTest {

    @InjectMocks
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;
    @Mock
    private OpenAlarmParser openAlarmParser;

    @Mock
    private ComparatorHelper comparatorHelper;

    private final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
    private static final List<String> integerTypeAttributes = new ArrayList<String>();
    private static final List<String> longTypeAttributes = new ArrayList<String>();
    private static final List<String> dateTypeAttributes = new ArrayList<String>();
    private static final List<String> booleanTypeAttributes = new ArrayList<String>();

    @Before
    public void setUp() {

        longTypeAttributes.add(ALARM_ID);
        longTypeAttributes.add(ALARM_NUMBER);
        integerTypeAttributes.add(REPEAT_COUNT);
        integerTypeAttributes.add(OSCILLATION_COUNT);

        dateTypeAttributes.add(CEASE_TIME);
        dateTypeAttributes.add(ACK_TIME);
        dateTypeAttributes.add(INSERT_TIME);
        dateTypeAttributes.add(EVENT_TIME);
        dateTypeAttributes.add(LAST_UPDATED);

        booleanTypeAttributes.add(VISIBILITY);
        booleanTypeAttributes.add(SYNC_STATE);
        booleanTypeAttributes.add(MANUAL_CEASE);
        booleanTypeAttributes.add(CORRELATED_RECORD_NAME);
        booleanTypeAttributes.add(BACKUP_STATUS);
        booleanTypeAttributes.add(CORRELATED_VISIBILITY);

        when(openAlarmParser.getIntegerTypeAttributes()).thenReturn(integerTypeAttributes);
        when(openAlarmParser.getLongTypeAttributes()).thenReturn(longTypeAttributes);
        when(openAlarmParser.getDateTypeAttributes()).thenReturn(dateTypeAttributes);
        when(openAlarmParser.getBooleanTypeAttributes()).thenReturn(booleanTypeAttributes);

        final Map<String, Object> alarmAttributeMap = new HashMap<String, Object>();
        alarmAttributeMap.put(ALARM_NUMBER, 111L);
        alarmAttributeMap.put(REPEAT_COUNT, 2);
        alarmAttributeMap.put(OSCILLATION_COUNT, 0);
        alarmAttributeMap.put(EVENT_TIME, new Date());
        alarmAttributeMap.put(SPECIFIC_PROBLEM, "specificProblem1");
        alarmAttributeMap.put(EVENT_TYPE, "eventType1");
        alarmAttributeMap.put(BACKUP_STATUS, true);
        alarmAttributeMap.put(SYNC_STATE, true);
        alarmAttributeMap.put(ADDITIONAL_INFORMATION, "fmx1:value1");
        final AlarmRecord alarmRecord1 = new AlarmRecord(alarmAttributeMap, null, null);

        final Map<String, Object> alarmAttributeMap1 = new HashMap<String, Object>();
        alarmAttributeMap1.put(ALARM_NUMBER, 333L);
        alarmAttributeMap1.put(REPEAT_COUNT, 1);
        alarmAttributeMap1.put(OSCILLATION_COUNT, 3);
        alarmAttributeMap1.put(EVENT_TIME, new Date());
        alarmAttributeMap1.put(SPECIFIC_PROBLEM, "specificProblem2");
        alarmAttributeMap1.put(EVENT_TYPE, "eventType2");
        alarmAttributeMap1.put(BACKUP_STATUS, false);
        alarmAttributeMap1.put(SYNC_STATE, false);
        alarmAttributeMap1.put(ADDITIONAL_INFORMATION, "fmx1:value1");

        final AlarmRecord alarmRecord2 = new AlarmRecord(alarmAttributeMap1, null, null);
        final AlarmRecord alarmRecord3 = new AlarmRecord(new HashMap<String, Object>(), null, null);
        alarmRecords.add(alarmRecord1);
        alarmRecords.add(alarmRecord3);
        alarmRecords.add(alarmRecord2);

        when(comparatorHelper.compareAttributesWithNullValuesForAscending(Matchers.anyObject(), Matchers.anyObject())).thenReturn(-1);
        when(comparatorHelper.compareAttributesWithNullValuesForDescending(Matchers.anyObject(), Matchers.anyObject())).thenReturn(-1);
        alarmAttributeResponseBuilder.prepare();
    }

    @Test
    public void testBuild_AlarmRecords_NoAlarms() {

        final AlarmAttributeResponse alarmQueryResponse = alarmAttributeResponseBuilder.buildAttributeResponse(new ArrayList<AlarmRecord>());
        assertEquals(NO_ALARMS, alarmQueryResponse.getResponse());

    }

    @Test
    public void testBuild_AlarmRecords_Success() {

        when(openAlarmParser.getLongTypeAttributes()).thenReturn(longTypeAttributes);
        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, ALARM_NUMBER, SortingOrder.ASCENDING));

    }

    @Test
    public void testBuild_AlarmRecordsWithLongAttributeDescendingSort_Success() {

        when(openAlarmParser.getLongTypeAttributes()).thenReturn(longTypeAttributes);
        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, ALARM_NUMBER, SortingOrder.DESCENDING));

    }

    @Test
    public void testBuild_AlarmRecordsWithIntegerAttributeDescendingSort_Success() {

        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, REPEAT_COUNT, SortingOrder.DESCENDING));

    }

    @Test
    public void testBuild_AlarmRecordsWithIntegerAttributeAscendingSort_Success() {

        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, OSCILLATION_COUNT, SortingOrder.ASCENDING));

    }

    @Test
    public void testBuild_AlarmRecordsWithDateAttributeDescendingSort_Success() {

        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, INSERT_TIME, SortingOrder.DESCENDING));

    }

    @Test
    public void testBuild_AlarmRecordsWithDateAttributeAscendingSort_Success() {

        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, EVENT_TIME, SortingOrder.ASCENDING));

    }

    @Test
    public void testBuild_AlarmRecordsWithStringAttributeDescendingSort_Success() {

        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, SPECIFIC_PROBLEM, SortingOrder.DESCENDING));

    }

    @Test
    public void testBuild_AlarmRecordsWithStringAttributeAscendingSort_Success() {

        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, EVENT_TYPE, SortingOrder.ASCENDING));

    }

    @Test
    public void testSort_AlarmRecordsWithBooleanAttributeDescendingSort() {

        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, BACKUP_STATUS, SortingOrder.DESCENDING));

    }

    @Test
    public void testSort_AlarmRecordsWithBooleanAttributeAscendingSort() {

        Assert.assertNotNull(alarmAttributeResponseBuilder.sort(alarmRecords, SYNC_STATE, SortingOrder.ASCENDING));
    }

    @Test
    public void testBuildSeverityResponse_PresentSeverityASC() {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        setUpSeveritySortingData(alarmRecords);
        final List<AlarmRecord> severitySortedRecords = alarmAttributeResponseBuilder.sortRecordsBasedOnSeverity(alarmRecords, PRESENT_SEVERITY,
                SortingOrder.ASCENDING);
        final AlarmAttributeResponse alarmQueryResponse = alarmAttributeResponseBuilder.buildAttributeResponse(severitySortedRecords);
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());
        assertEquals(INDETERMINATE, alarmQueryResponse.getAlarmRecords().get(0).getPresentSeverity().toString());
    }

    @Test
    public void testBuildSeverityResponse_PresentSeverityDESC() {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        setUpSeveritySortingData(alarmRecords);
        final List<AlarmRecord> severitySortedRecords = alarmAttributeResponseBuilder.sortRecordsBasedOnSeverity(alarmRecords, PRESENT_SEVERITY,
                SortingOrder.DESCENDING);
        final AlarmAttributeResponse alarmQueryResponse = alarmAttributeResponseBuilder.buildAttributeResponse(severitySortedRecords);
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());
        assertEquals(CRITICAL, alarmQueryResponse.getAlarmRecords().get(0).getPresentSeverity().toString());
    }

    @Test
    public void testBuildSeverityResponse_PreviousSeverityASC() {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        setUpSeveritySortingData(alarmRecords);
        final List<AlarmRecord> severitySortedRecords = alarmAttributeResponseBuilder.sortRecordsBasedOnSeverity(alarmRecords, PREVIOUS_SEVERITY,
                SortingOrder.ASCENDING);
        final AlarmAttributeResponse alarmQueryResponse = alarmAttributeResponseBuilder.buildAttributeResponse(severitySortedRecords);
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());
        assertEquals(UNDEFINED, alarmQueryResponse.getAlarmRecords().get(0).getPreviousSeverity().toString());
    }

    @Test
    public void testBuildSeverityResponse_PreviousSeverityDESC() {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        setUpSeveritySortingData(alarmRecords);
        final List<AlarmRecord> severitySortedRecords = alarmAttributeResponseBuilder.sortRecordsBasedOnSeverity(alarmRecords, PREVIOUS_SEVERITY,
                SortingOrder.DESCENDING);
        final AlarmAttributeResponse alarmQueryResponse = alarmAttributeResponseBuilder.buildAttributeResponse(severitySortedRecords);
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());
        assertEquals(MAJOR, alarmQueryResponse.getAlarmRecords().get(0).getPreviousSeverity().toString());
    }

    private void setUpSeveritySortingData(final List<AlarmRecord> alarmRecords) {

        final Map<String, Object> alarmAttributeMap1 = new HashMap<String, Object>();
        alarmAttributeMap1.put(PRESENT_SEVERITY, CRITICAL);
        alarmAttributeMap1.put(PREVIOUS_SEVERITY, WARNING);
        alarmAttributeMap1.put(EVENT_PO_ID, 28656456469L);
        final AlarmRecord alarmRecord1 = new AlarmRecord(alarmAttributeMap1, null, null);

        final Map<String, Object> alarmAttributeMap2 = new HashMap<String, Object>();
        alarmAttributeMap2.put(PRESENT_SEVERITY, CLEARED);
        alarmAttributeMap2.put(PREVIOUS_SEVERITY, MAJOR);
        alarmAttributeMap2.put(EVENT_PO_ID, 28148956146L);
        final AlarmRecord alarmRecord2 = new AlarmRecord(alarmAttributeMap2, null, null);

        final Map<String, Object> alarmAttributeMap3 = new HashMap<String, Object>();
        alarmAttributeMap3.put(PRESENT_SEVERITY, INDETERMINATE);
        alarmAttributeMap3.put(PREVIOUS_SEVERITY, UNDEFINED);
        alarmAttributeMap3.put(EVENT_PO_ID, 28548646655L);
        final AlarmRecord alarmRecord3 = new AlarmRecord(alarmAttributeMap3, null, null);

        final Map<String, Object> alarmAttributeMap4 = new HashMap<String, Object>();
        alarmAttributeMap4.put(PRESENT_SEVERITY, CRITICAL);
        alarmAttributeMap4.put(PREVIOUS_SEVERITY, MINOR);
        alarmAttributeMap4.put(EVENT_PO_ID, 28654656456L);
        final AlarmRecord alarmRecord4 = new AlarmRecord(alarmAttributeMap4, null, null);

        alarmRecords.add(alarmRecord1);
        alarmRecords.add(alarmRecord2);
        alarmRecords.add(alarmRecord3);
        alarmRecords.add(alarmRecord4);
    }

    @Test
    public void testSortRecordsBasedOnDynamicAlarmAttribute_ASCENDING() {
        List<AlarmRecord> sortedAlarmRecords = alarmAttributeResponseBuilder.sortRecordsBasedOnDynamicAlarmAttribute(alarmRecords, "fmx1",
                SortingOrder.ASCENDING);
        Assert.assertEquals("value1", sortedAlarmRecords.get(1).getAdditionalAttributeMap().get("fmx1"));
    }

    @Test
    public void testSortRecordsBasedOnDynamicAlarmAttribute_DESCENDING() {
        List<AlarmRecord> sortedAlarmRecords = alarmAttributeResponseBuilder.sortRecordsBasedOnDynamicAlarmAttribute(alarmRecords, "fmx1",
                SortingOrder.DESCENDING);
        Assert.assertEquals("value1", sortedAlarmRecords.get(1).getAdditionalAttributeMap().get("fmx1"));
    }
}
