/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PREVIOUS_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.NO_ALARMS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.AdditionalAttributeAscendingcomparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.AdditionalAttributeDescendingcomparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.BooleanTypeAscendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.BooleanTypeDescendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.DateTypeAscendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.DateTypeDescendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.IntegerTypeAscendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.IntegerTypeDescendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.LongTypeAscendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.LongTypeDescendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.PresentSeverityAscendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.PresentSeverityDescendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.PreviousSeverityAscendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.PreviousSeverityDescendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.StringTypeAscendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.StringTypeDescendingComparator;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.SecondLevelSortingHandler;

/**
 * Responsible for building {@link AlarmAttributeResponse} based on the alarms records.
 *
 **/
@ApplicationScoped
public class AlarmAttributeResponseBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmAttributeResponseBuilder.class);

    private static final List<String> INTEGER_TYPE_ATTRIBUTES = new ArrayList<String>();
    private static final List<String> LONG_TYPE_ATTRIBUTES = new ArrayList<String>();
    private static final List<String> DATE_TYPE_ATTRIBUTES = new ArrayList<String>();
    private static final List<String> BOOLEAN_TYPE_ATTRIBUTES = new ArrayList<String>();

    @Inject
    private OpenAlarmParser openAlarmParser;

    @Inject
    private SecondLevelSortingHandler secondLevelSortingHandler;

    /**
     * Prepares the static list of number, boolean and date type attribute name list, also prepares string operator list.
     */
    @PostConstruct
    public void prepare() {
        INTEGER_TYPE_ATTRIBUTES.addAll(openAlarmParser.getIntegerTypeAttributes());
        LONG_TYPE_ATTRIBUTES.addAll(openAlarmParser.getLongTypeAttributes());
        DATE_TYPE_ATTRIBUTES.addAll(openAlarmParser.getDateTypeAttributes());
        BOOLEAN_TYPE_ATTRIBUTES.addAll(openAlarmParser.getBooleanTypeAttributes());
    }

    public AlarmAttributeResponse buildAttributeResponse(final List<AlarmRecord> alarmRecords) {
        AlarmAttributeResponse alarmAttributeResponse = null;
        if (alarmRecords.isEmpty()) {
            alarmAttributeResponse = new AlarmAttributeResponse(alarmRecords, NO_ALARMS);
        } else {
            alarmAttributeResponse = new AlarmAttributeResponse(alarmRecords, SUCCESS);
        }
        return alarmAttributeResponse;
    }

    public List<AlarmRecord> sortRecordsBasedOnSeverity(final List<AlarmRecord> alarmRecords, final String sortAttribute,
                                                        final SortingOrder sortingOrder) {
        if (sortAttribute != null) {
            if (SortingOrder.DESCENDING.equals(sortingOrder)) {
                if (PRESENT_SEVERITY.equals(sortAttribute)) {
                    Collections.sort(alarmRecords, new PresentSeverityDescendingComparator(PRESENT_SEVERITY));
                } else if (PREVIOUS_SEVERITY.equals(sortAttribute)) {
                    Collections.sort(alarmRecords, new PreviousSeverityDescendingComparator(PREVIOUS_SEVERITY));
                }
            } else {
                if (PRESENT_SEVERITY.equals(sortAttribute)) {
                    Collections.sort(alarmRecords, new PresentSeverityAscendingComparator(PRESENT_SEVERITY));
                } else if (PREVIOUS_SEVERITY.equals(sortAttribute)) {
                    Collections.sort(alarmRecords, new PreviousSeverityAscendingComparator(PREVIOUS_SEVERITY));
                }
            }
        }
        return alarmRecords;
    }

    /**
     * Method takes all the alarm records {@code List<AlarmRecord>} , sortAttribute and sortingOrder. And perform sorting {@link SortingOrder} based
     * on the sortAttribute.
     *
     * @param alarmRecords
     *            --contains alarm information.
     * @param sortAttribute
     *            -- provided alarm additional attribute for sorting.
     * @param sortingOrder
     *            -- indicates SortOrder.
     * @return returns list of alarm records in sorted order.
     */

    public List<AlarmRecord> sortRecordsBasedOnDynamicAlarmAttribute(final List<AlarmRecord> alarmRecords, final String sortAttribute,
                                                                     final SortingOrder sortingOrder) {
        LOGGER.debug("sortRecordsBasedOnDynamicAlarmAttribute: sort attribute received is: {}", sortAttribute);
        if (!alarmRecords.isEmpty()) {
            if (SortingOrder.DESCENDING.equals(sortingOrder)) {
                Collections.sort(alarmRecords, new AdditionalAttributeDescendingcomparator(sortAttribute));
            } else {
                Collections.sort(alarmRecords, new AdditionalAttributeAscendingcomparator(sortAttribute));
            }
        }
        LOGGER.debug("sortRecordsBasedOnDynamicAlarmAttribute: Total number of alarms after sorting :{}", alarmRecords.size());
        return alarmRecords;
    }

    /**
     * Method to sort Alarm Records in memory using comparator.
     *
     * @param alarmRecords
     *            alarm Records to be sorted.
     * @param sortAttribute
     *            alarm attribute based on which, sorting to be done.
     * @param sortingOrder
     *            sorting order.
     * @return list of alarm records in sorted order.
     */
    public List<AlarmRecord> sort(final List<AlarmRecord> alarmRecords, final String sortAttribute, final SortingOrder sortingOrder) {
        if (sortAttribute != null) {
            if (SortingOrder.DESCENDING.equals(sortingOrder)) {
                if (LONG_TYPE_ATTRIBUTES.contains(sortAttribute)) {
                    Collections.sort(alarmRecords, new LongTypeDescendingComparator(sortAttribute));
                } else if (DATE_TYPE_ATTRIBUTES.contains(sortAttribute)) {
                    Collections.sort(alarmRecords, new DateTypeDescendingComparator(sortAttribute));
                } else if (BOOLEAN_TYPE_ATTRIBUTES.contains(sortAttribute)) {
                    Collections.sort(alarmRecords, new BooleanTypeDescendingComparator(sortAttribute));
                } else if (INTEGER_TYPE_ATTRIBUTES.contains(sortAttribute)) {
                    Collections.sort(alarmRecords, new IntegerTypeDescendingComparator(sortAttribute));
                } else {
                    Collections.sort(alarmRecords, new StringTypeDescendingComparator(sortAttribute));
                }
            } else {
                if (LONG_TYPE_ATTRIBUTES.contains(sortAttribute)) {
                    Collections.sort(alarmRecords, new LongTypeAscendingComparator(sortAttribute));
                } else if (DATE_TYPE_ATTRIBUTES.contains(sortAttribute)) {
                    Collections.sort(alarmRecords, new DateTypeAscendingComparator(sortAttribute));
                } else if (BOOLEAN_TYPE_ATTRIBUTES.contains(sortAttribute)) {
                    Collections.sort(alarmRecords, new BooleanTypeAscendingComparator(sortAttribute));
                } else if (INTEGER_TYPE_ATTRIBUTES.contains(sortAttribute)) {
                    Collections.sort(alarmRecords, new IntegerTypeAscendingComparator(sortAttribute));
                } else {
                    Collections.sort(alarmRecords, new StringTypeAscendingComparator(sortAttribute));
                }
            }
        }
        return alarmRecords;
    }

    /**
     * Method clubs the alarm records in a sorted order.
     *
     * @param alarmRecords
     *            list of alarm record to be sorted.
     * @param alarmSortCriteria
     *            list of {@link AlarmSortCriterion}
     */
    public List<AlarmRecord> mergeAllSortedAlarmRecords(List<AlarmRecord> alarmRecords, final List<AlarmSortCriterion> alarmSortCriteria,
                                                        final List<String> dynamicSortAttributes) {
        for (final AlarmSortCriterion alarmSortCriterion : alarmSortCriteria) {
            final String sortAttribute = alarmSortCriterion.getSortAttribute();
            final SortingOrder sortOrder = alarmSortCriterion.getSortOrder();
            if (dynamicSortAttributes.contains(sortAttribute)) {
                break;
            } else if (SortSequence.FIRST_LEVEL_SORT.equals(alarmSortCriterion.getSortSequence())) {
                if (PRESENT_SEVERITY.equals(alarmSortCriterion.getSortAttribute())
                        || PREVIOUS_SEVERITY.equals(alarmSortCriterion.getSortAttribute())) {
                    alarmRecords = sortRecordsBasedOnSeverity(alarmRecords, sortAttribute, sortOrder);
                } else {
                    alarmRecords = sort(alarmRecords, sortAttribute, sortOrder);
                }
            } else {
                alarmRecords = secondLevelSortingHandler.sortBasedOnStandardAttributeWhenFirstSortedOnStandard(alarmRecords, alarmSortCriteria);
            }
        }
        return alarmRecords;
    }
}
