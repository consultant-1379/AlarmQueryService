package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
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

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.CompositeEventTimeCriteriaRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.DateRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.LogicalOperatorRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.NodeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CompositeEventTimeCriteriaHandlerTest {

    @InjectMocks
    private CompositeEventTimeCriteriaHandler compositeEventTimeCriteriaHandler;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private DataPersistenceService dps;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private AlarmReader alarmReader;

    @Mock
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Mock
    private CompositeEventTimeCriteriaRestrictionBuilder compositeRestrictionBuilder;

    @Mock
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Mock
    private LogicalOperatorRestrictionBuilder logicalOperationRestrictionBuilder;

    @Mock
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private Restriction restriction;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private AlarmAttributeResponseBuilder responseBuilder;

    private final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
    private final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();
    private AlarmAttributeResponse alarmAttributeResponse = null;
    private final List<String> nodes = new ArrayList<String>();
    private final Date fromDate = new Date();
    private final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
    private final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();
    private final List<String> outputAttributes = new ArrayList<String>();
    private final Map<String, Object> alarmAttributeMap = new HashMap<String, Object>();
    private final List<Map<String, Object>> historyAlarmData = new ArrayList<Map<String, Object>>();
    private final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();

    @Before
    public void setUp() {

        alarmAttributeCriteria.setAttributeName("attributeName");
        alarmAttributeCriteria.setAttributeValue("value");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        outputAttributes.add("alarmNumber");
        nodes.add("LTE01345612");
        alarmAttributeMap.put("eventPoId", 11L);
        final AlarmRecord alarmRecord = new AlarmRecord(alarmAttributeMap, null, null);
        alarmRecords.add(alarmRecord);
        alarmAttributeResponse = new AlarmAttributeResponse(alarmRecords, SUCCESS);
        expectedOutputAttributes.setOutputAttributes(outputAttributes);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);
        compositeEventTimeCriteria.setNodes(nodes);
        compositeEventTimeCriteria.setFromTime(fromDate);
        compositeEventTimeCriteria.setOperator(Operator.EQ);
        final AlarmSortCriterion alarmSortCriterion = new AlarmSortCriterion();
        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();
        alarmSortCriterion.setSortAttribute(FDN);
        alarmSortCriterion.setSortOrder(SortingOrder.ASCENDING);
        alarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        alarmSortCriteria.add(alarmSortCriterion);
        compositeEventTimeCriteria.setAlarmSortCriteria(alarmSortCriteria);
        when(responseBuilder.buildAttributeResponse(alarmRecords)).thenReturn(alarmAttributeResponse);
        when(alarmReader.getAlarmRecordsForSelectedAttributes(typeQuery, outputAttributes)).thenReturn(alarmRecords);
        when(dpsProxy.getService()).thenReturn(dps);
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM)).thenReturn(typeQuery);

        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);

        when(compositeRestrictionBuilder.build(typeQuery, compositeEventTimeCriteria)).thenReturn(restriction);
    }

    @Test
    public void testGetAlarms_compositeEventTimeCriteria_AlarmAttributeResponse() {
        when(queryExecutor.executeCount(typeQuery)).thenReturn(2L);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        when(compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeEventTimeCriteria)).thenReturn(restrictions);

        alarmAttributeResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes,
                new ArrayList<String>());
        assertEquals(1, alarmAttributeResponse.getAlarmRecords().size());

    }

    @Test
    public void testGetAlarms_compositeEventTimeCriteriaWithOutNodes_AlarmAttributeResponse() {
        when(queryExecutor.executeCount(typeQuery)).thenReturn(2L);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        when(compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeEventTimeCriteria)).thenReturn(restrictions);
        when(compositeRestrictionBuilder.build(typeQuery, compositeEventTimeCriteria)).thenReturn(null);
        alarmAttributeResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes,
                new ArrayList<String>());
        assertEquals(1, alarmAttributeResponse.getAlarmRecords().size());

    }

    @Test
    public void testGetAlarms_compositeEventTimeCriteriaWithOutOutputAttributes_AlarmAttributeResponse() {
        when(queryExecutor.executeCount(typeQuery)).thenReturn(2L);
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        compositeEventTimeCriteria.setSortDirection(SortingOrder.DESCENDING);
        when(responseBuilder.buildAttributeResponse(alarmRecords)).thenReturn(alarmAttributeResponse);
        when(compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeEventTimeCriteria)).thenReturn(restrictions);
        when(alarmReader.getAlarmRecords(typeQuery, false)).thenReturn(alarmRecords);
        alarmAttributeResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, null, new ArrayList<String>());
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
        when(compositeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeEventTimeCriteria)).thenReturn(restrictions);
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        when(responseBuilder.buildAttributeResponse(alarmRecords)).thenReturn(new AlarmAttributeResponse(alarmRecords, SUCCESS));
        final List<String> dynamicAttributes = new ArrayList<String>();
        dynamicAttributes.add("fmx1");
        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("fmx1");
        AlarmAttributeResponse alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria,
                expectedOutputAttributes1, dynamicAttributes);
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("presentSeverity");
        alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("previousSeverity");
        alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortOrder(SortingOrder.DESCENDING);
        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("presentSeverity");
        alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("previousSeverity");
        alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("specificProblem");
        alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

    }

    @Test
    public void testGetAlarms_CompositeNodeCriteriaWithDynamicAttributes_DESCNDING() {
        final ExpectedOutputAttributes expectedOutputAttributes1 = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add(ALARM_NUMBER);
        expectedOutputAttributes1.setOutputAttributes(outputAttributes);
        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortOrder(SortingOrder.DESCENDING);
        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("presentSeverity");
        AlarmAttributeResponse alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria,
                expectedOutputAttributes1, new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("previousSeverity");
        alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

        compositeEventTimeCriteria.getAlarmSortCriteria().get(0).setSortAttribute("specificProblem");
        alarmQueryResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes1,
                new ArrayList<String>());
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

    }

}
