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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.COMMENTS;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.COMMENT_OPERATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OPEN_ALARM_POID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible for reading history of comments on a specific alarm. <br>
 * An Operator can provide multiple comments on an alarm. And a History of such comments can be viewed only when he asks for a detailed view of alarm.
 *
 */
public class CommentHistoryReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentHistoryReader.class);

    @Inject
    private DPSProxy dpsProxy;

    /**
     * Returns all the comments of an alarm.<br>
     *
     * @param poId
     *            -- poId of an alarm.
     * @return -- List of maps having Comment time, comment operator and comment
     */
    public List<Map<String, Object>> getAllComments(final Long openAlarmPOId) {
        List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
        try {

            final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, COMMENT_OPERATION);
            final Restriction restriction = typeQuery.getRestrictionBuilder().equalTo(OPEN_ALARM_POID, openAlarmPOId);
            typeQuery.setRestriction(restriction);
            final QueryExecutor queryExecutor = dpsProxy.getLiveBucket().getQueryExecutor();
            final Iterator<PersistenceObject> historyCommentPOIterator = queryExecutor.execute(typeQuery);

            while (historyCommentPOIterator.hasNext()) {
                final PersistenceObject commentOperationPO = historyCommentPOIterator.next();
                comments = commentOperationPO.getAttribute(COMMENTS);
            }

        } catch (final Exception exception) {
            LOGGER.error("Exception occured when fetching the comments from DPS :: {}", exception);
        }
        LOGGER.debug("Number of comments fetched from the db are:: {} ", comments.size());
        return comments;
    }
}
