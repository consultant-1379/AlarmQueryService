/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PREVIOUS_SEVERITY;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;

/**
 * The class is responsible to handle the second level sorting when the sorting criteria contains FMX additional attributes.
 */
public class SecondLevelSortingHandler {

    @Inject
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;

    /**
     * Method handles second level sorting (in case of two column sorting) based on standard attribute provided in the sort criteria. This method is
     * called when 1st level sorting is done on standard attribute.
     *
     * @param firstLevelSortedAlarmRecords
     *            alarm records after sorting at 1st level.
     * @param alarmSortCriteria
     *            alarm sorting criteria.
     * @return the sorted list of alarm records.
     */
    public List<AlarmRecord> sortBasedOnStandardAttributeWhenFirstSortedOnStandard(final List<AlarmRecord> firstLevelSortedAlarmRecords,
                                                                                   final List<AlarmSortCriterion> alarmSortCriteria) {
        final List<AlarmRecord> sortedAlarmRecords = new ArrayList<AlarmRecord>();
        final AlarmSortCriterion secondSortCriterion = alarmSortCriteria.get(1);

        final String sortingAttribute = alarmSortCriteria.get(0).getSortAttribute();
        for (int index = 0; index < firstLevelSortedAlarmRecords.size();) {
            final List<AlarmRecord> tempListToBeSorted = new ArrayList<AlarmRecord>();

            index = extractTempListToBeSortedBasedOnStandardAttribute(firstLevelSortedAlarmRecords, sortingAttribute, index, tempListToBeSorted);

            if (PRESENT_SEVERITY.equals(secondSortCriterion.getSortAttribute())
                    || PREVIOUS_SEVERITY.equals(secondSortCriterion.getSortAttribute())) {
                sortedAlarmRecords.addAll(alarmAttributeResponseBuilder.sortRecordsBasedOnSeverity(tempListToBeSorted, secondSortCriterion
                        .getSortAttribute(), secondSortCriterion.getSortOrder()));
            } else {
                sortedAlarmRecords.addAll(alarmAttributeResponseBuilder.sort(tempListToBeSorted, secondSortCriterion.getSortAttribute(),
                        secondSortCriterion.getSortOrder()));
            }
        }

        return sortedAlarmRecords;
    }

    /**
     * Method handled the 2nd level sorting (in case of two column sorting) based on dynamic attribute provided in the sort criteria. This method is
     * called when 1st level sorting is done on standard attribute.
     *
     * @param firstLevelSortedAlarmRecords
     *            alarm records after sorting at 1st level.
     * @param alarmSortCriteria
     *            alarm sorting criteria.
     * @return the sorted list of alarm records.
     */
    public List<AlarmRecord> sortBasedOnDynamicAttributeWhenFirstSortedOnStandard(final List<AlarmRecord> firstLevelSortedAlarmRecords,
                                                                                  final List<AlarmSortCriterion> alarmSortCriteria) {
        final List<AlarmRecord> sortedAlarmRecords = new ArrayList<AlarmRecord>();
        final AlarmSortCriterion dynamicSortingCriterion = alarmSortCriteria.get(1);

        // From client max sorting attribute that can be specified is two. So getting the first sorting attribute using the index of array list.
        final String firstSortingAttribute = alarmSortCriteria.get(0).getSortAttribute();

        for (int index = 0; index < firstLevelSortedAlarmRecords.size();) {
            final List<AlarmRecord> tempListToBeSorted = new ArrayList<AlarmRecord>();

            index = extractTempListToBeSortedBasedOnStandardAttribute(firstLevelSortedAlarmRecords, firstSortingAttribute, index, tempListToBeSorted);

            sortedAlarmRecords.addAll(alarmAttributeResponseBuilder.sortRecordsBasedOnDynamicAlarmAttribute(tempListToBeSorted,
                    dynamicSortingCriterion.getSortAttribute(), dynamicSortingCriterion.getSortOrder()));
        }

        return sortedAlarmRecords;
    }

    /**
     * Method handled the 2nd level sorting (in case of two column sorting) based on dynamic attribute provided in the sort criteria. This method is
     * called when 1st level sorting is done on dynamic attribute.
     *
     * @param completeAlarmRecords
     *            complete alarm records.
     * @param firstLevelSortedAlarmRecords
     *            alarm records after sorting at 1st level.
     * @param alarmSortCriteria
     *            alarm sorting criteria.
     * @return sorted list of alarm records.
     */
    public List<AlarmRecord> sortBasedOnDynamicAttributeWhenFirstSortedOnDynamic(final List<AlarmRecord> firstLevelSortedAlarmRecords,
                                                                                 final List<AlarmSortCriterion> alarmSortCriteria) {
        final List<AlarmRecord> sortedAlarmRecords = new ArrayList<AlarmRecord>();

        final String firstSortingAttribute = alarmSortCriteria.get(0).getSortAttribute();
        for (int index = 0; index < firstLevelSortedAlarmRecords.size();) {
            final List<AlarmRecord> tempListToBeSorted = new ArrayList<AlarmRecord>();

            index = extractTempListToBeSortedBasedOnAdditionalAttribute(firstLevelSortedAlarmRecords, firstSortingAttribute, index,
                    tempListToBeSorted);

            // Dynamic alarm attribute alarms sorted based on the sortAttribute.
            sortedAlarmRecords.addAll(alarmAttributeResponseBuilder.sortRecordsBasedOnDynamicAlarmAttribute(tempListToBeSorted, alarmSortCriteria
                    .get(1).getSortAttribute(), alarmSortCriteria.get(1).getSortOrder()));
        }

        return sortedAlarmRecords;
    }

    /**
     * Method handled the 2nd level sorting (in case of two column sorting) based on standard attribute provided in the sort criteria. This method is
     * called when 1st level sorting is done on dynamic attribute.
     *
     * @param completeAlarmRecords
     *            complete alarm records.
     * @param firstLevelSortedAlarmRecords
     *            alarm records after sorting at 1st level.
     * @param alarmSortCriteria
     *            alarm sorting criteria.
     * @return sorted list of alarm records.
     */
    public List<AlarmRecord> sortBasedOnStandardAttributeWhenFirstSortedOnDynamic(final List<AlarmRecord> firstLevelSortedAlarmRecords,
                                                                                  final List<AlarmSortCriterion> alarmSortCriteria) {
        final List<AlarmRecord> sortedAlarmRecords = new ArrayList<AlarmRecord>();

        final String firstSortingAttribute = alarmSortCriteria.get(0).getSortAttribute();

        for (int index = 0; index < firstLevelSortedAlarmRecords.size();) {
            final List<AlarmRecord> tempListToBeSorted = new ArrayList<AlarmRecord>();

            index = extractTempListToBeSortedBasedOnAdditionalAttribute(firstLevelSortedAlarmRecords, firstSortingAttribute, index,
                    tempListToBeSorted);

            // Dynamic alarm attribute alarms sorted based on the sortAttribute.
            if (PRESENT_SEVERITY.equals(alarmSortCriteria.get(1).getSortAttribute())
                    || PREVIOUS_SEVERITY.equals(alarmSortCriteria.get(1).getSortAttribute())) {
                sortedAlarmRecords.addAll(alarmAttributeResponseBuilder.sortRecordsBasedOnSeverity(tempListToBeSorted, alarmSortCriteria.get(1)
                        .getSortAttribute(), alarmSortCriteria.get(1).getSortOrder()));
            } else {
                sortedAlarmRecords.addAll(alarmAttributeResponseBuilder.sort(tempListToBeSorted, alarmSortCriteria.get(1).getSortAttribute(),
                        alarmSortCriteria.get(1).getSortOrder()));
            }
        }

        return sortedAlarmRecords;
    }

    private int extractTempListToBeSortedBasedOnStandardAttribute(final List<AlarmRecord> firstLevelSortedAlarmRecords,
                                                                  final String sortingAttribute, int index,
                                                                  final List<AlarmRecord> tempListToBeSorted) {
        final AlarmRecord firstAlarmRecord = firstLevelSortedAlarmRecords.get(index);
        final Object attributeValueFromFirstRecord = firstAlarmRecord.getAttribute(sortingAttribute);

        for (; index < firstLevelSortedAlarmRecords.size(); index++) {
            final AlarmRecord secondAlarmRecord = firstLevelSortedAlarmRecords.get(index);
            final Object attributeValueFromSecondRecord = secondAlarmRecord.getAttribute(sortingAttribute);
            if (attributeValueFromFirstRecord != null && attributeValueFromSecondRecord != null) {
                if (attributeValueFromFirstRecord.equals(attributeValueFromSecondRecord)) {
                    tempListToBeSorted.add(secondAlarmRecord);
                } else {
                    break;
                }
            } else if ((attributeValueFromFirstRecord == null || String.valueOf(attributeValueFromFirstRecord).isEmpty())
                    && (attributeValueFromSecondRecord == null || String.valueOf(attributeValueFromSecondRecord).isEmpty())) {
                tempListToBeSorted.add(secondAlarmRecord);
            } else {
                break;
            }
        }
        return index;
    }

    private int extractTempListToBeSortedBasedOnAdditionalAttribute(final List<AlarmRecord> firstLevelSortedAlarmRecords,
                                                                    final String firstSortingAttribute, int index,
                                                                    final List<AlarmRecord> tempListToBeSorted) {
        final AlarmRecord firstAlarmRecord = firstLevelSortedAlarmRecords.get(index);
        final String attributeValueFromFirstRecord = firstAlarmRecord.getAdditionalAttributeMap().get(firstSortingAttribute);

        for (; index < firstLevelSortedAlarmRecords.size(); index++) {
            final AlarmRecord secondAlarmRecord = firstLevelSortedAlarmRecords.get(index);
            final String attributeValueFromSecondRecord = secondAlarmRecord.getAdditionalAttributeMap().get(firstSortingAttribute);
            if (attributeValueFromFirstRecord != null && attributeValueFromSecondRecord != null) {
                if (attributeValueFromFirstRecord.equals(attributeValueFromSecondRecord)) {
                    tempListToBeSorted.add(secondAlarmRecord);
                } else {
                    break;
                }
            } else if ((attributeValueFromFirstRecord == null || String.valueOf(attributeValueFromFirstRecord).isEmpty())
                    && (attributeValueFromSecondRecord == null || String.valueOf(attributeValueFromSecondRecord).isEmpty())) {
                tempListToBeSorted.add(secondAlarmRecord);
            } else {
                break;
            }
        }
        return index;
    }
}
