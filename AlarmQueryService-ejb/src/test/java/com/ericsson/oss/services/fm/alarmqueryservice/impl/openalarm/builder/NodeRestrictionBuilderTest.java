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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;

@RunWith(MockitoJUnitRunner.class)
public class NodeRestrictionBuilderTest {
    @InjectMocks
    private NodeRestrictionBuilder nodeRestrictionBuilder;

    @Mock
    private Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    private TypeRestrictionBuilder typeRestrictionBuilder;

    @Mock
    private RestrictionBuilder restrictionBuilder;

    @Mock
    private Restriction restriction;

    @Mock
    private ConfigurationListener configurationListener;

    private final List<String> nodes = new ArrayList<String>();

    @Test
    public void test_BuildBatchNodeRestriction_Nodes_Restriction() {
        nodes.add("LTE");
        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);
        when(typeQuery.getRestrictionBuilder()).thenReturn(typeRestrictionBuilder);
        when(typeRestrictionBuilder.in(FDN, nodesArray)).thenReturn(restriction);
        when(typeRestrictionBuilder.anyOf(restriction)).thenReturn(restriction);
        when(configurationListener.getMaxNEsAllowedPerInRestriction()).thenReturn(300);
        assertEquals(restriction, nodeRestrictionBuilder.build(typeRestrictionBuilder, nodes));
    }

}