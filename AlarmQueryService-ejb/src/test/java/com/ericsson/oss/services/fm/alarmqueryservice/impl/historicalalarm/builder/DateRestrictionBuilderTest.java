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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.DATE_FORMAT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.UTC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;

@RunWith(MockitoJUnitRunner.class)
public class DateRestrictionBuilderTest {

    @InjectMocks
    private DateRestrictionBuilder historicalDateRestrictionBuilder;

    @Mock
    private Restriction dateRestriction;

    @Mock
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    @Mock
    private RestrictionBuilder restrictionBuilder;
    final String dateAttribute = EVENT_TIME;
    private final List<Date> dateList = new ArrayList<Date>();
    final SimpleDateFormat dateFormatUTC = new SimpleDateFormat(DATE_FORMAT);

    @Before
    public void setup() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date;
        Date date1;
        date = formatter.parse("2014-04-13 05:00");
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        date1 = formatter.parse("2014-04-11 06:00");

        dateList.add(date1);
        dateList.add(date);

        dateFormatUTC.setTimeZone(TimeZone.getTimeZone(UTC));

    }

    @Test
    public void testGetDateRestriction_BETWEENDates_DateRestriction() throws Exception {

        final Operator dateComparisionOperator = Operator.BETWEEN;

        when(restrictionBuilder.anyOf(null, null, null)).thenReturn(dateRestriction);

        assertEquals(dateRestriction, historicalDateRestrictionBuilder.build(restrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testGetDateRestrictionElseIf_LE() throws Exception {

        final Operator dateComparisionOperator = Operator.LE;

        when(
                logicalOperatorRestrictionBuilder.build(restrictionBuilder, dateAttribute, dateFormatUTC.format(dateList.get(0).getTime()),
                        Operator.LE)).thenReturn(dateRestriction);
        assertEquals(dateRestriction, historicalDateRestrictionBuilder.build(restrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testGetDateRestrictionElseIf_EQ_DateRestriction() throws Exception {

        final Operator dateComparisionOperator = Operator.EQ;
        when(logicalOperatorRestrictionBuilder.build(restrictionBuilder, dateAttribute, dateFormatUTC.format(dateList.get(0)), Operator.EQ))
                .thenReturn(dateRestriction);
        when(restrictionBuilder.anyOf(null, null, null)).thenReturn(dateRestriction);

        assertEquals(dateRestriction, historicalDateRestrictionBuilder.build(restrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testGetDateRestrictionElseIf_GE_DateRestriction() throws Exception {

        when(logicalOperatorRestrictionBuilder.build(restrictionBuilder, dateAttribute, dateFormatUTC.format(dateList.get(0)), Operator.GE))
                .thenReturn(dateRestriction);
        assertEquals(dateRestriction, historicalDateRestrictionBuilder.build(restrictionBuilder, dateAttribute, dateList, Operator.GE));
    }
}