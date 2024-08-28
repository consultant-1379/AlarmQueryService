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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;

/**
 *
 * Responsible for building the HQS restriction for single attribute based on the logical operator and its value.
 *
 *
 */

@ApplicationScoped
public class LogicalOperatorRestrictionBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogicalOperatorRestrictionBuilder.class);

    public Restriction build(final RestrictionBuilder restrictionBuilder, final String attributeName, final Object attributeValue,
                             final Object operator) {
        LOGGER.debug("restrion is forming for attribute :: {}  with value :: {} operator :: {}", attributeName, attributeValue, operator);
        Restriction restriction = null;
        switch ((Operator) operator) {
            case EQ:
                restriction = restrictionBuilder.equalTo(attributeName, attributeValue);
                break;
            case LE:
                restriction = restrictionBuilder.lessThanEqualTo(attributeName, attributeValue);
                break;
            case GE:
                restriction = restrictionBuilder.greaterThanEqualTo(attributeName, attributeValue);
                break;
            case GT:
                restriction = restrictionBuilder.greaterThan(attributeName, attributeValue);
                break;
            case LT:
                restriction = restrictionBuilder.lessThan(attributeName, attributeValue);
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
                restriction = restrictionBuilder.not(restrictionBuilder.equalTo(attributeName, attributeValue));
                break;
        }
        return restriction;
    }

}
