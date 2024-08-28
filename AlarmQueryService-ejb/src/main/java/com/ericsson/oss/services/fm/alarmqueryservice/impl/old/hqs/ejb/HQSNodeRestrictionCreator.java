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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.BATCH_SIZE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FDN;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;

public class HQSNodeRestrictionCreator {

    public List<Restriction> getNodesRestriction(final RestrictionBuilder restrictionBuilder, final List<String> nodes) {
        final List<Restriction> nodeRestrictions = new ArrayList<Restriction>();
        if (nodes != null && !nodes.isEmpty()) {
            final int size = nodes.size();

            for (int i = 0; i < size; i += BATCH_SIZE) {
                final List<String> subList = new ArrayList<String>(nodes.subList(i, Math.min(size, i + BATCH_SIZE)));
                final Restriction nodeRestriction = getNodeRestrictionInQuery(restrictionBuilder, subList);
                if (nodeRestriction != null) {
                    nodeRestrictions.add(nodeRestriction);
                }
            }
        }
        return nodeRestrictions;
    }

    private Restriction getNodeRestrictionInQuery(final RestrictionBuilder restrictionBuilder, final List<String> nodes) {

        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);
        return restrictionBuilder.anyOf(restrictionBuilder.in(FDN, nodesArray));
    }

}
