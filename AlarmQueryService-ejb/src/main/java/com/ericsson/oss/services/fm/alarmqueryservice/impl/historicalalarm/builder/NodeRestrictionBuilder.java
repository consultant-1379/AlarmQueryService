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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;

/**
 * Responsible for building the HQS restriction for nodes with In restriction.
 */

@ApplicationScoped
public class NodeRestrictionBuilder {

    /**
     * Returns list of restrictions for the nodes.
     * <p>
     *
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param compositeEventTimeCriteria
     *            -- {@link CompositeEventTimeCriteria}
     * @return --{@code {@link Restriction}
     */

    public Restriction build(final RestrictionBuilder restrictionBuilder, final List<String> nodes) {
        Restriction nodeRestriction = null;
        if (nodes != null && !nodes.isEmpty()) {
            nodeRestriction = buildInRestriction(restrictionBuilder, nodes);
        }
        return nodeRestriction;
    }

    private Restriction buildInRestriction(final RestrictionBuilder restrictionBuilder, final List<String> nodes) {
        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);
        return restrictionBuilder.anyOf(restrictionBuilder.in(FDN, nodesArray));
    }

}