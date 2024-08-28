package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder;

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
import org.mockito.Matchers;
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
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.AlarmReader;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CompositeEventTimeRestrictionBuilderTest {

    @InjectMocks
    private CompositeEventTimeCriteriaRestrictionBuilder compositeEventTimeRestrictionBuilder;

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
    private CompositeEventTimeCriteriaRestrictionBuilder CompositeRestrictionBuilder;

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
    private AlarmAttributeResponse alarmQueryResponse = null;
    private final List<String> nodes = new ArrayList<String>();
    private final Date fromDate = new Date();
    private final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
    private final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();
    private final List<String> outputAttributes = new ArrayList<String>();
    private final Map<String, Object> historyAlarmMap = new HashMap<String, Object>();
    private final List<Map<String, Object>> historyAlarmData = new ArrayList<Map<String, Object>>();
    private final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();

    @Before
    public void setUp() {

        alarmAttributeCriteria.setAttributeName(ALARM_NUMBER);
        alarmAttributeCriteria.setAttributeValue(111L);
        alarmAttributeCriteria.setOperator(Operator.EQ);

        otherAlarmAttributes.add(alarmAttributeCriteria);
        outputAttributes.add(ALARM_NUMBER);
        nodes.add("LTE01345612");
        historyAlarmMap.put("eventPoId", 11L);
        final AlarmRecord alarmRecord = new AlarmRecord(historyAlarmMap, null, null);
        alarmRecords.add(alarmRecord);
        alarmQueryResponse = new AlarmAttributeResponse(alarmRecords, SUCCESS);

        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);
        compositeEventTimeCriteria.setNodes(nodes);
        compositeEventTimeCriteria.setFromTime(fromDate);
        compositeEventTimeCriteria.setOperator(Operator.EQ);
        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        when(dpsProxy.getService()).thenReturn(dps);
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM)).thenReturn(typeQuery);

        when(
                logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd((RestrictionBuilder) Matchers.anyObject(),
                        (Restriction) Matchers.anyObject(), (Restriction) Matchers.anyObject())).thenReturn(restriction);
        when(CompositeRestrictionBuilder.build(typeQuery, compositeEventTimeCriteria)).thenReturn(restriction);
    }

    @Test
    public void testBuild_CompositeEventTimeCriteria_AlarmQueryResponse() {

        final Restriction result = compositeEventTimeRestrictionBuilder.build(typeQuery, compositeEventTimeCriteria);
        assertEquals(restriction, result);

    }

    @Test
    public void testBuildNodeRestrictions_CompositeEventTimeCriteria_Alarms() {

        final List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        when(nodeRestrictionBuilder.buildListOfNodeRestrictions(typeQuery.getRestrictionBuilder(), nodes)).thenReturn(restrictions);

        final List<Restriction> result = compositeEventTimeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeEventTimeCriteria);
        assertEquals(1, result.size());
    }
}
