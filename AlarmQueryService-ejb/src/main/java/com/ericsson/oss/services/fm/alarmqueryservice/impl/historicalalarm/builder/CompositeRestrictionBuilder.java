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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_POID_AS_STRING;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;

/**
 * Responsible for building composite restriction based on conditions set in {@link CompositeEventTimeCriteria}. <br>
 * This class builds the event time, nodes and Alarm Attribute restrictions.
 */

@ApplicationScoped
public class CompositeRestrictionBuilder {

    // TODO: Use decorator pattern to build composite restriction

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeRestrictionBuilder.class);

    @Inject
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Inject
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Inject
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    /**
     * Returns a composite restriction, which is build based on the combination of all the provided inputs.
     * @param restrictionBuilder
     *            {@link RestrictionBuilder}
     * @param compositeEventTimeCriteria
     *            {@link CompositeEventTimeCriteria}
     * @return compositeRestriction -- composite restriction formed by the criteria.
     */
    public Restriction build(final RestrictionBuilder restrictionBuilder, final CompositeEventTimeCriteria compositeEventTimeCriteria) {
        Restriction compositeRestriction = null;
        final List<Date> eventTimeList = getEventTimesList(compositeEventTimeCriteria);

        if (!eventTimeList.isEmpty()) {
            // Builds event time based restriction
            final Restriction dateRestriction = dateRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimeList,
                    compositeEventTimeCriteria.getOperator());
            compositeRestriction = dateRestriction;
        }

        final List<AlarmAttributeCriteria> alarmAttributes = compositeEventTimeCriteria.getAlarmAttributeCriteria();

        if (alarmAttributes != null && !alarmAttributes.isEmpty()) {
            // Builds a combination of event time and alarm attributes restriction
            final Map<String, List<AlarmAttributeCriteria>> groupAlarmAttributes = groupAlarmAttributes(alarmAttributes);
            // Will be handled further in the code added to nodeRestriction.
            groupAlarmAttributes.remove(FDN);
            for (final List<AlarmAttributeCriteria> attributeValues : groupAlarmAttributes.values()) {
                final Restriction restriction = attributeRestrictionBuilder.build(restrictionBuilder, attributeValues);
                compositeRestriction = buildCompositeRestrictionByAnd(restrictionBuilder, compositeRestriction, restriction);
            }
        }
        return compositeRestriction;
    }

    /**
     * Returns node restrictions, for better performance from HQS each restriction will be having maximum of 750(configurable) nodes.
     * <p>
     * Eg. When 1400 fdns are input to this method, 2 restrictions formed each having nodes of 750 and 650 are created.
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param compositeEventTimeCriteria
     *            -- {@link CompositeEventTimeCriteria}
     * @return --{@code List<{@link Restriction}>}
     */
    public Restriction buildNodeRestrictions(final RestrictionBuilder restrictionBuilder,
                                             final CompositeEventTimeCriteria compositeEventTimeCriteria) {
        final List<String> nodes = compositeEventTimeCriteria.getNodes();
        final List<AlarmAttributeCriteria> alarmAttributes = compositeEventTimeCriteria.getAlarmAttributeCriteria();
        if (alarmAttributes != null && !alarmAttributes.isEmpty()) {
            // Builds a combination of event time and alarm attributes restriction
            final Map<String, List<AlarmAttributeCriteria>> groupAlarmAttributes = groupAlarmAttributes(alarmAttributes);
            final List<AlarmAttributeCriteria> fdnAlarmAttributeCriteria = groupAlarmAttributes.get(FDN);
            if (fdnAlarmAttributeCriteria != null && !fdnAlarmAttributeCriteria.isEmpty()) {
                final Restriction restriction = attributeRestrictionBuilder.build(restrictionBuilder, fdnAlarmAttributeCriteria);
                final Restriction nodesInRestriction = nodeRestrictionBuilder.build(restrictionBuilder, nodes);
                return buildCompositeRestrictionWithOROperand(restrictionBuilder, restriction, nodesInRestriction);
            } else {
                LOGGER.debug("fdnAlarmAttributeCriteria is null/empty.So only nodes are to be considered.");
                return nodeRestrictionBuilder.build(restrictionBuilder, nodes);
            }
        }
        return nodeRestrictionBuilder.build(restrictionBuilder, nodes);
    }

    private List<Date> getEventTimesList(final CompositeEventTimeCriteria compositeEventTimeCriteria) {
        final Operator dateOperator = compositeEventTimeCriteria.getOperator();
        final Date startDate = compositeEventTimeCriteria.getFromTime();
        final Date endDate = compositeEventTimeCriteria.getToTime();

        final List<Date> eventTimes = new ArrayList<Date>(2);
        if (dateOperator != null) {
            if (!(dateOperator == Operator.LE || dateOperator == Operator.LT)) {
                if (startDate != null) {
                    eventTimes.add(startDate);
                }
            }
            if (!(dateOperator == Operator.GE || dateOperator == Operator.GT)) {
                if (endDate != null) {
                    eventTimes.add(endDate);
                }
            }
        }
        return eventTimes;
    }

    /**
     * Returns a compositeRestriction by combining restrictions with OR operation.
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param compositeRestriction
     *            -- final restriction
     * @param restrictionToBeAdded
     *            -- Restriction need to be added
     * @return -- compositeRestriction after OR operation.
     */
    public Restriction buildCompositeRestrictionWithOROperand(final RestrictionBuilder restrictionBuilder, final Restriction compositeRestriction,
                                                              final Restriction restrictionToBeAdded) {
        Restriction finalRestriction = compositeRestriction;

        if (restrictionToBeAdded != null) {
            if (compositeRestriction != null) {
                finalRestriction = restrictionBuilder.anyOf(compositeRestriction, restrictionToBeAdded);
            } else {
                finalRestriction = restrictionToBeAdded;
            }
        }
        return finalRestriction;
    }

    /**
     * Returns a compositeRestriction by combining restrictions with AND operation.
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param compositeRestriction
     *            -- final restriction
     * @param restrictionToBeAdded
     *            -- Restriction need to be added
     * @return -- compositeRestriction after AND operation.
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

    /**
     * Grouping is required to consolidate different criteria set on a single alarm attribute and do a OR operation. <br>
     * Method returns a Map having key as attribute and value as list of AlarmAttributeCriteria defined on attribute(key)
     * @param alarmAttributeCriteria
     *            -- {@link AlarmAttributeCriteria}
     * @return Map, <br>
     *         Key -- Alarm Attribute <br>
     *         Value -- all the Criteria defined on Alarm Attribute(key).
     */

    private Map<String, List<AlarmAttributeCriteria>> groupAlarmAttributes(final List<AlarmAttributeCriteria> alarmAttributes) {
        final Map<String, List<AlarmAttributeCriteria>> sortedAlarmAttributesMap = new HashMap<String, List<AlarmAttributeCriteria>>();

        for (final AlarmAttributeCriteria attribute : alarmAttributes) {
            String attributeName = attribute.getAttributeName();

            final List<AlarmAttributeCriteria> attributeList = new ArrayList<AlarmAttributeCriteria>();
            if (EVENT_POID_AS_STRING.equals(attributeName)) {
                attributeName = EVENT_PO_ID;
                attribute.setAttributeName(attributeName);
            }

            if (sortedAlarmAttributesMap.containsKey(attributeName)) {
                sortedAlarmAttributesMap.get(attributeName).add(attribute);
            } else {
                attributeList.add(attribute);
                sortedAlarmAttributesMap.put(attributeName, attributeList);
            }
        }
        return sortedAlarmAttributesMap;
    }
}
