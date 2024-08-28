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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.BATCH_SIZE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FAILED_TO_READ_FROM_DB;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.LOG_ERROR_MESSAGE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SERVER_REFUSED_CONNECTION_ERROR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.STAR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeAlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.instrumentation.AqsInstrumentationBean;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.HQSProxy;

/**
 * Delegate class responsible for retrieving the historical alarms. It uses HistoricalQueryService for the retrieval
 **/
public class CompositeAlarmAttributeCriteriaHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeAlarmAttributeCriteriaHandler.class);

    @Inject
    private HQSProxy hqsProxy;

    @Inject
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Inject
    private AlarmReader alarmReader;

    @Inject
    private ConfigurationListener configurationListener;

    @Inject
    private AqsInstrumentationBean aqsInstrumentationBean;

    /**
     * Returns the historical alarms based on the conditions set in {@link CompositeAlarmAttributeCriteria} Queries HQS with batch restrictions , each
     * batch having maximum of 400 (BATCH_SIZE)restrictions. <br>
     * <p>
     * Eg. When 800 restrictions are input to this method, 2 calls will be made to HQS for alarms.
     * @param compositeAlarmAttributeCriteria
     *            --{@link CompositeAlarmAttributeCriteria}
     * @return -- {@link AlarmAttributeResponse}
     */
    public AlarmAttributeResponse getAlarms(final List<CompositeAlarmAttributeCriteria> compositeAlarmAttributeCriteria) {
        AlarmAttributeResponse alarmAttributeResponse = null;
        try {
            final Query query = hqsProxy.getQuery();
            final RestrictionBuilder restrictionBuilder = query.getRestrictionBuilder();
            final int configuredMaxNumberOfHistoryAlarmShown = configurationListener.getMaxNumberOfHistoryAlarmsShown();
            query.setPageFilter(0, configuredMaxNumberOfHistoryAlarmShown);
            final List<Restriction> restrictions = buildAlarmAttributeRestriction(compositeAlarmAttributeCriteria, restrictionBuilder);
            final int size = restrictions.size();
            final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();

            for (int i = 0; i < size; i += BATCH_SIZE) {
                final List<Restriction> subList = new ArrayList<Restriction>(restrictions.subList(i, Math.min(size, i + BATCH_SIZE)));
                query.setAttributes(new String[] { STAR });
                final Restriction compositeRestriction = query.getRestrictionBuilder().anyOf(subList.toArray(new Restriction[subList.size()]));
                alarmRecords.addAll(alarmReader.getHistoricalAlarms(query, compositeRestriction, configuredMaxNumberOfHistoryAlarmShown));
            }

            LOGGER.debug("Historical alarms found {} for compositeAlarmAttributeCriteria {} ", alarmRecords.size(), compositeAlarmAttributeCriteria);
            alarmAttributeResponse = new AlarmAttributeResponse(alarmRecords, SUCCESS);
        } catch (final AttributeConstraintViolationException | ModelConstraintViolationException exception) {
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append(LOG_ERROR_MESSAGE).append("with given compositeAlarmAttributeCriteria {}");
            LOGGER.error(errorMessageBuilder.toString(), exception, compositeAlarmAttributeCriteria);
            alarmAttributeResponse = new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), exception.getMessage());
        } catch (final Exception exception) {
            final StringBuilder errorLogBuilder = new StringBuilder();
            errorLogBuilder.append(LOG_ERROR_MESSAGE).append("with given compositeAlarmAttributeCriteria {} ");
            LOGGER.error(errorLogBuilder.toString(), exception, compositeAlarmAttributeCriteria);
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
     * Returns the Restriction, which is combination of all the attribute conditions.
     * @param compositeAlarmAttributeCriteria
     *            -- {@link CompositeAlarmAttributeCriteria}
     * @param restrictionBuilder -- {@link RestrictionBuilder
     * @return -- {@link Restriction } which is composite of all the criteria set.
     */

    private List<Restriction> buildAlarmAttributeRestriction(final List<CompositeAlarmAttributeCriteria> compositeAlarmAttributeCriteria,
            final RestrictionBuilder restrictionBuilder) {
        Restriction compositeRestriction = null;
        final List<Restriction> restrictions = new ArrayList<Restriction>();
        for (final CompositeAlarmAttributeCriteria compositeAlarmAttributeCriterion : compositeAlarmAttributeCriteria) {
            compositeRestriction = null;
            final List<AlarmAttributeCriteria> alarmAttributeCriteria = compositeAlarmAttributeCriterion.getAlarmAttributeCritera();

            if (alarmAttributeCriteria != null && !alarmAttributeCriteria.isEmpty()) {
                final Map<String, List<AlarmAttributeCriteria>> sortedAlarmAttributeCriteria = groupAlarmAttributes(alarmAttributeCriteria);
                for (final List<AlarmAttributeCriteria> attributeCriterion : sortedAlarmAttributeCriteria.values()) {
                    final Restriction restriction = attributeRestrictionBuilder.build(restrictionBuilder, attributeCriterion);
                    compositeRestriction = buildCompositeRestrictionByAnd(restrictionBuilder, compositeRestriction, restriction);
                }
            }
            restrictions.add(compositeRestriction);
        }
        return restrictions;
    }

    /**
     * Returns a compositeRestriction by combining restrictions with AND operation.
     * @param restrictionBuilder
     *            -- {@link RestrictionBuilder}
     * @param compositeRestriction
     *            -- final restriction
     * @param restrictionToBeAdded
     *            -- Restriction need to be added
     * @return -- compositeRestriction after AND operation.
     */
    private Restriction buildCompositeRestrictionByAnd(final RestrictionBuilder restrictionBuilder, final Restriction compositeRestriction,
            final Restriction restrictionToBeAdded) {
        Restriction finalRestriction = compositeRestriction;
        if (restrictionToBeAdded != null) {
            if (compositeRestriction != null) {
                finalRestriction = restrictionBuilder.allOf(compositeRestriction, restrictionToBeAdded);
            } else {
                finalRestriction = restrictionToBeAdded;
            }
        }
        return finalRestriction;
    }

    /**
     * Grouping is required to consolidate different criteria set on a single alarm attribute and do a OR operation. <br>
     * Method returns a Map having key as attribute and value as list of AlarmAttributeCriteria defined on attribute(key)
     * @param alarmAttributeCriteria
     *            -- {@link AlarmAttributeCriteria}
     * @return Map, <br>
     *         Key -- Alarm Attribute <br>
     *         Value -- all the Criteria defined on Alarm Attribute(key).
     */

    private Map<String, List<AlarmAttributeCriteria>> groupAlarmAttributes(final List<AlarmAttributeCriteria> alarmAttributes) {
        final Map<String, List<AlarmAttributeCriteria>> sortedAlarmAttributesMap = new HashMap<String, List<AlarmAttributeCriteria>>();
        for (final AlarmAttributeCriteria attribute : alarmAttributes) {
            final String attributeName = attribute.getAttributeName();
            final List<AlarmAttributeCriteria> attributeList = new ArrayList<AlarmAttributeCriteria>();
            if (sortedAlarmAttributesMap.containsKey(attributeName)) {
                sortedAlarmAttributesMap.get(attributeName).add(attribute);
            } else {
                attributeList.add(attribute);
                sortedAlarmAttributesMap.put(attributeName, attributeList);
            }
        }
        return sortedAlarmAttributesMap;
    }
}
