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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.NotDefinedInModelException;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.alarm.query.service.models.AlarmLogData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.DPSAlarmSearchHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants;

@RunWith(MockitoJUnitRunner.class)
public class DPSAlarmSearchHandlerTest {
    @InjectMocks
    DPSAlarmSearchHandler dpsAlarmSearchHandler;

    @Mock
    AlarmQueryData alarmQueryData;

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
    RestrictionBuilder restrictionBuilder;

    @Mock
    AlarmRecord alarmRecord;

    @Mock
    ManagedObject managedObject;

    @Mock
    NotDefinedInModelException modelException;

    String fdn = "MeContext=1,ManagedElement=1,ENodeBFunction=1";
    String fdn1 = "MeContext=2,ManagedElement=1,ENodeBFunction=1";

    public void setUp() {
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery(QueryConstants.FM, QueryConstants.HISTORY_ALARM)).thenReturn(typeQuery);
        when(queryBuilder.createTypeQuery(QueryConstants.FM, QueryConstants.OPEN_ALARM)).thenReturn(typeQuery);

    }

    public void endSetUp() {
        final Map<String, Object> openAlarmMap = new HashMap<String, Object>();
        openAlarmMap.put("alarmState", "ACTIVE_ACKNOWLEDGED");
        final Iterator<Object> poListIterator = mock(Iterator.class);

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.hasNext()).thenReturn(true, true, false);
        when(poListIterator.next()).thenReturn(persistenceObject);
        when(persistenceObject.getAllAttributes()).thenReturn(openAlarmMap);
    }

    @Test
    public void testFetchHistoryAlarmsWithAll() {
        setUp();
        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> alarmAttributes = new ArrayList<>();
        final List<String> nodeList = new ArrayList<>();
        final List<Date> dateList = new ArrayList<>();
        nodeList.add(fdn1);
        nodeList.add(fdn);
        final String attribute1 = "alarmNumber#111#>";
        final String attribute2 = "specificProblem#specificProblem1#=";
        alarmAttributes.add(attribute1);
        alarmAttributes.add(attribute2);
        dateList.add(new Date());
        alarmLogData.setDateAttribute("eventTime");
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setAlarmAttributes(alarmAttributes);
        alarmLogData.setDate(dateList);
        alarmLogData.setNodeList(nodeList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.greaterThan("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo("specificProblem", "specificProblem1")).thenReturn(restriction);
        when(typeRestrictionBuilder.greaterThan("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(managedObject);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn1, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithAllNull() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> alarmAttributes = null;
        final List<String> nodeList = null;
        final List<Date> dateList = null;

        alarmLogData.setDateAttribute("eventTime");
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setAlarmAttributes(alarmAttributes);
        alarmLogData.setDate(dateList);
        alarmLogData.setNodeList(nodeList);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithNodeAndDate() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> nodeList = new ArrayList<>();
        final List<Date> dateList = new ArrayList<>();
        nodeList.add(fdn);
        dateList.add(new Date());
        alarmLogData.setDateAttribute("eventTime");
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setDate(dateList);
        alarmLogData.setNodeList(nodeList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.greaterThan("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithSearchTypeAsOpen() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> nodeList = new ArrayList<>();
        final List<Date> dateList = new ArrayList<>();
        nodeList.add(fdn);
        dateList.add(new Date());
        alarmLogData.setDateAttribute("eventTime");
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setDate(dateList);
        alarmLogData.setNodeList(nodeList);
        alarmLogData.setSearchType("open");
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.greaterThan("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithSearchTypeOtherThanOpen() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> nodeList = new ArrayList<>();
        final List<Date> dateList = new ArrayList<>();
        nodeList.add(fdn);
        dateList.add(new Date());
        alarmLogData.setDateAttribute("eventTime");
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setDate(dateList);
        alarmLogData.setNodeList(nodeList);
        alarmLogData.setSearchType("notopen");
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.greaterThan("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithNodeListAsEmpty() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> nodeList = new ArrayList<>();

        alarmLogData.setNodeList(nodeList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithNodeListAsNull() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> nodeList = null;

        alarmLogData.setNodeList(nodeList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithalarmAttributesAndDate() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> alarmAttributes = new ArrayList<>();
        final List<Date> dateList = new ArrayList<>();
        final String attribute2 = "presentSeverity#CRITICAL#=";
        alarmAttributes.add(attribute2);
        dateList.add(new Date());
        alarmLogData.setDateAttribute("eventTime");
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setAlarmAttributes(alarmAttributes);
        alarmLogData.setDate(dateList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.greaterThan("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.equalTo("presentSeverity", "CRITICAL")).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithNullDate() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();

        final List<Date> dateList = new ArrayList<>();

        dateList.add(new Date());
        alarmLogData.setDateAttribute("eventTime");
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setDate(null);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.greaterThan("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithZeroSizeDate() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<Date> dateList = new ArrayList<>();
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setDate(dateList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    /*
     * @Test public void testFetchHistoryAlarmsWithDateOperatorAsNull() {
     * 
     * setUp();
     * 
     * AlarmLogData alarmLogData = new AlarmLogData(); final List<Date> dateList = new ArrayList<>(); alarmLogData.setDateOperator(null);
     * dateList.add(new Date()); alarmLogData.setDate(dateList);
     * 
     * when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
     * 
     * when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
     * 
     * when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction); when(typeRestrictionBuilder.anyOf(restriction,
     * restriction)).thenReturn(restriction);
     * 
     * when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
     * 
     * endSetUp();
     * 
     * historyAlarmHandler.fetchHistoryAlarms(alarmLogData);
     * 
     * }
     */
    @Test
    public void testFetchHistoryAlarmsWithalarmAttributesAsNull() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> alarmAttributes = null;

        alarmLogData.setAlarmAttributes(alarmAttributes);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithEmptyalarmAttributes() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> alarmAttributes = new ArrayList<>();

        alarmLogData.setAlarmAttributes(alarmAttributes);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithNodeAndAttribute() {
        setUp();
        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> alarmAttributes = new ArrayList<>();
        final List<String> nodeList = new ArrayList<>();
        nodeList.add(fdn);
        final String attribute1 = "alarmNumber#111#>";
        alarmAttributes.add(attribute1);
        alarmLogData.setAlarmAttributes(alarmAttributes);
        alarmLogData.setNodeList(nodeList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.greaterThan("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsWithDateRange() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<Date> dateList = new ArrayList<>();
        dateList.add(new Date());
        alarmLogData.setDateAttribute("eventTime");
        alarmLogData.setDateOperator(DateOperator.GE);
        alarmLogData.setDate(dateList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);

        when(typeRestrictionBuilder.greaterThan("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.lessThan("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("eventTime", dateList.get(0))).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

        final AlarmLogData alarmLogDataForBetween = new AlarmLogData();
        alarmLogDataForBetween.setDateOperator(DateOperator.BETWEEN);
        alarmLogDataForBetween.setDate(dateList);
        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogDataForBetween);

        final AlarmLogData alarmLogData1 = new AlarmLogData();
        alarmLogData1.setDateOperator(DateOperator.LT);
        alarmLogData1.setDate(dateList);
        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData1);

        final AlarmLogData alarmLogDataForGT = new AlarmLogData();
        alarmLogDataForGT.setDateOperator(DateOperator.GT);
        alarmLogDataForGT.setDate(dateList);
        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogDataForGT);

        final AlarmLogData alarmLogDataForEQ = new AlarmLogData();
        alarmLogDataForEQ.setDateOperator(DateOperator.EQ);
        alarmLogDataForEQ.setDate(dateList);
        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogDataForEQ);

        final AlarmLogData alarmLogDataForNE = new AlarmLogData();
        alarmLogDataForNE.setDateOperator(DateOperator.NE);
        alarmLogDataForNE.setDate(dateList);
        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogDataForNE);

        final AlarmLogData alarmLogDataForBothDates = new AlarmLogData();
        final List<Date> dateListForBothDates = new ArrayList<>();
        dateListForBothDates.add(new Date(new Date().getTime() - 3600000 * 2));
        dateListForBothDates.add(new Date());
        alarmLogDataForBothDates.setDate(dateListForBothDates);
        alarmLogDataForBothDates.setDateAttribute("eventTime");
        alarmLogDataForBothDates.setDateOperator(DateOperator.BETWEEN);
        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogDataForBothDates);

    }

    @Test
    public void testFetchHistoryAlarmsWithAlarmAttributes() {

        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> alarmAttributes = new ArrayList<>();
        final String longAttribute1 = "alarmNumber#111#>=";
        final String stringAttribute = "specificProblem#specificProblem1#!=";
        final String longAttribute2 = "alarmNumber#111#<=";
        final String longAttribute3 = "alarmNumber#111#<";
        final String longAttribute4 = "alarmNumber#111#=";
        final String longAttribute5 = "alarmNumber#111#!=";
        final String booleanAttribute1 = "backupStatus#true#=";
        //              String booleanAttribute2 = "backupStatus#fake#=";

        final String dateAttribute1 = "ceaseTime#14/02/1989 12:00:00#=";
        final String dateAttribute2 = "ceaseTime#14/02/1989 12:00:00#elder";
        final String dateAttribute3 = "ceaseTime#14/02/1989 12:00:00#younger";
        final String dateAttribute4 = "ceaseTime#14/02/1989 12:00:00#between";
        final String dateAttribute5 = "ceaseTime#14/02/1989 12:00:00#!=";

        alarmAttributes.add(longAttribute1);
        alarmAttributes.add(longAttribute2);
        alarmAttributes.add(longAttribute3);
        alarmAttributes.add(longAttribute4);
        alarmAttributes.add(longAttribute5);
        alarmAttributes.add(stringAttribute);
        alarmAttributes.add(booleanAttribute1);
        alarmAttributes.add(dateAttribute1);
        alarmAttributes.add(dateAttribute2);
        alarmAttributes.add(dateAttribute3);
        alarmAttributes.add(dateAttribute4);
        alarmAttributes.add(dateAttribute5);

        //              alarmAttributes.add(booleanAttribute2);

        alarmLogData.setAlarmAttributes(alarmAttributes);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.equalTo("specificProblem", "specificProblem1")).thenReturn(restriction);

        when(typeRestrictionBuilder.greaterThan("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.lessThan("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        //              historyAlarmHandler.fetchHistoryAlarms(alarmLogData);
    }

    @Test
    public void testFetchHistoryAlarmsWithInvalidValues() {
        setUp();
        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> alarmAttributes = new ArrayList<>();
        final String booleanAttribute2 = "backupStatus#fake#=";
        alarmAttributes.add(booleanAttribute2);
        alarmLogData.setAlarmAttributes(alarmAttributes);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.equalTo("specificProblem", "specificProblem1")).thenReturn(restriction);

        when(typeRestrictionBuilder.greaterThan("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.lessThan("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

        //              AlarmLogData alarmLogData1 = new AlarmLogData();
        //              final List<String> alarmAttributes1 = new ArrayList<>();
        //              String dateAttribute1 = "ceaseTime#fake#=";
        //              alarmAttributes1.add(dateAttribute1);
        //              alarmLogData1.setAlarmAttributes(alarmAttributes1);
        //              historyAlarmHandler.fetchHistoryAlarms(alarmLogData1);
    }

    @Test
    public void testFetchHistoryAlarmsWithInvalidDateValues() {
        setUp();
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String dateAttribute1 = "ceaseTime#fake#=";
        alarmAttributes1.add(dateAttribute1);
        alarmLogData1.setAlarmAttributes(alarmAttributes1);
        alarmLogData1.setDateFormat("dd/MM/yyyy HH:mm:ss");

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.equalTo("specificProblem", "specificProblem1")).thenReturn(restriction);

        when(typeRestrictionBuilder.greaterThan("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.lessThan("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("alarmNumber", 111L)).thenReturn(restriction);
        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData1);

    }

    @Test
    public void testFetchHistoryAlarmsForRepeatCount() {
        setUp();
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String repeatContAttribute = "repeatCount#111#=";
        final String backupStatus = "backupStatus#false#=";
        alarmAttributes1.add(repeatContAttribute);
        alarmAttributes1.add(backupStatus);
        alarmLogData1.setAlarmAttributes(alarmAttributes1);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.equalTo("specificProblem", "specificProblem1")).thenReturn(restriction);

        when(typeRestrictionBuilder.greaterThan("repeatCount", 111)).thenReturn(restriction);
        when(typeRestrictionBuilder.lessThan("repeatCount", 111)).thenReturn(restriction);
        when(typeRestrictionBuilder.equalTo("repeatCount", 111)).thenReturn(restriction);
        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData1);

    }

    @Test
    public void testFetchHistoryBasedOnMatchType() {
        setUp();
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String repeatContAttribute = "specificProblem#specificProblem1#contains";
        final String backupStatus = "backupStatus#false#=";
        alarmAttributes1.add(repeatContAttribute);
        alarmAttributes1.add(backupStatus);
        alarmLogData1.setAlarmAttributes(alarmAttributes1);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("specificProblem", "specificProblem1", StringMatchCondition.CONTAINS)).thenReturn(restriction);

        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData1);

    }

    @Test
    public void testFetchHistoryStartsWith() {
        setUp();
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String repeatContAttribute = "specificProblem#specificProblem1#startsWith";
        final String backupStatus = "backupStatus#false#=";
        alarmAttributes1.add(repeatContAttribute);
        alarmAttributes1.add(backupStatus);
        alarmLogData1.setAlarmAttributes(alarmAttributes1);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.matchesString("specificProblem", "specificProblem1", StringMatchCondition.STARTS_WITH)).thenReturn(restriction);

        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData1);

    }

    @Test
    public void testFetchHistoryWithNot() {
        setUp();
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> alarmAttributes1 = new ArrayList<>();
        final String repeatContAttribute = "specificProblem#specificProblem1#!=";
        final String backupStatus = "backupStatus#false#=";
        alarmAttributes1.add(repeatContAttribute);
        alarmAttributes1.add(backupStatus);
        alarmLogData1.setAlarmAttributes(alarmAttributes1);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.equalTo("specificProblem", "specificProblem1")).thenReturn(restriction);
        when(typeRestrictionBuilder.not(restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);

        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData1);

    }

    @Test
    public void testFetchHistoryAlarmsWithNode() {
        setUp();
        final AlarmLogData alarmLogData1 = new AlarmLogData();
        final List<String> nodeList1 = new ArrayList<>();
        nodeList1.add(fdn);
        alarmLogData1.setNodeList(nodeList1);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction, restriction)).thenReturn(restriction);
        endSetUp();

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData1);

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> nodeList = new ArrayList<>();
        nodeList.add(fdn1);
        nodeList.add(fdn);
        alarmLogData.setNodeList(nodeList);

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(null);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn1, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction, restriction)).thenReturn(restriction);

        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

    @Test
    public void testFetchHistoryAlarmsForManyNodes() {
        setUp();

        final AlarmLogData alarmLogData = new AlarmLogData();
        final List<String> nodeList = new ArrayList<>();
        final String fdnValue = "MeContext=";

        for (int i = 1; i < 400; i++) {
            final Integer value1 = i;
            final String value2 = value1.toString();
            final String newFdn = fdnValue + value2;
            nodeList.add(newFdn);
        }

        alarmLogData.setNodeList(nodeList);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(liveBucket.findMoByFdn(fdn)).thenReturn(managedObject);
        when(liveBucket.findMoByFdn(fdn1)).thenReturn(null);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.matchesString(QueryConstants.OOR, fdn1, StringMatchCondition.CONTAINS)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        when(typeRestrictionBuilder.allOf(restriction, restriction, restriction)).thenReturn(restriction);
        dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);

    }

}