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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.INDETERMINATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.MAJOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.MINOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.WARNING;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.CompositeNodeCriteriaRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.NodeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CompositeNodeCriteriaHandlerForCountTest {

    @InjectMocks
    private CompositeNodeCriteriaHandlerForCount compositeNodeCriteriaHandlerForCount;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private CompositeNodeCriteriaRestrictionBuilder compositeNodeCriteriaRestrictionBuilder;

    @Mock
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Mock
    private DataPersistenceService dps;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private Restriction restriction;

    @Mock
    private QueryExecutor queryExecutor;

    private final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
    private final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
    private final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();
    private final List<String> nodes = new ArrayList<String>();
    private final List<String> severities = new ArrayList<String>();
    List<Restriction> restrictions = new ArrayList<Restriction>();

    @Before
    public void setup() {

        nodes.add("LTE5467");
        compositeNodeCriteria.setNodes(nodes);

        severities.add(MAJOR);
        severities.add(MINOR);
        severities.add(INDETERMINATE);
        severities.add(WARNING);
        restrictions.add(restriction);
        alarmAttributeCriteria.setAttributeName("attributeName");
        alarmAttributeCriteria.setAttributeValue("value");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeNodeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);
        compositeNodeCriteriaRestrictionBuilder.build(typeQuery, compositeNodeCriteria);
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);
        when(dps.getLiveBucket()).thenReturn(liveBucket);
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(dpsProxy.getService()).thenReturn(dps);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.executeCount(typeQuery)).thenReturn(1L);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

    }

    @Test
    public void testGetAlarmsCount_compositeNodeCriteria_NoOfAlarms() {
        when(compositeNodeCriteriaRestrictionBuilder.build(typeQuery, compositeNodeCriteria)).thenReturn(restriction);
        when(compositeNodeCriteriaRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria)).thenReturn(restrictions);
        final AlarmCountResponse alarmCountResponse = compositeNodeCriteriaHandlerForCount.getAlarmCount(compositeNodeCriteria);
        final long count = alarmCountResponse.getAlarmCount();
        assertEquals(1L, count);
    }

    @Test
    public void testGetAlarmsCount_compositeNodeCriteria_AlarmsFound() {
        when(compositeNodeCriteriaRestrictionBuilder.build(typeQuery, compositeNodeCriteria)).thenReturn(restriction);
        when(compositeNodeCriteriaRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria)).thenReturn(restrictions);
        final AlarmCountResponse alarmCountResponse = compositeNodeCriteriaHandlerForCount.getAlarmCount(compositeNodeCriteria);
        final long count = alarmCountResponse.getAlarmCount();
        assertEquals(1L, count);
    }

    @Test
    public void testGetAlarmsCount_compositeNodeCriteriaWithOnlyAttributes_AlarmsFound() {

        when(compositeNodeCriteriaRestrictionBuilder.build(typeQuery, compositeNodeCriteria)).thenReturn(restriction);
        when(compositeNodeCriteriaRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria)).thenReturn(null);
        final AlarmCountResponse alarmCountResponse = compositeNodeCriteriaHandlerForCount.getAlarmCount(compositeNodeCriteria);
        final long count = alarmCountResponse.getAlarmCount();
        assertEquals(1L, count);
    }

    @Test
    public void testGetAlarmsCount_compositeNodeCriteriaWithOnlyNodes_AlarmsFound() {

        when(compositeNodeCriteriaRestrictionBuilder.build(typeQuery, compositeNodeCriteria)).thenReturn(null);
        when(compositeNodeCriteriaRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria)).thenReturn(restrictions);
        final AlarmCountResponse alarmCountResponse = compositeNodeCriteriaHandlerForCount.getAlarmCount(compositeNodeCriteria);
        final long count = alarmCountResponse.getAlarmCount();
        assertEquals(1L, count);
    }
}
