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
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.ALARM_ID;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.EVENT_TIME;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.FDN;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PREVIOUS_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.SPECIFIC_PROBLEM;

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
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.CompositeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.DateRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.NodeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.instrumentation.AqsInstrumentationBean;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.HQSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CompositeEventTimeHandlerTest {

    @InjectMocks
    private CompositeEventTimeHandler historicalAlarmHandler;

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
    private AlarmRecord alarmRecord;

    @Mock
    private AqsInstrumentationBean aqsInstrumentationBean;

    private final List<Date> eventTimeList = new ArrayList<Date>(1);
    private final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();
    private final List<String> nodes = new ArrayList<String>();
    private final List<Date> dates = new ArrayList<Date>();
    private final AlarmAttributeCriteria AlarmAttributeCriteria = new AlarmAttributeCriteria();
    private final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();
    private final Map<String, Object> historyAlarmMap = new HashMap<String, Object>();
    private final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();
    private final List<Map<String, Object>> historyAlarmData = new ArrayList<Map<String, Object>>();
    private final Date fromDate = new Date();

    @Before
    public void setUp() {
        nodes.add("LTE01ERBS0001");
        dates.add(new Date());
        final Date eventTime = compositeEventTimeCriteria.getFromTime();

        eventTimeList.add(eventTime);
        otherAlarmAttributes.add(AlarmAttributeCriteria);

        AlarmAttributeCriteria.setAttributeName(ALARM_ID);
        AlarmAttributeCriteria.setAttributeValue(4768);
        AlarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(AlarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);
        compositeEventTimeCriteria.setNodes(nodes);
        compositeEventTimeCriteria.setFromTime(fromDate);
        compositeEventTimeCriteria.setOperator(Operator.EQ);
        historyAlarmMap.put(FDN, "NETWORKELEMENT=LTE01ERBS0001");

        historyAlarmData.add(historyAlarmMap);

        final AlarmSortCriterion alarmSortCriterion1 = new AlarmSortCriterion();
        alarmSortCriterion1.setSortAttribute(PRESENT_SEVERITY);
        alarmSortCriterion1.setSortOrder(SortingOrder.ASCENDING);

        final AlarmSortCriterion alarmSortCriterion2 = new AlarmSortCriterion();
        alarmSortCriterion2.setSortAttribute(PREVIOUS_SEVERITY);
        alarmSortCriterion2.setSortOrder(SortingOrder.DESCENDING);

        alarmSortCriteria.add(alarmSortCriterion1);
        alarmSortCriteria.add(alarmSortCriterion2);

        final List<AlarmAttributeCriteria> alarmAttributes = compositeEventTimeCriteria.getAlarmAttributeCriteria();

        when(dateRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList, compositeEventTimeCriteria.getOperator()))
                .thenReturn(restriction);
        when(attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes)).thenReturn(restriction);

        when(hqsProxy.getHistoricalQueryService()).thenReturn(historicalQueryService);
        when(hqsProxy.getQuery()).thenReturn(query);
        when(query.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);

        when(hqsProxy.getHistoricalQueryService().execute(query)).thenReturn(historyAlarmData);
        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_Alarms() {

        otherAlarmAttributes.add(AlarmAttributeCriteria);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.DESCENDING);

        final Date eventTime = compositeEventTimeCriteria.getFromTime();
        final List<Date> eventTimeList = new ArrayList<Date>(1);
        eventTimeList.add(eventTime);

        compositeEventTimeCriteria.setAlarmSortCriteria(alarmSortCriteria);
        when(hqsProxy.getHistoricalQueryService().executeCount(query)).thenReturn(600L);
        when(compositeRestrictionBuilder.build(restrictionBuilder, compositeEventTimeCriteria)).thenReturn(restriction);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        when(compositeRestrictionBuilder.buildNodeRestrictions(restrictionBuilder, compositeEventTimeCriteria)).thenReturn(restriction);

        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        alarmRecords.add(alarmRecord);
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);

        assertNotNull(historicalAlarmHandler.getAlarms(compositeEventTimeCriteria));

    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteriaSortAttributes_Alarms() {

        otherAlarmAttributes.add(AlarmAttributeCriteria);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.DESCENDING);

        final Date eventTime = compositeEventTimeCriteria.getFromTime();
        final List<Date> eventTimeList = new ArrayList<Date>(1);
        eventTimeList.add(eventTime);

        alarmSortCriteria.get(0).setSortAttribute(PREVIOUS_SEVERITY);
        alarmSortCriteria.get(0).setSortOrder(SortingOrder.ASCENDING);
        alarmSortCriteria.get(1).setSortAttribute(PRESENT_SEVERITY);
        alarmSortCriteria.get(1).setSortOrder(SortingOrder.DESCENDING);
        compositeEventTimeCriteria.setAlarmSortCriteria(alarmSortCriteria);
        when(hqsProxy.getHistoricalQueryService().executeCount(query)).thenReturn(600L);
        when(compositeRestrictionBuilder.build(restrictionBuilder, compositeEventTimeCriteria)).thenReturn(restriction);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        final List<String> sortAttributes = new ArrayList<String>();
        sortAttributes.add(FDN);
        sortAttributes.add(EVENT_TIME);
        compositeEventTimeCriteria.setSortAttributes(sortAttributes);
        when(compositeRestrictionBuilder.buildNodeRestrictions(restrictionBuilder, compositeEventTimeCriteria)).thenReturn(restriction);

        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);

        assertNotNull(historicalAlarmHandler.getAlarms(compositeEventTimeCriteria));

    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteriaWithNodes_Alarms() {

        otherAlarmAttributes.add(AlarmAttributeCriteria);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.DESCENDING);

        alarmSortCriteria.get(0).setSortAttribute(FDN);
        alarmSortCriteria.get(0).setSortOrder(SortingOrder.ASCENDING);
        alarmSortCriteria.get(1).setSortAttribute(SPECIFIC_PROBLEM);
        alarmSortCriteria.get(1).setSortOrder(SortingOrder.DESCENDING);
        compositeEventTimeCriteria.setAlarmSortCriteria(alarmSortCriteria);

        final Date eventTime = compositeEventTimeCriteria.getFromTime();
        final List<Date> eventTimeList = new ArrayList<Date>(1);
        eventTimeList.add(eventTime);

        when(hqsProxy.getHistoricalQueryService().executeCount(query)).thenReturn(600L);
        when(compositeRestrictionBuilder.build(restrictionBuilder, compositeEventTimeCriteria)).thenReturn(restriction);

        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);

        assertNotNull(historicalAlarmHandler.getAlarms(compositeEventTimeCriteria));

    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria6000Alarms() {

        when(compositeRestrictionBuilder.build(restrictionBuilder, compositeEventTimeCriteria)).thenReturn(restriction);
        when(hqsProxy.getHistoricalQueryService().executeCount(query)).thenReturn(6000L);
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        alarmRecords.add(alarmRecord);
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);
        final AlarmAttributeResponse alarmAttributeResponse = historicalAlarmHandler.getAlarms(compositeEventTimeCriteria);
        assertEquals(alarmAttributeResponse.getResponse(), SUCCESS);
        assertEquals(Long.valueOf(6000), alarmAttributeResponse.getAlarmCountForSearchCriteria());

    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteriaWithNodes6000Alarms() {

        final List<Restriction> restrictions = new ArrayList<Restriction>();
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        alarmRecords.add(alarmRecord);
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);
        restrictions.add(restriction);
        when(compositeRestrictionBuilder.buildNodeRestrictions(restrictionBuilder, compositeEventTimeCriteria)).thenReturn(restriction);
        when(compositeRestrictionBuilder.build(restrictionBuilder, compositeEventTimeCriteria)).thenReturn(restriction);
        when(hqsProxy.getHistoricalQueryService().executeCount(query)).thenReturn(6000L);

        when(compositeRestrictionBuilder.buildCompositeRestrictionByAnd(query.getRestrictionBuilder(), restriction, restriction))
                .thenReturn(restriction);
        when(configurationListener.getMaxNumberOfHistoryAlarmsShown()).thenReturn(20000);

        final AlarmAttributeResponse alarmAttributeResponse = historicalAlarmHandler.getAlarms(compositeEventTimeCriteria);
        assertEquals(alarmAttributeResponse.getResponse(), SUCCESS);
        assertEquals(Long.valueOf(6000), alarmAttributeResponse.getAlarmCountForSearchCriteria());

    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_ErrorMessage() {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);
        assertNotNull(historicalAlarmHandler.getAlarms(compositeEventTimeCriteria));

    }

    @Test
    public void testGetAlarms_CompositeEventTimeCriteria_Exception() {

        when(hqsProxy.getQuery()).thenReturn(null);
        given(hqsProxy.getQuery()).willThrow(new NullPointerException("hqs obj is null"));
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();

        when(alarmReader.getHistoricalAlarms(query, restriction, 10000)).thenReturn(alarmRecords);
        assertEquals(historicalAlarmHandler.getAlarms(compositeEventTimeCriteria).getResponse(),
                "Failed to read alarms from DB. Exception details are:hqs obj is null");

    }
}
