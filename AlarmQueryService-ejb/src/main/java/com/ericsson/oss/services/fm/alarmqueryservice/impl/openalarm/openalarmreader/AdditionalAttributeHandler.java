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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;

/**
 * AdditionalAttributeHandler provides functionality to support Additional attribute sort and search.
 */
public class AdditionalAttributeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdditionalAttributeHandler.class);

    @Inject
    private DynamicAlarmAttributeInfoReader dynamicAlarmAttributeInfoReader;

    @Inject
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;

    @Inject
    private SecondLevelSortingHandler secondLevelSortingHandler;

    /**
     * Get alarms matched to search attribute criteria.
     *
     * @param normalSearchAttributesResponse
     *            normal search criteria matched alarm response.
     * @param additionlAttributeCriteria
     *            additional attribute search criteria matched alarm response
     * @param expectedOutputAttributes
     *            expected output alarm attributes in response.
     * @param sortOrder
     * @param sortAttribute
     * @return {@link AlarmAttributeResponse}.
     */
    public AlarmAttributeResponse getSearchResponse(final AlarmAttributeResponse normalSearchAttributesResponse,
                                                    final List<AlarmAttributeCriteria> additionlAttributeCriteria,
                                                    final ExpectedOutputAttributes expectedOutputAttributes) {
        AlarmAttributeResponse dynamicSearchAttributesResponse = null;

        if (dynamicAlarmAttributeInfoReader.isSingleAdditionalAttributeWithNotEquals(additionlAttributeCriteria)) {
            final AlarmAttributeCriteria additionalAttributeCriteria = additionlAttributeCriteria.get(0);
            additionalAttributeCriteria.setOperator(Operator.EQ);
            additionlAttributeCriteria.add(0, additionalAttributeCriteria);

            dynamicSearchAttributesResponse = dynamicAlarmAttributeInfoReader.readDynamicAttributesMatchedSearchAlarms(additionlAttributeCriteria,
                    expectedOutputAttributes);

            if (!dynamicSearchAttributesResponse.getAlarmRecords().isEmpty()) {
                normalSearchAttributesResponse.getAlarmRecords().removeAll(dynamicSearchAttributesResponse.getAlarmRecords());
            }
            LOGGER.debug("Single Additional attribute Not Equal case alarms response count{}:", normalSearchAttributesResponse.getAlarmRecords());
            return normalSearchAttributesResponse;
        } else {
            dynamicSearchAttributesResponse = dynamicAlarmAttributeInfoReader.readDynamicAttributesMatchedSearchAlarms(additionlAttributeCriteria,
                    expectedOutputAttributes);
            if (!dynamicSearchAttributesResponse.getAlarmRecords().isEmpty()) {
                normalSearchAttributesResponse.getAlarmRecords().retainAll(dynamicSearchAttributesResponse.getAlarmRecords());
                return normalSearchAttributesResponse;
            } else {
                return dynamicSearchAttributesResponse = new AlarmAttributeResponse(new ArrayList<AlarmRecord>(), null);
            }
        }
    }

    /**
     * Method takes dynamicAlarmAttributeResponse {@link AlarmAttributeResponse} ,totalAlarmRecordResponse {@link AlarmAttributeResponse} ,sort
     * attribute and Sorting order and sort all the alarms according to sortingOrder .
     *
     * @param dynamicAlarmAttributeResponse
     *            additional sort attribute matched alarm response.
     * @param totalAlarmRecordResponse
     *            normal search criteria matched
     * @param alarmSortCriteria
     *            list of {<link> AlarmSortCriterion}
     * @param dynamicSortingAttributes
     *            list of dynamic attributes provided in sorting criteria.
     * @return {@link AlarmAttributeResponse}
     */
    public AlarmAttributeResponse getSortedAlarmRecords(final AlarmAttributeResponse totalAlarmRecordResponse,
                                                        final List<AlarmSortCriterion> alarmSortCriteria, final List<String> dynamicSortingAttributes) {

        LOGGER.debug("Total alarm record size is {} and dynamic attribute list is {}", totalAlarmRecordResponse.getAlarmRecords().size(),
                dynamicSortingAttributes);

        final List<AlarmRecord> finalAlarmRecords = new ArrayList<AlarmRecord>();
        final List<AlarmRecord> firstLevelSortedRecords = new ArrayList<AlarmRecord>();

        for (final AlarmSortCriterion alarmSortCriterion : alarmSortCriteria) {
            final String sortingAttribute = alarmSortCriterion.getSortAttribute();
            final SortingOrder sortOrder = alarmSortCriterion.getSortOrder();
            final SortSequence sortingSequence = alarmSortCriterion.getSortSequence();

            if (SortSequence.FIRST_LEVEL_SORT.equals(sortingSequence) && dynamicSortingAttributes.contains(sortingAttribute)) {
                // Dynamic alarm attribute alarms sorted based on the sortAttribute.
                firstLevelSortedRecords.addAll(alarmAttributeResponseBuilder
                        .sortRecordsBasedOnDynamicAlarmAttribute(totalAlarmRecordResponse.getAlarmRecords(), sortingAttribute, sortOrder));

                // If only single FMX additional attribute is there, then we need to prepare response for single attribute only.
                if (alarmSortCriteria.size() == 1) {
                    finalAlarmRecords.addAll(firstLevelSortedRecords);
                    break;
                }

            } else if (SortSequence.SECOND_LEVEL_SORT.equals(sortingSequence)) {
                if (dynamicSortingAttributes.contains(sortingAttribute)) {
                    if (dynamicSortingAttributes.size() == 1) {
                        // When 1st sort Attribute is normal attribute and 2nd sort attribute is FMX additional attribute. This method takes the first
                        // level sorted records from DPS and sorts for second level.
                        finalAlarmRecords.addAll(secondLevelSortingHandler
                                .sortBasedOnDynamicAttributeWhenFirstSortedOnStandard(totalAlarmRecordResponse.getAlarmRecords(), alarmSortCriteria));
                        break;
                    } else if (dynamicSortingAttributes.size() == 2) {
                        // When 1st and 2nd both sort attributes are FMX additional attributes. This method takes the first level sorted records and
                        // sorts for second level.
                        finalAlarmRecords.addAll(secondLevelSortingHandler
                                .sortBasedOnDynamicAttributeWhenFirstSortedOnDynamic(firstLevelSortedRecords, alarmSortCriteria));
                    }
                } else {
                    // When 1st sort attribute is FMX additional attribute and 2nd sort attribute is normal attribute. This method takes the first
                    // level sorted records and sorts for second level.
                    finalAlarmRecords.addAll(secondLevelSortingHandler.sortBasedOnStandardAttributeWhenFirstSortedOnDynamic(firstLevelSortedRecords,
                            alarmSortCriteria));
                }
            }
        }
        return alarmAttributeResponseBuilder.buildAttributeResponse(finalAlarmRecords);
    }
}
