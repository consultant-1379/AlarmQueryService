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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SPECIFIC_PROBLEM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;

@RunWith(MockitoJUnitRunner.class)
public class LogicalOperatorRestrictionBuilderTest {
    @InjectMocks
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private Query query;

    private final List<Date> eventTimeList = new ArrayList<>();
    private final String attributeName = SPECIFIC_PROBLEM;
    private final String attributeValue = "specificProblem1";

    @Before
    public void setUp() {
        when(restrictionBuilder.equalTo(attributeName, attributeValue)).thenReturn(restriction);
        when(restrictionBuilder.matchesString(attributeName, attributeValue, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(restrictionBuilder.matchesString(attributeName, attributeValue, StringMatchCondition.STARTS_WITH)).thenReturn(restriction);
        when(restrictionBuilder.matchesString(attributeName, attributeValue, StringMatchCondition.ENDS_WITH)).thenReturn(restriction);
        when(restrictionBuilder.not(restrictionBuilder.equalTo(attributeName, attributeValue))).thenReturn(restriction);
    }

    @Test
    public void testGetRestrictionByComparison_GTOperator_Restriction() {

        eventTimeList.add(new Date());
        when(restrictionBuilder.greaterThan(EVENT_TIME, eventTimeList.get(0))).thenReturn(restriction);
        when(restrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(restrictionBuilder.not(restriction)).thenReturn(restriction);

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList.get(0), Operator.GT));
    }

    @Test
    public void testGetRestrictionByComparison_LTOperator_Restriction() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.lessThan(EVENT_TIME, eventTimeList.get(0))).thenReturn(restriction);

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList.get(0), Operator.LT));
    }

    @Test
    public void testGetRestrictionByComparison_EQOperator_Restriction() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.equalTo(EVENT_TIME, eventTimeList.get(0))).thenReturn(restriction);

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList.get(0), Operator.EQ));
    }

    @Test
    public void testGetRestrictionByComparison_GEOperator_Restriction() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.greaterThanEqualTo(EVENT_TIME, eventTimeList.get(0))).thenReturn(restriction);

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList.get(0), Operator.GE));
    }

    @Test
    public void testGetRestrictionByComparison_LEOperator_Restriction() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.lessThanEqualTo(EVENT_TIME, eventTimeList.get(0))).thenReturn(restriction);

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList.get(0), Operator.LE));
    }

    @Test
    public void testGetRestrictionByComparison_NEOperator_Restriction() {
        eventTimeList.add(new Date());
        when(restrictionBuilder.not(restrictionBuilder.equalTo(EVENT_TIME, eventTimeList.get(0)))).thenReturn(restriction);

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, ALARM_NUMBER, 111, Operator.NE));

    }

    @Test
    public void testGetRestrictionOnMatchCondition_EQOperator_Restriction() {

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, attributeName, attributeValue, Operator.EQ));

    }

    @Test
    public void testGetRestrictionOnMatchCondition_STARTWITHOperator_Restriction() {

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, attributeName, attributeValue, Operator.STARTS_WITH));

    }

    @Test
    public void testGetRestrictionOnMatchCondition_CONTAINSOperator_Restriction() {

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, attributeName, attributeValue, Operator.CONTAINS));

    }

    @Test
    public void testGetRestrictionOnMatchCondition_ENDSWITHOperator_Restriction() {

        assertEquals(restriction, logicalOperatorRestrictionBuilder.build(restrictionBuilder, attributeName, attributeValue, Operator.ENDS_WITH));

    }
}