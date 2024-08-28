package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSAttributeRestrictionCreator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSDateRestrictionCreator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSOperatorBasedRestriction;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants;

@RunWith(MockitoJUnitRunner.class)
public class HQSAttributeRestrictionCreatorTest {

    @InjectMocks
    private HQSAttributeRestrictionCreator hqsAttributeRestrictionCreator;
    @Mock
    private Restriction restriction;
    @Mock
    private HQSDateRestrictionCreator hqsDateRestrictionCreator;
    @Mock
    private HQSOperatorBasedRestriction hqsOperatorBasedRestriction;
    @Mock
    private RestrictionBuilder restrictionBuilder;

    Map<String, List<String>> alarmAttributeSplit = new HashMap<String, List<String>>();
    List<String> alarmAttributes = new ArrayList<String>();

    @Test
    public void testGetAttributesRestrictionForAlarmNumber() {

        alarmAttributes.add("alarmNumber#111#>");
        alarmAttributeSplit.put("alarmNumber#111#>", alarmAttributes);
        final long value = 111L;
        when(hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, "alarmNumber", value, ">")).thenReturn(restriction);
        when(restrictionBuilder.allOf(restriction, restriction)).thenReturn(restriction);
        assertEquals(restriction,
                hqsAttributeRestrictionCreator.getAttributesRestriction(restrictionBuilder, alarmAttributes, QueryConstants.DATE_FORMAT));
    }

    @Test
    public void testGetAttributesRestrictionForRepeatCount() {

        alarmAttributes.add("repeatCount#111#>");
        alarmAttributeSplit.put("repeatCount#111#>", alarmAttributes);
        final int value = 111;
        when(hqsOperatorBasedRestriction.getRestrictionByComparisonOperator(restrictionBuilder, "repeatCount", value, ">")).thenReturn(restriction);

        assertEquals(restriction,
                hqsAttributeRestrictionCreator.getAttributesRestriction(restrictionBuilder, alarmAttributes, QueryConstants.DATE_FORMAT));
    }

    @Test
    public void testGetAttributesRestrictionForBackupStatus() {

        alarmAttributes.add("backupStatus#false#=");
        alarmAttributeSplit.put("backupStatus#false#=", alarmAttributes);
        when(restrictionBuilder.equalTo("backupStatus", false)).thenReturn(restriction);
        assertEquals(restriction,
                hqsAttributeRestrictionCreator.getAttributesRestriction(restrictionBuilder, alarmAttributes, QueryConstants.DATE_FORMAT));
    }

    @Test
    public void testGetAttributesRestrictionForInvalidAttribute() {

        alarmAttributes.add("specificProblem#specificProblem1#!=");
        alarmAttributeSplit.put("specificProblem#specificProblem1#!=", alarmAttributes);
        when(hqsOperatorBasedRestriction.getRestrictionOnMatchCondition(restrictionBuilder, "specificProblem", "specificProblem1", "!=")).thenReturn(
                restriction);
        assertEquals(restriction,
                hqsAttributeRestrictionCreator.getAttributesRestriction(restrictionBuilder, alarmAttributes, QueryConstants.DATE_FORMAT));
    }

    @Test
    public void testGetAttributesRestrictionFor_CEASETIME() {
        final String dateAttribute = "eventTime";
        final String operator = "between";
        final String betweenDates = "11-04-2014 05:00:12,13-04-2014 06:00:13";
        final List<String> alarmAttributes1 = new ArrayList<String>();
        alarmAttributes1.add("eventTime#11-04-2014 05:00:12,13-04-2014 06:00:13#between");
        when(
                hqsDateRestrictionCreator.getRestrictionOnDateAttributes(restrictionBuilder, dateAttribute, betweenDates, operator, null,
                        "dd/MM/yyyy HH:mm:ss")).thenReturn(restriction);
        assertEquals(restriction,
                hqsAttributeRestrictionCreator.getAttributesRestriction(restrictionBuilder, alarmAttributes1, QueryConstants.DATE_FORMAT));
    }
}