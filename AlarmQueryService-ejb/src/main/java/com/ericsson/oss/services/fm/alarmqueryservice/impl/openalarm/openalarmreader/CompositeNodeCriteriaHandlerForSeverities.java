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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.FilterConstants.CLEARED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.FilterConstants.CRITICAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.FilterConstants.INDETERMINATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.FilterConstants.MAJOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.FilterConstants.MINOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.FilterConstants.WARNING;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.SeveritiesMapper.SEVERITY_MAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.NodeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible for retrieving the alarms based on the conditions set in NodeCriteria.
 **/
@Stateless
public class CompositeNodeCriteriaHandlerForSeverities {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeNodeCriteriaHandlerForSeverities.class);

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Inject
    private SeverityReader severityReader;

    @Inject
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Inject
    private ConfigurationListener configurationListener;

    /**
     * Returns the map of alarm count by severity for the conditions set in {@link CompositeNodeCriteria} <br>
     * Map contains as key as severity and value will be the corresponding alarm count.<br>
     *
     * @param compositeNodeCriteria
     *            -- {@link CompositeNodeCriteria}
     * @return -- Map<String,Long>
     */
    public Map<String, Long> getAlarmCountBySeverity(final CompositeNodeCriteria compositeNodeCriteria) {
        LOGGER.debug("Request received  for severity count with CompositeNodeCriteria {}  ", compositeNodeCriteria);
        final Map<String, Long> severityBasedAlarmCount = initializeSeverityCountMap();
        try {
            final List<String> nodes = compositeNodeCriteria.getNodes();
            final List<AlarmAttributeCriteria> alarmAttributeCriteria = compositeNodeCriteria.getAlarmAttributeCriteria();

            AlarmAttributeCriteria alarmAttributeCriterion = new AlarmAttributeCriteria();
            if (alarmAttributeCriteria != null) {
                alarmAttributeCriterion = alarmAttributeCriteria.get(0);
            }
            final List<String> severities = getOpenAlarmSeverities(nodes, alarmAttributeCriterion);

            for (final String severity : severities) {
                final String severityKey = SEVERITY_MAP.get(severity);
                if (severityKey != null) {
                    Long severityValue = severityBasedAlarmCount.get(severityKey);
                    severityBasedAlarmCount.put(severityKey, ++severityValue);
                }
            }
            LOGGER.debug(
                    " indeterminateAlarms :: {} criticalAlarms :: {} majorAlarms :: {} minorAlarms :: {} warningAlarms :: {} clearedAlarms :: {} "
                            + "for the compositeNodeCriteria {} ",
                    severityBasedAlarmCount.get(CRITICAL), severityBasedAlarmCount.get(MAJOR), severityBasedAlarmCount.get(MINOR),
                    severityBasedAlarmCount.get(WARNING), severityBasedAlarmCount.get(INDETERMINATE), severityBasedAlarmCount.get(CLEARED),
                    compositeNodeCriteria);
            return severityBasedAlarmCount;
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while getting the alarm count from DB {} for compositeNodeCriteria {}", exception, compositeNodeCriteria);
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append("Failed to read alarmcount by severity from DB. Exception details are: ").append(exception.getMessage());
            throw new RuntimeException(errorMessageBuilder.toString());
        }
    }

    /**
     * Returns the map of alarm count by severity for the given {@link AlarmRecord} list. The presentSeverity attribute has to be properly filled in
     * the AlarmRecord list <br>
     * Map contains as key as severity and value will be the corresponding alarm count.<br>
     *
     * @param alarmRecords
     *            -- {@link List<AlarmRecord>}
     * @return -- Map<String,Long>
     */
    public Map<String, Long> getAlarmCountBySeverity(final Collection<AlarmRecord> alarmRecords) {
        LOGGER.debug("Request received  for severity count with alarmRecords {}  ", alarmRecords);
        final Map<String, Long> severityBasedAlarmCount = initializeSeverityCountMap();
        getSeverityCounts(alarmRecords, severityBasedAlarmCount);
        LOGGER.debug(" indeterminateAlarms :: {} criticalAlarms :: {} majorAlarms :: {} minorAlarms :: {} warningAlarms :: {} clearedAlarms :: {} ",
                severityBasedAlarmCount.get(CRITICAL), severityBasedAlarmCount.get(MAJOR), severityBasedAlarmCount.get(MINOR),
                severityBasedAlarmCount.get(WARNING), severityBasedAlarmCount.get(INDETERMINATE), severityBasedAlarmCount.get(CLEARED));
        return severityBasedAlarmCount;
    }

    /**
     * @param alarmRecords
     * @param severityBasedAlarmCount
     */
    private void getSeverityCounts(final Collection<AlarmRecord> alarmRecords, final Map<String, Long> severityBasedAlarmCount) {
        final List<String> severities = new ArrayList<>(0);
        for (final AlarmRecord alarmRecord : alarmRecords) {
            final String severity = String.valueOf(alarmRecord.getPresentSeverity());
            if (severity != "null") {
                severities.add(severity);
            }
        }
        for (final String severity : severities) {
            final String severityKey = SEVERITY_MAP.get(severity);
            if (severityKey != null) {
                Long severityValue = severityBasedAlarmCount.get(severityKey);
                severityBasedAlarmCount.put(severityKey, ++severityValue);
            }
        }
    }

    private Map<String, Long> initializeSeverityCountMap() {
        final Map<String, Long> severityBasedAlarmsCount = new LinkedHashMap<String, Long>(6);
        severityBasedAlarmsCount.put(CRITICAL, 0L);
        severityBasedAlarmsCount.put(MAJOR, 0L);
        severityBasedAlarmsCount.put(MINOR, 0L);
        severityBasedAlarmsCount.put(WARNING, 0L);
        severityBasedAlarmsCount.put(INDETERMINATE, 0L);
        severityBasedAlarmsCount.put(CLEARED, 0L);
        return severityBasedAlarmsCount;
    }

    /**
     * Returns the severities based on the nodes and alarm attributes.<br>
     * Nodes will be batched before creating a DPS query. <br>
     * If size is more than 1500(configurable), will batch and query DPS
     * <p>
     * Eg. When 2000 fdns are input to this method, 2 batches formed each having nodes of 1500 and 500 are created.
     *
     * @param nodes
     *            -- list fdns of nodes.
     * @param alarmAttributeCriterian
     *            -- alarm attribute criteria
     * @return --{@code List<String> } list of severities for the nodes given
     */
    private List<String> getOpenAlarmSeverities(final List<String> nodes, final AlarmAttributeCriteria alarmAttributeCriterian) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final List<String> severites = new ArrayList<String>(0);
        final int size = nodes.size();
        // default is 3000
        final Integer maxNEsAllowedPerOpenAlarmQuery = configurationListener.getMaxNEsAllowedPerOpenAlarmQuery();
        // Creating a batch of nodes of size 3000 (configurable)
        for (int i = 0; i < size; i += maxNEsAllowedPerOpenAlarmQuery) {
            final List<String> subList = new ArrayList<String>(nodes.subList(i, Math.min(size, i + maxNEsAllowedPerOpenAlarmQuery)));
            final List<String> batchSeverities = getSeveritiesForABatchOfNodes(liveBucket, typeQuery, subList, alarmAttributeCriterian);
            if (!batchSeverities.isEmpty()) {
                severites.addAll(batchSeverities);
            }
        }
        return severites;
    }

    /**
     * Method that returns the poIds for the given set of nodes.
     *
     * @param liveBucket
     *            -- {@link DataBucket}
     * @param typeQuery
     *            --{@link Query}
     * @param nodes
     *            -- list nodes
     * @param alarmAttributeCriterion
     *            -- {@link AlarmAttributeCriteria}
     * @return -- list of severities
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private List<String> getSeveritiesForABatchOfNodes(final DataBucket liveBucket, final Query<TypeRestrictionBuilder> typeQuery,
                                                       final List<String> nodes, final AlarmAttributeCriteria alarmAttributeCriterion) {
        Restriction nodeRestriction = null;
        nodeRestriction = nodeRestrictionBuilder.build(typeQuery.getRestrictionBuilder(), nodes);
        Restriction finalRestriction = nodeRestriction;
        Restriction alarmAttributeRestriction = null;
        if (alarmAttributeCriterion != null) {
            alarmAttributeRestriction = attributeRestrictionBuilder.buildSingleAttributeRestriction(typeQuery.getRestrictionBuilder(),
                    alarmAttributeCriterion.getAttributeName(), alarmAttributeCriterion.getAttributeValue(), alarmAttributeCriterion.getOperator());
        }

        // Only alarms which are having VISIBILITY value true, will be retrieved from DPS.
        final Restriction visibilityRestriction = typeQuery.getRestrictionBuilder().equalTo(VISIBILITY, true);
        if (alarmAttributeRestriction == null) {
            finalRestriction = typeQuery.getRestrictionBuilder().allOf(finalRestriction, visibilityRestriction);
        } else {
            finalRestriction = typeQuery.getRestrictionBuilder().allOf(finalRestriction, visibilityRestriction, alarmAttributeRestriction);
        }
        typeQuery.setRestriction(finalRestriction);
        final List<String> severities = new ArrayList<String>(100);
        severities.addAll(severityReader.getSeverities(liveBucket, typeQuery));
        return severities;
    }
}
