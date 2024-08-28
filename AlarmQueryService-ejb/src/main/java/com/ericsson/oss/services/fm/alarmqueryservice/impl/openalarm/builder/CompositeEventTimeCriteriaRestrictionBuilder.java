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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;

/**
 * Responsible for building Composite Restriction based on conditions set in {@link CompositeEventTimeCriteria}.
 */

@ApplicationScoped
public class CompositeEventTimeCriteriaRestrictionBuilder {

    // TODO: Use decorator pattern to build composite restriction
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeEventTimeCriteriaRestrictionBuilder.class);

    @Inject
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Inject
    private DateRestrictionBuilder dateRestrictionBuilder;

    @Inject
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    @Inject
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    /**
     * Returns a composite restriction, which is build based on {@link CompositeEventTimeCriteria}.<br>
     * Composite restriction a combination of event times, alarm attributes and nodes.
     * @param typeQuery
     *            {@link Query}
     * @param compositeEventTimeCriteria
     *            {@link CompositeEventTimeCriteria}
     * @return compositeRestriction -- composite restriction formed by the given criteria.
     */
    public Restriction build(final Query<TypeRestrictionBuilder> typeQuery, final CompositeEventTimeCriteria compositeEventTimeCriteria) {

        final TypeRestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();

        final List<Date> eventTimesList = getEventTimesList(compositeEventTimeCriteria);
        Restriction finalRestriction = null;

        if (!eventTimesList.isEmpty()) {
            // Builds event time based restriction
            final Operator dateOperator = compositeEventTimeCriteria.getOperator();

            final Restriction eventTimeRestriction = dateRestrictionBuilder.build(restrictionBuilder, EVENT_TIME, eventTimesList, dateOperator);
            finalRestriction = eventTimeRestriction;
        }
        List<AlarmAttributeCriteria> alarmAttributeCriteria = null;
        if (compositeEventTimeCriteria.getAlarmAttributeCriteria() != null) {
            alarmAttributeCriteria = new ArrayList<AlarmAttributeCriteria>(compositeEventTimeCriteria.getAlarmAttributeCriteria());
        }
        Restriction attributeCriteriaRestriction = null;

        if (alarmAttributeCriteria != null && !alarmAttributeCriteria.isEmpty()) {
            // Builds a combination of event time, nodes and alarm attributes restriction
            // Added a check to removed AlarmAttibuteCriteria containing fdn as this will be added to nodeRestrictions to provide OR
            // support for nodes.
            final Iterator<AlarmAttributeCriteria> iterator = alarmAttributeCriteria.iterator();
            while (iterator.hasNext()) {
                final AlarmAttributeCriteria alarmCriteria = iterator.next();
                if (FDN.equalsIgnoreCase(alarmCriteria.getAttributeName())) {
                    iterator.remove();
                }
            }
            if (!alarmAttributeCriteria.isEmpty()) {
                attributeCriteriaRestriction = attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributeCriteria);
                finalRestriction = logicalOperatorRestrictionBuilder.buildCompositeRestrictionByAnd(restrictionBuilder, finalRestriction,
                        attributeCriteriaRestriction);
            } else {
                LOGGER.debug("No restrictions other than fdn in AlarmAttributeCriteria..");
            }
        }
        // Only alarms which are having VISIBILITY value true, will be retrieved from DPS.
        final Restriction visibilityRestriction = restrictionBuilder.equalTo(VISIBILITY, true);
        finalRestriction = logicalOperatorRestrictionBuilder.buildCompositeRestrictionByAnd(restrictionBuilder, finalRestriction,
                visibilityRestriction);
        return finalRestriction;
    }

    /**
     * Returns event time list, which build from fromTime and toTime attributes of {@link CompositeEventTimeCriteria}. <br>
     * If Operator is other than LE / LT we will consider startTime. <br>
     * If Operator is other than GE / GT method considers endTime
     * @param compositeEventTimeCriteria
     *            -- {@link CompositeEventTimeCriteria}
     * @return -- event times ({@code List<Date> })
     */
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
     * Returns the list of Node restrictions, each restriction contains maximum of 3000 nodes.
     * @param typeQuery
     *            -- {@link Query}
     * @param compositeEventTimeCriteria
     *            -- {@link CompositeEventTimeCriteria}
     * @return ({@code List<{@link Restriction> })
     */

    public List<Restriction> buildNodeRestrictions(final Query<TypeRestrictionBuilder> typeQuery,
            final CompositeEventTimeCriteria compositeEventTimeCriteria) {
        final List<String> nodes = compositeEventTimeCriteria.getNodes();

        final List<Restriction> nodeRestrictions = new ArrayList<Restriction>();

        // Read AlarmAttributeCriteria for fdn attribute and add it to nodeRestrictions to provide OR support for nodes.
        final List<AlarmAttributeCriteria> alarmAttributeCriteria = compositeEventTimeCriteria.getAlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> fdnAlarmCriteria = new ArrayList<AlarmAttributeCriteria>();
        if (alarmAttributeCriteria != null && !alarmAttributeCriteria.isEmpty()) {
            final Iterator<AlarmAttributeCriteria> iterator = alarmAttributeCriteria.iterator();
            while (iterator.hasNext()) {
                final AlarmAttributeCriteria criteria = iterator.next();
                if (FDN.equalsIgnoreCase(criteria.getAttributeName())) {
                    fdnAlarmCriteria.add(criteria);
                }
            }
        }
        if (!fdnAlarmCriteria.isEmpty()) {
            final Restriction restriction = attributeRestrictionBuilder.build(typeQuery.getRestrictionBuilder(), fdnAlarmCriteria);
            nodeRestrictions.add(restriction);
        }

        if (nodes != null && !nodes.isEmpty()) {
            // Builds nodes restriction
            final List<Restriction> restriction = nodeRestrictionBuilder.buildListOfNodeRestrictions(typeQuery.getRestrictionBuilder(), nodes);
            nodeRestrictions.addAll(restriction);
        }
        return nodeRestrictions;
    }
}
