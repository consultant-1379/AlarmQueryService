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
 * This class will take Attribute of type Long.
 *
 */
public class LongTypeAscendingComparator implements Comparator<AlarmRecord> {

    String attribute;

    public LongTypeAscendingComparator(final String attribute) {
        this.attribute = attribute;
    }

    @Override
    public int compare(final AlarmRecord firstAlarmRecord, final AlarmRecord secondAlarmRecord) {
        final String attributeName = attribute;
        final Object firstValue = firstAlarmRecord.getAttribute(attributeName);
        final Object secondValue = secondAlarmRecord.getAttribute(attributeName);

        if (firstValue != null && secondValue != null) {
            return Long.compare((Long) firstValue, (Long) secondValue);
        } else {
            final ComparatorHelper comparatorHelper = new ComparatorHelper();
            return comparatorHelper.compareAttributesWithNullValuesForAscending(firstValue, secondValue);
        }
    }
}