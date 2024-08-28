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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;

/**
 * Responsible for retrieving the alarms based on the conditions set in {@link CompositeNodeCriteria}
 **/
public class DynamicAttributeNodeCriteriaHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAttributeNodeCriteriaHandler.class);

    @Inject
    private CompositeNodeCriteriaHandlerForAlarms compositeNodeCriteriaHandlerForAlarms;

    @Inject
    private AdditionalAttributeHandler additionalAttributeHandler;

    @Inject
    private DynamicAlarmAttributeInfoReader dynamicAlarmAttributeInfoReader;

    /**
     * Method takes {@link CompositeNodeCriteria} and {@link ExpectedOutputAttributes} and perform sorting based on {@link ExpectedOutputAttributes}
     * which alarms are matched with the {@link CompositeNodeCriteria}.If alarms not matched with {@link CompositeNodeCriteria} those alarms will be
     * sorted based on the insertTime of an alarms.
     *
     * @param compositeNodeCriteria
     *            - an encapsulation of nodes and alarm attributes
     * @param expectedOutputAttributes
     *            contains alarm attributes should come as response {@link ExpectedOutputAttributes}.
     * @return returns {@link AlarmAttributeResponse}
     * @throws Exception
     */
    public AlarmAttributeResponse getSortedSearchedAlarms(final CompositeNodeCriteria compositeNodeCriteria,
                                                          final ExpectedOutputAttributes expectedOutputAttributes,
                                                          final List<String> dynamicAttributes) throws Exception {
        AlarmAttributeResponse dynamicSearchSortAttributesMatchedAlarmResponse = null;

        expectedOutputAttributes.getOutputAttributes().removeAll(dynamicAttributes);

        compositeNodeCriteria.setSortAttribute(null);
        compositeNodeCriteria.setSortDirection(null);

        final List<AlarmSortCriterion> alarmSortCriteria = compositeNodeCriteria.getAlarmSortCriteria();

        dynamicSearchSortAttributesMatchedAlarmResponse = getSearchAlarms(compositeNodeCriteria, expectedOutputAttributes, dynamicAttributes);

        return additionalAttributeHandler
                .getSortedAlarmRecords(dynamicSearchSortAttributesMatchedAlarmResponse, alarmSortCriteria, dynamicAttributes);
    }

    /**
     * Gets alarms in sorted order.
     *
     * @param compositeNodeCriteria
     *            - an encapsulation of nodes and alarm attributes
     * @param expectedOutputAttributes
     *            contains alarm attributes should come as response {@link ExpectedOutputAttributes}.
     * @return {@link AlarmAttributeResponse}
     */
    public AlarmAttributeResponse getSortedAlarms(final CompositeNodeCriteria compositeNodeCriteria,
                                                  final ExpectedOutputAttributes expectedOutputAttributes, final List<String> dynamicAttributes) {
        if (expectedOutputAttributes != null && expectedOutputAttributes.getOutputAttributes() != null) {
            expectedOutputAttributes.getOutputAttributes().removeAll(dynamicAttributes);
        }

        final List<AlarmSortCriterion> alarmSortCriteria = compositeNodeCriteria.getAlarmSortCriteria();

        compositeNodeCriteria.setSortAttribute(null);
        compositeNodeCriteria.setSortDirection(null);

        final AlarmAttributeResponse alarmAttributeResponse = compositeNodeCriteriaHandlerForAlarms.getAlarms(compositeNodeCriteria,
                expectedOutputAttributes, dynamicAttributes);

        return additionalAttributeHandler.getSortedAlarmRecords(alarmAttributeResponse, alarmSortCriteria, dynamicAttributes);
    }

    /**
     * Gets alarms matched to search attribute.
     *
     * @param compositeNodeCriteria
     *            - an encapsulation of nodes and alarm attributes
     * @param expectedOutputAttributes
     *            contains alarm attributes should come as response {@link ExpectedOutputAttributes}.
     * @return {@link AlarmAttributeResponse}
     * @throws Exception
     */
    public AlarmAttributeResponse getSearchAlarms(final CompositeNodeCriteria compositeNodeCriteria,
                                                  final ExpectedOutputAttributes expectedOutputAttributes, final List<String> dynamicAttributes)
            throws Exception {
        AlarmAttributeResponse dynamicSearchAttributesMatchedResponse = null;
        AlarmAttributeResponse alarmAttributesMatchedResponse = null;

        final List<AlarmAttributeCriteria> alarmAttributeCriterias = compositeNodeCriteria.getAlarmAttributeCriteria();

        // fetching all the dynamic search attributes from alarm attribute criteria.
        final List<AlarmAttributeCriteria> dynamicAlarmAttributes = dynamicAlarmAttributeInfoReader
                .readDynamicSearchAttributes(alarmAttributeCriterias);
        compositeNodeCriteria.setAlarmAttributeCriteria(alarmAttributeCriterias);

        alarmAttributesMatchedResponse = compositeNodeCriteriaHandlerForAlarms.getAlarms(compositeNodeCriteria, expectedOutputAttributes,
                dynamicAttributes);

        dynamicSearchAttributesMatchedResponse = additionalAttributeHandler.getSearchResponse(alarmAttributesMatchedResponse, dynamicAlarmAttributes,
                expectedOutputAttributes);
        return dynamicSearchAttributesMatchedResponse;
    }

}
