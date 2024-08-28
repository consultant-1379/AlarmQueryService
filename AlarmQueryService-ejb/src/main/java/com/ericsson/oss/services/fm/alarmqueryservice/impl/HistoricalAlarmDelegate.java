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

import javax.inject.Inject;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeAlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.historicalalarmreader.CompositeAlarmAttributeCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.historicalalarmreader.CompositeEventTimeHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;

/**
 *
 * A class responsible for delegating to respective historical alarm handler classes based on the input.
 *
 *
 */
public class HistoricalAlarmDelegate {

    @Inject
    private CompositeEventTimeHandler compositeEventTimeHandler;

    @Inject
    private AuthorizationHandler authorizationHandler;

    @Inject
    private CompositeAlarmAttributeCriteriaHandler compositeAlarmAttributeCriteriaHandler;

    @Inject
    private OpenAlarmParser openAlarmParser;

    public AlarmAttributeResponse getHistoricalAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                                      final boolean authorizationRequired) {
        if (authorizationRequired) {
            authorizationHandler.checkAuthorization();
        }
        if (!openAlarmParser.isAttributesLoaded()) {
            openAlarmParser.extractAttributesFromModel();
        }
        return compositeEventTimeHandler.getAlarms(compositeEventTimeCriteria);
    }

    public AlarmAttributeResponse getHistoricalAlarms(final List<CompositeAlarmAttributeCriteria> compositeAlarmAttributeCriteria,
                                                      final boolean authorizationRequired) {
        if (authorizationRequired) {
            authorizationHandler.checkAuthorization();
        }
        if (!openAlarmParser.isAttributesLoaded()) {
            openAlarmParser.extractAttributesFromModel();
        }
        return compositeAlarmAttributeCriteriaHandler.getAlarms(compositeAlarmAttributeCriteria);
    }
}
