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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.DYNAMIC_ALARM_ATTRIBUTE_MODEL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.POIDS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OPEN_ALARM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DynamicAlarmAttributeValidator;

@RunWith(MockitoJUnitRunner.class)
public class DynamicAlarmAttributeInfoReaderTest {

    @InjectMocks
    private DynamicAlarmAttributeInfoReader dynamicAlarmAttributeInfoReader;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private AlarmPoIdCriteriaHandler alarmPoIdCriteriaHandler;

    @Mock
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;

    @Mock
    private OpenAlarmParser openAlarmParser;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private Projection projection;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private Iterator<Object> poListIterator;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private PersistenceObject poId;

    @Mock
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Mock
    private DynamicAlarmAttributeValidator dynamicAlarmAttributeValidator;

    private final List<Map<String, Object>> poIds = new ArrayList<Map<String, Object>>();
    private final Map<String, Object> poIdMap = new HashMap<String, Object>();

    @Test
    public void test_readDynamicAlarmAttributes() {
        when(dpsProxy.getService()).thenReturn(dataPersistenceService);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery(FM, DYNAMIC_ALARM_ATTRIBUTE_MODEL)).thenReturn(typeQuery);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.executeProjection(typeQuery, projection)).thenReturn(new ArrayList<Object>());
        assertNotNull(dynamicAlarmAttributeInfoReader.readDynamicAlarmAttributes());
    }

    private void readPoIdsForDynamicAlarmAttributeSetup() {
        when(dpsProxy.getService()).thenReturn(dataPersistenceService);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery(FM, DYNAMIC_ALARM_ATTRIBUTE_MODEL)).thenReturn(typeQuery);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.equalTo(anyString(), anyString())).thenReturn(restriction);
        when(poListIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(poListIterator.next()).thenReturn(poId);
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        poIdMap.put(EVENT_PO_ID, 1L);
        poIds.add(poIdMap);
        when(poId.getAttribute(POIDS)).thenReturn(poIds);
    }

    @Test
    public void test_readPoIdsForDynamicAlarmAttribute() {
        readPoIdsForDynamicAlarmAttributeSetup();
        final List<String> attributeNames = new ArrayList<String>();
        attributeNames.add("FMX");
        assertNotNull(dynamicAlarmAttributeInfoReader.readPoIdsForDynamicAlarmAttribute(attributeNames));
    }

    @Test
    public void test_readDynamicAttributesMatchedSearchAlarms() {
        final List<AlarmAttributeCriteria> attributeCriteria = getDynamicAlarmAttributes();
        final AlarmAttributeCriteria criteria = new AlarmAttributeCriteria();
        criteria.setAttributeName("FMX");
        criteria.setAttributeValue("test");
        criteria.setOperator(Operator.EQ);
        attributeCriteria.add(criteria);

        final List<String> list = new ArrayList<String>();
        list.add("ss");
        list.add("FMX");

        final Map<String, List<AlarmAttributeCriteria>> groupAttributes = getGroupedAlarmAttributeCriteria(criteria);
        when(alarmPoIdCriteriaHandler.getAlarmsForAdditionalAttributeSearchSort(any(AlarmPoIdCriteria.class), any(ExpectedOutputAttributes.class)))
                .thenReturn(getAlarmAttributeResponse());
        when(alarmAttributeResponseBuilder.buildAttributeResponse(anyListOf(AlarmRecord.class))).thenReturn(getAlarmAttributeResponse());
        when(dpsProxy.getService()).thenReturn(dataPersistenceService);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery(FM, DYNAMIC_ALARM_ATTRIBUTE_MODEL)).thenReturn(typeQuery);
        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(queryBuilder.createTypeQuery(FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(openAlarmParser.getAllalarmAttributes()).thenReturn(list);
        when(attributeRestrictionBuilder.groupAlarmAttributes(anyListOf(AlarmAttributeCriteria.class))).thenReturn(groupAttributes);
        assertNotNull(dynamicAlarmAttributeInfoReader.readDynamicAttributesMatchedSearchAlarms(attributeCriteria, getExpectedOutputAttributes()));
    }

    private Map<String, List<AlarmAttributeCriteria>> getGroupedAlarmAttributeCriteria(final AlarmAttributeCriteria criteria) {
        final Map<String, List<AlarmAttributeCriteria>> attributeCriteraGroup = new HashMap<String, List<AlarmAttributeCriteria>>();
        final List<AlarmAttributeCriteria> attributeCriteriaList = getAlarmAttributeCriteria();
        attributeCriteraGroup.put(criteria.getAttributeName(), attributeCriteriaList);
        return attributeCriteraGroup;
    }

    @Test
    public void test_readDynamicSearchAttributes() throws Exception {

        final List<String> list = new ArrayList<String>();
        list.add("presentSeverity");
        list.add("previousSeverity");
        when(openAlarmParser.getAllalarmAttributes()).thenReturn(list);
        List<String> dynamicAttributes = new ArrayList<String>();
        dynamicAttributes.add("FMX");
        when(dynamicAlarmAttributeValidator.filterFmxAdditionalAttributes(anyListOf(String.class))).thenReturn(dynamicAttributes);
        final List<AlarmAttributeCriteria> dynamicAlarmAttributes = dynamicAlarmAttributeInfoReader
                .readDynamicSearchAttributes(getAlarmAttributeCriteria());

        assertEquals(dynamicAlarmAttributes.get(0).getAttributeName(), "FMX");
    }

    @Test
    public void test_isSingleAdditionalAttributeWithNotEquals() {
        assertTrue(dynamicAlarmAttributeInfoReader.isSingleAdditionalAttributeWithNotEquals(getAlarmAttributeCriteria()));
    }

    private List<AlarmAttributeCriteria> getAlarmAttributeCriteria() {
        final List<AlarmAttributeCriteria> alarmAttributeCriterias = new ArrayList<AlarmAttributeCriteria>();
        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName("FMX");
        alarmAttributeCriteria.setAttributeValue("test");
        alarmAttributeCriteria.setOperator(Operator.NE);
        alarmAttributeCriterias.add(alarmAttributeCriteria);
        return alarmAttributeCriterias;
    }

    private AlarmAttributeResponse getAlarmAttributeResponse() {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        return new AlarmAttributeResponse(alarmRecords, "");
    }

    private ExpectedOutputAttributes getExpectedOutputAttributes() {
        return new ExpectedOutputAttributes();
    }

    private List<AlarmAttributeCriteria> getDynamicAlarmAttributes() {
        return new ArrayList<AlarmAttributeCriteria>();
    }

}
