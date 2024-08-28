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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.DATE_FORMAT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.UTC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;

/**
 * Responsible for building the HQS restriction for date type attributes.
 */
@ApplicationScoped
public class DateRestrictionBuilder {

    @Inject
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    /**
     * Returns restriction which build based on the values of alarm attribute of Date type. HQS provides a millisecond granularity on time. So alarms
     * are stored with millisecond granularity. But as per the FM requirement,
     * a search on alarm should include all alarms upto "second" granularity. <br>
     * So this method creates two dates with 0th millisecond and 999th millisecond for a given second.
     * <p>
     * Eg. When 10:10:05.769 is an input to this method and operator EQ, it creates a list of dates with the following values: 1. 10:10:05.000 2.
     * 10:10:05.999 <br>
     * Eg. When 10:10:05.769 is an input to this method and operator LE, it creates a date with the following value : 10:10:05.999
     *
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param dateAttribute
     *            -- alarm attribute of Date type
     * @param unOrderedDates
     *            --{@code List<Date>} List of dates (may not be in ascending order)
     * @param dateComparisionOperator
     *            -- {@link Operator}
     * @return
     */
    public Restriction build(final RestrictionBuilder restrictionBuilder, final String dateAttribute, final List<Date> unOrderedDates,
                             final Operator dateComparisionOperator) {
        final List<Date> orderedDates = getOrderedDates(unOrderedDates);
        Restriction dateRestriction = null;
        final SimpleDateFormat dateFormatUTC = new SimpleDateFormat(DATE_FORMAT);
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone(UTC));

        if (dateComparisionOperator == Operator.BETWEEN) {
            if (orderedDates.size() == 2) {
                dateRestriction = buildBetweenRestriction(restrictionBuilder, dateAttribute, dateFormatUTC.format(orderedDates.get(0)),
                        dateFormatUTC.format(orderedDates.get(1)));
            } else {
                final StringBuilder reason = new StringBuilder();
                reason.append("Input date ").append(orderedDates).append(" it is not valid for the between operator of this query");
                throw new AttributeConstraintViolationException(reason.toString());
            }
        } else if (dateComparisionOperator == Operator.NOT_BETWEEN) {
            if (orderedDates.size() == 2) {
                dateRestriction = buildNotBetweenRestriction(restrictionBuilder, dateAttribute, dateFormatUTC.format(orderedDates.get(0)),
                        dateFormatUTC.format(orderedDates.get(1)));
            } else {
                final StringBuilder reason = new StringBuilder();
                reason.append("Input date ").append(orderedDates).append("is not valid for the not between operator of this query");
                throw new AttributeConstraintViolationException(reason.toString());
            }
        } else {
            if (orderedDates.get(0) != null) {
                dateRestriction = logicalOperatorRestrictionBuilder.build(restrictionBuilder, dateAttribute,
                        dateFormatUTC.format(orderedDates.get(0)), dateComparisionOperator);
            }
        }
        return dateRestriction;
    }

    /**
     * HQS between restriction, do between operation with excluding boundaries. <br>
     * This method including boundaries also in between Restriction.
     *
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param attribute
     *            -- alarm Attribute of type {@code Date}
     * @param startValue
     *            -- Starting value of dates range
     * @param endValue
     *            -- -- Ending value of dates range
     * @return -- between Restriction
     */
    private Restriction buildBetweenRestriction(final RestrictionBuilder restrictionBuilder, final String attribute, final String fromDate,
                                                final String toDate) {
        Restriction restriction = restrictionBuilder.between(attribute, fromDate, toDate);

        final Restriction fromDateEqualRestriction = restrictionBuilder.equalTo(attribute, fromDate);
        final Restriction toDateEqualRestriction = restrictionBuilder.equalTo(attribute, toDate);

        restriction = restrictionBuilder.anyOf(restriction, fromDateEqualRestriction, toDateEqualRestriction);

        return restriction;
    }

    /**
     * This method builds not between restriction. HQS between restriction does between operation with excluding boundaries.<br>
     * This method includes boundaries in between Restriction and finally forms a restrictionby negating the between restriction.
     *
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param attribute
     *            -- alarm Attribute of type {@code Date}
     * @param startValue
     *            -- Starting value of dates range
     * @param endValue
     *            -- -- Ending value of dates range
     * @return -- not between Restriction
     */
    private Restriction buildNotBetweenRestriction(final RestrictionBuilder restrictionBuilder, final String attribute, final String fromDate,
                                                   final String toDate) {
        return restrictionBuilder.not(buildBetweenRestriction(restrictionBuilder, attribute, fromDate, toDate));
    }

    /**
     * Returns a order list of dates in ascending order.
     *
     * @param unOrderedDates
     *            -- {@code List<Date>} dates
     * @return -- {@code List<Date>} list of dates in ascending order
     */
    private List<Date> getOrderedDates(final List<Date> unOrderedDates) {
        List<Date> orderedDates = new ArrayList<Date>(2);
        if (unOrderedDates.size() == 2) {
            if (unOrderedDates.get(0).after(unOrderedDates.get(1))) {

                orderedDates.add(unOrderedDates.get(1));
                orderedDates.add(unOrderedDates.get(0));
            } else {
                orderedDates = unOrderedDates;
            }
            return orderedDates;
        } else {
            return unOrderedDates;
        }
    }
}
