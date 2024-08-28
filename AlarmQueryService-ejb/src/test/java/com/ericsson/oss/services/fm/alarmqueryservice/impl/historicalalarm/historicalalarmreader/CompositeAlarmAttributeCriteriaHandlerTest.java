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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.LAST_UPDATED;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeAlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.CompositeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.DateRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.NodeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.instrumentation.AqsInstrumentationBean;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.HQSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CompositeAlarmAttributeCriteriaHandlerTest {

    @InjectMocks
    private CompositeAlarmAttributeCriteriaHandler compositeAlarmAttributeCriteriaHandler;

    @Mock
    private HQSProxy hqsProxy;

    @Mock
    private HistoricalQueryService historicalQueryService;

    @Mock
    private CompositeRestrictionBuilder compositeRestrictionBuilder;

    @Mock
    private Query query;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Mock
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Mock
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Mock
    private AlarmAttributeResponse alarmQueryResponse;

    @Mock
    private Restriction restriction;

    @Mock
    private AlarmReader alarmReader;

    @Mock
    private ConfigurationListener configurationListener;

    @Mock
    private AqsInstrumentationBean aqsInstrumentationBean;

    private final List<Date> eventTimeList = new ArrayList<Date>(1);
    private final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();

    private final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();
    private final Map<String, Object> historyAlarmMap = new HashMap<String, Object>();
    private final List<CompositeAlarmAttributeCriteria> compositeAlarmAttributeCriteria = new ArrayList<CompositeAlarmAttributeCriteria>();
    private final List<Map<String, Object>> historyAlarmData = new ArrayList<Map<String, Object>>();

    @Before
    public void setUp() {

        final AlarmAttributeCriteria AlarmAttributeCriteria = new AlarmAttributeCriteria();
        AlarmAttributeCriteria.setAttributeName(EVENT_PO_ID);
        AlarmAttributeCriteria.setAttributeValue(4768);
        AlarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(AlarmAttributeCriteria);

        final AlarmAttributeCriteria AlarmAttributeCriteria1 = new AlarmAttributeCriteria();
        AlarmAttributeCriteria1.setAttributeName(LAST_UPDATED);
        AlarmAttributeCriteria1.setAttributeValue(4768);
        AlarmAttributeCriteria1.setOperator(Operator.EQ);
        otherAlarmAttributes.add(AlarmAttributeCriteria1);

        final CompositeAlarmAttributeCriteria compositeAlarmAttributeCriterian = new CompositeAlarmAttributeCriteria();
        compositeAlarmAttributeCriterian.setAlarmAttributeCritera(otherAlarmAttributes);

        compositeAlarmAttributeCriteria.add(compositeAlarmAttributeCriterian);

        historyAlarmMap.put(FDN, "NETWORKELEMENT=LTE01ERBS0001");

        historyAlarmData.add(historyAlarmMap);

        when(dateRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList, compositeEventTimeCriteria.getOperator()))
                .thenReturn(restriction);
        final List<AlarmAttributeCriteria> alarmAttributes = new ArrayList<AlarmAttributeCriteria>();
        alarmAttributes.add(AlarmAttributeCriteria);
        when(attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes)).thenReturn(restriction);

        final List<AlarmAttributeCriteria> alarmAttributes1 = new ArrayList<AlarmAttributeCriteria>();
        alarmAttributes1.add(AlarmAttributeCriteria1);
        when(attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes1)).thenReturn(restriction);

        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(restrictionBuilder.allOf(null, restriction)).thenReturn(restriction);

        when(hqsProxy.getHistoricalQueryService()).thenReturn(historicalQueryService);
        when(hqsProxy.getQuery()).thenReturn(query);
        when(query.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);

        when(hqsProxy.getHistoricalQueryService().execute(query)).thenReturn(historyAlarmData);
        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(configurationListener.getMaxNumberOfHistoryAlarmsShown()).thenReturn(10000);
    }

    @Test
    public void testGetAlarms_CompositeAlarmAttributeCriteria_Alarms() {

        assertNotNull(compositeAlarmAttributeCriteriaHandler.getAlarms(compositeAlarmAttributeCriteria));

    }

    @Test
    public void testGetAlarms_CompositeAlarmAttributeCriteria_Exception() {

        when(hqsProxy.getQuery()).thenReturn(null);
        given(hqsProxy.getQuery()).willThrow(new NullPointerException("hqs obj is null"));
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);
        assertEquals(compositeAlarmAttributeCriteriaHandler.getAlarms(compositeAlarmAttributeCriteria).getResponse(),
                "Failed to read alarms from DB. Exception details are:hqs obj is null");
    }
}
