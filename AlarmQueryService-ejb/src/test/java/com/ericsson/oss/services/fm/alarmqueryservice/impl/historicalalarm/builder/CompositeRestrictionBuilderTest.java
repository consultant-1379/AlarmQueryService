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
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_POID_AS_STRING;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;

@RunWith(MockitoJUnitRunner.class)
public class CompositeRestrictionBuilderTest {
    @InjectMocks
    private CompositeRestrictionBuilder compositeNodeRestrictionBuilder;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Mock
    private DateRestrictionBuilder DateRestrictionBuilder;

    @Mock
    private AttributeRestrictionBuilder AttributeRestrictionBuilder;

    @Mock
    private Restriction restriction;

    private final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();
    private final List<String> nodes = new ArrayList<String>();
    private final List<Date> dates = new ArrayList<Date>();
    private final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
    private final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();
    private final List<Date> eventTimeList = new ArrayList<Date>(1);

    @Test
    public void testBuild_CompositeEventTimeCriteria_Alarms() {

        final Date fromDate = new Date();
        alarmAttributeCriteria.setAttributeName(ALARM_NUMBER);
        alarmAttributeCriteria.setAttributeValue(222L);
        alarmAttributeCriteria.setOperator(Operator.EQ);
        final AlarmAttributeCriteria eventPoIdCriteria1 = new AlarmAttributeCriteria();
        eventPoIdCriteria1.setAttributeName(EVENT_POID_AS_STRING);
        eventPoIdCriteria1.setAttributeValue(123123222L);
        eventPoIdCriteria1.setOperator(Operator.EQ);
        final AlarmAttributeCriteria eventPoIdCriteria2 = new AlarmAttributeCriteria();
        eventPoIdCriteria2.setAttributeName(EVENT_POID_AS_STRING);
        eventPoIdCriteria2.setAttributeValue(1212323123222L);
        eventPoIdCriteria2.setOperator(Operator.EQ);
        otherAlarmAttributes.add(eventPoIdCriteria2);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        otherAlarmAttributes.add(alarmAttributeCriteria);

        dates.add(new Date());

        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setFromTime(fromDate);
        compositeEventTimeCriteria.setOperator(Operator.EQ);

        final Date eventTime = compositeEventTimeCriteria.getFromTime();

        final List<AlarmAttributeCriteria> alarmAttributes = compositeEventTimeCriteria.getAlarmAttributeCriteria();
        //        final List<String> nodes = compositeEventTimeCriteria.getNodes();

        eventTimeList.add(eventTime);

        when(DateRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList, compositeEventTimeCriteria.getOperator()))
                .thenReturn(restriction);

        when(AttributeRestrictionBuilder.build(restrictionBuilder, alarmAttributes)).thenReturn(restriction);
        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        assertEquals(restriction, compositeNodeRestrictionBuilder.build(restrictionBuilder, compositeEventTimeCriteria));

    }

    @Test
    public void testBuildNodeRestrictions_CompositeEventTimeCriteria_Alarms() {
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        nodes.add("LTE01ERBS0001");
        compositeEventTimeCriteria.setNodes(nodes);
        when(nodeRestrictionBuilder.build(restrictionBuilder, nodes)).thenReturn(restriction);
        assertEquals(restriction, compositeNodeRestrictionBuilder.buildNodeRestrictions(restrictionBuilder, compositeEventTimeCriteria));
    }

}