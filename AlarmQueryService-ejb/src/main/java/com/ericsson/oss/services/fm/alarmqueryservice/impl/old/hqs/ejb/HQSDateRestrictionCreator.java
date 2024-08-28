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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.BETWEEN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ELDER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EQUALOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.NOTEQUALOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.UTC;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.alarm.query.service.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.exception.InputFormatConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;

public class HQSDateRestrictionCreator {

    @Inject
    HQSOperatorBasedRestriction hqsOperatorBasedRestriction;

    public Restriction getDateRestriction(final RestrictionBuilder restrictionBuilder, final String dateAttribute, final List<Date> date,
                                          final DateOperator dateComparisionOperator) {
        Restriction dateRestriction = null;
        final SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone(UTC));

        if (dateComparisionOperator == DateOperator.BETWEEN) {
            if (date.size() == 2) {
                dateRestriction = hqsOperatorBasedRestriction.restrictionWithToAndFromDate(restrictionBuilder, dateAttribute,
                        dateFormatUTC.format(date.get(0)), dateFormatUTC.format(date.get(1).getTime() + 999L));
            } else {
                final StringBuilder reason = new StringBuilder();
                reason.append("Input date ").append(date).append(" it is not valid for the between operator of this query");
                final InputFormatConstraintViolationException inputFormatConstraintViolationException = new InputFormatConstraintViolationException(
                        reason.toString());
                throw inputFormatConstraintViolationException;
            }
        } else if (DateOperator.LE == dateComparisionOperator) {
            dateRestriction = hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, dateAttribute,
                    dateFormatUTC.format(date.get(0).getTime() + 999L), dateComparisionOperator);
        } else if (DateOperator.EQ == dateComparisionOperator) {
            dateRestriction = hqsOperatorBasedRestriction.restrictionWithToAndFromDate(restrictionBuilder, dateAttribute,
                    dateFormatUTC.format(date.get(0)), dateFormatUTC.format(date.get(1).getTime() + 999L));
        } else {
            dateRestriction = hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, dateAttribute,
                    dateFormatUTC.format(date.get(0)), dateComparisionOperator);
        }

        return dateRestriction;
    }

    public Restriction getRestrictionOnDateAttributes(final RestrictionBuilder restrictionBuilder, final String attribute, final String value,
                                                      final String operator, Restriction restriction, final String dateFormat) {
        final List<Date> dateAttribute = new ArrayList<>();
        final DateFormat dateFormatFromUI = new SimpleDateFormat(dateFormat);
        try {
            if (BETWEEN.equalsIgnoreCase(operator)) {
                final String[] dateValues = value.split(",");
                if (dateValues.length == 2) {
                    if (dateFormatFromUI.parse(dateValues[0]).after(dateFormatFromUI.parse(dateValues[1]))) {
                        dateAttribute.add(dateFormatFromUI.parse(dateValues[1]));
                        dateAttribute.add(dateFormatFromUI.parse(dateValues[0]));
                    } else {
                        dateAttribute.add(dateFormatFromUI.parse(dateValues[0]));
                        dateAttribute.add(dateFormatFromUI.parse(dateValues[1]));
                    }
                    restriction = getDateRestriction(restrictionBuilder, attribute, dateAttribute, DateOperator.BETWEEN);
                } else {
                    final StringBuilder reason = new StringBuilder();
                    reason.append("Date ").append(value).append(" is invalid for between operations for").append(attribute).append(" in this query");
                    throw new AttributeConstraintViolationException(reason.toString(), attribute, value);
                }
            } else {
                dateAttribute.add(dateFormatFromUI.parse(value));
                dateAttribute.add(dateFormatFromUI.parse(value));

                if (EQUALOPERATOR.equalsIgnoreCase(operator)) {
                    restriction = getDateRestriction(restrictionBuilder, attribute, dateAttribute, DateOperator.EQ);
                } else if (NOTEQUALOPERATOR.equalsIgnoreCase(operator)) {
                    restriction = restrictionBuilder.not(getDateRestriction(restrictionBuilder, attribute, dateAttribute, DateOperator.EQ));
                } else if (ELDER.equalsIgnoreCase(operator)) {
                    restriction = getDateRestriction(restrictionBuilder, attribute, dateAttribute, DateOperator.LE);
                } else {
                    restriction = getDateRestriction(restrictionBuilder, attribute, dateAttribute, DateOperator.GE);
                }
            }
        } catch (final ParseException e) {
            final StringBuilder reason = new StringBuilder();
            reason.append("Date ").append(value).append(" is not valid for ").append(attribute).append(" of this query");
            throw new AttributeConstraintViolationException(reason.toString(), attribute, value);
        }
        return restriction;
    }

}