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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORExpression;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.LogicalOperatorRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.OORCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.PoIdReader;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class OORCriteriaHandlerTest {
    @InjectMocks
    private OORCriteriaHandler oORCriteriaHandler;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private DataPersistenceService dps;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private DataPersistenceService dataPersistenceService;

    @Mock
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private PoIdReader poIdReader;

    @Mock
    private DPSProxy dpsProxy;

    private final OORCriteria oorCriteria = new OORCriteria();
    private final List<Long> poIds = new ArrayList<Long>();
    private final List<OORExpression> oorExpressions = new ArrayList<OORExpression>();
    private final OORExpression oORExpression = new OORExpression();

    @Before
    public void setup() {

        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(dpsProxy.getService()).thenReturn(dps);
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM)).thenReturn(typeQuery);
        when(dps.getQueryBuilder()).thenReturn(queryBuilder);
        when(dps.getLiveBucket()).thenReturn(liveBucket);
        when(logicalOperatorRestrictionBuilder.build(typeRestrictionBuilder, OBJECT_OF_REFERENCE, "MECONTEXT=NETWORKELEMENT=1", Operator.EQ))
                .thenReturn(restriction);
        when(poIdReader.getPoIds(liveBucket, typeQuery)).thenReturn(poIds);

        oORExpression.setObjectOfReference("MECONTEXT=NETWORKELEMENT=1");
        oORExpression.setOperator(Operator.EQ);
        poIds.add(112L);
        oorExpressions.add(oORExpression);
        oorCriteria.setOorExpressions(oorExpressions);
    }

    @Test
    public void testGetPoIds_OORCritetia_PoIds() {

        final AlarmPoIdResponse alarmPoIdResponse = oORCriteriaHandler.getAlarmPoIds(oorCriteria);
        final long poId = alarmPoIdResponse.getPoIds().get(0);
        assertEquals(112L, poId);
    }
}
