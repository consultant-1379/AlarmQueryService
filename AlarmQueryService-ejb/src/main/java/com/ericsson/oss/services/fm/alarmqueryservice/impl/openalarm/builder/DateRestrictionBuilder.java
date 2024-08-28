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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;

/**
 * Responsible for building the restriction for date type attributes.
 */

@ApplicationScoped
public class DateRestrictionBuilder {

    @Inject
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    /**
     * Returns restriction which builds based on the values of alarm attribute of Date type.
     * @param restrictionBuilder -- {@link RestrictionBuilder}
     * @param dateAttribute -- alarm attribute of Date type
     * @param unOrderedDates --{@code List<Date>} List of dates (may not be in ascending order)
     * @param dateComparisionOperator -- {@link Operator}
     * @return
     */

    public Restriction build(final TypeRestrictionBuilder restrictionBuilder, final String attribute, final List<Date> dates,
        final Operator operator) {
        Restriction restriction = null;
        final List<Date> finalDates = new ArrayList<Date>(2);
        List<Date> dateList = null;

        if (Operator.BETWEEN.equals(operator) || Operator.NOT_BETWEEN.equals(operator)) {
            dateList = new ArrayList<Date>(2);

            if (dates.size() == 2) {
                finalDates.addAll(dates);
            } else {
                final String reason = "For Between operator, both From and To Dates are required. From and/or To Date is missing";
                final AttributeConstraintViolationException attributeConstraintViolationException = new AttributeConstraintViolationException(reason);
                throw attributeConstraintViolationException;
            }
        } else {
            dateList = new ArrayList<Date>(1);
            dateList.add(dates.get(0));
        }

        if (Operator.BETWEEN.equals(operator)) {
            restriction = restrictionBuilder.between(attribute, finalDates.get(0), finalDates.get(1));
        } else if (Operator.NOT_BETWEEN.equals(operator)) {
            final Restriction lessThanRestriction = restrictionBuilder.lessThan(attribute, finalDates.get(0));
            final Restriction greaterThanRestriction = restrictionBuilder.greaterThan(attribute, finalDates.get(1));
            restriction = restrictionBuilder.anyOf(lessThanRestriction, greaterThanRestriction);
        } else {
            restriction = logicalOperatorRestrictionBuilder.build(restrictionBuilder, attribute, dateList.get(0), operator);
        }

        return restriction;
    }
}
