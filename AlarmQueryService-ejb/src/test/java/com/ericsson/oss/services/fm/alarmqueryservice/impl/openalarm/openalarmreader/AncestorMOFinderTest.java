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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@RunWith(MockitoJUnitRunner.class)
public class AncestorMOFinderTest {

    @InjectMocks
    private AncestorMOFinder ancestorMOFinder;

    @Mock
    private DPSProxy dpsProxy;

    @Mock
    private DataBucket liveBucket;

    @Mock
    private ManagedObject managedObject;

    @Test
    public void testRetriveManagedElementId_OOR_NoNodeId() {
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        assertEquals("", ancestorMOFinder.find("LTE01ERBS001"));
    }

    @Test
    public void testRetriveManagedElementId_OOR_NodeId() {
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.findMoByFdn("SubNetwork=LTE_NETWORK,SubNetwork=LTE_NETWORK,MeContext=LTE01dg2ERBS00001,Cabinet,EquipmentSupportFunction"))
                .thenReturn(null);
        when(liveBucket.findMoByFdn("SubNetwork=LTE_NETWORK,SubNetwork=LTE_NETWORK,MeContext=LTE01dg2ERBS00001"))
                .thenReturn(managedObject);
        when(managedObject.getPoId()).thenReturn(111L);
        assertEquals("111",
                ancestorMOFinder
                        .find("SubNetwork=LTE_NETWORK,SubNetwork=LTE_NETWORK,MeContext=LTE01dg2ERBS00001,Cabinet,EquipmentSupportFunction"));
    }

    @Test
    public void testRetriveManagedElementId_OORParent_NodeId() {
        when(dpsProxy.getLiveBucket()).thenReturn(liveBucket);
        when(liveBucket.findMoByFdn("MeContext=LTE01ERBS001")).thenReturn(managedObject);
        when(managedObject.getPoId()).thenReturn(111L);

        assertEquals("111", ancestorMOFinder.find("MeContext=LTE01ERBS001,ManagedElement=1"));
    }
}
