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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FAILED_TO_READ_FROM_DB;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.query.ObjectField;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AlarmAttributeResponseBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible for retrieving the alarms on the conditions set in {@link PoIdCriteria}.
 **/

public class AlarmPoIdCriteriaHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmPoIdCriteriaHandler.class);

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private AlarmReader alarmReader;

    @Inject
    private AlarmAttributeResponseBuilder alarmAttributeResponseBuilder;

    /**
     * Returns the {@link AlarmAttributeResponse} for the poIds set in {@link AlarmPoIdCriteria} <br>
     * {@link AlarmAttributeResponse} may contain all the attributes of specific attributes based on the conditions set in
     * {@link ExpectedOutputAttributes}
     *
     * @param alarmPoIdCriteria
     *            -- {@link AlarmPoIdCriteria}
     * @param alarmAttributes
     *            -- {@link ExpectedOutputAttributes}
     * @return -- {@link AlarmAttributeResponse}
     */
    public AlarmAttributeResponse getAlarms(final AlarmPoIdCriteria alarmPoIdCriteria, final ExpectedOutputAttributes alarmAttributes) {
        AlarmAttributeResponse alarmAttributeResponse = null;
        try {
            boolean nodeIdRequired = false;
            boolean commentHistoryRequired = false;
            List<String> outputAttributes = null;

            if (alarmAttributes != null) {
                nodeIdRequired = alarmAttributes.isNodeIdRequired();
                commentHistoryRequired = alarmAttributes.isCommentHistoryRequired();
                outputAttributes = alarmAttributes.getOutputAttributes();
            }
            final List<Long> poIds = alarmPoIdCriteria.getPoIds();
            alarmAttributeResponse = getAlarms(poIds, commentHistoryRequired, nodeIdRequired, outputAttributes);
            LOGGER.trace("{} alarms found with given alarmPoIdCriteria {} and expectedOutputAttributes {} ", alarmAttributeResponse.getAlarmRecords()
                    .size(), alarmPoIdCriteria, alarmAttributes);
        } catch (final Exception exception) {
            LOGGER.error("Error while retrieving alarms from DB {} with given alarmPoIdCriteria {} and the expected attributes are {}", exception,
                    alarmPoIdCriteria, alarmAttributes);
            alarmAttributeResponse = new AlarmAttributeResponse(Collections.<AlarmRecord> emptyList(), FAILED_TO_READ_FROM_DB
                    + exception.getMessage());
        }
        return alarmAttributeResponse;
    }

    /**
     * TODO has to remove the method in the next push after finding alternative solution
     *
     * @param alarmPoIdCriteria
     * @param alarmAttributes
     * @return
     */
    public AlarmAttributeResponse getAlarmsForAdditionalAttributeSearchSort(final AlarmPoIdCriteria alarmPoIdCriteria,
                                                                            final ExpectedOutputAttributes alarmAttributes) {

        LOGGER.debug("Request received for alarms with alarmPoIdCriteria {} and expectedOutputAttributes {} ", alarmPoIdCriteria, alarmAttributes);

        boolean nodeIdRequired = false;
        boolean commentHistoryRequired = false;
        List<String> outputAttributes = null;
        if (alarmAttributes != null) {
            nodeIdRequired = alarmAttributes.isNodeIdRequired();
            commentHistoryRequired = alarmAttributes.isCommentHistoryRequired();
            outputAttributes = alarmAttributes.getOutputAttributes();
        }
        final List<Long> poIds = alarmPoIdCriteria.getPoIds();
        return getAlarmsForAdditionalAttributeSearchSort(poIds, commentHistoryRequired, nodeIdRequired, outputAttributes);
    }

    /**
     * Returns the {@link AlarmAttributeResponse} for the given poIds <br>
     * {@link AlarmAttributeResponse} contain outputAttributes data and commentHistory, nodeId based on the input
     *
     * @param poIds
     *            -- list of poids
     * @param commentHistoryRequired
     *            -- Weather history of comments required or not
     * @param nodeIdRequired
     *            -- Po id of node required or not
     * @param outputAttributes
     *            -- list attributes needed in response
     * @return
     */
    private AlarmAttributeResponse getAlarms(final List<Long> poIds, final boolean commentHistoryRequired, final boolean nodeIdRequired,
                                             final List<String> outputAttributes) {
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM);
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        if (poIds != null && !poIds.isEmpty()) {
            final Restriction restriction = restrictionBuilder.in(ObjectField.PO_ID, poIds.toArray());
            typeQuery.setRestriction(restriction);
            alarmRecords = alarmReader.getAlarmRecordsWithPoIds(poIds, commentHistoryRequired, nodeIdRequired, outputAttributes);
        }
        return alarmAttributeResponseBuilder.buildAttributeResponse(alarmRecords);
    }

    private AlarmAttributeResponse getAlarmsForAdditionalAttributeSearchSort(final List<Long> poIds, final boolean commentHistoryRequired,
                                                                             final boolean nodeIdRequired, final List<String> outputAttributes) {
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM);
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();

        if (poIds != null && !poIds.isEmpty()) {
            final Restriction restriction = restrictionBuilder.in(ObjectField.PO_ID, poIds.toArray());
            typeQuery.setRestriction(restriction);
            alarmRecords = alarmReader.getAlarmRecordsForAdditionalAttributeSearchSortWithPoIds(poIds, commentHistoryRequired, nodeIdRequired,
                    outputAttributes);
        }
        return alarmAttributeResponseBuilder.buildAttributeResponse(alarmRecords);
    }
}
