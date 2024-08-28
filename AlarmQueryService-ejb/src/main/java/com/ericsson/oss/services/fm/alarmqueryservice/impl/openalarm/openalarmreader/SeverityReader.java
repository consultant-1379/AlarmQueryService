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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRESENT_SEVERITY;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;

/**
 * Responsible for reading alarm severities based on the restrictions set in Query.
 *
 */
public class SeverityReader {

    /**
     * Returns the list of alarm severities based on the query and the restrictions set in query.<br>
     * As the data retrieved from DPS is not serialized, this method serializes and sent.
     *
     * @param liveBucket
     *            -- {@link DataBucket}
     * @param typeQuery
     *            -- {@link Query}
     * @return -- list alarm severities
     */
    public List<String> getSeverities(final DataBucket liveBucket, final Query<TypeRestrictionBuilder> typeQuery) {
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Projection severityProjection = ProjectionBuilder.attribute(PRESENT_SEVERITY);

        final List<String> severities = queryExecutor.executeProjection(typeQuery, severityProjection);
        // As the data coming from DPS is not serialized, we are serializing here.
        return new ArrayList<String>(severities);
    }

}
