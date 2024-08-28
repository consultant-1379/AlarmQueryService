/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.ericsson.oss.services.fm.alarmqueryservice.api.AlarmQueryService;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeAlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORCriteria;
import com.ericsson.oss.services.fm.common.tbac.FMTBAC;
import com.ericsson.oss.services.fm.common.tbac.FMTBACInputParameter;

/**
 * A class responsible for delegating to respective implementation classes : OPEN/HISTORY.
 */
@Stateless
public class AlarmQueryServiceBean implements AlarmQueryService {

    @Inject
    private OpenAlarmDelegate openAlarmDelegate;

    @Inject
    private HistoricalAlarmDelegate historicalAlarmDelegate;

    @Inject
    private FMXAdditionalAttributeReader fmxAdditionalAttributeReader;

    @Override
    public AlarmPoIdResponse getAllAlarmPoIds() {
        return openAlarmDelegate.getAllAlarmPoIds();
    }

    @Override
    public AlarmPoIdResponse getAlarmPoIds(final OORCriteria oorCriteria) {
        return openAlarmDelegate.getAlarmPoIds(oorCriteria);
    }

    @Override
    @FMTBAC(handlerId = "FMTBACCompositeNodeCriteriaHandler")
    public AlarmPoIdResponse getAlarmPoIds(@FMTBACInputParameter final CompositeNodeCriteria compositeNodeCriteria,
                                           final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarmPoIds(compositeNodeCriteria, authorizationRequired);
    }

    @Override
    public Map<String, Long> getAlarmCountBySeverity(final CompositeNodeCriteria compositeNodeCriteria, final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarmCountBySeverity(compositeNodeCriteria, authorizationRequired);
    }

    @Override
    public Map<String, Long> getAlarmCountBySeverity(final List<CompositeNodeCriteria> compositeNodeCriterias,
                                                               final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarmCountBySeverity(compositeNodeCriterias, authorizationRequired);
    }

    @Override
    public AlarmAttributeResponse getAlarms(final AlarmPoIdCriteria alarmPoIdCriteria, final ExpectedOutputAttributes expectedOutputAttributes,
                                            final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarms(alarmPoIdCriteria, expectedOutputAttributes, authorizationRequired);
    }

    @Override
    @FMTBAC(handlerId = "FMTBACAlarmPoIdCriteriaHandler")
    public AlarmAttributeResponse getAlarms(@FMTBACInputParameter final AlarmPoIdCriteria alarmPoIdCriteria,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final boolean authorizationRequired,
                                            final boolean tbacValidationRequired) {
        return openAlarmDelegate.getAlarms(alarmPoIdCriteria, expectedOutputAttributes, authorizationRequired);
    }

    @Override
    public AlarmAttributeResponse getAlarms(final CompositeNodeCriteria compositeNodeCriteria,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarms(compositeNodeCriteria, expectedOutputAttributes, authorizationRequired);
    }

    @Override
    @FMTBAC(handlerId = "FMTBACCompositeEventTimeCriteriaHandler")
    public AlarmAttributeResponse getAlarms(@FMTBACInputParameter final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, authorizationRequired);
    }

    @Override
    @FMTBAC(handlerId = "FMTBACCompositeNodeCriteriaListHandler")
    public AlarmAttributeResponse getAlarms(@FMTBACInputParameter final List<CompositeNodeCriteria> compositeNodeCriterias,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final List<AlarmSortCriterion> alarmSortCriteria,
                                            final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarms(compositeNodeCriterias, expectedOutputAttributes, alarmSortCriteria, authorizationRequired);
    }

    @Override
    @FMTBAC(handlerId = "FMTBACCompositeEventTimeCriteriaHandlerForHistoricalAlarms")
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public AlarmAttributeResponse getHistoricalAlarms(@FMTBACInputParameter final CompositeEventTimeCriteria compositeDateCriteria,
                                                      final boolean authorizationRequired) {
        return historicalAlarmDelegate.getHistoricalAlarms(compositeDateCriteria, authorizationRequired);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public AlarmAttributeResponse getHistoricalAlarms(final List<CompositeAlarmAttributeCriteria> compositeAlarmAttributeCriteria,
                                                      final boolean authorizationRequired) {
        return historicalAlarmDelegate.getHistoricalAlarms(compositeAlarmAttributeCriteria, authorizationRequired);
    }

    @Override
    public List<String> getAlarmAdditionalAttributes() {
        return fmxAdditionalAttributeReader.getAlarmAdditionalAttributes();
    }

    @Override
    public AlarmCountResponse getAlarmCount(final CompositeNodeCriteria compositeNodeCriteria, final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarmCount(compositeNodeCriteria, authorizationRequired);
    }

    @Override
    @FMTBAC(handlerId = "FMTBACCompositeEventTimeCriteriaHandler")
    public AlarmCountResponse getAlarmCount(@FMTBACInputParameter final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                            final boolean authorizationRequired) {
        return openAlarmDelegate.getAlarmCount(compositeEventTimeCriteria, authorizationRequired);
    }

    @Override
    @FMTBAC(handlerId = "FMTBACCompositeEventTimeCriteriaHandler")
    public AlarmAttributeResponse getOpenAlarmsWithCount(@FMTBACInputParameter final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                                         final Integer maxNumberOfAlarmsInCli,
                                                         final ExpectedOutputAttributes expectedOutputAttributes,
                                                         final AlarmAttributeResponse alarmAttributeResponse) {
        return openAlarmDelegate.getOpenAlarmsWithCount(compositeEventTimeCriteria, maxNumberOfAlarmsInCli, expectedOutputAttributes,
                alarmAttributeResponse);
    }
}
