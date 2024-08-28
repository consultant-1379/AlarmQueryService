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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.LogicalOperatorRestrictionBuilder;

@RunWith(MockitoJUnitRunner.class)
public class LogicalOperatorRestrictionBuilderTest {
    @InjectMocks
    private LogicalOperatorRestrictionBuilder logicalOperationRestrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private OpenAlarmParser openAlarmParser;

    @Mock
    private Query query;

    @Before
    public void setup() {
        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
    }

    @Test
    public void testGetRestrictionByComparisonOperator_EQ_Restriction() {

        final String attribute = "alarmNumber";
        final int value = 111;

        when(typeRestrictionBuilder.equalTo(attribute, value)).thenReturn(restriction);
        assertEquals(restriction, logicalOperationRestrictionBuilder.build(typeRestrictionBuilder, attribute, value, Operator.NE));
    }

    @Test
    public void testGetRestrictionByComparisonOperator_GT_Restriction() {

        final String attribute = "alarmNumber";
        final int value = 111;

        when(typeRestrictionBuilder.greaterThan(attribute, value)).thenReturn(restriction);
        assertEquals(restriction, logicalOperationRestrictionBuilder.build(typeRestrictionBuilder, attribute, value, Operator.GT));
    }

    @Test
    public void testGetRestrictionByComparisonOperator_LE_Restriction() {

        final String toDate = "ceaseTime#16/02/1989 12:00:00#=";
        final String fromDate = "ceaseTime#14/02/1989 12:00:00#=";

        when(typeRestrictionBuilder.lessThan(fromDate, toDate)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(fromDate, toDate)).thenReturn(restriction);
        assertEquals(restriction, logicalOperationRestrictionBuilder.build(typeRestrictionBuilder, fromDate, toDate, Operator.LE));
    }

    @Test
    public void testGetRestrictionByComparisonOperator_LT_Restriction() {

        final String attribute = "alarmNumber";
        final int value = 111;

        when(typeRestrictionBuilder.lessThan(attribute, value)).thenReturn(restriction);

        assertEquals(restriction, logicalOperationRestrictionBuilder.build(typeRestrictionBuilder, attribute, value, Operator.LT));
    }

    @Test
    public void testGetRestrictionByComparisonOperator_GE_Restriction() {

        final String toDate = "ceaseTime#16/02/1989 12:00:00#=";
        final String fromDate = "ceaseTime#14/02/1989 12:00:00#=";

        when(typeRestrictionBuilder.greaterThan(fromDate, toDate)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo(fromDate, toDate)).thenReturn(restriction);
        assertEquals(restriction, logicalOperationRestrictionBuilder.build(typeRestrictionBuilder, fromDate, toDate, Operator.GE));
    }

    @Test
    public void testGetRestrictionByComparisonOperator_CONTAINS_Restriction() {

        final String attributeName = "severity";
        final String attributeValue = "critical";

        when(typeRestrictionBuilder.matchesString(attributeName, attributeValue, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        assertEquals(restriction, logicalOperationRestrictionBuilder.build(typeRestrictionBuilder, attributeName, attributeValue, Operator.CONTAINS));
    }

    @Test
    public void testGetRestrictionByComparisonOperator_STARTS_WITH_Restriction() {

        final String attributeName = "severity";
        final String attributeValue = "critical";

        when(typeRestrictionBuilder.matchesString(attributeName, attributeValue, StringMatchCondition.STARTS_WITH)).thenReturn(restriction);
        assertEquals(restriction,
                logicalOperationRestrictionBuilder.build(typeRestrictionBuilder, attributeName, attributeValue, Operator.STARTS_WITH));
    }

    @Test
    public void testGetRestrictionByComparisonOperator_ENDS_WITH_Restriction() {

        final String attributeName = "severity";
        final String attributeValue = "critical";

        when(typeRestrictionBuilder.matchesString(attributeName, attributeValue, StringMatchCondition.ENDS_WITH)).thenReturn(restriction);
        assertEquals(restriction, logicalOperationRestrictionBuilder.build(typeRestrictionBuilder, attributeName, attributeValue, Operator.ENDS_WITH));
    }

    @Test
    public void testGetRestrictionByComparisonOperator_AllRestricions_FinalRestriction() {

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        assertEquals(restriction, logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, restriction, restriction));
    }
}
