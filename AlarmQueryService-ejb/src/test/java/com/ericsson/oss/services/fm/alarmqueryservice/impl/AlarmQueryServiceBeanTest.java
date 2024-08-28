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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.NodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.AlarmPoIdCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeEventTimeCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForSeverities;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.OORCriteriaHandler;

@RunWith(MockitoJUnitRunner.class)
public class AlarmQueryServiceBeanTest {
    @InjectMocks
    private AlarmQueryServiceBean alarmQueryServiceBean;

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
    private HistoricalAlarmDelegate historicalAlarmDelegate;

    @Mock
    private OpenAlarmDelegate openAlarmDelegate;

    @Mock
    private CompositeEventTimeCriteriaHandler compositeEventTimeCriteriaHandler;

    @Mock
    private CompositeEventTimeCriteria compositeEventTimeCriteria;

    @Mock
    AlarmPoIdCriteria alarmPoIdCriteria;

    @Mock
    private CompositeNodeCriteriaHandlerForSeverities nodeCriteriaHandler;

    @Mock
    private FMXAdditionalAttributeReader fmxAdditionalAttributeReader;

    @Test
    public void testGetAlarms_CompositeNodeCriteria_Alarms() {

        alarmQueryServiceBean.getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);
        verify(openAlarmDelegate, times(1)).getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);

    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_Alarms() {

        alarmQueryServiceBean.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);
        verify(openAlarmDelegate, times(1)).getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);
    }

    @Test
    public void testGetAlarms_AlarmPoIdCriteria_AlarmPoIdCriteria_Alarms() {

        alarmQueryServiceBean.getAlarms(alarmPoIdCriteria, expectedOutputAttributes, true);
        verify(openAlarmDelegate, times(1)).getAlarms(alarmPoIdCriteria, expectedOutputAttributes, true);
    }

    @Test
    public void testGetPoIds_CompositeNodeCriteria_CompositeNodeCriteria_PoIds() {

        alarmQueryServiceBean.getAlarmPoIds(compositeNodeCriteria, true);
        verify(openAlarmDelegate, times(1)).getAlarmPoIds(compositeNodeCriteria, true);

    }

    @Test
    public void testGetPoIds_OORCriteria_OORCriteria_PoIds() {

        alarmQueryServiceBean.getAlarmPoIds(oorCriteria);
        verify(openAlarmDelegate, times(1)).getAlarmPoIds(oorCriteria);

    }

    @Test
    public void testGetAlarmCountBySeverity_CompositeNodeCriteria_Count() {

        alarmQueryServiceBean.getAlarmCountBySeverity(compositeNodeCriteria, false);
        verify(openAlarmDelegate, times(1)).getAlarmCountBySeverity(compositeNodeCriteria, false);

    }

    @Test
    public void testGetAlarmCount_CompositeNodeCriteria_Count() {

        alarmQueryServiceBean.getAlarmCount(compositeNodeCriteria, false);
        verify(openAlarmDelegate, times(1)).getAlarmCount(compositeNodeCriteria, false);

    }

    @Test
    public void testGetAlarmCount_CompositeEventTimeCriteria_Count() {

        alarmQueryServiceBean.getAlarmCount(compositeEventTimeCriteria, false);
        verify(openAlarmDelegate, times(1)).getAlarmCount(compositeEventTimeCriteria, false);

    }

    @Test
    public void testGetAllPoIds_NULL_PoIds() {

        alarmQueryServiceBean.getAllAlarmPoIds();
        verify(openAlarmDelegate, times(1)).getAllAlarmPoIds();

    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_HistoricalAlarms() {

        alarmQueryServiceBean.getHistoricalAlarms(compositeEventTimeCriteria, true);
        verify(historicalAlarmDelegate, times(1)).getHistoricalAlarms(compositeEventTimeCriteria, true);

    }

    @Test
    public void testGetAlarmAdditionalAttributes_CompositeEventTimeCriteria_HistoricalAlarms() {

        alarmQueryServiceBean.getAlarmAdditionalAttributes();
        verify(fmxAdditionalAttributeReader, times(1)).getAlarmAdditionalAttributes();

    }
}
