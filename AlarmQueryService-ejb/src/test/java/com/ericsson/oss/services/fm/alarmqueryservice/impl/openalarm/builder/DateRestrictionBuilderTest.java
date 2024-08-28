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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;

@RunWith(MockitoJUnitRunner.class)
public class DateRestrictionBuilderTest {
    @InjectMocks
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;
    private final String dateAttribute = EVENT_TIME;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private final List<Date> dateList = new ArrayList<Date>();
    Date fromDate;
    Date toDate;

    @Before
    public void setup() throws ParseException {

        toDate = formatter.parse("2014-04-13 05:00");
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        fromDate = formatter.parse("2014-04-11 06:00");

        when(typeRestrictionBuilder.between(dateAttribute, fromDate, toDate)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(dateAttribute, toDate)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(dateAttribute, fromDate)).thenReturn(restriction);
        dateList.add(fromDate);
        dateList.add(toDate);
    }

    @Test
    public void testBuild_BETWEENOperator_Restriction() throws ParseException {

        final Operator dateComparisionOperator = Operator.BETWEEN;

        final String dateAttribute = EVENT_TIME;

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        assertEquals(restriction, dateRestrictionBuilder.build(typeRestrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testBuild_EQOperator_Restriction() throws ParseException {
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);

        final Operator dateComparisionOperator = Operator.EQ;

        when(typeRestrictionBuilder.equalTo(Matchers.anyString(), Matchers.anyObject())).thenReturn(restriction);

        when(logicalOperatorRestrictionBuilder.build(typeRestrictionBuilder, dateAttribute, dateList.get(0), dateComparisionOperator)).thenReturn(
                restriction);
        when(typeRestrictionBuilder.equalTo(Matchers.anyString(), Matchers.anyObject())).thenReturn(restriction);
        assertEquals(restriction, dateRestrictionBuilder.build(typeRestrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testBuild_NEOperator_Restriction() throws ParseException {
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);

        final Operator dateComparisionOperator = Operator.NE;

        final String dateAttribute = EVENT_TIME;

        when(typeRestrictionBuilder.equalTo(Matchers.anyString(), Matchers.anyObject())).thenReturn(restriction);
        when(logicalOperatorRestrictionBuilder.build(typeRestrictionBuilder, dateAttribute, dateList.get(0), dateComparisionOperator)).thenReturn(
                restriction);
        assertEquals(restriction, dateRestrictionBuilder.build(typeRestrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testBuild_LEOperator_Restriction() throws ParseException {

        final Operator dateComparisionOperator = Operator.LE;

        dateList.add(toDate);
        final String dateAttribute = EVENT_TIME;

        when(logicalOperatorRestrictionBuilder.build(typeRestrictionBuilder, dateAttribute, dateList.get(0), dateComparisionOperator)).thenReturn(
                restriction);
        assertEquals(restriction, dateRestrictionBuilder.build(typeRestrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

}
