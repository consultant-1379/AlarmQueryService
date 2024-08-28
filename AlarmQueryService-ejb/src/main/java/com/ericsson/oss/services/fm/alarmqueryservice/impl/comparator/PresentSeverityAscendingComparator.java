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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.UNDER_SCORE;

import java.util.Comparator;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;

/**
 * Responsible for comparing {@link AlarmRecord}s in ascending order based on the attribute set.<br>
 * This class will take Severity Attributes, which are to be compared on their priorities by forming the
 * PseudoSeverity(PreviousSeverity_PresentSeverity) attribute. <br>
 *
 *
 */
public class PresentSeverityAscendingComparator implements Comparator<AlarmRecord> {

    String attribute;

    public PresentSeverityAscendingComparator(final String attribute) {
        this.attribute = attribute;
    }

    @Override
    public int compare(final AlarmRecord firstAlarmRecord, final AlarmRecord secondAlarmRecord) {
        final String firstPseudoSeverity = firstAlarmRecord.getPreviousSeverity() + UNDER_SCORE + firstAlarmRecord.getPresentSeverity();
        final String secondPseudoSeverity = secondAlarmRecord.getPreviousSeverity() + UNDER_SCORE + secondAlarmRecord.getPresentSeverity();

        return PseudoSeverities.PSEUDO_SEVERITIES_MAP.get(firstPseudoSeverity)
                .compareTo(PseudoSeverities.PSEUDO_SEVERITIES_MAP.get(secondPseudoSeverity));
    }
}