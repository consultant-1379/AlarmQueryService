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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.test;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENTOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENTTEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENTTIME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.HistoryCommentsHandler;

@RunWith(MockitoJUnitRunner.class)
public class HistoryCommentsHandlerTest {

    @InjectMocks
    HistoryCommentsHandler historyCommentsHandler;
    
    @Mock
    HistoricalQueryService historicalQueryService;
    
    @Mock
    Query query;
    
    @Mock
    RestrictionBuilder restrictionBuilder;
    
    @Mock
    Restriction restriction;
    
    List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
    
    @Test
    public void testGetAllComments_Failure() {
        final long eventPoId = 12345567L;
        
        List<Map<String, Object>> result = historyCommentsHandler.getAllComments(eventPoId);
        assertEquals(0, result.size());
    }
    
    @Test
    public void testGetAllComments_Sccess() {
        final Map<String,Object> attributes = new HashMap<String, Object>();
        attributes.put(COMMENTOPERATOR,"operator");
        attributes.put(COMMENTTEXT, "testcomment");
        attributes.put(COMMENTTIME, new Date());
        comments.add(attributes);
        final long eventPoId = 12345567L;
        when(historicalQueryService.createQuery()).thenReturn(query);
        when(query.getRestrictionBuilder()).thenReturn(restrictionBuilder);
        when(historicalQueryService.execute(query)).thenReturn(comments);

        List<Map<String, Object>> result = historyCommentsHandler.getAllComments(eventPoId);
        assertEquals(1, result.size());
    }
    
}
