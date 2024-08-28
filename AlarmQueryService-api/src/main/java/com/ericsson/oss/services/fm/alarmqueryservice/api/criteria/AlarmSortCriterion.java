/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.api.criteria;

import java.io.Serializable;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;

/**
 * A class that encapsulates a single sorting condition. The class consist of a sort attribute, sort order and sort sequence.
 **/
public class AlarmSortCriterion implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Name of the attribute based on which sorting to be done.
     */
    private String sortAttribute;
    /**
     * Order of sorting. {<link> SortingOrder}
     */
    private SortingOrder sortOrder;
    /**
     * Sequence of sorting. (Used for multiple column sorting.)
     */
    private SortSequence sortSequence;

    public enum SortSequence {
        FIRST_LEVEL_SORT, SECOND_LEVEL_SORT
    }

    public String getSortAttribute() {
        return sortAttribute;
    }

    public void setSortAttribute(final String attribute) {
        this.sortAttribute = attribute;
    }

    public SortingOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(final SortingOrder order) {
        this.sortOrder = order;
    }

    public SortSequence getSortSequence() {
        return sortSequence;
    }

    public void setSortSequence(final SortSequence sequence) {
        this.sortSequence = sequence;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AlarmSortCriterion [sortAttribute=");
        builder.append(sortAttribute);
        builder.append(", sortOrder=");
        builder.append(sortOrder);
        builder.append(", sortSequence=");
        builder.append(sortSequence);
        builder.append("]");
        return builder.toString();
    }
}
