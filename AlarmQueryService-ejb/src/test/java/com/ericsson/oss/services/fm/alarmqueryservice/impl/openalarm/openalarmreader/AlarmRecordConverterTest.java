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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;
import com.ericsson.oss.services.fm.common.addinfo.TargetAdditionalInformationHandler;

@RunWith(MockitoJUnitRunner.class)
public class AlarmRecordConverterTest {

    @InjectMocks
    private AlarmRecordConverter alarmRecordConverter;

    @Mock
    TargetAdditionalInformationHandler targetAdditionalInformationHandler;

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
    private PersistenceObject persistenceObject;

    @Mock
    private AncestorMOFinder ancestorMOFinder;

    @Mock
    private CommentHistoryReader historyCommentsReader;

    @Mock
    private Iterator<Object> poListIterator;

    private final List<PersistenceObject> persistenceObjects = new ArrayList<PersistenceObject>();
    private final List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
    private final List<String> outputAttributes = new ArrayList<String>();
    private final Map<String, Object> alarmAttributeMap = new HashMap<String, Object>();
    private final Map<String, Object> commentMap = new HashMap<String, Object>();
    private final List<Long> poIds = new ArrayList<Long>();

    @Before
    public void setup() {

        commentMap.put(EVENT_PO_ID, 1L);
        comments.add(commentMap);
        alarmAttributeMap.put(ALARM_NUMBER, 333L);
        alarmAttributeMap.put(OBJECT_OF_REFERENCE, "NETWORKELEMENT=1");
        alarmAttributeMap.put(EVENT_PO_ID, 11L);
        poIds.add(1L);
        persistenceObjects.add(persistenceObject);
        when(persistenceObject.getAttribute(VISIBILITY)).thenReturn(true);
        when(persistenceObject.getAllAttributes()).thenReturn(alarmAttributeMap);
        when(persistenceObject.getPoId()).thenReturn(1L);
        when(poListIterator.hasNext()).thenReturn(true, false);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.next()).thenReturn(persistenceObject);
        when(persistenceObject.getPoId()).thenReturn(1L);
        when(liveBucket.findPosByIds(poIds)).thenReturn(persistenceObjects);
        when(historyCommentsReader.getAllComments(1L)).thenReturn(comments);
    }

    @Test
    public void test_PrepareAlarmRecord_PersistenceObject_AlarmRecord() {

        final AlarmRecord alarmRecord = alarmRecordConverter.prepareAlarmRecordForUserInterfaces(persistenceObject, true, true);
        assertEquals(1L, (long) alarmRecord.getEventPoIdAsLong());

    }

    @Test
    public void test_PrepareAlarmRecord_OutputAttributes_AlarmRecord() {
        final Object[] openAlarm = { 222L, 111L };

        outputAttributes.add(EVENT_PO_ID);
        outputAttributes.add(ALARM_NUMBER);

        final AlarmRecord record = alarmRecordConverter.prepareAlarmRecord(outputAttributes, 2, openAlarm);
        assertEquals(111L, (long) record.getAlarmNumber());

    }

    @Test
    public void test_PrepareAlarmRecord_OutputAttributs_AlarmRecord() {

        outputAttributes.add(EVENT_PO_ID);
        outputAttributes.add(ALARM_NUMBER);

        final AlarmRecord record = alarmRecordConverter.prepareAlarmRecordForSeletedAttributes(persistenceObject, outputAttributes);
        assertEquals(333L, (long) record.getAlarmNumber());

    }
}