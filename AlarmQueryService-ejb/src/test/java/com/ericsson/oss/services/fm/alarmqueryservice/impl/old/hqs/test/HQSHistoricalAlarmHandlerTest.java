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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.test;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.INSERTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LIMIT_EXCEEDED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.SortOrder;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.alarm.query.service.models.AlarmLogData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryResponse;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSAttributeRestrictionCreator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSDateRestrictionCreator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSHistoricalAlarmHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSNodeRestrictionCreator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSOperatorBasedRestriction;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants;

@RunWith(MockitoJUnitRunner.class)
public class HQSHistoricalAlarmHandlerTest {

    @InjectMocks
    private HQSHistoricalAlarmHandler hqshistoricalAlarmHandler;

    @Mock
    private AlarmQueryResponse alarmQueryResponse;

    @Mock
    private HistoricalQueryService historicalQueryService;

    @Mock
    private HQSDateRestrictionCreator hqsDateRestrictionCreator;

    @Mock
    private HQSAttributeRestrictionCreator hqsAttributeRestrictionCreator;

    @Mock
    private HQSNodeRestrictionCreator hqsNodeRestrictionCreator;

    @Mock
    private Restriction restriction;

    @Mock
    private Query query;

    @Mock
    private HQSOperatorBasedRestriction hqsOperatorBasedRestriction;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Test
    public void testfetchHistoryAlarms() {
        when(historicalQueryService.createQuery()).thenReturn(query);
        when(query.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        final AlarmLogData historicalQueryData = new AlarmLogData();
        final List<String> alarmAttributesList = new ArrayList<>();
        final List<String> nodeList = new ArrayList<>();
        final List<Date> eventTimeList = new ArrayList<>();
        final List<Map<String, Object>> historyAlarmData = new ArrayList<Map<String, Object>>();
        final String fdn1 = "MeContext=1,ManagedElement=1,ENodeBFunction=1";
        final String fdn2 = "MeContext=2,ManagedElement=1,ENodeBFunction=1";
        nodeList.add(fdn1);
        nodeList.add(fdn2);
        final String attribute1 = "alarmNumber#111#>";
        final String attribute2 = "specificProblem#specificProblem1#=";
        alarmAttributesList.add(attribute1);
        alarmAttributesList.add(attribute2);
        eventTimeList.add(new Date());
        historicalQueryData.setDateAttribute("eventTime");
        historicalQueryData.setDateOperator(DateOperator.GT);
        historicalQueryData.setAlarmAttributes(alarmAttributesList);
        historicalQueryData.setDate(eventTimeList);
        historicalQueryData.setNodeList(nodeList);
        query.setRestriction(restriction);
        query.setAttributes(new String[] { "*" });
        query.setPageFilter(0, 100000);
        query.orderBy(INSERTTIME, SortOrder.DESCENDING);

        when(
                hqsDateRestrictionCreator.getDateRestriction(restrictionBuilder, historicalQueryData.getDateAttribute(), eventTimeList,
                        historicalQueryData.getDateOperator())).thenReturn(restriction);

        when(historicalQueryService.execute(query)).thenReturn(historyAlarmData);
        when(hqsAttributeRestrictionCreator.getAttributesRestriction(restrictionBuilder, alarmAttributesList, QueryConstants.DATE_FORMAT))
                .thenReturn(restriction);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        restrictions.add(restriction);
        when(hqsNodeRestrictionCreator.getNodesRestriction(restrictionBuilder, nodeList)).thenReturn(restrictions);
        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        assertEquals(SUCCESS, hqshistoricalAlarmHandler.fetchHistoryAlarms(historicalQueryData).getResponse());

        when(historicalQueryService.executeCount(query)).thenReturn(5001L);
        assertEquals(LIMIT_EXCEEDED, hqshistoricalAlarmHandler.fetchHistoryAlarms(historicalQueryData).getResponse());

    }

    @Test
    public void testfetchHistoryAlarmsForNull() {
        when(historicalQueryService.createQuery()).thenReturn(query);
        when(query.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        final AlarmLogData historicalQueryData = new AlarmLogData();
        historicalQueryData.setDateAttribute("eventTime");
        historicalQueryData.setDateOperator(DateOperator.GT);
        final List<Date> eventTimeList = new ArrayList<>();
        eventTimeList.add(new Date());
        historicalQueryData.setDate(eventTimeList);
        when(
                hqsDateRestrictionCreator.getDateRestriction(restrictionBuilder, historicalQueryData.getDateAttribute(), eventTimeList,
                        historicalQueryData.getDateOperator())).thenReturn(restriction);
        assertEquals(SUCCESS, hqshistoricalAlarmHandler.fetchHistoryAlarms(historicalQueryData).getResponse());

        when(historicalQueryService.executeCount(query)).thenReturn(5001L);
        assertEquals(LIMIT_EXCEEDED, hqshistoricalAlarmHandler.fetchHistoryAlarms(historicalQueryData).getResponse());
    }

}