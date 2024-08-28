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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;

/**
 *
 * Responsible for building Composite Restriction based on conditions set in {@link CompositeNodeCriteria}.
 *
 *
 */
@ApplicationScoped
public class CompositeNodeCriteriaRestrictionBuilder {

    // TODO: Use decorator pattern to build composite restriction

    @Inject
    private LogicalOperatorRestrictionBuilder logicalOperationRestrictionBuilder;

    @Inject
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Inject
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    /**
     * Returns a composite restriction, which is build based on {@link CompositeNodeCriteria}.<br>
     * Composite restriction a combination of alarm attributes and nodes. inputs.
     *
     * @param typeQuery
     *            {@link Query}
     * @param compositeEventTimeCriteria
     *            {@link CompositeEventTimeCriteria}
     * @return compositeRestriction -- composite restriction formed by the criteria.
     */

    public Restriction build(final Query<TypeRestrictionBuilder> typeQuery, final CompositeNodeCriteria compositeNodeCriteria) {
        final TypeRestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        // Only alarms which are having VISIBILITY value true, will be retrieved from DPS.
        final Restriction visibilityRestriction = restrictionBuilder.equalTo(VISIBILITY, true);
        Restriction finalRestriction = null;

        final List<AlarmAttributeCriteria> alarmAttributeCriteria = compositeNodeCriteria.getAlarmAttributeCriteria();
        // Builds a combination of nodes and alarm attributes restriction
        if (alarmAttributeCriteria != null && !alarmAttributeCriteria.isEmpty()) {
            final Restriction alarmAttributeRestriction = attributeRestrictionBuilder.build(restrictionBuilder, alarmAttributeCriteria);
            finalRestriction = logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(restrictionBuilder, finalRestriction,
                    alarmAttributeRestriction);
        }

        finalRestriction = logicalOperationRestrictionBuilder.buildCompositeRestrictionByAnd(restrictionBuilder, finalRestriction,
                visibilityRestriction);
        return finalRestriction;
    }

    /**
     * Returns the list of Node restrictions, each restriction contains maximum of 3000 nodes.
     *
     * @param typeQuery
     *            -- {@link Query}
     * @param compositeEventTimeCriteria
     *            -- {@link CompositeEventTimeCriteria}
     * @return ({@code List<{@link Restriction> })
     */
    public List<Restriction> buildNodeRestrictions(final Query<TypeRestrictionBuilder> typeQuery, final CompositeNodeCriteria compositeNodeCriteria) {
        final List<String> nodes = compositeNodeCriteria.getNodes();

        List<Restriction> restrictions = new ArrayList<Restriction>();
        // Builds nodes restriction
        if (nodes != null && !nodes.isEmpty()) {
            restrictions = nodeRestrictionBuilder.buildListOfNodeRestrictions(typeQuery.getRestrictionBuilder(), nodes);
        }
        return restrictions;
    }
}
