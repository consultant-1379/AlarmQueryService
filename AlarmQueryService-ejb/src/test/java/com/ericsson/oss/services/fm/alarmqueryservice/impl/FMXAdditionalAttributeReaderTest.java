/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmqueryservice.impl;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARM_ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FM;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class FMXAdditionalAttributeReaderTest {

    @InjectMocks
    private FMXAdditionalAttributeReader fmxAdditionalAttributeReader;

    @Mock
    private DataPersistenceService dps;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private Projection projection;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private ProjectionBuilder projectionBuilder;

    @Before
    public void setUp() {

        when(dpsProxy.getService()).thenReturn(dps);
        when(dps.getLiveBucket()).thenReturn(liveBucket);
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);
        when(queryBuilder.createTypeQuery(FM, ALARM_ADDITIONAL_INFORMATION)).thenReturn(typeQuery);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);

    }

    @Test
    public void testPopulateAdditionalAttributes() {
        final List<Object> resultList = new ArrayList<Object>();
        resultList.add("AdditionalAttribute1");
        resultList.add("AdditionalAttribute2");

        when(queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Mockito.anyObject(), (Projection) Mockito.anyObject())).thenReturn(
                resultList);

        final List<String> result = fmxAdditionalAttributeReader.getAlarmAdditionalAttributes();
        assertTrue(result.contains("AdditionalAttribute1"));

    }
}
