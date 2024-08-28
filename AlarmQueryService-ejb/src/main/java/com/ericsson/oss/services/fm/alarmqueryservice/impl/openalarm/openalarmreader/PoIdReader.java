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

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.query.ObjectField;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;

/**
 * Responsible for retrieving PoIds based on the restriction set in {@link Query}.
 *
 *
 *
 */
public class PoIdReader {

    /**
     * Returns the PoIds based on the query and the restrictions set in query.<br>
     * As the data retrieved from DPS is not serialized, this method serializes and sent.
     *
     * @param liveBucket
     *            -- {@link DataBucket}
     * @param typeQuery
     *            -- {@link Query}
     * @return -- list PoIds
     */

    public List<Long> getPoIds(final DataBucket liveBucket, final Query<TypeRestrictionBuilder> typeQuery) {
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);

        final List<Long> poIds = queryExecutor.executeProjection(typeQuery, poIdProjection);
        // As the data coming from DPS is not serialized, we are serializing here.
        return new ArrayList<Long>(poIds);
    }
}
