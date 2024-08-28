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

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;

/**
 * Responsible for retrieving the alarms based on the conditions set in {@link CompositeEventTimeCriteria}.
 **/

public class DynamicAttributeEventTimeCriteriaHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAttributeEventTimeCriteriaHandler.class);

    @Inject
    private CompositeEventTimeCriteriaHandler compositeEventTimeCriteriaHandler;

    @Inject
    private AdditionalAttributeHandler additionalAttributeHandler;

    @Inject
    private DynamicAlarmAttributeInfoReader dynamicAlarmAttributeInfoReader;

    /**
     * Method takes {@link CompositeEventTimeCriteria} and {@link ExpectedOutputAttributes} and perform sorting based on
     * {@link ExpectedOutputAttributes} which alarms are matched with the {@link CompositeEventTimeCriteria}.If alarms not matched with
     * {@link CompositeEventTimeCriteria} those alarms will be sorted based on the insertTime of an alarms.
     *
     * @param compositeEventTimeCriteria
     *            - an encapsulation of nodes and alarm attributes
     * @param expectedOutputAttributes
     *            contains sort order information {@link ExpectedOutputAttributes}.
     * @param dynamicSortingAttributes
     *            list of dynamic attributes provided in sorting criteria.
     * @return return {@link AlarmAttributeResponse}
     * @throws Exception
     */
    public AlarmAttributeResponse getSortedSearchedAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                                          final ExpectedOutputAttributes expectedOutputAttributes,
                                                          final List<String> dynamicSortingAttributes) throws Exception {

        AlarmAttributeResponse dynamicSearchAttributesMatchedAlarmResponse = null;

        expectedOutputAttributes.getOutputAttributes().removeAll(dynamicSortingAttributes);

        compositeEventTimeCriteria.setSortAttribute(null);
        compositeEventTimeCriteria.setSortDirection(null);

        final List<AlarmSortCriterion> alarmSortCriteria = compositeEventTimeCriteria.getAlarmSortCriteria();

        dynamicSearchAttributesMatchedAlarmResponse = getSearchAlarms(compositeEventTimeCriteria, expectedOutputAttributes, dynamicSortingAttributes);

        return additionalAttributeHandler.getSortedAlarmRecords(dynamicSearchAttributesMatchedAlarmResponse, alarmSortCriteria,
                dynamicSortingAttributes);
    }

    /**
     * Gets alarms in sorted order.
     *
     * @param compositeEventTimeCriteria
     * @param expectedOutputAttributes
     * @param dynamicSortingAttributes
     * @return
     */
    public AlarmAttributeResponse getSortedAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                                  final ExpectedOutputAttributes expectedOutputAttributes, final List<String> dynamicSortingAttributes) {

        LOGGER.debug("getSortedAlarms() method criteria {} and output :{}", compositeEventTimeCriteria, expectedOutputAttributes);

        final String sortAttribute = compositeEventTimeCriteria.getSortAttribute();

        expectedOutputAttributes.getOutputAttributes().remove(sortAttribute);

        final List<AlarmSortCriterion> alarmSortCriteria = compositeEventTimeCriteria.getAlarmSortCriteria();

        compositeEventTimeCriteria.setSortAttribute(null);
        compositeEventTimeCriteria.setSortDirection(null);

        final AlarmAttributeResponse alarmAttributeResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria,
                expectedOutputAttributes, dynamicSortingAttributes);

        return additionalAttributeHandler.getSortedAlarmRecords(alarmAttributeResponse, alarmSortCriteria, dynamicSortingAttributes);
    }

    /**
     * Gets alarms matched to search attribute.
     *
     * @param compositeEventTimeCriteria
     * @param expectedOutputAttributes
     * @param dynamicAttributes
     * @return
     * @throws Exception
     */
    public AlarmAttributeResponse getSearchAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                                  final ExpectedOutputAttributes expectedOutputAttributes, final List<String> dynamicAttributes)
            throws Exception {

        AlarmAttributeResponse dynamicSearchAttributesMatchedResponse = null;
        AlarmAttributeResponse alarmAttributesMatchedResponse = null;

        final List<AlarmAttributeCriteria> alarmAttributeCriterias = compositeEventTimeCriteria.getAlarmAttributeCriteria();

        // fetching all the dynamic search attributes from alarm attribute criterias.
        final List<AlarmAttributeCriteria> dynamicAlarmAttributes = dynamicAlarmAttributeInfoReader
                .readDynamicSearchAttributes(alarmAttributeCriterias);

        if (alarmAttributeCriterias.isEmpty() && isEventTimeCriteriaEmpty(compositeEventTimeCriteria)) {
            compositeEventTimeCriteria.setToTime(new Date());
            compositeEventTimeCriteria.setOperator(Operator.LE);
        }

        compositeEventTimeCriteria.setAlarmAttributeCriteria(alarmAttributeCriterias);

        alarmAttributesMatchedResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes,
                dynamicAttributes);

        dynamicSearchAttributesMatchedResponse = additionalAttributeHandler.getSearchResponse(alarmAttributesMatchedResponse, dynamicAlarmAttributes,
                expectedOutputAttributes);
        return dynamicSearchAttributesMatchedResponse;
    }

    private boolean isEventTimeCriteriaEmpty(final CompositeEventTimeCriteria compositeEventTimeCriteria) {
        final Date fromDate = compositeEventTimeCriteria.getFromTime();
        final Date toDate = compositeEventTimeCriteria.getToTime();
        final List<String> nodes = compositeEventTimeCriteria.getNodes();

        if (fromDate == null && toDate == null && (nodes == null || nodes.isEmpty())) {
            return true;
        }
        return false;
    }
}
