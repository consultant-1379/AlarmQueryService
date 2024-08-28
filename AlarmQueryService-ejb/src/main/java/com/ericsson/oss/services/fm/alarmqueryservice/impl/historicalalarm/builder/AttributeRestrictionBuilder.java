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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;

/**
 * Responsible for preparing the alarm attribute restriction for historical alarm attributes.
 */

@ApplicationScoped
public class AttributeRestrictionBuilder {

    private static final List<String> NUMBER_TYPE_ATTRIBUTES = new ArrayList<String>();
    private static final List<Operator> STRING_TYPE_OPERATORS = new ArrayList<Operator>();
    private static final List<String> DATE_TYPE_ATTRIBUTES = new ArrayList<String>();
    private static final List<String> BOOLEAN_TYPE_ATTRIBUTES = new ArrayList<String>();

    @Inject
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    @Inject
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Inject
    private OpenAlarmParser openAlarmParser;

    /**
     * Prepares the static list of number, boolean and date type attribute name list, also prepares string operator list.
     */
    @PostConstruct
    public void prepare() {
        NUMBER_TYPE_ATTRIBUTES.addAll(openAlarmParser.getIntegerTypeAttributes());
        NUMBER_TYPE_ATTRIBUTES.addAll(openAlarmParser.getLongTypeAttributes());
        DATE_TYPE_ATTRIBUTES.addAll(openAlarmParser.getDateTypeAttributes());
        BOOLEAN_TYPE_ATTRIBUTES.addAll(openAlarmParser.getBooleanTypeAttributes());

        STRING_TYPE_OPERATORS.add(Operator.CONTAINS);
        STRING_TYPE_OPERATORS.add(Operator.STARTS_WITH);
        STRING_TYPE_OPERATORS.add(Operator.ENDS_WITH);
    }

    /**
     * Returns a composite restriction which is build based on the different criteria set on single attribute.<br>
     * As HQS failing while querying for a query having more than one NOT restrictions, <br>
     * to avoid the same, method builds same restriction by making composite restriction using AND and applying NOT on it.
     * <p>
     * Eg : If we want to do OR operation on 2 NOT restrictions say ~R1, ~R2 , will be written as ~(R1 AND R2)
     *
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param alarmAttributes
     *            -- {@code List<{@link AlarmAttributeCriteria}>
     * @return -- {@link Restriction}
     */
    public Restriction build(final RestrictionBuilder restrictionBuilder, final List<AlarmAttributeCriteria> alarmAttributes) {
        Restriction attributeRestriction = null;
        boolean isNotEqual = false;

        for (final AlarmAttributeCriteria attribute : alarmAttributes) {
            final Restriction tempRestriction = buildRestrictionOnSpecificAttribute(restrictionBuilder, attribute.getAttributeName(),
                    attribute.getAttributeValue(), attribute.getOperator());

            if (tempRestriction != null) {
                if (Operator.NE == attribute.getOperator()) {
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
        }
        return attributeRestriction;
    }

    /**
     * Returns a restriction which is build based on the criteria set on single attribute.<br>
     *
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param attribute
     *            -- Alarm Attribute
     * @param value
     *            -- Attribute value
     * @param operator
     *            -- {@link Operator}
     * @return -- {@link Restriction} for single attribute
     */
    @SuppressWarnings("unchecked")
    private Restriction buildRestrictionOnSpecificAttribute(final RestrictionBuilder restrictionBuilder, final String attribute, final Object value,
                                                            final Operator operator) {
        Restriction restriction = null;
        if (operator != null) {

            if (DATE_TYPE_ATTRIBUTES.contains(attribute)) {
                final List<Date> dates = new ArrayList<Date>();
                if (STRING_TYPE_OPERATORS.contains(operator)) {
                    // contains, startswith and endswith operator are not applicable for date type attributes.
                    throwAttributeConstraintViolationException(attribute, operator);
                } else if (operator == Operator.BETWEEN || operator == Operator.NOT_BETWEEN) {
                    try {
                        dates.addAll((List<Date>) value);
                    } catch (final Exception e) {
                        final AttributeConstraintViolationException attributeConstraintViolationException = new AttributeConstraintViolationException(
                                e.getMessage());
                        throw attributeConstraintViolationException;
                    }
                } else {
                    dates.add((Date) value);
                }
                restriction = dateRestrictionBuilder.build(restrictionBuilder, attribute, dates, operator);
            } else {
                if ((NUMBER_TYPE_ATTRIBUTES.contains(attribute) || BOOLEAN_TYPE_ATTRIBUTES.contains(attribute))
                        && (Operator.BETWEEN == operator || STRING_TYPE_OPERATORS.contains(operator))) {
                    // Between, contains, startswith and endswith operator are not applicable for Number type and boolean type attributes.
                    throwAttributeConstraintViolationException(attribute, operator);
                } else {
                    restriction = logicalOperatorRestrictionBuilder.build(restrictionBuilder, attribute, value, operator);
                }
            }
        }
        return restriction;
    }

    /**
     * Builds and throws { @link AttributeConstraintViolationException } with given alarm attribute and Operator.
     * AttributeConstraintViolationException can be thrown which there is miss match in attribute and operator.
     *
     * @param attribute
     *            -- Alarm attribute
     * @param operator
     *            -- {@link Operator}
     */
    private void throwAttributeConstraintViolationException(final String attribute, final Operator operator) {
        final StringBuilder reasonBuilder = new StringBuilder();

        reasonBuilder.append("For attribute  ").append(attribute).append(" , given operator :: ").append(operator).append(" is not valid");

        final AttributeConstraintViolationException attributeConstraintViolationException = new AttributeConstraintViolationException(
                reasonBuilder.toString());

        throw attributeConstraintViolationException;
    }

}
