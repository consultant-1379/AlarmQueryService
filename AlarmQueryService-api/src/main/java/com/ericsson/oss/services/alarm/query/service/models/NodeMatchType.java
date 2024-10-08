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

package com.ericsson.oss.services.alarm.query.service.models;

public enum NodeMatchType {
    EQUALS(0),
    CONTAINS(1),
    ALL(2);

    private int matchType;

    NodeMatchType() {
    }

    NodeMatchType(final int s) {
        matchType = s;
    }

    public int getMatchType() {
        return matchType;
    }
}