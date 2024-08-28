package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class PoIdCriteriaHandlerTest {
    @InjectMocks
    private AlarmPoIdCriteriaHandler poIdCriteriaHandler;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private DataPersistenceService dps;

    @Mock
    private PersistenceObject persistenceObject;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private AlarmReader alarmReader;

    @Mock
    private AlarmAttributeResponseBuilder responseBuilder;

    @Mock
    private ManagedObject managedObject;

    @Mock
    private DPSProxy dpsProxy;

    private final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
    private final AlarmPoIdCriteria poIdCriteria = new AlarmPoIdCriteria();
    private final List<String> outputAttributes = new ArrayList<String>();
    private final List<Long> poIds = new ArrayList<Long>();
    private final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
    private final Map<String, Object> alarmAttributeMap = new HashMap<String, Object>();
    private final List<PersistenceObject> persistanceObjects = new ArrayList<PersistenceObject>();
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private AlarmAttributeResponse alarmQueryResponse = null;

    @Before
    public void setUp() {
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);
        when(dps.getLiveBucket()).thenReturn(liveBucket);
        when(dpsProxy.getService()).thenReturn(dps);
        when(queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(liveBucket.findMoByFdn(null)).thenReturn(managedObject);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        alarmAttributeMap.put(OBJECT_OF_REFERENCE, "LTE1,LTE2");
        alarmAttributeMap.put(ALARM_ID, 333L);
        alarmAttributeMap.put(EVENT_PO_ID, 11L);
        final AlarmRecord alarmRecord = new AlarmRecord(alarmAttributeMap, null, null);

        poIds.add(111L);
        poIds.add(222L);
        poIdCriteria.setPoIds(poIds);
        outputAttributes.add(FDN);
        outputAttributes.add(OBJECT_OF_REFERENCE);
        alarmRecords.add(alarmRecord);
        alarmQueryResponse = new AlarmAttributeResponse(alarmRecords, SUCCESS);
    }

    @Test
    public void testGetAlarms_PoIdCriteria_Alarms() {
        setUp();
        persistanceObjects.add(persistenceObject);

        expectedOutputAttributes.setNodeIdRequired(true);
        expectedOutputAttributes.setCommentHistoryRequired(true);
        expectedOutputAttributes.setOutputAttributes(outputAttributes);
        expectedOutputAttributes.setNodeIdRequired(false);

        attributes.put(FDN, "NetworkElement=LTE01ERBS001");
        attributes.put(OBJECT_OF_REFERENCE, "MeContext=LTE09ERBS00006,MeContext=LTE09ERBS00004");

        when(liveBucket.findPosByIds(poIds)).thenReturn(persistanceObjects);

        when(persistenceObject.getAllAttributes()).thenReturn(attributes);
        when(alarmReader.getAlarmRecordsWithPoIds(poIds, true, false, outputAttributes)).thenReturn(alarmRecords);
        when(responseBuilder.buildAttributeResponse(alarmRecords)).thenReturn(alarmQueryResponse);

        final AlarmAttributeResponse alarmQueryResponse = poIdCriteriaHandler.getAlarms(poIdCriteria, expectedOutputAttributes);
        assertEquals(SUCCESS, alarmQueryResponse.getResponse());

    }

    @Test
    public void testGetAlarmsForAdditionalAttributeSearchSort() {
        setUp();
        poIdCriteriaHandler.getAlarmsForAdditionalAttributeSearchSort(poIdCriteria, expectedOutputAttributes);
    }
}
