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

import java.util.List;

import javax.inject.Inject;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.DynamicAttributeEventTimeCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.DynamicAttributeNodeCriteriaHandler;

/**
 * DynamicAttributeCriteriaHandler provides functionality to getAlarms based on the criterias like {@link CompositeNodeCriteria},
 * {@link CompositeEventTimeCriteria}.
 */
public class DynamicAttributeCriteriaHandler {

    @Inject
    private DynamicAttributeNodeCriteriaHandler dynamicAttributeNodeCriteriaHandler;

    @Inject
    private DynamicAttributeEventTimeCriteriaHandler dynamicAttributeEventTimeCriteriaHandler;

    /**
     * Method gets alarms based on criteria {@link CompositeNodeCriteria} and expectedOutputAttributes {@link ExpectedOutputAttributes} and whether
     * criteria contains "only sorted" or "only search" or "both search and sort" alarms from open alarm DB.
     *
     * @param compositeNodeCriteria
     *            contains all the search, sort and all the criteria information.
     * @param expectedOutputAttributes
     *            contains what are the attributes required in alarm.
     * @param isDynamicSearchCriteria
     *            contains true/false whether search attributes contains additional attribute or not.
     * @param dynamicSortingAttributes
     *            contains list of dynamic sorting attributes.
     * @return
     * @throws Exception
     */
    public AlarmAttributeResponse getAlarms(final CompositeNodeCriteria compositeNodeCriteria,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final boolean isDynamicSearchCriteria,
                                            final List<String> dynamicSortingAttributes) throws Exception {
        AlarmAttributeResponse alarmAttributeResponse = null;

        if (!dynamicSortingAttributes.isEmpty() && isDynamicSearchCriteria) {
            alarmAttributeResponse = dynamicAttributeNodeCriteriaHandler.getSortedSearchedAlarms(compositeNodeCriteria, expectedOutputAttributes,
                    dynamicSortingAttributes);
        } else if (!dynamicSortingAttributes.isEmpty()) {
            alarmAttributeResponse = dynamicAttributeNodeCriteriaHandler.getSortedAlarms(compositeNodeCriteria, expectedOutputAttributes,
                    dynamicSortingAttributes);
        } else if (isDynamicSearchCriteria) {
            alarmAttributeResponse = dynamicAttributeNodeCriteriaHandler.getSearchAlarms(compositeNodeCriteria, expectedOutputAttributes,
                    dynamicSortingAttributes);
        }
        return alarmAttributeResponse;
    }

    /**
     * Method gets alarms based on criteria {@link CompositeEventTimeCriteria} and expectedOutputAttributes {@link ExpectedOutputAttributes} and
     * whether criteria contains "only sorted" or "only search" or "both search and sort" alarms from open alarm DB.
     *
     * @param compositeEventTimeCriteria
     *            contains all the search, sort and all the criteria information.
     * @param expectedOutputAttributes
     *            contains what are the attributes required in alarm.
     * @param isDynamicSearchCriteria
     *            contains true/false whether search attributes contains additional attribute or not.
     * @param dynamicSortingAttributes
     *            contains list of dynamic sorting attributes.
     * @return
     * @throws Exception
     */
    public AlarmAttributeResponse getAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final List<String> dynamicSortAttributes,
                                            final boolean isDynamicSearchCriteria) throws Exception {
        AlarmAttributeResponse alarmAttributeResponse = null;

        if (!dynamicSortAttributes.isEmpty() && isDynamicSearchCriteria) {
            alarmAttributeResponse = dynamicAttributeEventTimeCriteriaHandler.getSortedSearchedAlarms(compositeEventTimeCriteria,
                    expectedOutputAttributes, dynamicSortAttributes);
        } else if (!dynamicSortAttributes.isEmpty()) {
            alarmAttributeResponse = dynamicAttributeEventTimeCriteriaHandler.getSortedAlarms(compositeEventTimeCriteria, expectedOutputAttributes,
                    dynamicSortAttributes);
        } else if (isDynamicSearchCriteria) {
            alarmAttributeResponse = dynamicAttributeEventTimeCriteriaHandler.getSearchAlarms(compositeEventTimeCriteria, expectedOutputAttributes,
                    dynamicSortAttributes);
        }
        return alarmAttributeResponse;
    }

}
