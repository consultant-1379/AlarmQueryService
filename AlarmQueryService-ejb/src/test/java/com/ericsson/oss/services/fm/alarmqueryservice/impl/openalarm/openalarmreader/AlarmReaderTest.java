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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;

import java.util.ArrayList;
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
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class AlarmReaderTest {

    @InjectMocks
    private AlarmReader alarmReader;

    @Mock
    private Restriction restriction;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private PersistenceObject persistenceObject;

    @Mock
    private CommentHistoryReader historyCommentsReader;

    @Mock
    private Iterator<Object> poListIterator;

    @Mock
    private AlarmRecordConverter alarmRecordConverter;

    private final List<PersistenceObject> persistenceObjects = new ArrayList<PersistenceObject>();

    AlarmRecord alarmRecord;

    private final List<String> outputAttributes = new ArrayList<String>();
    private final Map<String, Object> alarmAttributeMap = new HashMap<String, Object>();

    private final List<Long> poIds = new ArrayList<Long>();

    private final Long poId = 333L;

    @Before
    public void setup() {

        alarmAttributeMap.put(ALARM_NUMBER, 333L);
        alarmAttributeMap.put(OBJECT_OF_REFERENCE, "NETWORKELEMENT=1");
        alarmRecord = new AlarmRecord(alarmAttributeMap, null, null);
        poIds.add(1L);
        persistenceObjects.add(persistenceObject);
        when(persistenceObject.getAttribute(VISIBILITY)).thenReturn(true);
        when(poListIterator.hasNext()).thenReturn(true, false);
        when(dpsProxy.getService()).thenReturn(dataPersistenceService);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.next()).thenReturn(persistenceObject);
        when(persistenceObject.getPoId()).thenReturn(1L);
        when(liveBucket.findPosByIds(poIds)).thenReturn(persistenceObjects);

    }

    @Test
    public void testGetAlarmRecords_2Attributes_AlarmRecords() {
        final List<Object[]> openAlarms = new ArrayList<Object[]>();
        final Object[] poIds = { 122L, 22L };
        final Object[] alarmNumbers = { 333, 22 };
        openAlarms.add(poIds);
        openAlarms.add(alarmNumbers);
        outputAttributes.add(EVENT_PO_ID);
        outputAttributes.add(ALARM_NUMBER);

        when(alarmRecordConverter.prepareAlarmRecord(outputAttributes, 2, openAlarms.get(0))).thenReturn(alarmRecord);
        when(alarmRecordConverter.prepareAlarmRecord(outputAttributes, 2, openAlarms.get(1))).thenReturn(alarmRecord);
        when(
                queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Matchers.anyObject(), (Projection) Matchers.anyObject(),
                        (Projection) Matchers.anyObject())).thenReturn(openAlarms);

        final List<AlarmRecord> alarmRecords = alarmReader.getAlarmRecordsForSelectedAttributes(typeQuery, outputAttributes);
        assertEquals(333L, (long) alarmRecords.get(0).getAlarmNumber());

    }

    @Test
    public void testGetAlarmRecords_1Attribute_AlarmRecord() {
        final List<Object> openAlarms = new ArrayList<Object>();
        final Object poIds = 122L;
        final Object alarmNumbers = 333L;
        openAlarms.add(poIds);
        openAlarms.add(alarmNumbers);
        outputAttributes.add(ALARM_NUMBER);

        when(queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Matchers.anyObject(), (Projection) Matchers.anyObject())).thenReturn(
                openAlarms);

        final List<AlarmRecord> alarmRecords = alarmReader.getAlarmRecordsForSelectedAttributes(typeQuery, outputAttributes);

        assertEquals(122L, (long) alarmRecords.get(0).getAlarmNumber());

    }

    @Test
    public void testGetAlarmRecords_PoIds_AlarmRecords() {

        when(alarmRecordConverter.prepareAlarmRecordForUserInterfaces(persistenceObject, true, true)).thenReturn(alarmRecord);
        final List<AlarmRecord> alarmRecord = alarmReader.getAlarmRecordsWithPoIds(poIds, true, true, outputAttributes);
        assertEquals(poId, alarmRecord.get(0).getAlarmNumber());

    }

    @Test
    public void testGetAlarmRecords_PoIdsAndZeroOutputAttributes_AlarmRecords() {
        final List<String> outputAttributes = new ArrayList<String>();
        when(alarmRecordConverter.prepareAlarmRecordForUserInterfaces(persistenceObject, true, true)).thenReturn(alarmRecord);
        final List<AlarmRecord> alarmRecord = alarmReader.getAlarmRecordsWithPoIds(poIds, true, true, outputAttributes);

        assertEquals(poId, alarmRecord.get(0).getAlarmNumber());

    }

    @Test
    public void testGetAlarmRecords_TypeQuery_AlarmRecords() {
        when(alarmRecordConverter.prepareAlarmRecordForUserInterfaces(persistenceObject, true, false)).thenReturn(alarmRecord);
        final List<AlarmRecord> alarmRecord = alarmReader.getAlarmRecords(typeQuery, true);
        assertEquals(poId, alarmRecord.get(0).getAlarmNumber());

    }

}
