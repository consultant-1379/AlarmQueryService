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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.historicalalarmreader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
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

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.HQSProxy;

@RunWith(MockitoJUnitRunner.class)
public class AlarmReaderTest {

    @InjectMocks
    private AlarmReader alarmReader;

    @Mock
    private HQSProxy hqsProxy;

    @Mock
    private Query query;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private HistoricalQueryService historicalQueryService;

    @Mock
    private ConfigurationListener configurationListener;

    @Mock
    private CompositeEventTimeCriteria compositeEventTimeCriteria;

    private final List<Map<String, Object>> historyAlarmData = new ArrayList<Map<String, Object>>();

    @Test
    public void testGetAlarms_Restriction_Alarms() {
        when(hqsProxy.getHistoricalQueryService()).thenReturn(historicalQueryService);
        when(hqsProxy.getHistoricalQueryService().execute((Query) anyObject(), anyInt(), anyInt())).thenReturn(historyAlarmData);
        assertNotNull(alarmReader.getHistoricalAlarms(query, restriction, 10000));
    }

    @Test
    public void testGetAlarms_Restriction_Alarms1() {
        when(hqsProxy.getHistoricalQueryService()).thenReturn(historicalQueryService);
        fillHistoryAlarmData();
        when(configurationListener.getMaxNumberOfHistoryAlarmsShown()).thenReturn(10000);
        when(hqsProxy.getHistoricalQueryService().execute((Query) anyObject(), anyInt(), anyInt())).thenReturn(historyAlarmData);
        final List<AlarmRecord> result = alarmReader.getHistoricalAlarms(query, restriction, 10000);
        assertNotNull(result);
        assertTrue(result.size() == 1);
    }

    private void fillHistoryAlarmData() {
        final Map<String, Object> alarm1 = new HashMap<String, Object>();
        alarm1.put("ackOperator", "administrator");
        alarm1.put("oscillationCount", 0);
        alarm1.put("fmxGenerated", "NOT_SET");
        alarm1.put("alarmingObject", "ManagedElement=1");
        alarm1.put("trendIndication", "UNDEFINED");
        alarm1.put("lastUpdated", "2017-05-15T14:21:00.460Z");

        final Map<String, Object> alarm2 = new HashMap<String, Object>();
        alarm2.put("ackOperator", "administrator");
        alarm2.put("oscillationCount", 0);
        alarm2.put("fmxGenerated", "NOT_SET");
        alarm2.put("alarmingObject", "ManagedElement=1");
        alarm2.put("trendIndication", "UNDEFINED");
        alarm2.put("lastUpdated", "2017-05-15T14:21:00.460Z");

        historyAlarmData.add(alarm1);
        historyAlarmData.add(alarm2);
    }
}
