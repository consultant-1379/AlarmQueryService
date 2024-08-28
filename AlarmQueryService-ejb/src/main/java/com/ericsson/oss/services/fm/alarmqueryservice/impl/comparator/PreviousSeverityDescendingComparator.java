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

import java.util.Comparator;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;

/**
 * Responsible for comparing {@link AlarmRecord}s in ascending order based on the attribute set.<br>
 * This class will take Severity Attributes, which are to be compared on their priority basis. <br>
 *
 *
 */
public class PreviousSeverityDescendingComparator implements Comparator<AlarmRecord> {

    String attribute;

    public PreviousSeverityDescendingComparator(final String attribute) {
        this.attribute = attribute;
    }

    @Override
    public int compare(final AlarmRecord firstAlarmRecord, final AlarmRecord secondAlarmRecord) {
        return PseudoSeverities.PSEUDO_SEVERITIES_MAP.get(secondAlarmRecord.getPreviousSeverity().toString()).compareTo(
                PseudoSeverities.PSEUDO_SEVERITIES_MAP.get(firstAlarmRecord.getPreviousSeverity().toString()));
    }
}