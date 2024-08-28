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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.test;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FDN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSNodeRestrictionCreator;

@RunWith(MockitoJUnitRunner.class)
public class HQSNodeRestrictionCreatorTest {

    @InjectMocks
    private HQSNodeRestrictionCreator hqsNodeRestrictionCreator;
    @Mock
    private RestrictionBuilder restrictionBuilder;
    @Mock
    private Restriction restriction;

    @Test
    public void testGetNodesRestriction() {

        @SuppressWarnings("unchecked")
        final List<String> nodes = Mockito.mock(ArrayList.class);
        when(nodes.size()).thenReturn(1000);
        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);
        when(restrictionBuilder.anyOf(restrictionBuilder.in(FDN, nodesArray))).thenReturn(restriction);
        when(restrictionBuilder.anyOf(restriction, restriction)).thenReturn(restriction);
        List<Restriction> restrictions = new ArrayList<Restriction>();
        restrictions.add(restriction);
        restrictions.add(restriction);
        assertEquals(restrictions, hqsNodeRestrictionCreator.getNodesRestriction(restrictionBuilder, nodes));

    }
}
