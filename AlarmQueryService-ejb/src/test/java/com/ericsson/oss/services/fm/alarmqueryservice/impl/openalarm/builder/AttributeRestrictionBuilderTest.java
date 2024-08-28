/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ACK_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.BACKUP_STATUS;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CEASE_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CORRELATED_RECORD_NAME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CORRELATED_VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_POID_AS_STRING;
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

import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;

@RunWith(MockitoJUnitRunner.class)
public class AttributeRestrictionBuilderTest {

    @InjectMocks
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Mock
    private OpenAlarmParser openAlarmParser;

    private final List<AlarmAttributeCriteria> alarmAttributeConditions = new ArrayList<AlarmAttributeCriteria>();
    private final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
    private final List<Date> dates = new ArrayList<Date>();

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
    public void testBuildAlarmAttributeRestriction_DateEQ_Restriction() {

        final Date date = new Date();
        dates.add(date);
        when(dateRestrictionBuilder.build(typeRestrictionBuilder, CEASE_TIME, dates, Operator.EQ)).thenReturn(restriction);
        when(logicalOperatorRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, null, restriction)).thenReturn(restriction);

        alarmAttributeCriteria.setAttributeName(CEASE_TIME);
        alarmAttributeCriteria.setAttributeValue(date);
        alarmAttributeCriteria.setOperator(Operator.EQ);
        alarmAttributeConditions.add(alarmAttributeCriteria);

        final Restriction[] restrictions = { restriction };
        when(typeRestrictionBuilder.anyOf(restrictions)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restrictions)).thenReturn(restriction);

        assertEquals(restriction, attributeRestrictionBuilder.build(typeRestrictionBuilder, alarmAttributeConditions));
    }

    @Test
    public void testBuildAlarmAttributeRestriction_Number_Restriction() {

        final Date date = new Date();
        dates.add(date);

        when(logicalOperatorRestrictionBuilder.build(typeRestrictionBuilder, ALARM_NUMBER, 111L, Operator.EQ)).thenReturn(restriction);
        when(logicalOperatorRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, null, restriction)).thenReturn(restriction);

        alarmAttributeCriteria.setAttributeName(ALARM_NUMBER);
        alarmAttributeCriteria.setAttributeValue(111L);
        alarmAttributeCriteria.setOperator(Operator.EQ);
        alarmAttributeConditions.add(alarmAttributeCriteria);
        final AlarmAttributeCriteria eventPoIdCriteria1 = new AlarmAttributeCriteria();
        eventPoIdCriteria1.setAttributeName(EVENT_POID_AS_STRING);
        eventPoIdCriteria1.setAttributeValue(123123222L);
        eventPoIdCriteria1.setOperator(Operator.EQ);
        final AlarmAttributeCriteria eventPoIdCriteria2 = new AlarmAttributeCriteria();
        eventPoIdCriteria2.setAttributeName(EVENT_POID_AS_STRING);
        eventPoIdCriteria2.setAttributeValue(1212323123222L);
        eventPoIdCriteria2.setOperator(Operator.EQ);
        alarmAttributeConditions.add(eventPoIdCriteria1);
        alarmAttributeConditions.add(eventPoIdCriteria2);

        final Restriction[] restrictions = { restriction };
        when(typeRestrictionBuilder.anyOf(restrictions)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restrictions)).thenReturn(restriction);

        assertEquals(restriction, attributeRestrictionBuilder.build(typeRestrictionBuilder, alarmAttributeConditions));
    }
}
