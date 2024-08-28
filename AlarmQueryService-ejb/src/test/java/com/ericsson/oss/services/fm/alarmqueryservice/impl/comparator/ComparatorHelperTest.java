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
package com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ComparatorHelperTest {

    @InjectMocks
    ComparatorHelper ComparatorHelper;

    @Test
    public void testCompareAttributesWithNullValuesForAscendingForNullAttributeValues() {
        assertEquals(0, ComparatorHelper.compareAttributesWithNullValuesForAscending(null, null));
    }

    @Test
    public void testCompareAttributesWithNullValuesForAscendingForNullAttributeValue() {
        assertEquals(-1, ComparatorHelper.compareAttributesWithNullValuesForAscending(null, true));
    }

    @Test
    public void testCompareAttributesWithNullValuesForAscending_FirstNotNull_SecondNull() {
        assertEquals(1, ComparatorHelper.compareAttributesWithNullValuesForAscending(true, null));
    }

    @Test
    public void testCompareAttributesWithNullValuesForDecendingForNullAttributeValues() {
        assertEquals(0, ComparatorHelper.compareAttributesWithNullValuesForDescending(null, null));
    }

    @Test
    public void testCompareAttributesWithNullValuesForDecendingForNullAttributeValue() {
        assertEquals(-1, ComparatorHelper.compareAttributesWithNullValuesForDescending(true, null));
    }

    @Test
    public void testCompareAttributesWithNullValuesForDecending_FirstNull_SecondNotNull() {
        assertEquals(1, ComparatorHelper.compareAttributesWithNullValuesForDescending(null, true));
    }

    @Test
    public void testCompareForAscending_BothNull() {
        assertEquals(0, ComparatorHelper.compareForAscending(null, null));
    }

    @Test
    public void testCompareForAscending_FirstNull_SecondNotNull() {
        assertEquals(-1, ComparatorHelper.compareForAscending(null, "true"));
    }

    @Test
    public void testCompareForAscending_FirstNotNull_SecondNull() {
        assertEquals(1, ComparatorHelper.compareForAscending("true", null));
    }

    @Test
    public void testCompareForAscending_BothNotNull() {
        Assert.assertTrue(ComparatorHelper.compareForAscending("first", "second") < 0);
    }

    @Test
    public void testCompareForDescending_BothNull() {
        assertEquals(0, ComparatorHelper.compareForDescending(null, null));
    }

    @Test
    public void testCompareForDescending_FirstNull_SecondNotNull() {
        assertEquals(1, ComparatorHelper.compareForDescending(null, "true"));
    }

    @Test
    public void testCompareForDescending_FirstNotNull_SecondNull() {
        assertEquals(-1, ComparatorHelper.compareForDescending("true", null));
    }

    @Test
    public void testCompareForDescending_BothNotNull() {
        Assert.assertTrue(ComparatorHelper.compareForDescending("first", "second") > 0);
    }
}
