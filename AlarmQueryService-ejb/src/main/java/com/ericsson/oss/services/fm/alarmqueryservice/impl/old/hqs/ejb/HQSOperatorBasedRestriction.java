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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CONTAINS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ELDER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ENDSWITH;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EQUALOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.GREATER_THAN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.GREATER_THAN_OR_EQUAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LESS_THAN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LESS_THAN_OR_EQUAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.STARTSWITH;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.YOUNGER;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;

public class HQSOperatorBasedRestriction {

    public Restriction restrictionWithToAndFromDate(final RestrictionBuilder restrictionBuilder, final String attribute, final String fromDate,
                                                    final String toDate) {
        Restriction restriction = restrictionBuilder.between(attribute, fromDate, toDate);
        final Restriction fromDateEqualRestriction = restrictionBuilder.equalTo(attribute, fromDate);
        final Restriction toDateEqualRestriction = restrictionBuilder.equalTo(attribute, toDate);
        restriction = restrictionBuilder.anyOf(restriction, fromDateEqualRestriction, toDateEqualRestriction);

        return restriction;
    }

    public Restriction getRestrictionByComparisonOperator(final RestrictionBuilder restrictionBuilder, final String attributeName,
                                                          final Object attributeValue, final Object operator) {
        Restriction restriction = null;
        if ((operator == DateOperator.LE) || (LESS_THAN_OR_EQUAL.equals(operator)) || (ELDER.equals(operator))) {
            restriction = restrictionBuilder.lessThanEqualTo(attributeName, attributeValue);
        } else if ((operator == DateOperator.GE) || (GREATER_THAN_OR_EQUAL.equals(operator)) || (YOUNGER.equals(operator))) {
            restriction = restrictionBuilder.greaterThanEqualTo(attributeName, attributeValue);
        } else if ((operator == DateOperator.GT) || (GREATER_THAN.equals(operator))) {
            restriction = restrictionBuilder.greaterThan(attributeName, attributeValue);
        } else if ((operator == DateOperator.LT) || (LESS_THAN.equals(operator))) {
            restriction = restrictionBuilder.lessThan(attributeName, attributeValue);
        } else if ((operator == DateOperator.EQ) || (EQUALOPERATOR.equals(operator))) {
            restriction = restrictionBuilder.equalTo(attributeName, attributeValue);
        } else {
            restriction = restrictionBuilder.not(restrictionBuilder.equalTo(attributeName, attributeValue));
        }
        return restriction;
    }

    public Restriction getRestrictionOnMatchCondition(final RestrictionBuilder restrictionBuilder, final String attribute, final String value,
                                                      final String operator) {
        Restriction restriction = null;
        if (EQUALOPERATOR.equalsIgnoreCase(operator)) {
            restriction = restrictionBuilder.equalTo(attribute, value);
        } else if (CONTAINS.equalsIgnoreCase(operator)) {
            restriction = restrictionBuilder.matchesString(attribute, value, StringMatchCondition.CONTAINS);
        } else if (STARTSWITH.equalsIgnoreCase(operator)) {
            restriction = restrictionBuilder.matchesString(attribute, value, StringMatchCondition.STARTS_WITH);
        } else if (ENDSWITH.equalsIgnoreCase(operator)) {
            restriction = restrictionBuilder.matchesString(attribute, value, StringMatchCondition.ENDS_WITH);
        } else {
            restriction = restrictionBuilder.not(restrictionBuilder.equalTo(attribute, value));
        }
        return restriction;
    }

}
