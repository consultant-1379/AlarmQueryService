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
package com.ericsson.oss.services.fm.alarmqueryservice.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.DynamicAttributeEventTimeCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.DynamicAttributeNodeCriteriaHandler;

@RunWith(MockitoJUnitRunner.class)
public class DynamicAttributeCriteriaHandlerTest {

    @InjectMocks
    private DynamicAttributeCriteriaHandler dynamicAttributeCriteriaHandler;

    @Mock
    private DynamicAttributeNodeCriteriaHandler dynamicAttributeNodeCriteriaHandler;

    @Mock
    private DynamicAttributeEventTimeCriteriaHandler dynamicAttributeEventTimeCriteriaHandler;

    @Test
    public void testGetAlarms_CompositeNodeCriteria_Search_And_Sort_Alarms() throws Exception {
        when(
                dynamicAttributeNodeCriteriaHandler.getSortedSearchedAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                        anyList())).thenReturn(getAlarmAttributeResponse());
        final List<String> dynamicAttributes = new ArrayList<String>();
        dynamicAttributes.add("attribute1");
        assertNotNull(dynamicAttributeCriteriaHandler.getAlarms(getCompositeNodeCriteria(), getExpectedOutputAttributes(), true, dynamicAttributes));
        verify(dynamicAttributeNodeCriteriaHandler, times(1)).getSortedSearchedAlarms(any(CompositeNodeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
        verify(dynamicAttributeNodeCriteriaHandler, times(0)).getSortedAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                anyList());
        verify(dynamicAttributeNodeCriteriaHandler, times(0)).getSearchAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                anyList());
    }

    @Test
    public void testGetAlarms_CompositeNodeCriteria_Sorted_Alarms() throws Exception {
        when(dynamicAttributeNodeCriteriaHandler.getSortedAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class), anyList()))
                .thenReturn(getAlarmAttributeResponse());
        final List<String> dynamicAttributes = new ArrayList<String>();
        dynamicAttributes.add("attribute1");
        assertNotNull(dynamicAttributeCriteriaHandler.getAlarms(getCompositeNodeCriteria(), getExpectedOutputAttributes(), false, dynamicAttributes));
        verify(dynamicAttributeNodeCriteriaHandler, times(1)).getSortedAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                anyList());
        verify(dynamicAttributeNodeCriteriaHandler, times(0)).getSearchAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                anyList());
        verify(dynamicAttributeNodeCriteriaHandler, times(0)).getSortedSearchedAlarms(any(CompositeNodeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
    }

    @Test
    public void testGetAlarms_CompositeNodeCriteria_Search_Alarms() throws Exception {
        when(dynamicAttributeNodeCriteriaHandler.getSearchAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class), anyList()))
                .thenReturn(getAlarmAttributeResponse());
        assertNotNull(dynamicAttributeCriteriaHandler.getAlarms(getCompositeNodeCriteria(), getExpectedOutputAttributes(), true,
                new ArrayList<String>()));
        verify(dynamicAttributeNodeCriteriaHandler, times(1)).getSearchAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                anyList());
        verify(dynamicAttributeNodeCriteriaHandler, times(0)).getSortedSearchedAlarms(any(CompositeNodeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
        verify(dynamicAttributeNodeCriteriaHandler, times(0)).getSortedAlarms(any(CompositeNodeCriteria.class), any(ExpectedOutputAttributes.class),
                anyList());
    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_Search_And_Sort_Alarms() throws Exception {
        when(
                dynamicAttributeEventTimeCriteriaHandler.getSortedSearchedAlarms(any(CompositeEventTimeCriteria.class),
                        any(ExpectedOutputAttributes.class), anyList())).thenReturn(getAlarmAttributeResponse());
        final List<String> dynamicAttributes = new ArrayList<String>();
        dynamicAttributes.add("attribute1");
        assertNotNull(dynamicAttributeCriteriaHandler.getAlarms(getCompositeEventTimeCriteria(), getExpectedOutputAttributes(), dynamicAttributes,
                true));
        verify(dynamicAttributeEventTimeCriteriaHandler, times(1)).getSortedSearchedAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
        verify(dynamicAttributeEventTimeCriteriaHandler, times(0)).getSortedAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
        verify(dynamicAttributeEventTimeCriteriaHandler, times(0)).getSearchAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_Sorted_Alarms() throws Exception {
        when(
                dynamicAttributeEventTimeCriteriaHandler.getSortedAlarms(any(CompositeEventTimeCriteria.class), any(ExpectedOutputAttributes.class),
                        anyList())).thenReturn(getAlarmAttributeResponse());
        final List<String> dynamicAttributes = new ArrayList<String>();
        dynamicAttributes.add("attribute1");
        assertNotNull(dynamicAttributeCriteriaHandler.getAlarms(getCompositeEventTimeCriteria(), getExpectedOutputAttributes(), dynamicAttributes,
                false));
        verify(dynamicAttributeEventTimeCriteriaHandler, times(1)).getSortedAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
        verify(dynamicAttributeEventTimeCriteriaHandler, times(0)).getSearchAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
        verify(dynamicAttributeEventTimeCriteriaHandler, times(0)).getSortedSearchedAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_Search_Alarms() throws Exception {
        when(
                dynamicAttributeEventTimeCriteriaHandler.getSearchAlarms(any(CompositeEventTimeCriteria.class), any(ExpectedOutputAttributes.class),
                        anyList())).thenReturn(getAlarmAttributeResponse());
        assertNotNull(dynamicAttributeCriteriaHandler.getAlarms(getCompositeEventTimeCriteria(), getExpectedOutputAttributes(),
                new ArrayList<String>(), true));
        verify(dynamicAttributeEventTimeCriteriaHandler, times(1)).getSearchAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
        verify(dynamicAttributeEventTimeCriteriaHandler, times(0)).getSortedSearchedAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
        verify(dynamicAttributeEventTimeCriteriaHandler, times(0)).getSortedAlarms(any(CompositeEventTimeCriteria.class),
                any(ExpectedOutputAttributes.class), anyList());
    }

    private ExpectedOutputAttributes getExpectedOutputAttributes() {
        return new ExpectedOutputAttributes();
    }

    private CompositeNodeCriteria getCompositeNodeCriteria() {
        return new CompositeNodeCriteria();
    }

    private CompositeEventTimeCriteria getCompositeEventTimeCriteria() {
        return new CompositeEventTimeCriteria();
    }

    private AlarmAttributeResponse getAlarmAttributeResponse() {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        return new AlarmAttributeResponse(alarmRecords, "Response");
    }

}
