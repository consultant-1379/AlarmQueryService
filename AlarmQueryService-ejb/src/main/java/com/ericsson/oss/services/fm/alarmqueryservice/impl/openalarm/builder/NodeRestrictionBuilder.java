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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;

/**
 * Responsible for building the restriction for nodes based on value.
 *
 */

@ApplicationScoped
public class NodeRestrictionBuilder {

    @Inject
    private ConfigurationListener configurationListener;

    /**
     * Returns list of restrictions, each restriction will be having maximum of 50(configurable) nodes.
     * <p>
     * Eg. When 110 fdns are input to this method, 2 restrictions formed each having nodes of 50 and 10 are created.
     *
     * @param restrictionBuilder
     *            -- {@link TypeRestrictionBuilder}
     * @param nodes
     *            -- list of nodes
     * @return --{@code List<{@link Restriction}>}
     */

    public Restriction build(final TypeRestrictionBuilder restrictionBuilder, final List<String> nodes) {

        final List<Restriction> nodeRestrictions = new ArrayList<Restriction>();

        final int size = nodes.size();

        // default is 50
        final Integer maxNEsAllowedPerInRestriction = configurationListener.getMaxNEsAllowedPerInRestriction();

        // Creating a batch of InRestrictions with 50 (configurable) NEs per restriction
        for (int batchCounter = 0; batchCounter < size; batchCounter += maxNEsAllowedPerInRestriction) {

            final List<String> subList = new ArrayList<String>(nodes.subList(batchCounter,
                    Math.min(size, batchCounter + maxNEsAllowedPerInRestriction)));
            final Restriction nodeTempRestriction = buildInRestriction(restrictionBuilder, subList);

            if (nodeTempRestriction != null) {
                nodeRestrictions.add(nodeTempRestriction);
            }
        }
        return restrictionBuilder.anyOf(nodeRestrictions.toArray(new Restriction[nodeRestrictions.size()]));
    }

    private Restriction buildInRestriction(final TypeRestrictionBuilder restrictionBuilder, final List<String> nodes) {

        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);
        return restrictionBuilder.in(FDN, nodesArray);
    }

    /**
     * Returns list of restrictions, each restriction will be having maximum of 3000(configurable) nodes.
     * <p>
     * Eg. When 7000 fdns are input to this method, 2 restrictions formed each having nodes of 3000 and 1000 are created.
     *
     * @param restrictionBuilder
     *            -- {@link TypeRestrictionBuilder}
     * @param nodes
     *            -- list of nodes
     * @return --{@code List<{@link Restriction}>}
     */

    public List<Restriction> buildListOfNodeRestrictions(final TypeRestrictionBuilder restrictionBuilder, final List<String> nodes) {

        final List<Restriction> nodeRestrictions = new ArrayList<Restriction>();

        if (nodes != null && !nodes.isEmpty()) {
            final int size = nodes.size();
            // default is 3000
            final Integer maxNEsAllowedPerOpenAlarmQuery = configurationListener.getMaxNEsAllowedPerOpenAlarmQuery();
            // Creating a batch of InRestrictions with 3000 (configurable) NEs per restriction
            for (int batchCounter = 0; batchCounter < size; batchCounter += maxNEsAllowedPerOpenAlarmQuery) {
                final List<String> subList = new ArrayList<String>(nodes.subList(batchCounter,
                        Math.min(size, batchCounter + maxNEsAllowedPerOpenAlarmQuery)));
                final Restriction nodeRestriction = build(restrictionBuilder, subList);
                if (nodeRestriction != null) {
                    nodeRestrictions.add(nodeRestriction);
                }
            }
        }
        return nodeRestrictions;
    }
}
