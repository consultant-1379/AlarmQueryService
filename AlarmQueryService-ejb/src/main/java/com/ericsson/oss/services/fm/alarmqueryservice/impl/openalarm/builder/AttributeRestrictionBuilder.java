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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_POID_AS_STRING;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.DYNAMIC_ALARM_ATTRIBUTE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.DYNAMIC_ALARM_ATTRIBUTE_VALUE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.dps.query.ObjectField;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.LogicalCondition;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;

/**
 * Responsible for building Composite Restriction based on conditions set on alarm attributes.
 */

@ApplicationScoped
public class AttributeRestrictionBuilder {

    private static final List<String> NUMBER_TYPE_ATTRIBUTES = new ArrayList<String>();
    private static final List<String> DATE_TYPE_ATTRIBUTES = new ArrayList<String>();
    private static final List<String> BOOLEAN_TYPE_ATTRIBUTES = new ArrayList<String>();
    private static final List<Operator> STRING_TYPE_OPERATORS = new ArrayList<Operator>();

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

        if (!openAlarmParser.isAttributesLoaded()) {
            openAlarmParser.extractAttributesFromModel();
        }
        NUMBER_TYPE_ATTRIBUTES.addAll(openAlarmParser.getIntegerTypeAttributes());
        NUMBER_TYPE_ATTRIBUTES.addAll(openAlarmParser.getLongTypeAttributes());
        DATE_TYPE_ATTRIBUTES.addAll(openAlarmParser.getDateTypeAttributes());
        BOOLEAN_TYPE_ATTRIBUTES.addAll(openAlarmParser.getBooleanTypeAttributes());

        STRING_TYPE_OPERATORS.add(Operator.CONTAINS);
        STRING_TYPE_OPERATORS.add(Operator.STARTS_WITH);
        STRING_TYPE_OPERATORS.add(Operator.ENDS_WITH);
    }

    /**
     * Returns the composite restriction for the all the attribute criteria given. <br> All the criteria set on same attribute are clubbed with OR
     * operator, <br> criteria defined on different attributes are clubbed with AND.
     * @param restrictionBuilder -- Restriction Builder to build Restriction.
     * @param alarmAttributeCriteria
     * @return compositeRestriction -- composite restriction formed by the criteria.
     */
    public Restriction build(final TypeRestrictionBuilder restrictionBuilder, final List<AlarmAttributeCriteria> alarmAttributeCriteria) {
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        if (alarmAttributeCriteria != null && !alarmAttributeCriteria.isEmpty()) {
            final Map<String, List<AlarmAttributeCriteria>> sortedAlarmAttributes = groupAlarmAttributes(alarmAttributeCriteria);
            if (sortedAlarmAttributes.containsKey(EVENT_POID_AS_STRING)) {
                final Restriction eventPoIdRestriction =
                        buildEventPoIdRestriction(restrictionBuilder, sortedAlarmAttributes.get(EVENT_POID_AS_STRING));
                if (eventPoIdRestriction != null) {
                    restrictions.add(eventPoIdRestriction);
                }
                sortedAlarmAttributes.remove(EVENT_POID_AS_STRING);
            }
            for (final Entry<String, List<AlarmAttributeCriteria>> attributeEntrySet : sortedAlarmAttributes.entrySet()) {
                final Restriction restriction = buildAttributeRestriction(restrictionBuilder, attributeEntrySet.getValue());
                if (restriction != null) {
                    restrictions.add(restriction);
                }
            }
        }
        return restrictionBuilder.allOf(restrictions.toArray(new Restriction[restrictions.size()]));
    }

    /**
     * Grouping is required to consolidate different criteria set on a single alarm attribute and do a OR operation. <br> Method returns a Map having
     * key as attribute and value as list of AlarmAttributeCriteria defined on attribute(key)
     * @param alarmAttributeCriteria -- {@link AlarmAttributeCriteria}
     * @return Map, <br> Key -- Alarm Attribute <br> Value -- all the Criteria defined on Alarm Attribute(key).
     */
    public Map<String, List<AlarmAttributeCriteria>> groupAlarmAttributes(final List<AlarmAttributeCriteria> alarmAttributeCriteria) {
        final Map<String, List<AlarmAttributeCriteria>> sortedAlarmAttributesMap = new HashMap<String, List<AlarmAttributeCriteria>>();

        for (final AlarmAttributeCriteria criterion : alarmAttributeCriteria) {
            final String attributeName = criterion.getAttributeName();
            final Object attributeValue = criterion.getAttributeValue();
            final Operator operator = criterion.getOperator();
            if (attributeName != null && attributeValue != null && operator != null) {
                final List<AlarmAttributeCriteria> attributes = sortedAlarmAttributesMap.get(attributeName);
                final List<AlarmAttributeCriteria> attributeList = new ArrayList<AlarmAttributeCriteria>();

                if (attributes != null) {
                    attributes.add(criterion);
                } else {
                    attributeList.add(criterion);
                    sortedAlarmAttributesMap.put(attributeName, attributeList);
                }
            } else {
                throw new RuntimeException("Attribute Name / Attribute Value given is invalid");
            }
        }
        return sortedAlarmAttributesMap;
    }

    /**
     * Returns a eventPoId's IN restriction which is build based on the different event poId's criteria set on single attribute.<br>
     * @param restrictionBuilder -- {@link TypeRestrictionBuilder}
     * @param criteria -- {@link AlarmAttributeCriteria}
     * @return {@link Restriction} -- IN restriction formed by the criteria.
     */
    private Restriction buildEventPoIdRestriction(final TypeRestrictionBuilder restrictionBuilder, final List<AlarmAttributeCriteria> criteria) {
        final List<Long> eventPoIds = new ArrayList<Long>();
        for (final AlarmAttributeCriteria poIdCriteria : criteria) {
            eventPoIds.add((Long) poIdCriteria.getAttributeValue());
        }
        if (!eventPoIds.isEmpty()) {
            return restrictionBuilder.in(ObjectField.PO_ID, eventPoIds.toArray());
        }
        return null;
    }

    /**
     * Returns a composite restriction which is build based on the different criteria set on single attribute.<br>
     * @param restrictionBuilder -- {@link TypeRestrictionBuilder}
     * @param criteria -- {@link AlarmAttributeCriteria}
     * @return compositeRestriction -- composite restriction formed by the criteria.
     */
    private Restriction buildAttributeRestriction(
           final TypeRestrictionBuilder restrictionBuilder,
           final List<AlarmAttributeCriteria> criteria) {
        final List<Restriction> orConditionRestrictions = new ArrayList<Restriction>();
        final List<Restriction> andConditionRestrictions = new ArrayList<Restriction>();
        boolean notBetweenSelected = false;
        Restriction notNullRestriction = null;
        for (final AlarmAttributeCriteria criterion : criteria) {
            if (Operator.NOT_BETWEEN.equals(criterion.getOperator()) && !notBetweenSelected) {
                notBetweenSelected = true;
                // For NE or NOT_BETWEEN Restrictions, it is mandatory to add NotNull restriction for neo4j.
                // Though we have multiple NE/NOT_BETWEEN restrictions, only one NOT_NULL restriction need to be added.
                notNullRestriction = logicalOperatorRestrictionBuilder.build(restrictionBuilder, criterion.getAttributeName(),
                        criterion.getAttributeValue(), Operator.NE);
            }
            if (LogicalCondition.AND.equals(criterion.getLogicalCondition())) {
                final Restriction tempRestriction = buildSingleAttributeRestriction(restrictionBuilder, criterion.getAttributeName(),
                        criterion.getAttributeValue(), criterion.getOperator());
                if (tempRestriction != null) {
                    andConditionRestrictions.add(tempRestriction);
                }
            } else {
                final Restriction tempRestriction = buildSingleAttributeRestriction(restrictionBuilder, criterion.getAttributeName(),
                        criterion.getAttributeValue(), criterion.getOperator());
                if (tempRestriction != null) {
                    orConditionRestrictions.add(tempRestriction);
                }
            }
        }
        Restriction compositeOrRestriction = null;

        if (!orConditionRestrictions.isEmpty()) {
            if (orConditionRestrictions.size() > 1) {
                compositeOrRestriction =
                        restrictionBuilder.anyOf(orConditionRestrictions.toArray(new Restriction[orConditionRestrictions.size()]));
            } else {
                compositeOrRestriction = orConditionRestrictions.get(0);
            }
        }
        if (!andConditionRestrictions.isEmpty()) {
            if (compositeOrRestriction != null) {
                andConditionRestrictions.add(compositeOrRestriction);
            }
            if (andConditionRestrictions.size() > 1) {
                compositeOrRestriction = restrictionBuilder.allOf(andConditionRestrictions.toArray(new Restriction[andConditionRestrictions.size()]));
            } else {
                compositeOrRestriction = andConditionRestrictions.get(0);
            }
        }
        if (notNullRestriction != null) {
            return restrictionBuilder.anyOf(compositeOrRestriction, notNullRestriction);
        } else {
            return compositeOrRestriction;
        }


    }

    /**
     * Returns the composite restriction for the all the additional attribute criteria given.<br>
     * @param restrictionBuilder -- {@link TypeRestrictionBuilder}
     * @param alarmAttributeCriteria -- {@link AlarmAttributeCriteria}
     * @return compositeRestriction -- composite restriction formed by the criteria.
     */
    public Restriction buildAdditionalAttributesRestriction(final TypeRestrictionBuilder restrictionBuilder,
            final List<AlarmAttributeCriteria> alarmAttributeCriteria, final String attributeName) {
        final List<Restriction> attributeRestrictions = new ArrayList<Restriction>();
        attributeRestrictions.add(buildSingleAttributeRestriction(restrictionBuilder, DYNAMIC_ALARM_ATTRIBUTE, attributeName, Operator.EQ));

        final Restriction attributeValueRestriction = buildAdditionalAttributeValueRestriction(restrictionBuilder, alarmAttributeCriteria);
        if (attributeValueRestriction != null) {
            attributeRestrictions.add(attributeValueRestriction);
        }
        return restrictionBuilder.allOf(restrictionBuilder.allOf(attributeRestrictions.toArray(new Restriction[attributeRestrictions.size()])));
    }

    private Restriction buildAdditionalAttributeValueRestriction(final TypeRestrictionBuilder restrictionBuilder,
            final List<AlarmAttributeCriteria> criterias) {
        final List<Restriction> restrictions = new ArrayList<Restriction>();

        for (final AlarmAttributeCriteria criteria : criterias) {
            final Restriction tempValueRestriction = buildSingleAttributeRestriction(restrictionBuilder, DYNAMIC_ALARM_ATTRIBUTE_VALUE,
                    criteria.getAttributeValue(), criteria.getOperator());
            if (tempValueRestriction != null) {
                restrictions.add(tempValueRestriction);
            }
        }
        return restrictionBuilder.anyOf(restrictions.toArray(new Restriction[restrictions.size()]));
    }

    /**
     * Returns a restriction which is build based on the criteria set on single attribute.<br>
     * @param restrictionBuilder -- {@link TypeRestrictionBuilder}
     * @param attribute -- Alarm Attribute
     * @param value -- Attribute value
     * @param operator -- {@link Operator}
     * @return -- {@link Restriction} for single attribute
     */
    public Restriction buildSingleAttributeRestriction(final TypeRestrictionBuilder restrictionBuilder, final String attribute, final Object value,
            final Operator operator) {
        Restriction restriction = null;
        if (operator != null) {
            if (DATE_TYPE_ATTRIBUTES.contains(attribute)) {
                final List<Date> dates = new ArrayList<Date>();

                if (STRING_TYPE_OPERATORS.contains(operator)) {
                    // contains, startswith and endswith operator are not applicable for date type attributes.
                    throwAttributeConstraintViolationException(attribute, operator);
                } else {
                    if (operator != null & (operator == Operator.BETWEEN || operator == Operator.NOT_BETWEEN)) {
                        try {
                            dates.addAll((List<Date>) value);
                        } catch (final Exception e) {
                            final AttributeConstraintViolationException attributeConstraintViolationException =
                                    new AttributeConstraintViolationException(
                                            e.getMessage());
                            throw attributeConstraintViolationException;
                        }
                    } else {
                        dates.add((Date) value);
                    }
                    restriction = dateRestrictionBuilder.build(restrictionBuilder, attribute, dates, operator);
                }
            } else {
                if ((NUMBER_TYPE_ATTRIBUTES.contains(attribute) || BOOLEAN_TYPE_ATTRIBUTES.contains(attribute))
                        && (Operator.BETWEEN == operator || STRING_TYPE_OPERATORS.contains(operator) || operator == Operator.NOT_BETWEEN)) {
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
     * @param attribute -- Alarm attribute
     * @param operator -- {@link Operator}
     */
    private void throwAttributeConstraintViolationException(final String attribute, final Operator operator) {
        final StringBuilder reasonBuilder = new StringBuilder();

        reasonBuilder.append("For attribute  ").append(attribute).append(" , given operator :: ").append(operator).append(" is not valid");

        final AttributeConstraintViolationException attributeConstraintViolationException = new AttributeConstraintViolationException(
                reasonBuilder.toString());

        throw attributeConstraintViolationException;
    }
}
