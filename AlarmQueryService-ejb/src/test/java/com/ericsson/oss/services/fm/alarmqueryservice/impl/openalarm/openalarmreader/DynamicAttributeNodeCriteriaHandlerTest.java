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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;

@RunWith(MockitoJUnitRunner.class)
public class DynamicAttributeNodeCriteriaHandlerTest {

    @InjectMocks
    private DynamicAttributeNodeCriteriaHandler dynamicAttributeNodeCriteriaHandler;

    @Mock
    private CompositeNodeCriteriaHandlerForAlarms compositeNodeCriteriaHandlerForAlarms;

    @Mock
    private AdditionalAttributeHandler additionalAttributeHandler;

    @Mock
    private DynamicAlarmAttributeInfoReader dynamicAlarmAttributeInfoReader;

    @Test
    public void test_getSortedSearchedAlarms() throws Exception {
        when(
                additionalAttributeHandler.getSortedAlarmRecords(any(AlarmAttributeResponse.class), anyListOf(AlarmSortCriterion.class),
                        anyListOf(String.class))).thenReturn(getAlarmAttributeResponse());
        assertNotNull(dynamicAttributeNodeCriteriaHandler.getSortedSearchedAlarms(new CompositeNodeCriteria(), getExpectedOutputAttributes(),
                new ArrayList<String>()));
    }

    @Test
    public void test_getSortedAlarms() {
        when(
                compositeNodeCriteriaHandlerForAlarms.getAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                        anyListOf(String.class))).thenReturn(getAlarmAttributeResponse());
        when(
                additionalAttributeHandler.getSortedAlarmRecords(any(AlarmAttributeResponse.class), anyListOf(AlarmSortCriterion.class),
                        anyListOf(String.class))).thenReturn(getAlarmAttributeResponse());
        assertNotNull(dynamicAttributeNodeCriteriaHandler.getSortedAlarms(new CompositeNodeCriteria(), getExpectedOutputAttributes(),
                new ArrayList<String>()));
    }

    @Test
    public void test_getSearchAlarms() throws Exception {
        when(dynamicAlarmAttributeInfoReader.readDynamicSearchAttributes(anyListOf(AlarmAttributeCriteria.class))).thenReturn(
                new ArrayList<AlarmAttributeCriteria>());
        when(
                additionalAttributeHandler.getSearchResponse(any(AlarmAttributeResponse.class), anyListOf(AlarmAttributeCriteria.class),
                        any(ExpectedOutputAttributes.class))).thenReturn(getAlarmAttributeResponse());

        when(
                compositeNodeCriteriaHandlerForAlarms.getAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                        anyListOf(String.class))).thenReturn(getAlarmAttributeResponse());
        assertNotNull(dynamicAttributeNodeCriteriaHandler.getSearchAlarms(new CompositeNodeCriteria(), getExpectedOutputAttributes(),
                new ArrayList<String>()));
    }

    private ExpectedOutputAttributes getExpectedOutputAttributes() {
        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        expectedOutputAttributes.setOutputAttributes(new ArrayList<String>());
        return expectedOutputAttributes;
    }

    private AlarmAttributeResponse getAlarmAttributeResponse() {
        return new AlarmAttributeResponse(new ArrayList<AlarmRecord>(), "Response");
    }

}
