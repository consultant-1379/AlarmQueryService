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

/**
 * Helper class responsible for comparing values which may have null values.
 *
 */
public class ComparatorHelper {

    public int compareAttributesWithNullValuesForAscending(final Object firstValue, final Object secondValue) {
        if (firstValue == null && secondValue != null) {
            return -1;
        } else if (firstValue != null && secondValue == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public int compareAttributesWithNullValuesForDescending(final Object firstValue, final Object secondValue) {
        if (firstValue != null && secondValue == null) {
            return -1;
        } else if (firstValue == null && secondValue != null) {
            return 1;
        } else {
            return 0;
        }
    }

    public int compareForAscending(final Object firstValue, final Object secondValue) {
        if (isEmpty((String) firstValue)) {
            if (isEmpty((String) secondValue)) {
                return 0;
            }
            return -1;
        }
        if (isEmpty((String) secondValue)) {
            return 1;
        }
        return ((String) firstValue).compareTo((String) secondValue);
    }

    public int compareForDescending(final Object firstValue, final Object secondValue) {
        if (isEmpty((String) secondValue)) {
            if (isEmpty((String) firstValue)) {
                return 0;
            }
            return -1;
        }

        if (isEmpty((String) firstValue)) {
            return 1;
        }
        return ((String) secondValue).compareTo((String) firstValue);
    }

    private static boolean isEmpty(final String val) {
        return val == null || val.trim().length() == 0;
    }

}
