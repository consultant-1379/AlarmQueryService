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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.CRITICAL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;

@RunWith(MockitoJUnitRunner.class)
public class SeverityReaderTest {
    @InjectMocks
    private SeverityReader severityReader;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private Query typeQuery;

    @Mock
    private QueryExecutor queryExecutor;

    private final List<Object> severities = new ArrayList<Object>();

    @Test
    public void testGetSeverities_Projection_SeverityList() {

        severities.add(CRITICAL);

        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);
        when(queryExecutor.executeProjection((Query<?>) Matchers.anyObject(), (Projection) Matchers.anyObject())).thenReturn(severities);
        final List<String> severity = severityReader.getSeverities(liveBucket, typeQuery);

        assertEquals(CRITICAL, severity.get(0));
    }

}
