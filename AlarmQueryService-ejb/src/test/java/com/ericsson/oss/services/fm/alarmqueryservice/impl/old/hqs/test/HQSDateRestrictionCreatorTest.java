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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.UTC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSAttributeRestrictionCreator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSDateRestrictionCreator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSOperatorBasedRestriction;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants;

@RunWith(MockitoJUnitRunner.class)
public class HQSDateRestrictionCreatorTest {

    @InjectMocks
    private HQSDateRestrictionCreator hqsDateRestrictionCreator;

    @Mock
    private HQSAttributeRestrictionCreator hqsAttributeRestrictionCreator;
    @Mock
    private Restriction dateRestriction;
    @Mock
    private HQSOperatorBasedRestriction hqsOperatorBasedRestriction;
    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Test
    public void testGetDateRestriction() throws Exception {
        final DateOperator dateComparisionOperator = DateOperator.BETWEEN;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date;
        Date date1;
        date = formatter.parse("2014-04-13 05:00");
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        date1 = formatter.parse("2014-04-11 06:00");
        final List<Date> dateList = new ArrayList<Date>();
        dateList.add(date1);
        dateList.add(date);
        final String dateAttribute = "eventTime";
        final SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone(UTC));

        when(
                hqsOperatorBasedRestriction.restrictionWithToAndFromDate(restrictionBuilder, dateAttribute,
                        dateFormatUTC.format(dateList.get(0)), dateFormatUTC.format(dateList.get(1).getTime() + 999L))).thenReturn(
                dateRestriction);

        assertEquals(dateRestriction,
                hqsDateRestrictionCreator.getDateRestriction(restrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testGetDateRestrictionElseIf_LE() throws Exception {
        final DateOperator dateComparisionOperator = DateOperator.LE;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date;
        Date date1;
        date = formatter.parse("2014-04-13 05:00");
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        date1 = formatter.parse("2014-04-11 06:00");
        final List<Date> dateList = new ArrayList<Date>();
        dateList.add(date1);
        dateList.add(date);
        final String dateAttribute = "eventTime";
        final SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone(UTC));
        when(
                hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, dateAttribute,
                        dateFormatUTC.format(dateList.get(0).getTime() + 999L), DateOperator.LE)).thenReturn(dateRestriction);
        assertEquals(dateRestriction,
                hqsDateRestrictionCreator.getDateRestriction(restrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testGetDateRestrictionElseIf_EQ() throws Exception {
        final DateOperator dateComparisionOperator = DateOperator.EQ;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date;
        Date date1;
        date = formatter.parse("2014-04-13 05:00");
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        date1 = formatter.parse("2014-04-11 06:00");
        final List<Date> dateList = new ArrayList<Date>();
        dateList.add(date1);
        dateList.add(date);
        final String dateAttribute = "eventTime";
        final SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone(UTC));
        when(
                hqsOperatorBasedRestriction.restrictionWithToAndFromDate(restrictionBuilder, dateAttribute,
                        dateFormatUTC.format(dateList.get(0)), dateFormatUTC.format(dateList.get(1).getTime() + 999L))).thenReturn(
                dateRestriction);
        assertEquals(dateRestriction,
                hqsDateRestrictionCreator.getDateRestriction(restrictionBuilder, dateAttribute, dateList, dateComparisionOperator));
    }

    @Test
    public void testGetDateRestrictionElseIf() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date;
        Date date1;
        date = formatter.parse("2014-04-13 05:00");
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        date1 = formatter.parse("2014-04-11 06:00");
        final List<Date> dateList = new ArrayList<Date>();
        dateList.add(date1);
        dateList.add(date);
        final String dateAttribute = "eventTime";
        final SimpleDateFormat dateFormatSolrUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatSolrUTC.setTimeZone(TimeZone.getTimeZone(UTC));
        when(
                hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, dateAttribute,
                        dateFormatSolrUTC.format(dateList.get(0)), DateOperator.GE)).thenReturn(dateRestriction);
        assertEquals(dateRestriction, hqsDateRestrictionCreator.getDateRestriction(restrictionBuilder, dateAttribute, dateList, DateOperator.GE));
    }

    @Test
    public void testGetRestrictionOnDateAttributesElseIf() throws Exception {

        final String dateAttribute = "eventTime ";
        final String betweenDates = "13/04/2014 05:00:00";
        final String operator = "!=";
        when(
                restrictionBuilder.not(hqsOperatorBasedRestriction.restrictionWithToAndFromDate(restrictionBuilder, dateAttribute,
                        "2014-04-12T23:30:12.000Z", "2014-04-12T23:30:12.999Z"))).thenReturn(dateRestriction);
        assertEquals(dateRestriction, hqsDateRestrictionCreator.getRestrictionOnDateAttributes(restrictionBuilder, dateAttribute, betweenDates,
                operator, dateRestriction, QueryConstants.DATE_FORMAT));
    }

}