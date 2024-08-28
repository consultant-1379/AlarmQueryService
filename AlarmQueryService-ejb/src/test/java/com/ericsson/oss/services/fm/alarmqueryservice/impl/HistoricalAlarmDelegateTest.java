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
package com.ericsson.oss.services.fm.alarmqueryservice.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.historicalalarmreader.CompositeEventTimeHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;

@RunWith(MockitoJUnitRunner.class)
public class HistoricalAlarmDelegateTest {
    @InjectMocks
    private HistoricalAlarmDelegate historicalAlarmDelegate;

    @Mock
    private CompositeEventTimeHandler historicalAlarmHandler;

    @Mock
    private AuthorizationHandler authorizationHandler;

    @Mock
    private CompositeEventTimeCriteria compositeEventTimeCriteria;

    @Mock
    private OpenAlarmParser openAlarmParser;

    @Test
    public void testGetHistoricalAlarms_CompositeEventTimeCriteria_HistoricalAlarms() {

        historicalAlarmDelegate.getHistoricalAlarms(compositeEventTimeCriteria, true);
        verify(historicalAlarmHandler, times(1)).getAlarms(compositeEventTimeCriteria);

    }
}
