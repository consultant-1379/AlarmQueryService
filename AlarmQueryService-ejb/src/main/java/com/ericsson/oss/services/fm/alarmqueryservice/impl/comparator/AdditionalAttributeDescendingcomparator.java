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
 * Responsible for comparing {@link AlarmRecord}s in ascending order based on the alarm additional attribute set.<br>
 * This class will take Attribute of type String. <br>
 *
 */

public class AdditionalAttributeDescendingcomparator implements Comparator<AlarmRecord> {
    private String attribute;

    public AdditionalAttributeDescendingcomparator(final String attribute) {
        this.attribute = attribute;
    }

    /**
     * Method takes {@link AlarmRecord} objects and compare both the instances and returns int value based on the compared attribute of the instances.
     */

    @Override
    public int compare(final AlarmRecord firstAlarmRecord, final AlarmRecord secondAlarmRecord) {
        final Object firstValue = firstAlarmRecord.getAdditionalAttributeMap().get(attribute);
        final Object secondValue = secondAlarmRecord.getAdditionalAttributeMap().get(attribute);

        return new ComparatorHelper().compareForDescending(firstValue, secondValue);
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }
}
