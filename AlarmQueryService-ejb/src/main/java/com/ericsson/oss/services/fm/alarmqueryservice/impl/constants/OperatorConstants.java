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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.constants;

/**
 * Class holds the Operator related constants which are used in query.
 *
 */
public final class OperatorConstants {

    public static final String CONTAINS = "contains";
    public static final String STARTS_WITH = "startsWith";
    public static final String ENDS_WITH = "endsWith";
    public static final String EQUAL_OPERATOR = "=";
    public static final String NOT_EQUAL_OPERATOR = "!=";
    public static final String BETWEEN = "between";
    public static final String DESC = "desc";
    public static final String ASC = "asc";

    public static final String GREATER_THAN_OR_EQUAL = ">=";
    public static final String GREATER_THAN = ">";
    public static final String LESS_THAN_OR_EQUAL = "<=";
    public static final String LESS_THAN = "<";

    public static final String ELDER = "elder";
    public static final String YOUNGER = "younger";

    private OperatorConstants() {
    }

}
