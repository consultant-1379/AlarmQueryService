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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.alarm.query.service.models.AlarmLogData;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSHistoricalAlarmHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSOperatorBasedRestriction;

@RunWith(MockitoJUnitRunner.class)
public class HQSOperatorBasedRestrictionTest {
    //	@InjectMocks
    //	HqsOperatorBasedRestriction hqsOperatorBasedRestriction;

    @Mock
    private Restriction restriction;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private HQSHistoricalAlarmHandler hqsHistoricalAlarmHandler;

    @Mock
    private Query query;

    final HQSOperatorBasedRestriction hqsOperatorBasedRestriction = new HQSOperatorBasedRestriction();

    final List<Date> eventTimeList = new ArrayList<>();

    @Test
    public void testRestrictionWithToAndFromDate() {

        final String attribute = "specificProblem#specificProblem1#!=";
        final String toDate = "ceaseTime#16/02/1989 12:00:00#=";
        final String fromDate = "ceaseTime#14/02/1989 12:00:00#=";
        when(restrictionBuilder.between(attribute, fromDate, toDate)).thenReturn(restriction);
        when(restrictionBuilder.equalTo(attribute, fromDate)).thenReturn(restriction);
        when(restrictionBuilder.equalTo(attribute, toDate)).thenReturn(restriction);
        when(restrictionBuilder.anyOf(restriction, restriction, restriction)).thenReturn(restriction);
        assertEquals(restriction, hqsOperatorBasedRestriction.restrictionWithToAndFromDate(restrictionBuilder, "specificProblem#specificProblem1#!=",
                "ceaseTime#14/02/1989 12:00:00#=", "ceaseTime#16/02/1989 12:00:00#="));
    }

    @Test
    public void testGetRestrictionByComparisonOperatorForGT() {

        eventTimeList.add(new Date());
        when(restrictionBuilder.greaterThan("eventTime", eventTimeList.get(0))).thenReturn(restriction);
        when(restrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(restrictionBuilder.not(restriction)).thenReturn(restriction);

        assertEquals(restriction, hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, "eventTime",
                eventTimeList.get(0), DateOperator.GT));
    }

    @Test
    public void testGetRestrictionByComparisonOperatorLT() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.lessThan("eventTime", eventTimeList.get(0))).thenReturn(restriction);

        assertEquals(restriction, hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, "eventTime",
                eventTimeList.get(0), DateOperator.LT));
    }

    @Test
    public void testGetRestrictionByComparisonOperatorForEQ() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.equalTo("eventTime", eventTimeList.get(0))).thenReturn(restriction);

        assertEquals(restriction, hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, "eventTime",
                eventTimeList.get(0), DateOperator.EQ));
    }

    @Test
    public void testGetRestrictionByComparisonOperatorForGE() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.greaterThanEqualTo("eventTime", eventTimeList.get(0))).thenReturn(restriction);

        assertEquals(restriction, hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, "eventTime",
                eventTimeList.get(0), DateOperator.GE));
    }

    @Test
    public void testGetRestrictionByComparisonOperatorForLE() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.lessThanEqualTo("eventTime", eventTimeList.get(0))).thenReturn(restriction);
        assertEquals(restriction, hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, "eventTime",
                eventTimeList.get(0), DateOperator.LE));
    }

    @Test
    public void testGetRestrictionByComparisonOperatorForNE() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.not(restrictionBuilder.equalTo("eventTime", eventTimeList.get(0)))).thenReturn(restriction);
        assertEquals(restriction,
                hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, "alarmNumber", 111, DateOperator.NE));
    }

    @Test
    public void testGetRestrictionOnMatchConditionForCONTAINS() {
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String repeatContAttribute = "specificProblem#specificProblem1#CONTAINS";
        alarmAttributes1.add(repeatContAttribute);

        alarmLogData1.setAlarmAttributes(alarmAttributes1);
        when(restrictionBuilder.matchesString("specificProblem", "specificProblem1", StringMatchCondition.CONTAINS)).thenReturn(restriction);
        assertEquals(restriction,
                hqsOperatorBasedRestriction.getRestrictionOnMatchCondition(restrictionBuilder, "specificProblem", "specificProblem1", "CONTAINS"));
    }

    @Test
    public void testGetRestrictionOnMatchConditionForEQUALOPERATOR() {
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String repeatContAttribute = "specificProblem#specificProblem#EQUALOPERATOR";
        alarmAttributes1.add(repeatContAttribute);

        alarmLogData1.setAlarmAttributes(alarmAttributes1);

        when(restrictionBuilder.equalTo("specificProblem", "specificProblem")).thenReturn(restriction);
        when(restrictionBuilder.not(restriction)).thenReturn(restriction);
        assertEquals(restriction,
                hqsOperatorBasedRestriction.getRestrictionOnMatchCondition(restrictionBuilder, "specificProblem", "specificProblem", "EQUALOPERATOR"));
    }

    @Test
    public void testGetRestrictionOnMatchConditionForSTARTS_WITH() {
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String repeatContAttribute = "specificProblem#specificProblem1#STARTSWITH";
        alarmAttributes1.add(repeatContAttribute);

        alarmLogData1.setAlarmAttributes(alarmAttributes1);
        when(restrictionBuilder.matchesString("specificProblem", "specificProblem1", StringMatchCondition.STARTS_WITH)).thenReturn(restriction);
        assertEquals(restriction,
                hqsOperatorBasedRestriction.getRestrictionOnMatchCondition(restrictionBuilder, "specificProblem", "specificProblem1", "STARTSWITH"));
    }

    @Test
    public void testGetRestrictionOnMatchConditionForENDS_WITH() {
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String repeatContAttribute = "specificProblem#specificProblem#ENDSWITH";
        alarmAttributes1.add(repeatContAttribute);
        alarmLogData1.setAlarmAttributes(alarmAttributes1);
        when(restrictionBuilder.matchesString("specificProblem", "specificProblem", StringMatchCondition.ENDS_WITH)).thenReturn(restriction);

        assertEquals(restriction,
                hqsOperatorBasedRestriction.getRestrictionOnMatchCondition(restrictionBuilder, "specificProblem", "specificProblem", "ENDSWITH"));
    }

}
