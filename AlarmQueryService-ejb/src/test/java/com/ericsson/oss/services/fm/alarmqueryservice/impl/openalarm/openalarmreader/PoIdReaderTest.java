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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.PoIdReader;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class PoIdReaderTest {
    @InjectMocks
    private PoIdReader poIdReader;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private Projection projection;

    private final List<Object> poIds = new ArrayList<Object>();

    @Test
    public void testGetPoIds_Restriction_OnePoIdsReturned() {
        Mockito.mock(ProjectionBuilder.class);

        poIds.add(222L);

        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.executeProjection((Query<?>) Matchers.anyObject(), (Projection) Matchers.anyObject())).thenReturn(poIds);

        final List<Long> poId = poIdReader.getPoIds(liveBucket, typeQuery);
        assertEquals(1, poId.size());
    }
}
