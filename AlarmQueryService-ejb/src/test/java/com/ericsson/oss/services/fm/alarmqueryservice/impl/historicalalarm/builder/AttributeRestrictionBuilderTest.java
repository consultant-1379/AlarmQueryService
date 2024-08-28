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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ACK_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.BACKUP_STATUS;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CEASE_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CORRELATED_RECORD_NAME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CORRELATED_VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.INSERT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.LAST_UPDATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.MANUAL_CEASE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OSCILLATION_COUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.REPEAT_COUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SYNC_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;
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

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;

@RunWith(MockitoJUnitRunner.class)
public class AttributeRestrictionBuilderTest {
    @InjectMocks
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Mock
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;
    @Mock
    private LogicalOperatorRestrictionBuilder hqsOperatorBasedRestriction;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Mock
    private OpenAlarmParser openAlarmParser;

    private final List<AlarmAttributeCriteria> alarmAttributes = new ArrayList<AlarmAttributeCriteria>();
    private final AlarmAttributeCriteria attribute = new AlarmAttributeCriteria();
    private final List<Date> dates = new ArrayList<Date>();
    private final Object attributeValue = 222L;
    private final Date date = new Date();

    private static final List<String> numberTypeAttributes = new ArrayList<String>();
    private static final List<String> dateTypeAttributes = new ArrayList<String>();
    private static final List<String> booleanTypeAttributes = new ArrayList<String>();

    @Before
    public void prepare() {
        numberTypeAttributes.add(ALARM_ID);
        numberTypeAttributes.add(ALARM_NUMBER);
        numberTypeAttributes.add(REPEAT_COUNT);
        numberTypeAttributes.add(OSCILLATION_COUNT);

        dateTypeAttributes.add(CEASE_TIME);
        dateTypeAttributes.add(ACK_TIME);
        dateTypeAttributes.add(INSERT_TIME);
        dateTypeAttributes.add(EVENT_TIME);
        dateTypeAttributes.add(LAST_UPDATED);

        booleanTypeAttributes.add(VISIBILITY);
        booleanTypeAttributes.add(SYNC_STATE);
        booleanTypeAttributes.add(MANUAL_CEASE);
        booleanTypeAttributes.add(CORRELATED_RECORD_NAME);
        booleanTypeAttributes.add(BACKUP_STATUS);
        booleanTypeAttributes.add(CORRELATED_VISIBILITY);

        when(openAlarmParser.getIntegerTypeAttributes()).thenReturn(numberTypeAttributes);
        when(openAlarmParser.getLongTypeAttributes()).thenReturn(numberTypeAttributes);
        when(openAlarmParser.getDateTypeAttributes()).thenReturn(dateTypeAttributes);
        when(openAlarmParser.getBooleanTypeAttributes()).thenReturn(booleanTypeAttributes);
        attributeRestrictionBuilder.prepare();
    }

    @Test
    public void testGetAttributesRestriction_AlarmAttributes_AttributeRestriction() {

        attributeRestrictionBuilder.prepare();
        attribute.setAttributeName(ALARM_NUMBER);
        attribute.setAttributeValue(222L);

        attribute.setOperator(Operator.GE);

        alarmAttributes.add(attribute);
        when(logicalOperatorRestrictionBuilder.build(restrictionBuilder, ALARM_NUMBER, attributeValue, Operator.GE)).thenReturn(restriction);
        when(restrictionBuilder.greaterThanEqualTo(ALARM_NUMBER, attributeValue)).thenReturn(restriction);
        when(restrictionBuilder.greaterThanEqualTo(ALARM_NUMBER, attributeValue)).thenReturn(restriction);
        when(hqsOperatorBasedRestriction.build(restrictionBuilder, ALARM_NUMBER, attributeValue, Operator.GE)).thenReturn(restriction);

        assertEquals(restriction, attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes));
    }

    @Test
    public void testGetAttributesRestriction_AlarmAttributesDate_AttributeRestriction() {

        attributeRestrictionBuilder.prepare();
        attribute.setAttributeName(CEASE_TIME);
        attribute.setAttributeValue(date);
        attribute.setOperator(Operator.GE);
        dates.add(date);
        alarmAttributes.add(attribute);

        when(logicalOperatorRestrictionBuilder.build(restrictionBuilder, CEASE_TIME, date, Operator.GE)).thenReturn(restriction);
        when(restrictionBuilder.greaterThanEqualTo(CEASE_TIME, date)).thenReturn(restriction);
        when(dateRestrictionBuilder.build(restrictionBuilder, CEASE_TIME, dates, Operator.GE)).thenReturn(restriction);

        assertEquals(restriction, attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes));
    }

    @Test
    public void testGetAttributesRestriction_AlarmAttributesDate_BetweenRestriction() {

        dates.add(date);
        dates.add(new Date(date.getTime() - 3600000));
        attributeRestrictionBuilder.prepare();
        attribute.setAttributeName(CEASE_TIME);
        attribute.setAttributeValue(dates);
        attribute.setOperator(Operator.BETWEEN);

        alarmAttributes.add(attribute);

        when(logicalOperatorRestrictionBuilder.build(restrictionBuilder, CEASE_TIME, dates, Operator.BETWEEN)).thenReturn(restriction);
        when(restrictionBuilder.greaterThanEqualTo(CEASE_TIME, date)).thenReturn(restriction);
        when(dateRestrictionBuilder.build(restrictionBuilder, CEASE_TIME, dates, Operator.BETWEEN)).thenReturn(restriction);
        assertEquals(restriction, attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes));
    }

    @Test
    public void testGetAttributesRestriction_SingleDateWithBetween_Exception() {

        attributeRestrictionBuilder.prepare();
        attribute.setAttributeName(CEASE_TIME);
        attribute.setAttributeValue(date);
        attribute.setOperator(Operator.BETWEEN);
        dates.add(date);
        alarmAttributes.add(attribute);

        when(logicalOperatorRestrictionBuilder.build(restrictionBuilder, CEASE_TIME, date, Operator.BETWEEN)).thenReturn(restriction);
        when(restrictionBuilder.greaterThanEqualTo(CEASE_TIME, date)).thenReturn(restriction);
        when(dateRestrictionBuilder.build(restrictionBuilder, CEASE_TIME, dates, Operator.BETWEEN)).thenReturn(restriction);
        Restriction restriction1 = null;
        try {
            restriction1 = attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes);
        } catch (final Exception e) {
            assertEquals("java.util.Date cannot be cast to java.util.List", e.getMessage());
        }
        assertEquals(null, restriction1);
    }

    @Test
    public void testGetAttributesRestriction_OperatorMissMatch_Exception() {

        attributeRestrictionBuilder.prepare();
        attribute.setAttributeName(ALARM_NUMBER);
        attribute.setAttributeValue(111L);
        attribute.setOperator(Operator.BETWEEN);
        dates.add(date);
        alarmAttributes.add(attribute);

        when(logicalOperatorRestrictionBuilder.build(restrictionBuilder, CEASE_TIME, date, Operator.BETWEEN)).thenReturn(restriction);
        when(restrictionBuilder.greaterThanEqualTo(CEASE_TIME, date)).thenReturn(restriction);
        when(dateRestrictionBuilder.build(restrictionBuilder, CEASE_TIME, dates, Operator.BETWEEN)).thenReturn(restriction);
        try {
            final Restriction restriction1 = attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes);
        } catch (final Exception e) {
            assertEquals("For attribute  alarmNumber , given operator :: BETWEEN is not valid", e.getMessage());
        }

    }
}
