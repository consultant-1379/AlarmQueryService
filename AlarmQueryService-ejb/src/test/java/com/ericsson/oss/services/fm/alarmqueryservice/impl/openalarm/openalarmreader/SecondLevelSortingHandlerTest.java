/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;

@RunWith(MockitoJUnitRunner.class)
public class SecondLevelSortingHandlerTest {

    @InjectMocks
    private SecondLevelSortingHandler secondLevelSortingHandler;

    @Mock
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;

    List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
    final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();

    private void prepareDummyOpenAlarm() {
        for (int i = 0; i <= 5; i++) {
            Map<String, Object> attributeMap1 = new HashMap<String, Object>();
            Map<String, String> additionalInfoMap = new HashMap<String, String>();
            additionalInfoMap.put("fmx1", "value1" + (i * 3));
            additionalInfoMap.put("fmx2", "value2" + (i * 2));
            attributeMap1.put("eventPoId", 1234L);
            attributeMap1.put("presentSeverity", "MAJOR");
            attributeMap1.put("previousSeverity", "MINOR");
            attributeMap1.put("additionalInformation", additionalInfoMap.toString());
            AlarmRecord alarmRecord1 = new AlarmRecord(attributeMap1, "LTE1", null);
            alarmRecords.add(alarmRecord1);
        }
    }

    private void prepareAlarmSortCriteria() {
        final AlarmSortCriterion alarmSortCriterion1 = new AlarmSortCriterion();
        alarmSortCriterion1.setSortAttribute("presentSeverity");
        alarmSortCriterion1.setSortOrder(SortingOrder.ASCENDING);
        alarmSortCriterion1.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        final AlarmSortCriterion alarmSortCriterion2 = new AlarmSortCriterion();
        alarmSortCriterion2.setSortAttribute("previousSeverity");
        alarmSortCriterion2.setSortOrder(SortingOrder.DESCENDING);
        alarmSortCriterion2.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        alarmSortCriteria.add(alarmSortCriterion1);
        alarmSortCriteria.add(alarmSortCriterion2);
    }

    @Test
    public void testSortBasedOnStandardAttributeWhenFirstSortedOnStandard() {
        prepareDummyOpenAlarm();
        prepareAlarmSortCriteria();
        final List<AlarmRecord> sortedAlarmRecords = secondLevelSortingHandler.sortBasedOnStandardAttributeWhenFirstSortedOnStandard(alarmRecords,
                alarmSortCriteria);
        assertNotNull(sortedAlarmRecords);
    }

    @Test
    public void testSortBasedOnDynamicAttributeWhenFirstSortedOnStandard() {
        prepareDummyOpenAlarm();
        prepareAlarmSortCriteria();
        alarmSortCriteria.get(0).setSortAttribute("fmx1");
        final List<AlarmRecord> sortedAlarmRecords = secondLevelSortingHandler.sortBasedOnDynamicAttributeWhenFirstSortedOnStandard(alarmRecords,
                alarmSortCriteria);
        assertNotNull(sortedAlarmRecords);
    }

    @Test
    public void testSortBasedOnDynamicAttributeWhenFirstSortedOnDynamic() {
        prepareDummyOpenAlarm();
        prepareAlarmSortCriteria();
        alarmSortCriteria.get(1).setSortAttribute("fmx2");
        final List<AlarmRecord> sortedAlarmRecords = secondLevelSortingHandler.sortBasedOnDynamicAttributeWhenFirstSortedOnDynamic(alarmRecords,
                alarmSortCriteria);
        assertNotNull(sortedAlarmRecords);
    }

    @Test
    public void testSortBasedOnStandardAttributeWhenFirstSortedOnDynamic() {
        prepareDummyOpenAlarm();
        prepareAlarmSortCriteria();
        alarmSortCriteria.get(1).setSortAttribute("previousSeverity");
        final List<AlarmRecord> sortedAlarmRecords = secondLevelSortingHandler.sortBasedOnStandardAttributeWhenFirstSortedOnDynamic(alarmRecords,
                alarmSortCriteria);
        assertNotNull(sortedAlarmRecords);
    }
}
