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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.historicalalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FAILED_TO_READ_FROM_DB;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.IMPROPER_INPUT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.LOG_ERROR_MESSAGE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.PSUEDO_PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.PSUEDO_PREVIOUS_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SERVER_REFUSED_CONNECTION_ERROR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.STAR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.common.constants.AlarmAttrConstants.PREVIOUS_SEVERITY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.SortOrder;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.CompositeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.instrumentation.AqsInstrumentationBean;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.HQSProxy;

/**
 * Delegate class responsible for retrieving the historical alarms. It uses {@link HistoricalQueryService} for the retrieval
 **/
public class CompositeEventTimeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeEventTimeHandler.class);

    @Inject
    private HQSProxy hqsProxy;

    @Inject
    private CompositeRestrictionBuilder compositeRestrictionBuilder;

    @Inject
    private AlarmReader alarmReader;

    @Inject
    private AqsInstrumentationBean aqsInstrumentationBean;

    /**
     * Returns the historical alarms based on the conditions set in {@link CompositeEventTimeCriteria}.
     * @param compositeEventTimeCriteria
     *            --{@link CompositeEventTimeCriteria}
     * @return -- {@link AlarmAttributeResponse}
     */
    public AlarmAttributeResponse getAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria) {
        AlarmAttributeResponse alarmAttributeResponse = null;
        try {
            final Query query = hqsProxy.getQuery();
            final RestrictionBuilder restrictionBuilder = query.getRestrictionBuilder();
            final Restriction compositeRestriction = compositeRestrictionBuilder.build(restrictionBuilder, compositeEventTimeCriteria);
            final Restriction nodeRestriction = compositeRestrictionBuilder.buildNodeRestrictions(restrictionBuilder, compositeEventTimeCriteria);
            if (compositeRestriction != null || nodeRestriction != null) {
                alarmAttributeResponse = getHistoricalAlarms(query, compositeRestriction, nodeRestriction, compositeEventTimeCriteria);
            } else {
                alarmAttributeResponse = new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), IMPROPER_INPUT);
                LOGGER.info("CompositeEventTimeCriteria : {} set by user is invalid.", compositeEventTimeCriteria);
            }
            LOGGER.debug("Historical alarms : {} found for compositeEventTimeCriteria {} ", compositeEventTimeCriteria,
                    alarmAttributeResponse.getAlarmCountForSearchCriteria());
        } catch (final AttributeConstraintViolationException | ModelConstraintViolationException exception) {
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append(LOG_ERROR_MESSAGE).append("with given compositeEventTimeCriteria {}");
            LOGGER.error(errorMessageBuilder.toString(), exception, compositeEventTimeCriteria);
            alarmAttributeResponse = new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), exception.getMessage());
        } catch (final Exception exception) {
            final StringBuilder errorLogBuilder = new StringBuilder();
            errorLogBuilder.append(LOG_ERROR_MESSAGE).append("with given compositeEventTimeCriteria {} ");
            LOGGER.error(errorLogBuilder.toString(), exception, compositeEventTimeCriteria);
            final String message = exception.getMessage();
            if (message != null && message.contains(SERVER_REFUSED_CONNECTION_ERROR)) {
                alarmAttributeResponse = new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), exception.getMessage());
            }
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append(FAILED_TO_READ_FROM_DB).append(exception.getMessage());
            alarmAttributeResponse = new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), errorMessageBuilder.toString());
            aqsInstrumentationBean.incrementNumberOfFmSolrReadFailure();
        }
        return alarmAttributeResponse;
    }

    /**
     * Method that returns the AlarmAttributeResponse for the given query.<br>
     * Method retrieves the total alarm count for the criteria and get alarms upto configured value of MaxNumberOfHistoryAlarmsShownInAlarmSearch
     * alarms using page filtering.
     * @param query
     *            -- {@link Query}
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param compositeRestriction
     *            -- Composite {@link Restriction} of alarm attributes and event times.
     * @param nodeRestrictions
     *            -- {@code List< link@ {@link Restriction}> builded on nodes
     * @return {@link AlarmAttributeResponse}
     */
    private AlarmAttributeResponse getHistoricalAlarms(final Query query, final Restriction compositeRestriction, final Restriction nodeRestriction,
            final CompositeEventTimeCriteria compositeEventTimeCriteria) {
        int maxNumberOfAlarmsToBeRetrieved = compositeEventTimeCriteria.getMaxNumberOfAlarmsToBeRetrieved();
        if (maxNumberOfAlarmsToBeRetrieved == 0) {
            maxNumberOfAlarmsToBeRetrieved = 10000;
        }
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>(maxNumberOfAlarmsToBeRetrieved);
        final List<String> sortAttributes = compositeEventTimeCriteria.getSortAttributes();
        final SortingOrder sortOrder = compositeEventTimeCriteria.getSortDirection();
        final String singleSortAttribute = compositeEventTimeCriteria.getSortAttribute();
        AlarmAttributeResponse alarmAttributeResponse = null;
        final Long alarmCount = getAlarmCount(query, compositeRestriction, nodeRestriction);
        query.setAttributes(new String[] { STAR });

        // Setting page filter to configurable value.Default value is 10K.Min value is 5K Max is 120K
        query.setPageFilter(0, maxNumberOfAlarmsToBeRetrieved);
        final List<AlarmSortCriterion> alarmSortCriteria = compositeEventTimeCriteria.getAlarmSortCriteria();
        if (alarmSortCriteria != null) {
            setSortCriteriaToTypeQuery(alarmSortCriteria, query);
        }
        // TODO Below code is kept for backward compatibility and to be removed later.
        if (sortAttributes != null && !sortAttributes.isEmpty()) {
            for (final String sortAttribute : sortAttributes) {
                if (sortAttribute != null) {
                    if (sortOrder.equals(SortingOrder.ASCENDING)) {
                        query.orderBy(sortAttribute, SortOrder.ASCENDING);
                    } else {
                        query.orderBy(sortAttribute, SortOrder.DESCENDING);
                    }
                }
            }
        } else {
            if (singleSortAttribute != null && !singleSortAttribute.isEmpty()) {
                if (sortOrder.equals(SortingOrder.ASCENDING)) {
                    query.orderBy(singleSortAttribute, SortOrder.ASCENDING);
                } else {
                    query.orderBy(singleSortAttribute, SortOrder.DESCENDING);
                }
            }
        }
        if (nodeRestriction != null) {
            final Restriction restriction = compositeRestrictionBuilder.buildCompositeRestrictionByAnd(query.getRestrictionBuilder(),
                    compositeRestriction, nodeRestriction);
            alarmRecords.addAll(alarmReader.getHistoricalAlarms(query, restriction, maxNumberOfAlarmsToBeRetrieved));
        } else {
            alarmRecords.addAll(alarmReader.getHistoricalAlarms(query, compositeRestriction, maxNumberOfAlarmsToBeRetrieved));
        }
        LOGGER.debug("Total number of Historyical Alarms returning after filtering the duplicates out are {}", alarmRecords.size());
        alarmAttributeResponse = new AlarmAttributeResponse(alarmRecords, SUCCESS);
        alarmAttributeResponse.setAlarmCountForSearchCriteria(alarmCount);
        return alarmAttributeResponse;
    }

    /**
     * Methods set the sort criteria to solr {@link Query}.
     * @param query
     *            solr {@link Query}
     * @param alarmSortCriteria
     *            list of {@link AlarmSortCriterion}
     */
    private void setSortCriteriaToTypeQuery(final List<AlarmSortCriterion> alarmSortCriteria, final Query query) {
        for (final AlarmSortCriterion alarmSortCriterion : alarmSortCriteria) {
            if (SortingOrder.ASCENDING.equals(alarmSortCriterion.getSortOrder())) {
                if (PRESENT_SEVERITY.equals(alarmSortCriterion.getSortAttribute())) {
                    query.orderBy(PSUEDO_PRESENT_SEVERITY, SortOrder.ASCENDING);
                } else if (PREVIOUS_SEVERITY.equals(alarmSortCriterion.getSortAttribute())) {
                    query.orderBy(PSUEDO_PREVIOUS_SEVERITY, SortOrder.ASCENDING);
                } else {
                    query.orderBy(alarmSortCriterion.getSortAttribute(), SortOrder.ASCENDING);
                }
            } else {
                if (PRESENT_SEVERITY.equals(alarmSortCriterion.getSortAttribute())) {
                    query.orderBy(PSUEDO_PRESENT_SEVERITY, SortOrder.DESCENDING);
                } else if (PREVIOUS_SEVERITY.equals(alarmSortCriterion.getSortAttribute())) {
                    query.orderBy(PSUEDO_PREVIOUS_SEVERITY, SortOrder.DESCENDING);
                } else {
                    query.orderBy(alarmSortCriterion.getSortAttribute(), SortOrder.DESCENDING);
                }
            }
        }
    }

    /**
     * This method gets the count of total number of history alarms present.
     * @param query
     *            -- { @link Query}
     * @param compositeRestriction
     *            -- composite {@link Restriction} of alarm attributes and event times.
     * @param nodeRestrictions
     *            -- {@code List< link@ {@link Restriction}> builded on nodes
     * @return The count of total number of alarms present.
     */

    private Long getAlarmCount(final Query query, final Restriction compositeRestriction, final Restriction nodeRestriction) {
        Long numberOfAlarms = 0L;
        if (nodeRestriction != null) {
            numberOfAlarms = numberOfAlarms + getCount(query, compositeRestriction, nodeRestriction);
        } else {
            numberOfAlarms = numberOfAlarms + getCount(query, compositeRestriction, null);
        }
        LOGGER.debug("The number of records retrieved from HQS: {}", numberOfAlarms);
        return numberOfAlarms;
    }

    private Long getCount(final Query query, Restriction compositeRestriction, final Restriction nodesRestriction) {
        compositeRestriction = compositeRestrictionBuilder.buildCompositeRestrictionByAnd(query.getRestrictionBuilder(), compositeRestriction,
                nodesRestriction);
        if (compositeRestriction != null) {
            query.setRestriction(compositeRestriction);
        }
        return hqsProxy.getHistoricalQueryService().executeCount(query);
    }
}
