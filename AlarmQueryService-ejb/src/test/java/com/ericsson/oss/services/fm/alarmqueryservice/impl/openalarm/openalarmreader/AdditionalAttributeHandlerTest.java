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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.NO_ALARMS;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalAttributeHandlerTest {

    @InjectMocks
    private AdditionalAttributeHandler additionalAttributeHandler;

    @Mock
    private AlarmPoIdCriteriaHandler alarmPoIdCriteriaHandler;

    @Mock
    private DynamicAlarmAttributeInfoReader dynamicAlarmAttributeInfoReader;

    @Mock
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;

    @Mock
    private SecondLevelSortingHandler secondLevelSortingHandler;

    @Test
    public void test_getSearchResponse_normalSearchAttribute_SingleAdditionalAttribute() {
        when(dynamicAlarmAttributeInfoReader.isSingleAdditionalAttributeWithNotEquals(anyListOf(AlarmAttributeCriteria.class))).thenReturn(true);
        when(
                dynamicAlarmAttributeInfoReader.readDynamicAttributesMatchedSearchAlarms(anyListOf(AlarmAttributeCriteria.class),
                        any(ExpectedOutputAttributes.class))).thenReturn(getAlarmAttribute());
        assertNotNull(additionalAttributeHandler.getSearchResponse(getAlarmAttribute(), getAlarmAttributeCriteriaList(),
                getExpectedOutputAttributes()));
    }

    @Test
    public void test_getSearchResponse_normalSearchAttribute_MultipleAdditionalAttribute() {
        when(dynamicAlarmAttributeInfoReader.isSingleAdditionalAttributeWithNotEquals(anyListOf(AlarmAttributeCriteria.class))).thenReturn(false);
        when(
                dynamicAlarmAttributeInfoReader.readDynamicAttributesMatchedSearchAlarms(anyListOf(AlarmAttributeCriteria.class),
                        any(ExpectedOutputAttributes.class))).thenReturn(getAlarmAttribute());
        assertNotNull(additionalAttributeHandler.getSearchResponse(getAlarmAttribute(), getAlarmAttributeCriteriaList(),
                getExpectedOutputAttributes()));
    }

    @Test
    public void test_getSearchResponse_dynamicSearchAttribute_MultipleAdditionalAttribute() {
        when(dynamicAlarmAttributeInfoReader.isSingleAdditionalAttributeWithNotEquals(anyListOf(AlarmAttributeCriteria.class))).thenReturn(false);
        when(
                dynamicAlarmAttributeInfoReader.readDynamicAttributesMatchedSearchAlarms(anyListOf(AlarmAttributeCriteria.class),
                        any(ExpectedOutputAttributes.class))).thenReturn(getAlarmAttributeWithEmptyAlarmRecords());
        assertNotNull(additionalAttributeHandler.getSearchResponse(getAlarmAttribute(), getAlarmAttributeCriteriaList(),
                getExpectedOutputAttributes()));
    }

    @Test
    public void test_getSortedAlarmRecords_Descending_Order() {
        when(
                alarmAttributeResponseBuilder.sortRecordsBasedOnDynamicAlarmAttribute(anyListOf(AlarmRecord.class), anyString(),
                        any(SortingOrder.class))).thenReturn(new ArrayList<AlarmRecord>());
        when(alarmAttributeResponseBuilder.buildAttributeResponse(anyListOf(AlarmRecord.class))).thenReturn(
                new AlarmAttributeResponse(new ArrayList<AlarmRecord>(), NO_ALARMS));
        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();
        final AlarmSortCriterion alarmSortCriterion = new AlarmSortCriterion();
        alarmSortCriterion.setSortAttribute("insertTime");
        alarmSortCriterion.setSortOrder(SortingOrder.DESCENDING);
        alarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        alarmSortCriteria.add(alarmSortCriterion);
        assertNotNull(additionalAttributeHandler.getSortedAlarmRecords(getAlarmAttributeWithEmptyAlarmRecords(), alarmSortCriteria,
                new ArrayList<String>()));
    }

    @Test
    public void test_getSortedAlarmRecords_Ascending_Order() {
        when(
                alarmAttributeResponseBuilder.sortRecordsBasedOnDynamicAlarmAttribute(anyListOf(AlarmRecord.class), anyString(),
                        any(SortingOrder.class))).thenReturn(new ArrayList<AlarmRecord>());
        when(alarmAttributeResponseBuilder.buildAttributeResponse(anyListOf(AlarmRecord.class))).thenReturn(
                new AlarmAttributeResponse(new ArrayList<AlarmRecord>(), NO_ALARMS));
        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();
        final AlarmSortCriterion alarmSortCriterion = new AlarmSortCriterion();
        alarmSortCriterion.setSortAttribute("insertTime");
        alarmSortCriterion.setSortOrder(SortingOrder.DESCENDING);
        alarmSortCriteria.add(alarmSortCriterion);
        assertNotNull(additionalAttributeHandler.getSortedAlarmRecords(getAlarmAttributeWithEmptyAlarmRecords(), alarmSortCriteria,
                new ArrayList<String>()));
    }

    private AlarmAttributeResponse getAlarmAttributeWithEmptyAlarmRecords() {
        return new AlarmAttributeResponse(new ArrayList<AlarmRecord>(), "Success");
    }

    private ExpectedOutputAttributes getExpectedOutputAttributes() {
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add("eventPoId");
        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        expectedOutputAttributes.setOutputAttributes(outputAttributes);
        return expectedOutputAttributes;
    }

    private AlarmAttributeResponse getAlarmAttribute() {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        final List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
        alarmRecords.add(new AlarmRecord(new HashMap<String, Object>(), "123", comments));
        return new AlarmAttributeResponse(alarmRecords, "Response");
    }

    private List<AlarmAttributeCriteria> getAlarmAttributeCriteriaList() {
        final List<AlarmAttributeCriteria> alarmAttributeCriteriaList = new ArrayList<AlarmAttributeCriteria>();
        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName("alarmNumber");
        alarmAttributeCriteria.setAttributeValue(111);
        alarmAttributeCriteriaList.add(alarmAttributeCriteria);
        return alarmAttributeCriteriaList;
    }

}
