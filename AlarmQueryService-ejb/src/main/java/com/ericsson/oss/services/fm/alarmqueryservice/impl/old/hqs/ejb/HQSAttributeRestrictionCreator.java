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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ACKTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMNUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.BACKUPSTATUS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CEASETIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.DELIMETER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FALSE_AS_STRING;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.INSERTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.NOTEQUALOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OSCILLATIONCOUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.REPEATCOUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SYNC_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.TRUE_AS_STRING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.alarm.query.service.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.exception.InputFormatConstraintViolationException;

public class HQSAttributeRestrictionCreator {

    @Inject
    private HQSDateRestrictionCreator hqsDateRestrictionCreator;

    @Inject
    private HQSOperatorBasedRestriction hqsOperatorBasedRestriction;

    public Restriction getAttributesRestriction(final RestrictionBuilder restrictionBuilder, final List<String> alarmAttributes,
                                                final String dateFormat) {
        Restriction attributeRestriction = null;

        final Map<String, List<String>> alarmAttributeSplit = new HashMap<>();

        for (final String attribute : alarmAttributes) {
            final String[] attributeArray = attribute.split(DELIMETER);
            final List<String> attributeList = new ArrayList<String>();
            if (alarmAttributeSplit.containsKey(attributeArray[0])) {
                alarmAttributeSplit.get(attributeArray[0]).add(attribute);
            } else {
                attributeList.add(attribute);
                alarmAttributeSplit.put(attributeArray[0], attributeList);
            }
        }

        for (final List<String> attributeValues : alarmAttributeSplit.values()) {
            final Restriction restriction = getLogicalConditionRestriction(restrictionBuilder, attributeValues, dateFormat);
            if (attributeRestriction == null) {
                attributeRestriction = restriction;
            } else {
                attributeRestriction = restrictionBuilder.allOf(attributeRestriction, restriction);
            }
        }
        return attributeRestriction;
    }

    private Restriction getLogicalConditionRestriction(final RestrictionBuilder restrictionBuilder, final List<String> alarmAttributes,
                                                       final String dateFormat) {
        Restriction attributeRestriction = null;
        boolean isNotEqual = false;
        for (final String attribute : alarmAttributes) {
            final String[] alarmAttributesArray = attribute.split(DELIMETER);
            if (alarmAttributesArray.length == 3) {
                final Restriction tempRestriction = getRestrictionOnSpecificAttribute(restrictionBuilder, alarmAttributesArray[0],
                        alarmAttributesArray[1], alarmAttributesArray[2], dateFormat);
                if (tempRestriction != null) {
                    if (NOTEQUALOPERATOR.equalsIgnoreCase(alarmAttributesArray[2])) {
                        isNotEqual = true;
                    }
                    if (attributeRestriction == null) {
                        attributeRestriction = tempRestriction;
                    } else {
                        if (!isNotEqual) {
                            attributeRestriction = restrictionBuilder.anyOf(tempRestriction, attributeRestriction);
                        } else {
                            attributeRestriction = restrictionBuilder.allOf(tempRestriction, attributeRestriction);
                            isNotEqual = false;
                        }
                    }
                }
            } else {
                final StringBuilder reason = new StringBuilder();
                reason.append("Input").append(attribute).append(" is not valid input format for this query");
                throw new InputFormatConstraintViolationException(reason.toString());
            }
        }
        return attributeRestriction;
    }

    private Restriction getRestrictionOnSpecificAttribute(final RestrictionBuilder restrictionBuilder, final String attribute, final String value,
                                                          final String operator, final String dateFormat) {
        Restriction restriction = null;

        if ((ALARMNUMBER.equalsIgnoreCase(attribute)) || (ALARMID.equalsIgnoreCase(attribute))) {
            final Long longValue = Long.parseLong(value);
            restriction = hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, attribute, longValue, operator);
        } else if ((REPEATCOUNT.equalsIgnoreCase(attribute)) || (OSCILLATIONCOUNT.equalsIgnoreCase(attribute))) {
            final Integer attributeValue = Integer.parseInt(value);
            restriction = hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, attribute, attributeValue, operator);
        } else if ((BACKUPSTATUS.equalsIgnoreCase(attribute)) || (SYNC_STATE.equalsIgnoreCase(attribute))) {
            if ((TRUE_AS_STRING.equalsIgnoreCase(value)) || (FALSE_AS_STRING.equalsIgnoreCase(value))) {
                final Boolean booleanValue = Boolean.parseBoolean(value);
                restriction = restrictionBuilder.equalTo(attribute, booleanValue);
            } else {
                final StringBuilder reason = new StringBuilder();
                reason.append("Value ").append(value).append(" is not valid for ").append(attribute).append(" of this query");
                throw new AttributeConstraintViolationException(reason.toString(), attribute, value);
            }
        } else if (CEASETIME.equalsIgnoreCase(attribute) || (EVENTTIME.equalsIgnoreCase(attribute)) || (ACKTIME.equalsIgnoreCase(attribute))
                || (INSERTTIME.equalsIgnoreCase(attribute))) {
            restriction = hqsDateRestrictionCreator.getRestrictionOnDateAttributes(restrictionBuilder, attribute, value, operator, restriction,
                    dateFormat);
        } else {
            restriction = hqsOperatorBasedRestriction.getRestrictionOnMatchCondition(restrictionBuilder, attribute, value, operator);
        }
        return restriction;
    }

}