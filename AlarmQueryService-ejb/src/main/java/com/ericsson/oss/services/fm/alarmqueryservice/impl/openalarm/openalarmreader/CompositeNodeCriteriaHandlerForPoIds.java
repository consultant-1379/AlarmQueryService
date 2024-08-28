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
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.LogicalOperatorRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.NodeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible for retrieving the poIds based on the conditions set in {@link CompositeNodeCriteria}.
 **/
@Stateless
public class CompositeNodeCriteriaHandlerForPoIds {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeNodeCriteriaHandlerForPoIds.class);

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private PoIdReader poIdReader;

    @Inject
    private LogicalOperatorRestrictionBuilder logicalOperationRestrictionBuilder;

    @Inject
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Inject
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Inject
    private ConfigurationListener configurationListener;

    /**
     * Returns the {@link AlarmPoIdResponse} for the conditions set in {@link CompositeNodeCriteria} <br>
     * {@link AlarmPoIdResponse} contains the retrieved poIds for {@link CompositeNodeCriteria} <br>
     *
     * @param compositeNodeCriteria
     *            -- {@link CompositeNodeCriteria}
     * @return -- {@link AlarmPoIdResponse}
     */
    public AlarmPoIdResponse getAlarmPoIds(final CompositeNodeCriteria compositeNodeCriteria) {
        AlarmPoIdResponse alarmPoIdResponse = null;
        try {
            final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM);
            dpsProxy.getService().setWriteAccess(false);
            final List<Long> poIds = getPoIds(typeQuery, compositeNodeCriteria);
            Collections.sort(poIds);
            Collections.reverse(poIds);
            LOGGER.debug("Total Po Ids found in Data base   for compositeNodeCriteria :: {} is :: {} ", compositeNodeCriteria, poIds.size());
            alarmPoIdResponse = new AlarmPoIdResponse(poIds, SUCCESS);
        } catch (final AttributeConstraintViolationException | ModelConstraintViolationException exception) {
            LOGGER.error("Error while retrieving poIds from DB {} with given compositeEventTimeCriteria {} ", exception, compositeNodeCriteria);
            alarmPoIdResponse = new AlarmPoIdResponse(Collections.<Long> emptyList(), exception.getMessage());
        } catch (final Exception exception) {
            LOGGER.error("Error while retrieving alarms from DB {} with given compositeNodeCriteria {} ", exception, compositeNodeCriteria);
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append("Failed to read poIds from DB. Exception details are : ").append(exception.getMessage());
            return new AlarmPoIdResponse(Collections.<Long> emptyList(), errorMessageBuilder.toString());
        }
        return alarmPoIdResponse;
    }

    /**
     * Returns the poIds for the conditions set in {@link CompositeNodeCriteria} and {@link Query} <br>
     * Method that returns the poIds for the given set of fdns set in {@link CompositeNodeCriteria} <br>
     * Nodes will be batched before creating a DPS query. <br>
     * Batch of 1500 fdns (configurable) in a single query
     * <p>
     * Eg. When 2000 fdns are input to this method, 2 batches formed each having nodes of 1500 and 500 are created.
     *
     * @param typeQuery
     *            -- {@link Query}
     * @param compositeNodeCriteria
     *            -- {@link CompositeNodeCriteria}
     * @return -- list of PoIds
     */
    private List<Long> getPoIds(final Query<TypeRestrictionBuilder> typeQuery, final CompositeNodeCriteria compositeNodeCriteria) {
        final List<AlarmAttributeCriteria> alarmAttributeConditions = compositeNodeCriteria.getAlarmAttributeCriteria();
        final TypeRestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        Restriction attributeRestriction = null;
        if (alarmAttributeConditions != null && !alarmAttributeConditions.isEmpty()) {
            attributeRestriction = attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributeConditions);
        }

        // Only alarms which are having VISIBILITY value true, will be retrieved from DPS.
        if (attributeRestriction != null || compositeNodeCriteria.getNodes() != null && !compositeNodeCriteria.getNodes().isEmpty()) {
            final Restriction visibilityRestriction = restrictionBuilder.equalTo(VISIBILITY, true);
            attributeRestriction =
                    logicalOperationRestrictionBuilder
                            .buildCompositeRestrictionByAnd(restrictionBuilder, attributeRestriction, visibilityRestriction);
        }
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final List<Long> poIds = new ArrayList<Long>(0);
        final List<String> nodes = compositeNodeCriteria.getNodes();
        if (nodes != null && !nodes.isEmpty()) {
            final int size = nodes.size();
            final Integer openAlarmQueryNELimit = configurationListener.getMaxNEsAllowedPerOpenAlarmQuery();
            for (int i = 0; i < size; i += openAlarmQueryNELimit) {
                final List<String> subList = new ArrayList<String>(nodes.subList(i, Math.min(size, i + openAlarmQueryNELimit)));
                final List<Long> batchPoIds = getPoIdsForBatchOfNodes(subList, liveBucket, typeQuery, attributeRestriction);
                if (!batchPoIds.isEmpty()) {
                    poIds.addAll(batchPoIds);
                }
            }
        } else {
            if (attributeRestriction != null) {
                typeQuery.setRestriction(attributeRestriction);
            }
            poIds.addAll(poIdReader.getPoIds(liveBucket, typeQuery));
        }
        return poIds;
    }

    /**
     * Method that returns the poIds for the given set of nodes.
     *
     * @param nodes
     *            -- list nodes
     * @param liveBucket
     *            -- {@link DataBucket}
     * @param typeQuery
     *            --{@link Query}
     * @param restriction
     *            --{@link Restriction}
     * @return list of PoIds
     */

    private List<Long> getPoIdsForBatchOfNodes(final List<String> nodes, final DataBucket liveBucket, final Query<TypeRestrictionBuilder> typeQuery,
                                               final Restriction restriction) {
        Restriction nodeRestriction = null;
        Restriction finalRestriction = restriction;
        final TypeRestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        nodeRestriction = nodeRestrictionBuilder.build(restrictionBuilder, nodes);
        finalRestriction = logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(restrictionBuilder, finalRestriction, nodeRestriction);
        final List<Long> poIds = new ArrayList<Long>(0);
        typeQuery.setRestriction(finalRestriction);
        poIds.addAll(poIdReader.getPoIds(liveBucket, typeQuery));
        return poIds;
    }
}
