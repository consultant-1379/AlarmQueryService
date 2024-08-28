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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalAttributeAscendingcomparatorTest {

    @InjectMocks
    private AdditionalAttributeAscendingcomparator additionalAttributeAscendingcomparator;

    @Mock
    private AlarmRecord alarmRecord;
    @Mock
    private AlarmRecord sAlarmRecord;

    @Test
    public void testCompare() {
        additionalAttributeAscendingcomparator.setAttribute("attribute");
        final Map<String, String> first = new HashMap<String, String>();
        first.put("attribute", "xabc");
        when(alarmRecord.getAdditionalAttributeMap()).thenReturn(first);
        final Map<String, String> second = new HashMap<String, String>();
        second.put("attribute", "xabc");
        when(sAlarmRecord.getAdditionalAttributeMap()).thenReturn(second);
        assertEquals(0, additionalAttributeAscendingcomparator.compare(alarmRecord, sAlarmRecord));
        second.put("attribute", "xabcd");
        assertEquals(-1, additionalAttributeAscendingcomparator.compare(alarmRecord, sAlarmRecord));
        first.put("attribute", "xabcde");
        assertEquals(1, additionalAttributeAscendingcomparator.compare(alarmRecord, sAlarmRecord));
        first.put("attribute", null);
        assertEquals(-1, additionalAttributeAscendingcomparator.compare(alarmRecord, sAlarmRecord));

    }
}