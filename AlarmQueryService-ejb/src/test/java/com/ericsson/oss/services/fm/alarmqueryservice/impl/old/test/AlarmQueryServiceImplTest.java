
package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.alarm.query.service.models.AlarmLogData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryResponse;
import com.ericsson.oss.services.alarm.query.service.models.NodeMatchType;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.ActiveAlarmHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.AlarmQueryServiceImpl;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.DPSAlarmSearchHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class AlarmQueryServiceImplTest {

    @InjectMocks
    AlarmQueryServiceImpl alarmQueryServiceImpl;

    @Mock
    ActiveAlarmHandler activeAlarmHandler;

    @Mock
    DPSAlarmSearchHandler dpsAlarmSearchHandler;

    @Mock
    AlarmQueryResponse alarmQueryResponse;

    @Mock
    SystemRecorder systemRecorder;

    @Mock
    AlarmLogData alarmLogData;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private DataPersistenceService dataPersistenceService;

    AlarmQueryData alarmQueryData = new AlarmQueryData();
    List<String> fdnList = new ArrayList<String>();
    List<String> objectOfReferences = new ArrayList<String>();

    String fdn = "NetworkElement=1";
    String objectOfReference = "MeContext=1";

    @Before
    public void setUp() {
        when(dpsProxy.getService()).thenReturn(dataPersistenceService);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
    }

    @Test
    public void testGetAlarmListAck() {
        alarmQueryData.setAck(true);
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");
        objectOfReferences.add(objectOfReference);
        alarmQueryData.setNodes(fdnList);

        alarmQueryData.setNodes(fdnList);
        objectOfReferences.add(objectOfReference);
        objectOfReferences.add("MeContext=2");
        alarmQueryData.setObjectOfReferences(objectOfReferences);
        when(activeAlarmHandler.fetchAllAcknowledgedAlarms(fdnList, objectOfReferences, false)).thenReturn(alarmQueryResponse);

        assertNotNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testGetAlarmListUnAck() {
        // List<AlarmRecord> alarmObjectList = mock(ArrayList.class);
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");
        alarmQueryData.setUnAck(true);

        alarmQueryData.setNodes(fdnList);
        objectOfReferences.add(objectOfReference);
        objectOfReferences.add("MeContext=2");
        alarmQueryData.setObjectOfReferences(objectOfReferences);

        when(activeAlarmHandler.fetchAllUnAcknowledgedAlarms(fdnList, objectOfReferences, false)).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testGetAlarmListForAckAndUnack() {
        // List<AlarmRecord> alarmObjectList = mock(ArrayList.class);
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");
        alarmQueryData.setUnAck(true);
        alarmQueryData.setAck(true);
        alarmQueryData.setNodes(fdnList);

        alarmQueryData.setNodes(fdnList);
        objectOfReferences.add(objectOfReference);
        objectOfReferences.add("MeContext=2");
        alarmQueryData.setObjectOfReferences(objectOfReferences);

        when(activeAlarmHandler.fetchBothAcknowledgedAndUnAcknowledgedAlarms(fdnList, objectOfReferences, false)).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testGetAlarmListForAckAsFalseAndUnackAsFalse() {
        // List<AlarmRecord> alarmObjectList = mock(ArrayList.class);
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");
        alarmQueryData.setUnAck(false);
        alarmQueryData.setAck(false);
        alarmQueryData.setNodes(fdnList);

        alarmQueryData.setNodes(fdnList);
        objectOfReferences.add(objectOfReference);
        objectOfReferences.add("MeContext=2");
        alarmQueryData.setObjectOfReferences(objectOfReferences);

        when(activeAlarmHandler.fetchBothAcknowledgedAndUnAcknowledgedAlarms(fdnList, objectOfReferences, false)).thenReturn(alarmQueryResponse);
        assertNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testGetAlarmsList() {
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");
        // List<AlarmRecord> alarmObjectList = mock(ArrayList.class);
        alarmQueryData.setNodes(fdnList);
        when(activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false)).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testGetAlarmsListWithNodeListAsNull() {

        // List<AlarmRecord> alarmObjectList = mock(ArrayList.class);
        alarmQueryData.setNodes(null);
        when(activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false)).thenReturn(alarmQueryResponse);
        assertNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testGetAlarmsListWithNodeListAsEmpty() {

        // List<AlarmRecord> alarmObjectList = mock(ArrayList.class);
        alarmQueryData.setNodes(new ArrayList<String>());
        when(activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false)).thenReturn(alarmQueryResponse);
        assertNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testGetAlarmsLisWithAlarmAttributes() {
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");
        // List<AlarmRecord> alarmObjectList = mock(ArrayList.class);
        alarmQueryData.setNodes(fdnList);
        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add("objectOfReference");
        outputAttributes.add("specificProblem");
        outputAttributes.add("probableCause");
        outputAttributes.add("alarmNumber");
        outputAttributes.add("presentSeverity");
        outputAttributes.add("commentText");
        outputAttributes.add("eventTime");
        alarmQueryData.setOutputAttributes(outputAttributes);
        when(activeAlarmHandler.fetchAllAlarmsUnderfdn(fdnList, false)).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testAllAlarmList() {
        alarmQueryData.setAllAlarms(true);
        when(activeAlarmHandler.fetchAllAlarmsFromdb()).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testAllAlarmListForPoId() {
        final List<Long> eventPoIdList = new ArrayList<Long>();
        eventPoIdList.add(123L);
        alarmQueryData.setEventPoIds(eventPoIdList);

        when(activeAlarmHandler.fetchAlarmsWithEventPoIds(alarmQueryData.getEventPoIds(), false)).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testAllAlarmListForPoIdWithNodeId() {
        final List<Long> eventPoIdList = new ArrayList<Long>();
        eventPoIdList.add(123L);
        alarmQueryData.setEventPoIds(eventPoIdList);
        alarmQueryData.setNodeIdRequired(true);
        when(activeAlarmHandler.fetchAlarmsWithEventPoIdsWithNodeIds(alarmQueryData.getEventPoIds(), false)).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getAlarmList(alarmQueryData));
    }

    @Test
    public void testGetAlarmsCount() {
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");
        final Map<String, Long> count = new HashMap<String, Long>();
        count.put("Critical", 2L);
        when(activeAlarmHandler.fetchAlarmsCount(fdnList)).thenReturn(count);
        assertNotNull(alarmQueryServiceImpl.getAlarmsCount(fdnList));
    }

    @Test
    public void testGetAlarmsCountWithNodeListAsNull() {
        final Map<String, Long> count = new HashMap<String, Long>();
        count.put("Critical", 2L);
        when(activeAlarmHandler.fetchAlarmsCount(fdnList)).thenReturn(count);
        assertNotNull(alarmQueryServiceImpl.getAlarmsCount(null));
    }

    @Test
    public void testGetAlarmsCountWithNodeListAsEmpty() {
        final Map<String, Long> count = new HashMap<String, Long>();
        count.put("Critical", 2L);
        when(activeAlarmHandler.fetchAlarmsCount(fdnList)).thenReturn(count);
        assertNotNull(alarmQueryServiceImpl.getAlarmsCount(new ArrayList<String>()));
    }

    @Test
    public void testGetPoIdsList() {
        final List<Long> poIdList = new ArrayList<Long>();
        poIdList.add(1234L);
        when(activeAlarmHandler.fetchAllPoIdsFromdb()).thenReturn(poIdList);
        assertNotNull(alarmQueryServiceImpl.getPoIdsList(null, null, NodeMatchType.ALL));
        when(activeAlarmHandler.fetchPoIds("NetworkElement=1", null, NodeMatchType.CONTAINS)).thenReturn(poIdList);
        assertNotNull(alarmQueryServiceImpl.getPoIdsList("NetworkElement=1", null, NodeMatchType.CONTAINS));

    }

    @Test
    public void testGetAlarmList() {
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");

        when(activeAlarmHandler.fetchAlarms(fdnList, null, null, false)).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getAlarmList(fdnList, null, null, false));
    }

    @Test
    public void testGetRecentlyUpdatedAlarms() {
        fdnList.add(fdn);
        fdnList.add("NetworkElement=1");

        final List<String> outputAttributes = new ArrayList<String>();
        outputAttributes.add("objectOfReference");
        outputAttributes.add("specificProblem");
        outputAttributes.add("probableCause");
        outputAttributes.add("alarmNumber");
        outputAttributes.add("presentSeverity");
        outputAttributes.add("commentText");
        outputAttributes.add("eventTime");

        when(activeAlarmHandler.fetchRecentlyUpdatedAlarms(fdnList, null, null, null, outputAttributes)).thenReturn(alarmQueryResponse);
        assertNotNull(alarmQueryServiceImpl.getRecentlyUpdatedAlarms(fdnList, null, null, null, outputAttributes));
    }

    @Test
    public void testFetchPoIdsBasedFilters() {
        final List<String> nodes = new ArrayList<String>();
        final List<String> oors = new ArrayList<String>();
        final List<String> alarmAttributes = new ArrayList<String>();
        final List<Long> poIds = new ArrayList<Long>();

        when(activeAlarmHandler.fetchPoIdsBasedOnFilters(nodes, oors, alarmAttributes)).thenReturn(poIds);

        assertNotNull(alarmQueryServiceImpl.fetchPoIdsBasedFilters(nodes, oors, alarmAttributes));
    }

    @Test
    public void testGetAlarmNumbersAndObjectOfRefrences() {
        final List<String> nodes = new ArrayList<String>();
        final List<String> oors = new ArrayList<String>();
        final List<String> alarmAttributes = new ArrayList<String>();
        final List<Object[]> alarmNumbersAndoors = new ArrayList<Object[]>();

        when(activeAlarmHandler.fetchAlarmNumbersAndObjectOfRefrences(nodes, oors, alarmAttributes)).thenReturn(alarmNumbersAndoors);

        assertNotNull(alarmQueryServiceImpl.getAlarmNumbersAndObjectOfRefrences(nodes, oors, alarmAttributes));
    }

    @Test
    public void testGetAlarmList1() {
        final boolean authorized = true;
        when(alarmQueryServiceImpl.getAlarmList(alarmQueryData, authorized)).thenReturn(alarmQueryResponse);
    }

}
