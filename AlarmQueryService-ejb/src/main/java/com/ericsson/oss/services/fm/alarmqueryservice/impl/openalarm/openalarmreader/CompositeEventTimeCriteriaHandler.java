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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PREVIOUS_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PSEUDO_PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PSEUDO_PREVIOUS_SEVERITY;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.SortDirection;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.CompositeEventTimeCriteriaRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible for retrieving the alarms based on the conditions set in {@link CompositeEventTimeCriteria}.
 **/

@Stateless
public class CompositeEventTimeCriteriaHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeEventTimeCriteriaHandler.class);

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private AlarmReader alarmReader;

    @Inject
    private CompositeEventTimeCriteriaRestrictionBuilder compositeEventTimeRestrictionBuilder;

    @Inject
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;

    /**
     * Returns the {@link AlarmAttributeResponse} for the conditions set in {@link CompositeEventTimeCriteria} <br>
     * {@link AlarmAttributeResponse} may contain all the attributes of specific attributes based on the conditions set in
     * {@link ExpectedOutputAttributes}.<br>
     *
     * @param compositeEventTimeCriteria
     *            -- {@link CompositeEventTimeCriteria}
     * @param alarmAttributes
     *            -- {@link ExpectedOutputAttributes}
     * @param dynamicSortAttributes
     *            list of dynamic attributes provided in sorting criteria.
     * @return -- {@link AlarmAttributeResponse}
     */
    public AlarmAttributeResponse getAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                            final ExpectedOutputAttributes alarmAttributes, final List<String> dynamicSortAttributes) {
        LOGGER.debug("Request received  for alarms with compositeEventTimeCriteria {} and expectedOutputAttributes {} ", compositeEventTimeCriteria,
                alarmAttributes);

        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);

        final Restriction compositeRestriction = compositeEventTimeRestrictionBuilder.build(typeQuery, compositeEventTimeCriteria);
        final List<Restriction> nodeRestrictions = compositeEventTimeRestrictionBuilder.buildNodeRestrictions(typeQuery, compositeEventTimeCriteria);

        final List<AlarmSortCriterion> alarmSortCriteria = compositeEventTimeCriteria.getAlarmSortCriteria();
        if (nodeRestrictions.size() <= 1) {
            // Checking node restriction size to set DPS sort criteria, because if node restriction size is more than 1, then in memory sorting will
            // take place.
            // So need of DPS sorting if node restriction size is greater than 1.
            for (final AlarmSortCriterion alarmSortCriterion : alarmSortCriteria) {
                final String sortAttribute = alarmSortCriterion.getSortAttribute();
                final SortingOrder sortOrder = alarmSortCriterion.getSortOrder();
                final SortSequence sortSequence = alarmSortCriterion.getSortSequence();

                if (dynamicSortAttributes.contains(sortAttribute) && SortSequence.FIRST_LEVEL_SORT.equals(sortSequence)) {
                    break;
                } else if (!dynamicSortAttributes.contains(sortAttribute)) {
                    setDpsSortingOrder(typeQuery, sortAttribute, sortOrder);
                }
            }
        }

        if (!dynamicSortAttributes.isEmpty()) {
            alarmAttributes.getOutputAttributes().removeAll(dynamicSortAttributes);
        }

        List<AlarmRecord> alarmRecords = getAlarmRecords(typeQuery, compositeRestriction, nodeRestrictions, compositeEventTimeCriteria,
                alarmAttributes);

        if (nodeRestrictions.size() > 1) {
            // If the nodeRestrictions are more than 1 then we need to club all the batch wise sorted alarm records and perform in memory sorting.
            alarmRecords = alarmAttributeResponseBuilder.mergeAllSortedAlarmRecords(alarmRecords, alarmSortCriteria, dynamicSortAttributes);
        }
        final AlarmAttributeResponse alarmAttributeResponse = alarmAttributeResponseBuilder.buildAttributeResponse(alarmRecords);

        LOGGER.debug(" Total Alarms :: {} found with given Criteria  :: {}", alarmRecords.size(), compositeEventTimeCriteria);
        return alarmAttributeResponse;
    }

    /**
     * Returns the {@code List<{@link AlarmRecord}>} for the conditions set in {@link CompositeEventTimeCriteria} <br>
     * {@link AlarmAttributeResponse} may contain all the attributes of specific attributes based on the conditions set in
     * {@link ExpectedOutputAttributes}<br>
     *
     * @param typeQuery
     *            -- @ AlarmPoIdCriteria
     * @param alarmAttributes
     *            {@link ExpectedOutputAttributes}
     * @return -- {@link AlarmAttributeResponse}
     */
    private List<AlarmRecord> getAlarmRecordsForBatch(final Query<TypeRestrictionBuilder> typeQuery, final ExpectedOutputAttributes alarmAttributes) {
        boolean commentHistoryRequired = false;
        List<String> outputAttributes = null;

        if (alarmAttributes != null) {
            commentHistoryRequired = alarmAttributes.isCommentHistoryRequired();
            outputAttributes = alarmAttributes.getOutputAttributes();
        }

        List<AlarmRecord> alarmRecords = null;

        if (outputAttributes == null || outputAttributes.isEmpty()) {
            // As per requirement comments history needs to be retrieved only with complete alarm record
            alarmRecords = alarmReader.getAlarmRecords(typeQuery, commentHistoryRequired);
        } else {
            alarmRecords = alarmReader.getAlarmRecordsForSelectedAttributes(typeQuery, outputAttributes);
        }
        return alarmRecords;
    }

    /**
     * Returns the {@code List<{@link AlarmRecord}>} for the conditions set in {@link CompositeEventTimeCriteria} <br>
     * {@link AlarmAttributeResponse} may contain all the attributes of specific attributes based on the conditions set in
     * {@link ExpectedOutputAttributes}<br>
     * DPS call will be made for each node restriction, will be added to list and will be returned complete list
     *
     * @param typeQuery
     *            -- {@link Query}
     * @param compositeRestriction
     *            -- composite {@link Restriction} of alarm attributes and event times.
     * @param nodeRestrictions
     *            -- {@code List< link@ {@link Restriction}> builded on nodes
     * @param compositeEventTimeCriteria
     *            - {@link CompositeEventTimeCriteria}
     * @param expectedOutputAttributes
     *            - {@link ExpectedOutputAttributes}
     * @return
     */

    private List<AlarmRecord> getAlarmRecords(final Query<TypeRestrictionBuilder> typeQuery, final Restriction compositeRestriction,
                                              final List<Restriction> nodeRestrictions, final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                              final ExpectedOutputAttributes expectedOutputAttributes) {
        LOGGER.debug("number of node batch restrictions formed is : {}", nodeRestrictions.size());
        final Set<AlarmRecord> alarmRecords = new LinkedHashSet<AlarmRecord>();

        for (final Restriction restriction : nodeRestrictions) {
            Restriction finalRestriction = null;

            if (compositeRestriction != null) {
                finalRestriction = typeQuery.getRestrictionBuilder().allOf(restriction, compositeRestriction);
            } else {
                finalRestriction = restriction;
            }

            typeQuery.setRestriction(finalRestriction);

            final List<AlarmRecord> batchAlarmRecords = getAlarmRecordsForBatch(typeQuery, expectedOutputAttributes);
            if (!batchAlarmRecords.isEmpty()) {
                alarmRecords.addAll(batchAlarmRecords);
            }
        }

        if (nodeRestrictions.isEmpty() && compositeRestriction != null) {
            typeQuery.setRestriction(compositeRestriction);
            alarmRecords.addAll(getAlarmRecordsForBatch(typeQuery, expectedOutputAttributes));
        }
        return new ArrayList<AlarmRecord>(alarmRecords);
    }

    /**
     * Sets the sorting order to open alarm type query.
     *
     * @param typeQuery
     *            DPS type query formed for open alarm.
     * @param sortAttribute
     *            sorting attribute.
     * @param sortOrder
     *            sorting order.
     */
    private void setDpsSortingOrder(final Query<TypeRestrictionBuilder> typeQuery, final String sortAttribute, final SortingOrder sortOrder) {
        if (SortingOrder.ASCENDING.equals(sortOrder)) {
            if (PRESENT_SEVERITY.equals(sortAttribute)) {
                typeQuery.addSortingOrder(PSEUDO_PRESENT_SEVERITY, SortDirection.ASCENDING);
            } else if (PREVIOUS_SEVERITY.equals(sortAttribute)) {
                typeQuery.addSortingOrder(PSEUDO_PREVIOUS_SEVERITY, SortDirection.ASCENDING);
            } else {
                typeQuery.addSortingOrder(sortAttribute, SortDirection.ASCENDING);
            }
        } else {
            if (PRESENT_SEVERITY.equals(sortAttribute)) {
                typeQuery.addSortingOrder(PSEUDO_PRESENT_SEVERITY, SortDirection.DESCENDING);
            } else if (PREVIOUS_SEVERITY.equals(sortAttribute)) {
                typeQuery.addSortingOrder(PSEUDO_PREVIOUS_SEVERITY, SortDirection.DESCENDING);
            } else {
                typeQuery.addSortingOrder(sortAttribute, SortDirection.DESCENDING);
            }
        }
    }
}
