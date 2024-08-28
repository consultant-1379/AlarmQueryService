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
import java.util.Date;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;

/**
 * Responsible for comparing {@link AlarmRecord}s in descending order based on the attribute set.<br>
 * This class will take Attribute of type Date. <br>
 *
 */
public class DateTypeDescendingComparator implements Comparator<AlarmRecord> {

    String attribute;

    public DateTypeDescendingComparator(final String attribute) {
        this.attribute = attribute;
    }

    @Override
    public int compare(final AlarmRecord firstAlarmRecord, final AlarmRecord secondAlarmRecord) {
        final String attributeName = attribute;
        final Object firstValue = firstAlarmRecord.getAttribute(attributeName);
        final Object secondValue = secondAlarmRecord.getAttribute(attributeName);

        if (firstValue != null && secondValue != null) {
            return ((Date) secondValue).compareTo((Date) firstValue);
        } else {
            final ComparatorHelper comparatorHelper = new ComparatorHelper();
            return comparatorHelper.compareAttributesWithNullValuesForDescending(firstValue, secondValue);
        }
    }
}