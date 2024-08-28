/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;

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

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.DateRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.LogicalOperatorRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.NodeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CompositeNodeCriteriaHandlerForPoIdsTest {

    @InjectMocks
    CompositeNodeCriteriaHandlerForPoIds compositeNodeCriteriaHandler;

    @Mock
    QueryBuilder queryBuilder;

    @Mock
    private DataPersistenceService dps;

    @Mock
    Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private AlarmReader alarmReader;

    @Mock
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Mock
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Mock
    private LogicalOperatorRestrictionBuilder logicalOperationRestrictionBuilder;

    @Mock
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Mock
    private PoIdReader poIdReader;

    @Mock
    private Restriction restriction;

    @Mock
    private AlarmAttributeResponse alarmQueryResponse;

    @Mock
    private ConfigurationListener configurationListener;

    List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();
    List<AlarmRecord> record = new ArrayList<AlarmRecord>();
    List<String> nodes = new ArrayList<String>();
    List<Date> dates = new ArrayList<Date>();
    List<Long> poIds = new ArrayList<Long>();
    Map<String, Object> alarmAttributeMap = new HashMap<String, Object>();
    static final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
    static final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
    static final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();

    @Before
    public void setUp() {

        when(dpsProxy.getService()).thenReturn(dps);
        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName("FDN");
        alarmAttributeCriteria.setAttributeValue("NETWORKELEMENT=1");
        alarmAttributeCriteria.setOperator(Operator.EQ);

        otherAlarmAttributes.add(alarmAttributeCriteria);
        nodes.add("LTE01113333");
        dates.add(new Date());
        alarmAttributeMap.put("eventPoId", 11L);
        final AlarmRecord alarmRecord = new AlarmRecord(alarmAttributeMap, null, null);
        record.add(alarmRecord);
        poIds.add(11L);

        compositeNodeCriteria.setNodes(nodes);
        compositeNodeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);
        compositeNodeCriteria.setSortAttribute("nodes");
        compositeNodeCriteria.setSortDirection(SortingOrder.ASCENDING);
        expectedOutputAttributes.setNodeIdRequired(true);
        expectedOutputAttributes.setCommentHistoryRequired(true);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, null, null)).thenReturn(restriction);
        when(logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, restriction, restriction))
                        .thenReturn(restriction);

        when(queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(poIdReader.getPoIds(liveBucket, typeQuery)).thenReturn(poIds);

        when(configurationListener.getMaxNEsAllowedPerOpenAlarmQuery()).thenReturn(1500);

    }

    @Test
    public void getAlarmPoIds_CompositeNodeCriteria_PoIds() {

        final AlarmPoIdResponse alarmPoIdResponse = compositeNodeCriteriaHandler.getAlarmPoIds(compositeNodeCriteria);
        assertEquals(1, alarmPoIdResponse.getPoIds().size());

    }

}
