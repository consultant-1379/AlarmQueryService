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

package com.ericsson.oss.services.alarm.query.service.models;

public enum SortingOrder {
    /**
     * Ascending order.
     */
    ASCENDING("ASC"),

    /**
     * Descending order.
     */
    DESCENDING("DESC");

    private String orderString;

    SortingOrder(final String orderString) {
        this.orderString = orderString;
    }

    /**
     * Gets the sort order as a <code>String</code>.
     *
     * @return the string defining the sort order
     */
    public String getOrderAsString() {
        return orderString;
    }
}
