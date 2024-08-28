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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.AlarmReader;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.PoIdReader;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CompositeNodeCriteriaRestrictionBuilderTest {

    @InjectMocks
    private CompositeNodeCriteriaRestrictionBuilder compositeRestrictionBuilder;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private DataPersistenceService dps;

    @Mock
    Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private AlarmReader alarmReader;

    @Mock
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Mock
    private LogicalOperatorRestrictionBuilder logicalOperationRestrictionBuilder;

    @Mock
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Mock
    private PoIdReader poIdReader;

    @Mock
    Restriction restriction;

    @Mock
    private AlarmAttributeResponseBuilder responseBuilder;

    AlarmAttributeResponse alarmQueryResponse = null;

    Map<String, Object> attributesMap = new HashMap<String, Object>();
    List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();
    List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
    List<String> nodes = new ArrayList<String>();
    List<Date> dates = new ArrayList<Date>();
    List<Long> poIds = new ArrayList<Long>();
    static final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
    static final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
    static final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();

    @Before
    public void setUp() {
        when(dpsProxy.getService()).thenReturn(dps);
        final AlarmAttributeCriteria alarmAttributeCriterion = new AlarmAttributeCriteria();
        alarmAttributeCriterion.setAttributeName(FDN);
        alarmAttributeCriterion.setAttributeValue("NETWORKELEMENT=1");
        alarmAttributeCriterion.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriterion);
        final List<AlarmAttributeCriteria> alarmAttributeCriteria = new ArrayList<AlarmAttributeCriteria>();
        alarmAttributeCriteria.add(alarmAttributeCriterion);

        nodes.add("LTE01113333");
        dates.add(new Date());
        attributesMap.put("eventPoId", 11L);
        final AlarmRecord alarmRecord = new AlarmRecord(attributesMap, null, null);
        alarmRecords.add(alarmRecord);
        poIds.add(11L);

        compositeNodeCriteria.setNodes(nodes);
        compositeNodeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);
        compositeNodeCriteria.setSortAttribute(FDN);
        compositeNodeCriteria.setSortDirection(SortingOrder.ASCENDING);
        expectedOutputAttributes.setNodeIdRequired(true);
        expectedOutputAttributes.setCommentHistoryRequired(true);
        alarmQueryResponse = new AlarmAttributeResponse(alarmRecords, SUCCESS);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, null, null)).thenReturn(restriction);
        when(attributeRestrictionBuilder.build(typeRestrictionBuilder, alarmAttributeCriteria)).thenReturn(restriction);

        when(logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, restriction, restriction)).thenReturn(
                restriction);

        when(responseBuilder.buildAttributeResponse(alarmRecords)).thenReturn(alarmQueryResponse);
        when(queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(alarmReader.getAlarmRecords(typeQuery, true)).thenReturn(alarmRecords);
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);

    }

    @Test
    public void testBuild_CompositeNodeCriteria_Alarms() {

        final Restriction result = compositeRestrictionBuilder.build(typeQuery, compositeNodeCriteria);
        assertEquals(restriction, result);
    }

    @Test
    public void testBuildNodeRestrictions_CompositeNodeCriteria_Alarms() {

        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        when(nodeRestrictionBuilder.buildListOfNodeRestrictions(typeQuery.getRestrictionBuilder(), nodes)).thenReturn(restrictions);

        final List<Restriction> result = compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria);
        assertEquals(1, result.size());
    }

}
