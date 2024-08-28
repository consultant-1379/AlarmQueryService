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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FM;
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

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
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
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.CompositeNodeCriteriaRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.LogicalOperatorRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.NodeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CompositeNodeCriteriaHandlerForAlarmsTest {

    @InjectMocks
    private CompositeNodeCriteriaHandlerForAlarms compositeNodeCriteriaHandler;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private DataPersistenceService dps;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

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
    private CompositeNodeCriteriaRestrictionBuilder compositeRestrictionBuilder;

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
    private AlarmAttributeResponseBuilder responseBuilder;

    private AlarmAttributeResponse alarmAttributeResponse = null;

    private final Map<String, Object> alarmAttributeMap = new HashMap<String, Object>();
    private final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();
    private final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
    private final List<String> nodes = new ArrayList<String>();
    private final List<Date> dates = new ArrayList<Date>();
    private final List<Long> poIds = new ArrayList<Long>();
    static final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
    static final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
    static final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();

    @Before
    public void setUp() {
        when(dpsProxy.getService()).thenReturn(dps);
        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(FDN);
        alarmAttributeCriteria.setAttributeValue("NETWORKELEMENT=1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        nodes.add("LTE01113333");
        dates.add(new Date());
        alarmAttributeMap.put("eventPoId", 11L);
        final AlarmRecord alarmRecord = new AlarmRecord(alarmAttributeMap, null, null);
        alarmRecords.add(alarmRecord);
        poIds.add(11L);

        compositeNodeCriteria.setNodes(nodes);
        compositeNodeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        final AlarmSortCriterion alarmSortCriterion = new AlarmSortCriterion();
        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();
        alarmSortCriterion.setSortAttribute(FDN);
        alarmSortCriterion.setSortOrder(SortingOrder.ASCENDING);
        alarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        alarmSortCriteria.add(alarmSortCriterion);
        compositeNodeCriteria.setAlarmSortCriteria(alarmSortCriteria);
        expectedOutputAttributes.setNodeIdRequired(true);
        expectedOutputAttributes.setCommentHistoryRequired(true);
        alarmAttributeResponse = new AlarmAttributeResponse(alarmRecords, SUCCESS);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, null, null)).thenReturn(restriction);
        when(logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(typeRestrictionBuilder, restriction, restriction)).thenReturn(
                restriction);
        when(compositeRestrictionBuilder.build(typeQuery, compositeNodeCriteria)).thenReturn(restriction);

        when(responseBuilder.buildAttributeResponse(alarmRecords)).thenReturn(alarmAttributeResponse);
        when(alarmReader.getAlarmRecords(typeQuery, true)).thenReturn(alarmRecords);
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add(ALARM_NUMBER);
        when(alarmReader.getAlarmRecordsForSelectedAttributes(typeQuery, outputAttributes)).thenReturn(alarmRecords);
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);

        when(alarmReader.getAlarmRecordsForSelectedAttributes(typeQuery, outputAttributes)).thenReturn(alarmRecords);

    }

    @Test
    public void testGetAlarms_CompositeNodeCriteria_Alarms() {
        when(queryExecutor.executeCount(typeQuery)).thenReturn(2L);
        final ExpectedOutputAttributes expectedOutputAttributes1 = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add(ALARM_NUMBER);
        expectedOutputAttributes1.setOutputAttributes(outputAttributes);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        when(compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria)).thenReturn(restrictions);
        final AlarmAttributeResponse alarmQueryResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());
    }

    @Test
    public void testGetAlarms_CompositeNodeCriteriaSortAttributes_Alarms() {
        when(queryExecutor.executeCount(typeQuery)).thenReturn(2L);
        final ExpectedOutputAttributes expectedOutputAttributes1 = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add(ALARM_NUMBER);
        expectedOutputAttributes1.setOutputAttributes(outputAttributes);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        final List<String> sortAttributes = new ArrayList<String>();
        sortAttributes.add(FDN);
        sortAttributes.add(EVENT_TIME);
        compositeNodeCriteria.setSortAttributes(sortAttributes);
        when(compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria)).thenReturn(restrictions);
        final AlarmAttributeResponse alarmQueryResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());
    }

    @Test
    public void testGetAlarms_CompositeNodeCriteriaWithOutOutputAttributes_AlarmAttributeResponse() {
        when(queryExecutor.executeCount(typeQuery)).thenReturn(2L);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        compositeNodeCriteria.setSortDirection(SortingOrder.DESCENDING);
        when(responseBuilder.buildAttributeResponse(alarmRecords)).thenReturn(alarmAttributeResponse);
        when(compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria)).thenReturn(restrictions);
        when(alarmReader.getAlarmRecords(typeQuery, false)).thenReturn(alarmRecords);
        alarmAttributeResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, null, new ArrayList<String>());
        assertEquals(1, alarmAttributeResponse.getAlarmRecords().size());

    }

    @Test
    public void testGetAlarms_CompositeNodeCriteriaWithDynamicAttributes_ASCENDING() {

        when(queryExecutor.executeCount(typeQuery)).thenReturn(2L);
        final ExpectedOutputAttributes expectedOutputAttributes1 = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add(ALARM_NUMBER);
        expectedOutputAttributes1.setOutputAttributes(outputAttributes);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        when(compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeNodeCriteria)).thenReturn(restrictions);
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        when(responseBuilder.buildAttributeResponse(alarmRecords)).thenReturn(new AlarmAttributeResponse(alarmRecords, SUCCESS));
        final List<String> dynamicAttributes = new ArrayList<String>();
        dynamicAttributes.add("fmx1");
        compositeNodeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("fmx1");
        AlarmAttributeResponse alarmQueryResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes1,
                dynamicAttributes);
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeNodeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("presentSeverity");
        alarmQueryResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes1, new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeNodeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("previousSeverity");
        alarmQueryResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes1, new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());
    }

    @Test
    public void testGetAlarms_CompositeNodeCriteriaWithDynamicAttributes() {
        final ExpectedOutputAttributes expectedOutputAttributes1 = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add(ALARM_NUMBER);
        expectedOutputAttributes1.setOutputAttributes(outputAttributes);
        compositeNodeCriteria.getAlarmSortCriteria().get(0).setSortOrder(SortingOrder.DESCENDING);
        compositeNodeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("presentSeverity");
        AlarmAttributeResponse alarmQueryResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeNodeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("previousSeverity");
        alarmQueryResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes1, new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeNodeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("specificProblem");
        alarmQueryResponse = compositeNodeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes1, new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());
    }

}
