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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENTOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENTTEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENT_POID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LAST_ALARM_OPERATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.SortOrder;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;

public class HistoryCommentsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryCommentsHandler.class);

    @EServiceRef
    private HistoricalQueryService historicalQueryService;

    public List<Map<String, Object>> getAllComments(final Long eventPoId) {
        List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
        try {
            final Query query = historicalQueryService.createQuery();
            final RestrictionBuilder restrictionBuilder = query.getRestrictionBuilder();
            final Restriction poIdRestriction = restrictionBuilder.equalTo(EVENT_POID, eventPoId);
            final Restriction commentRestriction = restrictionBuilder.equalTo(LAST_ALARM_OPERATION, COMMENT);
            query.setRestriction(restrictionBuilder.allOf(poIdRestriction, commentRestriction));
            query.setAttributes(COMMENTTEXT, COMMENTTIME, COMMENTOPERATOR);
            query.setPageFilter(0, 1000);
            query.orderBy(COMMENTTIME, SortOrder.ASCENDING);

            comments = historicalQueryService.execute(query);
        } catch (final Exception exception) {
            LOGGER.error("Exception occured when fetching the comments from  db(Solr is migrated with ES). {}", exception.getMessage());
        }
        LOGGER.debug("Number of comments fetched from the db are:{}", comments.size());
        return comments;
    }

}
