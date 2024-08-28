/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl;

import static org.junit.Assert.assertEquals;
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
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.NodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.AlarmPoIdCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeEventTimeCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeEventTimeCriteriaHandlerForCount;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForAlarms;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForCount;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForPoIds;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForSeverities;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.OORCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DynamicAlarmAttributeValidator;

@RunWith(MockitoJUnitRunner.class)
public class OpenAlarmDelegateTest {

    @InjectMocks
    private OpenAlarmDelegate openAlarmDelegate;

    @Mock
    private AlarmPoIdCriteria poIdCriteria;

    @Mock
    private ExpectedOutputAttributes expectedOutputAttributes;

    @Mock
    private AlarmPoIdCriteriaHandler poIdCriteriaHandler;

    @Mock
    private CompositeNodeCriteria compositeNodeCriteria;

    @Mock
    private OORCriteria oorCriteria;

    @Mock
    private NodeCriteria nodeCriteria;

    @Mock
    private OORCriteriaHandler oorCriteriaHandler;

    @Mock
    private CompositeNodeCriteriaHandlerForAlarms compositeNodeCriteriaHandlerForAlarms;

    @Mock
    private CompositeNodeCriteriaHandlerForPoIds compositeNodeCriteriaHandlerForPoIds;

    @Mock
    private CompositeEventTimeCriteriaHandler compositeEventTimeCriteriaHandler;

    @Mock
    private CompositeEventTimeCriteria compositeEventTimeCriteria;

    @Mock
    private AuthorizationHandler authorizationHandler;

    @Mock
    private CompositeNodeCriteriaHandlerForSeverities compositeNodeCriteriaHandlerForSeverities;

    @Mock
    private CompositeNodeCriteriaHandlerForCount compositeNodeCriteriaHandlerForCount;

    @Mock
    private CompositeEventTimeCriteriaHandlerForCount compositeEventTimeCriteriaHandlerForCount;

    @Mock
    private OpenAlarmParser openAlarmParser;

    @Mock
    private DynamicAlarmAttributeValidator dynamicAlarmAttributeValidator;

    @Mock
    private DynamicAttributeCriteriaHandler dynamicAttributeCriteriaHandler;

    @Test
    public void testGetAlarms_poIdCriteria_Alarms() {

        openAlarmDelegate.getAlarms(poIdCriteria, expectedOutputAttributes, true);
        verify(poIdCriteriaHandler, times(1)).getAlarms(poIdCriteria, expectedOutputAttributes);
    }

    @Test
    public void testGetAlarms_CompositeNodeCriteria_Alarms() {
        openAlarmDelegate.getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);
        verify(compositeNodeCriteriaHandlerForAlarms, times(1)).getAlarms(compositeNodeCriteria, expectedOutputAttributes, new ArrayList<String>());
    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_Alarms() {
        openAlarmDelegate.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);
        verify(compositeEventTimeCriteriaHandler, times(1)).getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, new ArrayList<String>());
    }

    @Test
    public void testGetAlarms_compositeEventTimeCriteria_Exception() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = null;
        final AlarmAttributeResponse alarmAttributeResponse = openAlarmDelegate
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, false);
        assertEquals("Error while retrieving alarms from DB {}null", alarmAttributeResponse.getResponse());
    }

    @Test
    public void testGetAlarms_compositeEventTimeCriteria_AttributeConstraintViolationException() throws Exception {
        final AttributeConstraintViolationException attributeConstraintViolationException = new AttributeConstraintViolationException(
                "test Exception");

        when(compositeEventTimeCriteria.getSortAttribute()).thenReturn("EventTime");
        when(dynamicAlarmAttributeValidator.filterFmxAdditionalAttributes(anyList())).thenThrow(attributeConstraintViolationException);

        final AlarmAttributeResponse alarmAttributeResponse = openAlarmDelegate
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, false);
        assertEquals("test Exception", alarmAttributeResponse.getResponse());
    }

    @Test
    public void testGetAlarms_compositeNodeCriteria_Exception() {
        final CompositeNodeCriteria compositeNodeCriteria = null;
        final AlarmAttributeResponse alarmAttributeResponse = openAlarmDelegate.getAlarms(compositeNodeCriteria, expectedOutputAttributes, false);
        assertEquals("Error while retrieving alarms from DB {}null", alarmAttributeResponse.getResponse());
    }

    @Test
    public void testGetAlarms_compositeNodeCriteria_AttributeConstraintViolationException() {
        final AttributeConstraintViolationException attributeConstraintViolationException = new AttributeConstraintViolationException(
                "test Exception");

        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();
        final AlarmSortCriterion alarmSortCriterion = new AlarmSortCriterion();
        alarmSortCriterion.setSortAttribute("EventTime");
        alarmSortCriterion.setSortOrder(SortingOrder.ASCENDING);
        alarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        alarmSortCriteria.add(alarmSortCriterion);
        when(compositeNodeCriteria.getAlarmSortCriteria()).thenReturn(alarmSortCriteria);

        when(compositeNodeCriteria.getSortAttribute()).thenReturn("EventTime");
        //        when(dynamicAlarmAttributeValidator.isFmxAdditionalAtribute("EventTime")).thenThrow(attributeConstraintViolationException);

        final AlarmAttributeResponse alarmAttributeResponse = openAlarmDelegate.getAlarms(compositeNodeCriteria, expectedOutputAttributes, false);
        //assertEquals("test Exception", alarmAttributeResponse.getResponse());
    }

    @Test
    public void testGetPoIds_CompositeNodeCriteria_PoIds() {
        openAlarmDelegate.getAlarmPoIds(compositeNodeCriteria, true);
        verify(compositeNodeCriteriaHandlerForPoIds, times(1)).getAlarmPoIds(compositeNodeCriteria);
    }

    @Test
    public void testGetPoIds_OORCriteria_PoIds() {
        openAlarmDelegate.getAlarmPoIds(oorCriteria);
        verify(oorCriteriaHandler, times(1)).getAlarmPoIds(oorCriteria);
    }

    @Test
    public void testGetAlarmCountBySeverity_CompositeNodeCriteria_Count() {
        openAlarmDelegate.getAlarmCountBySeverity(compositeNodeCriteria, true);
        verify(compositeNodeCriteriaHandlerForSeverities, times(1)).getAlarmCountBySeverity(compositeNodeCriteria);
    }

    @Test
    public void testGetAlarmCount_CompositeNodeCriteria_Count() {
        openAlarmDelegate.getAlarmCount(compositeNodeCriteria, true);
        verify(compositeNodeCriteriaHandlerForCount, times(1)).getAlarmCount(compositeNodeCriteria);
    }

    @Test
    public void testGetAlarmCount_CompositeEventTimeCriteria_Count() {
        openAlarmDelegate.getAlarmCount(compositeEventTimeCriteria, true);
        verify(compositeEventTimeCriteriaHandlerForCount, times(1)).getAlarmCount(compositeEventTimeCriteria);
    }

    @Test
    public void testGetAllPoIds_NULL_PoIds() {
        openAlarmDelegate.getAllAlarmPoIds();
        verify(oorCriteriaHandler, times(1)).getAlarmPoIds(null);
    }
}
