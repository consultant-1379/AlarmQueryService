/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
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
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class CommentHistoryReaderTest {

    @InjectMocks
    private CommentHistoryReader commentHistoryReader;
    @Mock
    private Query query;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    DPSProxy dpsProxy;

    @Mock
    private TypeRestrictionBuilder restrictionBuilder;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;
    
    @Mock
    private DataBucket liveBucket;
    
    @Mock
    private QueryExecutor queryExecutor;
    
    @Mock
    private Iterator<Object> poListIterator; 
    
    @Mock
    private PersistenceObject historyCommentPO;

    private final List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
    private final Map<String, Object> commentMap = new HashMap<String, Object>();

    @Test
    public void testGetAllComments_PoId_Comments() {
        when(query.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        when(dpsProxy.getService()).thenReturn(dataPersistenceService);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery("FM", "CommentOperation")).thenReturn(typeQuery);
        when(typeQuery.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(poListIterator.next()).thenReturn(historyCommentPO);
        commentMap.put(EVENT_PO_ID, 1L);
        comments.add(commentMap);
        when(historyCommentPO.getAttribute(COMMENTS)).thenReturn(comments);
        final List<Map<String, Object>> comment = commentHistoryReader.getAllComments(222L);

    }

}
