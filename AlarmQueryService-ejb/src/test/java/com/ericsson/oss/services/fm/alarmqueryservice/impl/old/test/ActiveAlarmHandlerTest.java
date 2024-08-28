
package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.test;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARM_OPERATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CLEARED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CRITICAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENT_POID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.INDETERMINATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LASTUPDATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MAJOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MINOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OBJECTOFREFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PRESENTSEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROBLEMDETAIL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROBLEMTEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.WARNING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.ericsson.oss.itpf.datalayer.dps.exception.model.NotDefinedInModelException;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.ObjectField;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.SortDirection;
import com.ericsson.oss.itpf.datalayer.dps.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryResponse;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;
import com.ericsson.oss.services.alarm.query.service.models.NodeMatchType;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.ActiveAlarmHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.DPSAlarmSearchHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.HistoryCommentsHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class ActiveAlarmHandlerTest {

    private static final String NE_LTE35ERBS0001 = "NetworkElement=LTE35ERBS0001";

    @InjectMocks
    ActiveAlarmHandler activeAlarmHandler;

    @Mock
    AlarmQueryData alarmQueryData;

    @Mock
    DPSAlarmSearchHandler dpsAlarmSearchHandler;

    @Mock
    HistoryCommentsHandler historyCommentsHandler;

    @Mock
    ActiveAlarmHandler mockdpsHandler;

    @Mock
    SystemRecorder systemRecorder;

    @Mock
    DataBucket liveBucket;

    @Mock
    DataPersistenceService dataPersistenceService;

    @Mock
    PersistenceObject persistenceObject;

    @Mock
    QueryBuilder queryBuilder;

    @Mock
    Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    QueryExecutor queryExecutor;

    @Mock
    Restriction restriction;

    @Mock
    Projection projection;

    @Mock
    ProjectionBuilder projectionBuilder;

    @Mock
    RestrictionBuilder restrictionBuilder;

    @Mock
    AlarmRecord alarmRecord;

    @Mock
    ManagedObject managedObject;

    @Mock
    NotDefinedInModelException modelException;

    @Mock
    Iterator<Object> iterator;

    @Mock
    AlarmQueryResponse alarmQueryResponse;

    @Mock
    Restriction finalRestriction;

    @Mock
    private DPSProxy dpsProxy;

    String fdn = "NetworkElement=1";
    String fdn1 = "NetworkElement=2";

    @Before
    public void setUp() {
        when(dpsProxy.getService()).thenReturn(dataPersistenceService);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
    }

    public void endSetUp() {
        final Map<String, Object> openAlarmMap = new HashMap<String, Object>();
        openAlarmMap.put("alarmState", "ACTIVE_ACKNOWLEDGED");
        openAlarmMap.put("objectOfReference", "MeContext=1,ManagedElement=1,ENodeBFunction=1");
        final Iterator<Object> poListIterator = mock(Iterator.class);
        final Iterator<Object> aoListIterator = mock(Iterator.class);
        final Collection<PersistenceObject> alramDescList = mock(Collection.class);

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.hasNext()).thenReturn(true, true, false);
        when(poListIterator.next()).thenReturn(persistenceObject);
        when(alramDescList.size()).thenReturn(1);

        when(persistenceObject.getAllAttributes()).thenReturn(openAlarmMap);

        when(queryBuilder.createTypeQuery(FM, ALARM_OPERATION)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmId", (long) 1234)).thenReturn(restriction);
        when(queryExecutor.execute(typeQuery)).thenReturn(aoListIterator);

        when(aoListIterator.hasNext()).thenReturn(true, true, false);

        when(aoListIterator.next()).thenReturn(persistenceObject);
        when(persistenceObject.getAttribute("operationTime")).thenReturn("time");
        when(persistenceObject.getAttribute("operatorName")).thenReturn("operator");
        when(persistenceObject.getAttribute("commentText")).thenReturn("comment");

    }

    @Test
    public void testFetchAllAlarmsWithNE() {

        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("objectOfReference", fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        endSetUp();
        final List<String> fdnList = new ArrayList<String>();
        fdnList.add(fdn);
        assertNotNull(activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false));
        fdnList.add(fdn1);
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(managedObject);
        assertNotNull(activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false));
    }

    @Test
    public void testFetchAllAlarmsWithNEForNullResponse() {

        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("objectOfReference", fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        endSetUp();
        final List<String> fdnList = new ArrayList<String>();
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(managedObject);

        when(alarmQueryResponse.getResponse()).thenReturn(null);
        assertEquals("Success", activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false).getResponse());

    }

    @Test
    public void testFetchAllAlarmsWithNEForEmptyResponse() {

        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("objectOfReference", fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        endSetUp();
        final List<String> fdnList = new ArrayList<String>();
        assertNotNull(activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false));
        fdnList.add(fdn1);
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(managedObject);
        when(alarmQueryResponse.getResponse()).thenReturn("");
        assertEquals("Success", activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false).getResponse());

    }

    @Test
    public void testFetchAllAcknowledgedPos() {
        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("objectOfReference", fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "ACTIVE_ACKNOWLEDGED")).thenReturn(restriction);
        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        endSetUp();
        final List<String> fdnList = new ArrayList<String>();
        fdnList.add(fdn);
        assertNotNull(activeAlarmHandler.fetchAllAcknowledgedAlarms(fdnList, fdnList, false));
        fdnList.add(fdn1);
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(managedObject);
        assertNotNull(activeAlarmHandler.fetchAllAcknowledgedAlarms(fdnList, fdnList, false));
    }

    @Test
    public void testFetchAllUnAcknowledgedPos() {
        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("objectOfReference", fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "ACTIVE_UNACKNOWLEDGED")).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "CLEARED_UNACKNOWLEDGED")).thenReturn(restriction);
        when(restrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        endSetUp();
        final List<String> fdnList = new ArrayList<String>();
        fdnList.add(fdn);
        assertNotNull(activeAlarmHandler.fetchAllUnAcknowledgedAlarms(fdnList, fdnList, false));
        fdnList.add(fdn1);
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(managedObject);
        assertNotNull(activeAlarmHandler.fetchAllUnAcknowledgedAlarms(fdnList, fdnList, false));
    }

    @Test
    public void testFetchAllBothUnAcknowledgedAndAcknowledgedPos() {
        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("objectOfReference", fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "ACTIVE_ACKNOWLEDGED")).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "ACTIVE_UNACKNOWLEDGED")).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "CLEARED_UNACKNOWLEDGED")).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(persistenceObject.getPoId()).thenReturn(123L);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmId", 123L)).thenReturn(restriction);

        endSetUp();
        final List<String> fdnList = new ArrayList<String>();
        fdnList.add(fdn);
        assertNotNull(activeAlarmHandler.fetchBothAcknowledgedAndUnAcknowledgedAlarms(fdnList, fdnList, false));
        fdnList.add(fdn1);
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(managedObject);
        assertNotNull(activeAlarmHandler.fetchBothAcknowledgedAndUnAcknowledgedAlarms(fdnList, fdnList, false));
    }

    @Test
    public void testFetchpreviousComments() {
        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("objectOfReference", fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "ACTIVE_ACKNOWLEDGED")).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "ACTIVE_UNACKNOWLEDGED")).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmState", "CLEARED_UNACKNOWLEDGED")).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(persistenceObject.getPoId()).thenReturn(123L);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmId", 123L)).thenReturn(restriction);
        final Map<String, Object> openAlarmMap = new HashMap<String, Object>();
        openAlarmMap.put("alarmState", "ACTIVE_ACKNOWLEDGED");

        final Iterator<Object> poListIterator = mock(Iterator.class);
        final Iterator<Object> aoListIterator = mock(Iterator.class);
        final Collection<PersistenceObject> alramDescList = mock(Collection.class);

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.hasNext()).thenReturn(true, true, false);
        when(poListIterator.next()).thenReturn(persistenceObject);

        when(alramDescList.size()).thenReturn(1);

        when(persistenceObject.getAllAttributes()).thenReturn(openAlarmMap);

        when(queryBuilder.createTypeQuery(FM, ALARM_OPERATION)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder().equalTo("alarmId", (long) 1234)).thenReturn(restriction);
        when(queryExecutor.execute(typeQuery)).thenReturn(aoListIterator);

        when(aoListIterator.hasNext()).thenReturn(true, true, false);

        when(aoListIterator.next()).thenReturn(persistenceObject);
        when(persistenceObject.getAttribute("operationTime")).thenReturn("time");
        when(persistenceObject.getAttribute("operatorName")).thenReturn("operator");
        when(persistenceObject.getAttribute("commentText")).thenReturn("comment");

        final Map<String, Object> comments = new HashMap<String, Object>();
        comments.put("operatorName", "AASOperator");
        comments.put("alarmID", 1234567L);
        openAlarmMap.put("alarmState", "ACTIVE_ACKNOWLEDGED");

        when(persistenceObject.getAllAttributes()).thenReturn(comments);
        final List<String> fdnList = new ArrayList<String>();
        fdnList.add(fdn);
        assertNotNull(activeAlarmHandler.fetchBothAcknowledgedAndUnAcknowledgedAlarms(fdnList, fdnList, true));
    }

    @Test
    public void testFetchAllAlarmsFromDb() {
        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAllAlarmsFromdb());
    }

    @Test
    public void testFetchAlarmsWithEventPoIds() {
        final List<Long> eventPoIdList = new ArrayList<Long>();
        eventPoIdList.add(1234L);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.findPoById(1234)).thenReturn(persistenceObject);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIds(eventPoIdList, false));
    }

    @Test
    public void testFetchAlarmsWithEventPoIdsWithComments() {
        final List<Long> eventPoIdList = new ArrayList<Long>();
        eventPoIdList.add(1234L);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.findPoById(1234)).thenReturn(persistenceObject);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIds(eventPoIdList, true));
    }

    @Test
    public void testFetchAlarmsWithEventPoIdsWithNodeIds() {
        final List<Long> eventPoIdList = new ArrayList<Long>();
        eventPoIdList.add(1234L);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        final List<PersistenceObject> persistenceObjects = new ArrayList<PersistenceObject>();
        persistenceObjects.add(persistenceObject);
        when(liveBucket.findPosByIds(eventPoIdList)).thenReturn(persistenceObjects);
        final Map<String, Object> attributeMap = new HashMap<String, Object>();
        attributeMap.put("objectOfReference", "MeContext=1,ManagedElement=1,ENodeBFunction=1");
        attributeMap.put("visibility", true);
        when(persistenceObject.getAllAttributes()).thenReturn(attributeMap);
        when(persistenceObject.getAttribute("visibility")).thenReturn(true);
        when(alarmRecord.getSpecificProblem()).thenReturn("specificProblem");
        when(alarmRecord.getAdditionalInformation()).thenReturn("additionalInformation");

        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(alarmRecord.getObjectOfReference()).thenReturn("MeContext=1,ManagedElement=1,ENodeBFunction=1");
        when(liveBucket.findMoByFdn("MeContext=1,ManagedElement=1")).thenReturn(managedObject);

        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIdsWithNodeIds(eventPoIdList, false));
    }

    @Test
    public void testFetchAlarmsWithEventPoIdsWithNoNode() {
        final List<Long> eventPoIdList = new ArrayList<Long>();
        eventPoIdList.add(1234L);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        final List<PersistenceObject> persistenceObjects = new ArrayList<PersistenceObject>();
        persistenceObjects.add(persistenceObject);
        when(liveBucket.findPosByIds(eventPoIdList)).thenReturn(persistenceObjects);
        when(persistenceObject.getAttribute("visibility")).thenReturn(true);
        when(alarmRecord.getSpecificProblem()).thenReturn("specificProblem");
        when(alarmRecord.getAdditionalInformation()).thenReturn("additionalInformation");

        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(alarmRecord.getObjectOfReference()).thenReturn("MeContext=1,ManagedElement=1,ENodeBFunction=1");

        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIdsWithNodeIds(eventPoIdList, false));
    }

    @Test
    public void testFetchAlarmsWithEventPoIdsTrueComment() {

        final List<Long> eventPoIdList = new ArrayList<Long>();
        eventPoIdList.add(1234L);
        final List<PersistenceObject> persistenceObjects = new ArrayList<PersistenceObject>();
        persistenceObjects.add(persistenceObject);
        when(liveBucket.findPosByIds(eventPoIdList)).thenReturn(persistenceObjects);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.findPoById(1234)).thenReturn(persistenceObject);
        when(persistenceObject.getAttribute("visibility")).thenReturn(true);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        endSetUp();

        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIds(eventPoIdList, true));
    }

    @Test
    public void testFetchAlarmsWithEventPoIdsWithNoAlarms() {
        final List<Long> eventPoIdList = new ArrayList<Long>();
        eventPoIdList.add(1234L);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.findPoById(1234)).thenReturn(null);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIds(eventPoIdList, false));

        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIds(eventPoIdList, true));
    }

    @Test
    public void testFetchAlarmsWithEventPoIdsWithWrongPoId() {
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.findPoById(1234)).thenReturn(null);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIds(null, false));
    }

    @Test
    public void testFetchAllPoIdsFromDb() {
        setUp();

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        endSetUp();
        assertNotNull(activeAlarmHandler.fetchAllPoIdsFromdb());
    }

    @Test
    public void testFetchRecentlyUpdatedAlarms() {

        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);

        final String oor1 = "MeContext=LTE09ERBS01";
        final String oor2 = "MeContext=LTE09ERBS02";
        final List<String> oors = new ArrayList<String>();
        oors.add(oor1);
        oors.add(oor2);

        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add("objectOfReference");
        outputAttributes.add("specificProblem");
        outputAttributes.add("probableCause");
        outputAttributes.add("alarmNumber");
        outputAttributes.add("presentSeverity");
        outputAttributes.add("commentText");
        outputAttributes.add("eventTime");

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo(FDN, fdn)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(FDN, fdn1)).thenReturn(restriction);

        when(typeRestrictionBuilder.matchesString(OOR, oor1, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.matchesString(OOR, oor2, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        final Iterator<Object> poListIterator = mock(Iterator.class);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.hasNext()).thenReturn(true, true, false);
        when(poListIterator.next()).thenReturn(persistenceObject);
        new HashMap<String, Object>();

        assertNotNull(activeAlarmHandler.fetchRecentlyUpdatedAlarms(nodes, oors, null, null, outputAttributes));

    }

    @Test
    public void testFetchRecentlyUpdatedAlarmsForManyNodes() {

        final List<String> nodes = new ArrayList<String>();
        final String fdnValue = "MeContext=";
        for (int i = 1; i < 400; i++) {
            final Integer value1 = i;
            final String value2 = value1.toString();
            final String newFdn = fdnValue + value2;
            nodes.add(newFdn);
        }

        final String oor1 = "MeContext=LTE09ERBS01";
        final String oor2 = "MeContext=LTE09ERBS02";
        final List<String> oors = new ArrayList<String>();
        oors.add(oor1);
        oors.add(oor2);

        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add("objectOfReference");
        outputAttributes.add("specificProblem");
        outputAttributes.add("probableCause");
        outputAttributes.add("alarmNumber");
        outputAttributes.add("presentSeverity");
        outputAttributes.add("commentText");
        outputAttributes.add("eventTime");

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo(FDN, fdn)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(FDN, fdn1)).thenReturn(restriction);

        when(typeRestrictionBuilder.matchesString(OOR, oor1, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.matchesString(OOR, oor2, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(VISIBILITY, true)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction, restriction)).thenReturn(restriction);

        final Iterator<Object> poListIterator = mock(Iterator.class);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.hasNext()).thenReturn(true, true, false);
        when(poListIterator.next()).thenReturn(persistenceObject);
        new HashMap<String, Object>();

        assertNotNull(activeAlarmHandler.fetchRecentlyUpdatedAlarms(nodes, oors, null, null, outputAttributes));
    }

    @Test
    public void testFetchAlarmsWithEventPoIdsWithProjection() {

        final List<Long> poIds = new ArrayList<Long>();
        poIds.add(1234L);
        poIds.add(5678L);

        final Boolean previousCommentsRequired = false;
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add("eventPoId");
        outputAttributes.add("objectOfReference");
        outputAttributes.add("specificProblem");
        outputAttributes.add("probableCause");
        outputAttributes.add("alarmNumber");
        outputAttributes.add("presentSeverity");
        outputAttributes.add("commentText");
        outputAttributes.add("eventTime");
        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.in(ObjectField.PO_ID, poIds.toArray())).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo(VISIBILITY, true)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(finalRestriction);

        final Object[] attributes = { 1234L, "NetworkElement=LTE35ERBS0001", "specificProblemTest", "probableCauseTest", 1234L, "CRITICAL",
                "commentTest", new Date() };
        final List<Object[]> attributeArray = new ArrayList<Object[]>();
        attributeArray.add(attributes);

        when(
                queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Matchers.anyObject(), (Projection) Matchers.anyObject(),
                        (Projection[]) Matchers.anyVararg())).thenReturn(attributeArray);

        assertNotNull(activeAlarmHandler.fetchAlarmsWithEventPoIds(poIds, previousCommentsRequired, outputAttributes));
    }

    @Test
    public void testFetchRecentlyUpdatedAlarmsForDates() {
        final List<Date> dates = new ArrayList<Date>();
        final Date date1 = new Date(12345668L);
        final Date date2 = new Date(12345585L);
        dates.add(date1);
        dates.add(date2);

        final List<Long> poIds = new ArrayList<Long>();
        poIds.add(1234L);
        poIds.add(5678L);

        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add("objectOfReference");
        outputAttributes.add("specificProblem");
        outputAttributes.add("probableCause");
        outputAttributes.add("alarmNumber");
        outputAttributes.add("presentSeverity");
        outputAttributes.add("commentText");
        outputAttributes.add("eventTime");

        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(dpsAlarmSearchHandler.getDateRestriction(LASTUPDATED, dates, DateOperator.BETWEEN, typeQuery)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo(VISIBILITY, true)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(finalRestriction);

        final Object[] projectionAttributes = { "NetworkElement=LTE35ERBS0001", "specificProblemTest", "probableCauseTest", 1234L, "CRITICAL",
                "commentTest", new Date() };
        final List<Object[]> attributeArray = new ArrayList<Object[]>();
        attributeArray.add(projectionAttributes);

        when(
                queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Matchers.anyObject(), (Projection) Matchers.anyObject(),
                        (Projection[]) Matchers.anyVararg())).thenReturn(attributeArray);

        assertNotNull(activeAlarmHandler.fetchRecentlyUpdatedAlarms(null, null, null, dates, outputAttributes));
    }

    @Test
    public void testFetchRecentlyUpdatedAlarmsForAttribtues() {
        setUp();
        final List<String> attributes = new ArrayList<String>();
        attributes.add("fake1");
        attributes.add("fake2");

        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add("objectOfReference");
        outputAttributes.add("specificProblem");
        outputAttributes.add("probableCause");
        outputAttributes.add("alarmNumber");
        outputAttributes.add("presentSeverity");
        outputAttributes.add("commentText");
        outputAttributes.add("eventTime");

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.equalTo(VISIBILITY, false)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction)).thenReturn(restriction);

        assertNotNull(activeAlarmHandler.fetchRecentlyUpdatedAlarms(null, null, attributes, null, outputAttributes));
    }

    @Test
    public void testFetchPoIds() {

        final String oor1 = "MeContext=LTE09ERBS01";
        final List<Object> poIds = new ArrayList<Object>();
        poIds.add(123L);
        poIds.add(345L);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(typeRestrictionBuilder.matchesString(FDN, fdn1, StringMatchCondition.CONTAINS)).thenReturn(restriction);

        when(typeRestrictionBuilder.matchesString(OOR, oor1, StringMatchCondition.CONTAINS)).thenReturn(restriction);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, "visibility", true, "=")).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        when(queryExecutor.executeProjection(typeQuery, poIdProjection)).thenReturn(poIds);
        assertNotNull(activeAlarmHandler.fetchPoIds(fdn1, oor1, NodeMatchType.CONTAINS));

    }

    @Test
    public void testFetchPoIdsWithOnlyOor() {

        final String oor1 = "MeContext=LTE09ERBS01";
        final List<Object> poIds = new ArrayList<Object>();
        poIds.add(123L);
        poIds.add(345L);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(typeRestrictionBuilder.matchesString(OOR, oor1, StringMatchCondition.CONTAINS)).thenReturn(restriction);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        when(dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, "visibility", true, "=")).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        when(queryExecutor.executeProjection(typeQuery, poIdProjection)).thenReturn(poIds);
        assertNotNull(activeAlarmHandler.fetchPoIds(fdn1, oor1, NodeMatchType.CONTAINS));

    }

    @Test
    public void testFetchAllAlarmsFromDbInBatches() {

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        typeQuery.addSortingOrder("insertTime", SortDirection.DESCENDING);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        final List<Date> dateList = new ArrayList<Date>();
        dateList.add(new Date());

        when(dpsAlarmSearchHandler.getDateRestriction("insertTime", dateList, DateOperator.LE, typeQuery)).thenReturn(restriction);

        final List<Object> poIdList = new ArrayList<Object>();// (ArrayList.class);

        final String oor1 = "MeContext=LTE09ERBS01";
        final String oor2 = "MeContext=LTE09ERBS02";
        final List<String> oors = new ArrayList<String>();
        oors.add(oor1);
        oors.add(oor2);
        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add("CRITICAL");
        alarmAttributes.add("MAJOR");

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.executeProjection(typeQuery, projection)).thenReturn(poIdList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.equalTo(FDN, fdn)).thenReturn(restriction);
        when(restrictionBuilder.anyOf((Restriction) Matchers.anyObject(), (Restriction) Matchers.isNull())).thenReturn(restriction);
        final List<Long> poIdsList = new ArrayList<Long>();
        final List<PersistenceObject> persistanceObjects = new ArrayList<PersistenceObject>();
        persistanceObjects.add(persistenceObject);
        persistanceObjects.add(persistenceObject);
        poIdList.add(1234657L);
        poIdList.add(4564354L);
        when(liveBucket.findPosByIds(poIdsList)).thenReturn(persistanceObjects);
        poIdList.add(1234657L);
        poIdList.add(4564354L);
        poIdList.add(1545647L);
        poIdList.add(2545354L);

        when(queryExecutor.executeProjection((Query<?>) Matchers.anyObject(), (Projection) Matchers.anyObject())).thenReturn(poIdList);
    }

    @Test
    public void testFetchAllAlarmsFromDbInBatchesForFirstRecordTime() {

        setUp();
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
    }

    @Test
    public void testFetchAlarmsCount() {
        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo(FDN, fdn)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(FDN, fdn1)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo("presentSeverity", "INDETERMINATE")).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.executeCount(typeQuery)).thenReturn((long) 123456);

        assertNotNull(activeAlarmHandler.fetchAlarmsCount(nodes));

    }

    @Test
    public void testFetchAlarms() {

        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);
        final String oor1 = "MeContext=LTE09ERBS01";
        final String oor2 = "MeContext=LTE09ERBS02";
        final List<String> oors = new ArrayList<String>();
        oors.add(oor1);
        oors.add(oor2);
        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add("CRITICAL");
        alarmAttributes.add("MAJOR");

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        assertNotNull(activeAlarmHandler.fetchAlarms(nodes, oors, alarmAttributes, false));

    }

    @Test
    public void testFetchPoIdsBasedFilters() {
        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);

        final List<String> oors = new ArrayList<String>();
        nodes.add("MeContext=1");

        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add("CRITICAL");
        alarmAttributes.add("MAJOR");

        final List<Object> poIds = new ArrayList<Object>();
        poIds.add(1234L);
        poIds.add(5678L);

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo(FDN, fdn)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(FDN, fdn1)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        when(queryExecutor.executeProjection(typeQuery, poIdProjection)).thenReturn(poIds);

        assertNotNull(activeAlarmHandler.fetchPoIdsBasedOnFilters(nodes, oors, alarmAttributes));

    }

    @Test
    public void testFetchPoIdsBasedFiltersWithSortCriteria() {
        final String sortAttribute = "specificProblem";
        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);

        final List<String> oors = new ArrayList<String>();
        nodes.add("MeContext=1");

        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add("CRITICAL");
        alarmAttributes.add("MAJOR");

        final List<Object[]> data = new ArrayList<Object[]>();
        final Object[] obj = new Object[2];
        obj[0] = 1234L;
        obj[1] = sortAttribute;
        data.add(obj);

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo(FDN, fdn)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(FDN, fdn1)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        final Projection sortAttributeProjection = ProjectionBuilder.attribute(sortAttribute);
        when(queryExecutor.executeProjection(typeQuery, poIdProjection, sortAttributeProjection)).thenReturn(data);

        assertNotNull(activeAlarmHandler.fetchPoIdsBasedOnFilters(nodes, oors, alarmAttributes, sortAttribute, "asc"));

    }

    @Test
    public void testFetchAlarmNumbersAndObjectOfRefrences() {
        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);

        final List<String> oors = new ArrayList<String>();
        nodes.add("MeContext=1");

        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add("CRITICAL");
        alarmAttributes.add("MAJOR");

        final List<Object> poIds = new ArrayList<Object>();
        poIds.add(1234L);
        poIds.add(5678L);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo(FDN, fdn)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo(FDN, fdn1)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        when(queryExecutor.executeProjection(typeQuery, poIdProjection)).thenReturn(poIds);

        assertNotNull(activeAlarmHandler.fetchAlarmNumbersAndObjectOfRefrences(nodes, oors, alarmAttributes));
    }

    @Test
    public void TestGetAlarmsForCLI() {
        setUp();
        final List<String> nodes = new ArrayList<String>();
        final List<String> oors = new ArrayList<String>();
        final List<String> alarmAttributes = new ArrayList<String>();
        final List<String> outputAttributes = new ArrayList<String>();
        final Object[] attributes = { "123456789", NE_LTE35ERBS0001, "specificProblemTest", "probableCauseTest", 1234L, CRITICAL, "commentTest",
                new Date() };
        final List<Object[]> attributeArray = new ArrayList<Object[]>();
        final String oor = "MeContext=1";
        final String oor2 = "MeContext=2";
        nodes.add(fdn);
        nodes.add(fdn1);
        oors.add(oor);
        oors.add(oor2);
        alarmAttributes.add(CRITICAL);
        alarmAttributes.add(MAJOR);
        outputAttributes.add(OBJECTOFREFERENCE);
        outputAttributes.add(ADDITIONAL_INFORMATION);
        outputAttributes.add(PROBLEMTEXT);
        outputAttributes.add(PROBLEMDETAIL);
        outputAttributes.add(EVENT_POID);
        attributeArray.add(attributes);
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeQuery.getRestrictionBuilder().equalTo(FDN, fdn)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo(FDN, fdn1)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().matchesString(OOR, oor, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().matchesString(OOR, oor2, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(
                queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Matchers.anyObject(), (Projection) Matchers.anyObject(),
                        (Projection[]) Matchers.anyVararg())).thenReturn(attributeArray);
        when(liveBucket.findPoById(Matchers.anyLong())).thenReturn(persistenceObject);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        assertNotNull(activeAlarmHandler.getAlarmsForCLI(nodes, oors, alarmAttributes, outputAttributes));
    }

    @Test
    public void testFetchPoIdsForLargeData() {
        setUp();
        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);
        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);

        final List<String> oors = new ArrayList<String>();
        final String oor = "MeContext=1";
        final String oor2 = "MeContext=2";
        oors.add(oor);
        oors.add(oor2);

        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add(CRITICAL);
        alarmAttributes.add(MAJOR);

        final List<Object> poIds = new ArrayList<Object>();
        poIds.add(1234L);
        poIds.add(5678L);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, "dd/MM/yyyy HH:mm:ss")).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().allOf(finalRestriction, restriction)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().in(FDN, nodesArray)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().anyOf(restriction)).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        when(queryExecutor.executeProjection(typeQuery, poIdProjection)).thenReturn(poIds);
        assertNotNull(activeAlarmHandler.fetchPoIdsForLargeData(nodes, oors, alarmAttributes, null));

    }

    @Test
    public void testFetchPoIdsForLargeData_NodesMoreThan300() {
        setUp();
        final List<String> nodes = new ArrayList<String>();
        for (int i = 0; i <= 400; i++) {
            nodes.add(fdn + i);
        }

        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);

        final List<String> oors = new ArrayList<String>();
        final String oor = "MeContext=1";
        final String oor2 = "MeContext=2";
        oors.add(oor);
        oors.add(oor2);

        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add(CRITICAL);
        alarmAttributes.add(MAJOR);

        final List<Object> poIds = new ArrayList<Object>();
        poIds.add(1234L);
        poIds.add(5678L);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, "dd/MM/yyyy HH:mm:ss")).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().allOf(finalRestriction, restriction)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().in(FDN, nodesArray)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().anyOf(restriction)).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        when(queryExecutor.executeProjection(typeQuery, poIdProjection)).thenReturn(poIds);
        assertNotNull(activeAlarmHandler.fetchPoIdsForLargeData(nodes, oors, alarmAttributes, null));

    }

    @Test
    public void testfetchSeveritiesForLargeData() {
        setUp();
        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);

        final List<Object> severites = new ArrayList<Object>();
        severites.add(CRITICAL);
        severites.add(MAJOR);
        severites.add(MINOR);
        severites.add(WARNING);
        severites.add(INDETERMINATE);
        severites.add(CLEARED);

        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeQuery.getRestrictionBuilder().in(FDN, nodesArray)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().anyOf(restriction)).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection severityProjection = ProjectionBuilder.attribute(PRESENTSEVERITY);
        when(queryExecutor.executeProjection(typeQuery, severityProjection)).thenReturn(severites);
        assertNotNull(activeAlarmHandler.fetchSeveritiesForLargeData(nodes, null));
    }

    @Test
    public void testfetchSeveritiesForLargeData_ForNodesGreaterThan1500() {
        setUp();
        final List<String> nodes = new ArrayList<String>();

        for (int i = 0; i < 1550; i++) {
            nodes.add("NetworkElement =" + i);
        }

        final List<String> nodes_sublist = new ArrayList<String>();

        for (int i = 0; i < 300; i++) {
            nodes_sublist.add("NetworkElement =" + i);
        }

        final List<Object> severities = new ArrayList<Object>();
        severities.add(CRITICAL);
        severities.add(MAJOR);
        severities.add(MINOR);
        severities.add(WARNING);
        severities.add(INDETERMINATE);
        severities.add(CLEARED);
        final List<String> expectedResult = new ArrayList<String>();
        final Object[] nodesArray = nodes_sublist.toArray(new String[nodes_sublist.size()]);
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeQuery.getRestrictionBuilder().in(FDN, nodesArray)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().anyOf(restriction)).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection severityProjection = ProjectionBuilder.attribute(PRESENTSEVERITY);
        when(typeQuery.getRestrictionBuilder().in(FDN, nodesArray)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().anyOf(restriction)).thenReturn(restriction);
        when(queryExecutor.executeProjection(typeQuery, severityProjection)).thenReturn(severities);
        when(activeAlarmHandler.fetchSeveritiesForLargeData(nodes, null)).thenReturn(expectedResult);
        assertNotNull(activeAlarmHandler.fetchSeveritiesForLargeData(nodes, null));
    }

    @Test
    public void testFetchPoIdsForLargeData_NodesGreaterThan1500() {
        setUp();
        final List<String> nodes = new ArrayList<String>();
        for (int i = 0; i < 1550; i++) {
            nodes.add("NetworkElement =" + i);
        }
        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);

        final List<String> oors = new ArrayList<String>();
        final String oor = "MeContext=1";
        final String oor2 = "MeContext=2";
        oors.add(oor);
        oors.add(oor2);

        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add(CRITICAL);
        alarmAttributes.add(MAJOR);

        final List<Object> poIds = new ArrayList<Object>();
        poIds.add(1234L);
        poIds.add(5678L);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, "dd/MM/yyyy HH:mm:ss")).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().allOf(finalRestriction, restriction)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().in(FDN, nodesArray)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().anyOf(restriction)).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        when(queryExecutor.executeProjection(typeQuery, poIdProjection)).thenReturn(poIds);
        assertNotNull(activeAlarmHandler.fetchPoIdsForLargeData(nodes, oors, alarmAttributes, null));

    }

    @Test
    public void testFetchAlarmsException() {
        final List<String> nodes = new ArrayList<String>();
        nodes.add(fdn);
        nodes.add(fdn1);
        final String oor1 = "MeContext=LTE09ERBS01";
        final String oor2 = "MeContext=LTE09ERBS02";
        final List<String> oors = new ArrayList<String>();
        oors.add(oor1);
        oors.add(oor2);
        final List<String> alarmAttributes = new ArrayList<String>();
        alarmAttributes.add("CRITICAL");
        alarmAttributes.add("MAJOR");

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);

        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeQuery.getRestrictionBuilder().equalTo(OOR, oor1)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().equalTo(FDN, fdn1)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().matchesString(FDN, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder().matchesString(OOR, oor2, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, "dd/MM/yyyy HH:mm:ss")).thenReturn(restriction);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(persistenceObject);
        assertNotNull(activeAlarmHandler.fetchAlarms(nodes, oors, alarmAttributes, true));

    }

}