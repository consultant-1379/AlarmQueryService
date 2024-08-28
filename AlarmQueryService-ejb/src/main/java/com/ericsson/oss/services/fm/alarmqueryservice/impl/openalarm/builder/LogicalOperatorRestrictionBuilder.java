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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;

/**
 * Responsible for building the DPS restriction for single attribute based on the logical operator and its value.
 */

@ApplicationScoped
public class LogicalOperatorRestrictionBuilder {

    @Inject
    private OpenAlarmParser openAlarmParser;

    public Restriction build(final TypeRestrictionBuilder restrictionBuilder, final String attributeName, final Object attributeValue,
        final Operator operator) {
        Restriction restriction = null;
        switch (operator) {
            case EQ:
                restriction = buildEqualRestriction(restrictionBuilder, attributeName, attributeValue);
                break;
            case LE:
                restriction = buildLessThanOrEqualRestriction(restrictionBuilder, attributeName, attributeValue);
                break;
            case GE:
                restriction = buildGreaterThanOrEqualRestriction(restrictionBuilder, attributeName, attributeValue);
                break;
            case GT:
                restriction = restrictionBuilder.greaterThan(attributeName, attributeValue);
                break;
            case LT:
                restriction = restrictionBuilder.lessThan(attributeName, attributeValue);
                break;
            case NE:
                final Restriction tempDateRestriction = buildEqualRestriction(restrictionBuilder, attributeName, attributeValue);
                restriction = restrictionBuilder.not(tempDateRestriction);
                if (openAlarmParser.getDateTypeAttributes().contains(attributeName)) {
                    restriction = buildRestrictionForNullValue(restrictionBuilder, attributeName);
                }
                break;
            case CONTAINS:
                restriction = restrictionBuilder.matchesString(attributeName, attributeValue.toString(), StringMatchCondition.CONTAINS);
                break;
            case STARTS_WITH:
                restriction = restrictionBuilder.matchesString(attributeName, attributeValue.toString(), StringMatchCondition.STARTS_WITH);
                break;
            case ENDS_WITH:
                restriction = restrictionBuilder.matchesString(attributeName, attributeValue.toString(), StringMatchCondition.ENDS_WITH);
                break;
            default:
                break;
        }
        return restriction;
    }

    /**
     * Returns a compositeRestriction by combining restrictions with AND operation.
     * @param restrictionBuilder -- {@link RestrictionBuilder}
     * @param compositeRestriction -- final restriction
     * @param restrictionToBeAdded -- Restriction need to be added
     * @return -- compositeRestriction after AND operation on not null restrictions.
     */
    public Restriction buildCompositeRestrictionByAnd(final RestrictionBuilder restrictionBuilder, final Restriction compositeRestriction,
        final Restriction restrictionToBeAdded) {
        Restriction finalRestriction = compositeRestriction;

        if (restrictionToBeAdded != null) {
            if (compositeRestriction != null) {
                finalRestriction = restrictionBuilder.allOf(compositeRestriction, restrictionToBeAdded);
            } else {
                finalRestriction = restrictionToBeAdded;
            }
        }
        return finalRestriction;
    }

    private Restriction buildGreaterThanOrEqualRestriction(final TypeRestrictionBuilder restrictionBuilder, final String attributeName,
        final Object attributeValue) {
        Restriction restriction;
        final Restriction greaterThanRestriction = restrictionBuilder.greaterThan(attributeName, attributeValue);
        final Restriction equalRestriction = buildEqualRestriction(restrictionBuilder, attributeName, attributeValue);
        restriction = restrictionBuilder.anyOf(greaterThanRestriction, equalRestriction);
        return restriction;
    }

    private Restriction buildLessThanOrEqualRestriction(final TypeRestrictionBuilder restrictionBuilder, final String attributeName,
        final Object attributeValue) {
        Restriction restriction;
        final Restriction lessThanRestriction = restrictionBuilder.lessThan(attributeName, attributeValue);
        final Restriction equalRestriction = buildEqualRestriction(restrictionBuilder, attributeName, attributeValue);
        restriction = restrictionBuilder.anyOf(lessThanRestriction, equalRestriction);
        return restriction;
    }

    private Restriction buildEqualRestriction(final TypeRestrictionBuilder restrictionBuilder, final String attribute, final Object value) {
        return restrictionBuilder.equalTo(attribute, value);
    }

    public Restriction buildRestrictionForNullValue(final TypeRestrictionBuilder restrictionBuilder, final String attribute) {
        return restrictionBuilder.nullValue(attribute);
    }
}
