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

package com.ericsson.oss.services.fm.alarmqueryservice.impl;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FAILED_TO_READ_FROM_DB;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.LOG_ERROR_MESSAGE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.NO_ALARMS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PREVIOUS_SEVERITY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.AlarmPoIdCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeEventTimeCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeEventTimeCriteriaHandlerForCount;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForAlarms;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForCount;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForPoIds;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.CompositeNodeCriteriaHandlerForSeverities;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader.OORCriteriaHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DynamicAlarmAttributeValidator;

/**
 * A class responsible for delegating to respective open alarm handler classes based on the input.
 */
public class OpenAlarmDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAlarmDelegate.class);

    @Inject
    private OORCriteriaHandler oorCriteriaHandler;

    @Inject
    private AlarmPoIdCriteriaHandler poIdCriteriaHandler;

    @Inject
    private CompositeNodeCriteriaHandlerForAlarms compositeNodeCriteriaHandlerForAlarms;

    @Inject
    private CompositeNodeCriteriaHandlerForPoIds compositeNodeCriteriaHandlerForPoIds;

    @Inject
    private CompositeEventTimeCriteriaHandler compositeEventTimeCriteriaHandler;

    @Inject
    private CompositeEventTimeCriteriaHandlerForCount compositeEventTimeCriteriaHandlerForCount;

    @Inject
    private CompositeNodeCriteriaHandlerForSeverities compositeNodeCriteriaHandlerForSeverities;

    @Inject
    private CompositeNodeCriteriaHandlerForCount compositeNodeCriteriaHandlerForCount;

    @Inject
    private AuthorizationHandler authorizationHandler;

    @Inject
    private OpenAlarmParser openAlarmParser;

    @Inject
    private DynamicAlarmAttributeValidator dynamicAlarmAttributeValidator;

    @Inject
    private DynamicAttributeCriteriaHandler dynamicAttributeCriteriaHandler;

    @Inject
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;

    public AlarmAttributeResponse getAlarms(final AlarmPoIdCriteria alarmPoIdCriteria, final ExpectedOutputAttributes expectedOutputAttributes,
                                            final boolean authorizationRequired) {
        isAuthorizedForOpenAlarms(authorizationRequired);
        isAlarmAttributesLoaded();
        return poIdCriteriaHandler.getAlarms(alarmPoIdCriteria, expectedOutputAttributes);
    }

    public AlarmAttributeResponse getAlarms(final CompositeNodeCriteria compositeNodeCriteria,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final boolean authorizationRequired) {
        isAuthorizedForOpenAlarms(authorizationRequired);
        isAlarmAttributesLoaded();
        AlarmAttributeResponse alarmAttributeResponse = null;

        try {
            boolean isDynamicSearchCriteria = false;

            List<AlarmSortCriterion> alarmSortCriteria = compositeNodeCriteria.getAlarmSortCriteria();
            if (alarmSortCriteria == null) {
                // To avoid null pointer for request coming from CLI. Because CLI won't have sorting criteria.
                alarmSortCriteria = Collections.<AlarmSortCriterion> emptyList();
                compositeNodeCriteria.setAlarmSortCriteria(alarmSortCriteria);
            }
            final String oldSortAttribute = compositeNodeCriteria.getSortAttribute();

            if (oldSortAttribute != null) {
                alarmSortCriteria.add(setAlarmSortCriterionForOldSortAttribute(compositeNodeCriteria.getSortDirection(), oldSortAttribute));
                compositeNodeCriteria.setAlarmSortCriteria(alarmSortCriteria);
            }

            final List<String> sortAttributes = extractSortAttributeNames(expectedOutputAttributes, alarmSortCriteria);

            final List<String> dynamicSortAttributes = dynamicAlarmAttributeValidator.filterFmxAdditionalAttributes(sortAttributes);
            isDynamicSearchCriteria = dynamicAlarmAttributeValidator
                    .isFmxAdditionalAttributeCriteria(compositeNodeCriteria.getAlarmAttributeCriteria());

            if (!(!dynamicSortAttributes.isEmpty() || isDynamicSearchCriteria)) { // standard attribute search/sort criteria
                alarmAttributeResponse = compositeNodeCriteriaHandlerForAlarms.getAlarms(compositeNodeCriteria, expectedOutputAttributes,
                        dynamicSortAttributes);
            } else {
                expectedOutputAttributes.getOutputAttributes().add(ADDITIONAL_INFORMATION);
                alarmAttributeResponse = dynamicAttributeCriteriaHandler.getAlarms(compositeNodeCriteria, expectedOutputAttributes,
                        isDynamicSearchCriteria, dynamicSortAttributes);
            }
            LOGGER.debug("Total Alarms :: {} found with given compositeNodeCriteria {} and the expected attributes are {}",
                    alarmAttributeResponse.getAlarmRecords().size(), compositeNodeCriteria, expectedOutputAttributes);
        } catch (final AttributeConstraintViolationException | ModelConstraintViolationException exception) {
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append(FAILED_TO_READ_FROM_DB).append("with given compositeNodeCriteria {} and the expected attributes are {}");
            LOGGER.error(errorMessageBuilder.toString(), exception, compositeNodeCriteria, expectedOutputAttributes);
            alarmAttributeResponse = new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), exception.getMessage());
        } catch (final Exception exception) {
            LOGGER.error("Error while retrieving alarms from DB {} with given compositeNodeCriteria {} and the expected attributes are {}", exception,
                    compositeNodeCriteria, expectedOutputAttributes);
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append(LOG_ERROR_MESSAGE).append(exception.getMessage());
            return new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), errorMessageBuilder.toString());
        }
        return alarmAttributeResponse;
    }

    public AlarmAttributeResponse getAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final boolean authorizationRequired) {
        isAuthorized(authorizationRequired);
        isAlarmAttributesLoaded();
        AlarmAttributeResponse alarmAttributeResponse = null;

        try {
            boolean isDynamicSearchCriteria = false;

            List<AlarmSortCriterion> alarmSortCriteria = compositeEventTimeCriteria.getAlarmSortCriteria();
            final String oldSortAttribute = compositeEventTimeCriteria.getSortAttribute();

            if (alarmSortCriteria == null) {
                // To avoid null pointer for request coming from CLI. Because CLI won't have sorting criteria.
                alarmSortCriteria = Collections.<AlarmSortCriterion> emptyList();
                compositeEventTimeCriteria.setAlarmSortCriteria(alarmSortCriteria);
            }

            if (oldSortAttribute != null) {
                alarmSortCriteria.add(setAlarmSortCriterionForOldSortAttribute(compositeEventTimeCriteria.getSortDirection(), oldSortAttribute));
                compositeEventTimeCriteria.setAlarmSortCriteria(alarmSortCriteria);
            }

            final List<String> sortAttributes = extractSortAttributeNames(expectedOutputAttributes, alarmSortCriteria);

            final List<String> dynamicSortAttributes = dynamicAlarmAttributeValidator.filterFmxAdditionalAttributes(sortAttributes);
            isDynamicSearchCriteria = dynamicAlarmAttributeValidator
                    .isFmxAdditionalAttributeCriteria(compositeEventTimeCriteria.getAlarmAttributeCriteria());

            if (!(!dynamicSortAttributes.isEmpty() || isDynamicSearchCriteria)) {
                // standard attribute search/sort criteria
                alarmAttributeResponse = compositeEventTimeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes,
                        dynamicSortAttributes);
            } else {
                expectedOutputAttributes.getOutputAttributes().add(ADDITIONAL_INFORMATION);
                // custom attribute search/sort criteria
                alarmAttributeResponse = dynamicAttributeCriteriaHandler.getAlarms(compositeEventTimeCriteria, expectedOutputAttributes,
                        dynamicSortAttributes, isDynamicSearchCriteria);
            }
            LOGGER.debug("Total Alarms :: {} found with given compositeEventTimeCriteria {} and the expected attributes are {}",
                    alarmAttributeResponse.getAlarmRecords().size(), compositeEventTimeCriteria, expectedOutputAttributes);
        } catch (final AttributeConstraintViolationException | ModelConstraintViolationException exception) {
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append(FAILED_TO_READ_FROM_DB).append("with given compositeEventTimeCriteria {} and the expected attributes are {}");
            LOGGER.error(errorMessageBuilder.toString(), exception, compositeEventTimeCriteria, expectedOutputAttributes);
            alarmAttributeResponse = new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), exception.getMessage());
        } catch (final Exception exception) {
            LOGGER.error("Error while retrieving alarms from DB {} with given compositeEventTimeCriteria {} and the expected attributes are {}",
                    exception, compositeEventTimeCriteria, expectedOutputAttributes);
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append(LOG_ERROR_MESSAGE).append(exception.getMessage());
            return new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), errorMessageBuilder.toString());
        }
        return alarmAttributeResponse;
    }

    /**
     * Extracts the attribute names from alarm sort criteria.
     *
     * @param expectedOutputAttributes
     *            --{@link ExpectedOutputAttributes}
     * @param alarmSortCriteria
     *            --list of {@link AlarmSortCriterion}
     * @return the list of attributes given in sort criteria.
     */
    private List<String> extractSortAttributeNames(final ExpectedOutputAttributes expectedOutputAttributes,
                                                   final List<AlarmSortCriterion> alarmSortCriteria) {
        final List<String> sortAttributes = new ArrayList<String>(2);
        for (final AlarmSortCriterion sortingCriterian : alarmSortCriteria) {
            final String sortAttributeName = sortingCriterian.getSortAttribute();
            // For in memory sorting of severity attributes, both present and previous severity will be required in alarm record.
            if (PRESENT_SEVERITY.equals(sortAttributeName)) {
                expectedOutputAttributes.getOutputAttributes().add(PREVIOUS_SEVERITY);
            }
            sortAttributes.add(sortAttributeName);
        }
        return sortAttributes;
    }

    public AlarmPoIdResponse getAlarmPoIds(final CompositeNodeCriteria compositeNodeCriteria, final boolean authorizationRequired) {
        isAuthorizedForOpenAlarms(authorizationRequired);
        isAlarmAttributesLoaded();
        return compositeNodeCriteriaHandlerForPoIds.getAlarmPoIds(compositeNodeCriteria);
    }

    public AlarmPoIdResponse getAlarmPoIds(final OORCriteria oorCriteria) {
        isAlarmAttributesLoaded();
        return oorCriteriaHandler.getAlarmPoIds(oorCriteria);
    }

    public AlarmPoIdResponse getAllAlarmPoIds() {
        isAlarmAttributesLoaded();
        return oorCriteriaHandler.getAlarmPoIds(null);
    }

    public Map<String, Long> getAlarmCountBySeverity(final CompositeNodeCriteria compositeNodeCriteria, final boolean authorizationRequired) {
        isAlarmAttributesLoaded();
        isAuthorizedForOpenAlarms(authorizationRequired);
        return compositeNodeCriteriaHandlerForSeverities.getAlarmCountBySeverity(compositeNodeCriteria);
    }

    public AlarmCountResponse getAlarmCount(final CompositeNodeCriteria compositeNodeCriteria, final boolean authorizationRequired) {
        isAlarmAttributesLoaded();
        isAuthorizedForOpenAlarms(authorizationRequired);
        return compositeNodeCriteriaHandlerForCount.getAlarmCount(compositeNodeCriteria);
    }

    public AlarmCountResponse getAlarmCount(final CompositeEventTimeCriteria compositeEventTimeCriteria, final boolean authorizationRequired) {
        isAlarmAttributesLoaded();
        isAuthorizedForOpenAlarms(authorizationRequired);
        return compositeEventTimeCriteriaHandlerForCount.getAlarmCount(compositeEventTimeCriteria);
    }

    private void isAlarmAttributesLoaded() {
        if (!openAlarmParser.isAttributesLoaded()) {
            openAlarmParser.extractAttributesFromModel();
        }
    }

    private void isAuthorized(final boolean authorizationRequired) {
        if (authorizationRequired) {
            authorizationHandler.checkAuthorization();
        }
    }

    private void isAuthorizedForOpenAlarms(final boolean authorizationRequired) {
        if (authorizationRequired) {
            authorizationHandler.checkAuthorizationForOpenAlarm();
        }
    }

    /**
     * The method sets new alarm sort criteria for old sort attribute to make further processing common.
     *
     * @param sortOrder
     *            old sort order.
     * @param sortAttribute
     *            old sort attribute.
     * @return AlarmSortCriterion
     */
    private AlarmSortCriterion setAlarmSortCriterionForOldSortAttribute(final SortingOrder sortOrder, final String sortAttribute) {
        // Setting Sort criteria for backward compatibility.
        final AlarmSortCriterion alarmSortCriterion = new AlarmSortCriterion();
        alarmSortCriterion.setSortAttribute(sortAttribute);
        alarmSortCriterion.setSortOrder(sortOrder);
        alarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        return alarmSortCriterion;
    }

    public AlarmAttributeResponse getOpenAlarmsWithCount(final CompositeEventTimeCriteria compositeEventTimeCriteria,
                                                         final Integer maxNumberOfAlarmsInCli,
                                                         final ExpectedOutputAttributes expectedOutputAttributes,
                                                         AlarmAttributeResponse alarmAttributeResponse) {
        final AlarmCountResponse alarmCountResponse = getAlarmCount(compositeEventTimeCriteria, true);
        Long alarmCount = alarmCountResponse.getAlarmCount();
        final String response = alarmCountResponse.getResponse();
        if (alarmCount > maxNumberOfAlarmsInCli) {
            alarmAttributeResponse = new AlarmAttributeResponse(null, response);
            alarmAttributeResponse.setAlarmCountForSearchCriteria(alarmCount);
        } else {
            alarmAttributeResponse = getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);
            // This was added as there is a chance for an exception from getAlarmCount in that case we would be getting count as -1
            alarmCount = (long) alarmAttributeResponse.getAlarmRecords().size();
            alarmAttributeResponse.setAlarmCountForSearchCriteria(alarmCount);
        }
        return alarmAttributeResponse;
    }

    public AlarmAttributeResponse getAlarms(final List<CompositeNodeCriteria> compositeNodeCriterias,
                                            final ExpectedOutputAttributes expectedOutputAttributes, final List<AlarmSortCriterion> alarmSortCriteria,
                                            final boolean authorizationRequired) {
        final Map<Long, AlarmRecord> uniqueAlarmRecordsData = new HashMap<>();
        List<AlarmRecord> allAlarmRecords = new ArrayList<>();

        final List<String> sortAttributes = extractSortAttributeNames(expectedOutputAttributes, alarmSortCriteria);
        try {
            final List<String> dynamicSortAttributes = dynamicAlarmAttributeValidator.filterFmxAdditionalAttributes(sortAttributes);

            if (expectedOutputAttributes.getOutputAttributes() != null) {
                if (!expectedOutputAttributes.getOutputAttributes().contains(EVENT_PO_ID)) {
                    expectedOutputAttributes.getOutputAttributes().add(EVENT_PO_ID);
                }
            }
            for (final CompositeNodeCriteria compositeNodeCriteria : compositeNodeCriterias) {
                compositeNodeCriteria.setAlarmSortCriteria(alarmSortCriteria);
                final AlarmAttributeResponse alarmQueryResponse = getAlarms(compositeNodeCriteria, expectedOutputAttributes, authorizationRequired);
                if (getAlarmsFailed(alarmQueryResponse)) {
                    return alarmQueryResponse;
                }
                final List<AlarmRecord> alarmRecords = alarmQueryResponse.getAlarmRecords();
                LOGGER.debug("Response to getAlarms is  {}, size of AlarmRecords received is {}", alarmQueryResponse.getResponse(),
                        alarmRecords.size());

                for (final AlarmRecord alarmRecord : alarmRecords) {
                    uniqueAlarmRecordsData.put(alarmRecord.getEventPoIdAsLong(), alarmRecord);
                }
            }
            allAlarmRecords.addAll(uniqueAlarmRecordsData.values());
            if (alarmSortCriteria != null && !alarmSortCriteria.isEmpty()) {
                allAlarmRecords = alarmAttributeResponseBuilder.mergeAllSortedAlarmRecords(allAlarmRecords, alarmSortCriteria, dynamicSortAttributes);
            }
            return alarmAttributeResponseBuilder.buildAttributeResponse(allAlarmRecords);
        } catch (final Exception exception) {
            LOGGER.error("Error while retrieving and sorting alarms {} with given compositeNodeCriteria {} on passed alarmSortCriteria {}", exception,
                    compositeNodeCriterias, alarmSortCriteria);
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append(LOG_ERROR_MESSAGE).append(exception.getMessage());
            return new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), errorMessageBuilder.toString());
        }
    }

    private boolean getAlarmsFailed(final AlarmAttributeResponse alarmQueryResponse) {
        return !NO_ALARMS.equals(alarmQueryResponse.getResponse()) && !SUCCESS.equals(alarmQueryResponse.getResponse());
    }

    public Map<String, Long> getAlarmCountBySeverity(final List<CompositeNodeCriteria> compositeNodeCriterias,
                                                               final boolean authorizationRequired) {
        final Map<Long, AlarmRecord> uniqueAlarmRecordsData = new LinkedHashMap<>();
        final List<String> outputAttributes = new ArrayList<>();

        isAlarmAttributesLoaded();
        isAuthorizedForOpenAlarms(authorizationRequired);

        outputAttributes.add(PRESENT_SEVERITY);
        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        if (!compositeNodeCriterias.isEmpty()) {
            final AlarmAttributeResponse alarmQueryResponse = getAlarms(compositeNodeCriterias, expectedOutputAttributes,
                    new ArrayList<AlarmSortCriterion>(0), authorizationRequired);
            final List<AlarmRecord> alarmRecords = alarmQueryResponse.getAlarmRecords();
            LOGGER.debug("Response to getAlarms is  {}, size of AlarmRecords received is {}", alarmQueryResponse.getResponse(), alarmRecords.size());
            for (final AlarmRecord alarmRecord : alarmRecords) {
                uniqueAlarmRecordsData.put(alarmRecord.getEventPoIdAsLong(), alarmRecord);
            }
        }
        return compositeNodeCriteriaHandlerForSeverities.getAlarmCountBySeverity(uniqueAlarmRecordsData.values());
    }

}
