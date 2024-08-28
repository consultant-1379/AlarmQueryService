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

package com.ericsson.oss.services.fm.alarmqueryservice.api.common;

/**
 * Defines the possible operators that an alarm query can constitute.
 */
public enum Operator {
    EQ, NE, LT, GT, LE, GE, BETWEEN, STARTS_WITH, ENDS_WITH, CONTAINS, NOT_BETWEEN;
}
