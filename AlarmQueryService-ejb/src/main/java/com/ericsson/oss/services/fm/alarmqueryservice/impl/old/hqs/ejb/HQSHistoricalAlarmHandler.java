/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LIMIT_EXCEEDED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SUCCESS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.SortOrder;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.RestrictionBuilder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.alarm.query.service.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.exception.InputFormatConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.models.AlarmLogData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryResponse;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb.AlarmObjectConverter;

public class HQSHistoricalAlarmHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HQSHistoricalAlarmHandler.class);

    @Inject
    private HQSNodeRestrictionCreator hqsNodeRestrictionCreator;

    @Inject
    private HQSDateRestrictionCreator hqsDateRestrictionCreator;

    @Inject
    private HQSAttributeRestrictionCreator hqsAttributeRestrictionCreator;

    @EServiceRef
    private HistoricalQueryService historicalQueryService;

    public static Comparator<AlarmRecord> insertTimeComparator = new Comparator<AlarmRecord>() {
        @Override
        public int compare(final AlarmRecord firstAlarmRecord, final AlarmRecord secondAlarmRecord) {
            return secondAlarmRecord.getInsertTime().compareTo(firstAlarmRecord.getInsertTime());
        }
    };

    public AlarmQueryResponse fetchHistoryAlarms(final AlarmLogData historicalQueryData) {
        LOGGER.debug("Fetching all the historical alarms with alarm log data {} ", historicalQueryData);

        final Query query = historicalQueryService.createQuery();
        final RestrictionBuilder restrictionBuilder = query.getRestrictionBuilder();

        Restriction compositeRestriction = null;

        AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        try {
            final List<Date> eventTimeList = historicalQueryData.getDate();
            if (eventTimeList != null && !eventTimeList.isEmpty()) {
                final Restriction dateRestriction = hqsDateRestrictionCreator.getDateRestriction(restrictionBuilder,
                        historicalQueryData.getDateAttribute(), eventTimeList, historicalQueryData.getDateOperator());
                compositeRestriction = dateRestriction;
            }

            final List<String> alarmAttributes = historicalQueryData.getAlarmAttributes();
            if (alarmAttributes != null && !alarmAttributes.isEmpty()) {
                final Restriction attributeRestriction = hqsAttributeRestrictionCreator.getAttributesRestriction(restrictionBuilder, alarmAttributes,
                        historicalQueryData.getDateFormat());
                compositeRestriction = setAllofRestriction(restrictionBuilder, compositeRestriction, attributeRestriction);
            }

            final List<String> nodes = historicalQueryData.getNodeList();
            final List<Restriction> nodeRestrictions = hqsNodeRestrictionCreator.getNodesRestriction(restrictionBuilder, nodes);

            if (compositeRestriction != null || !nodeRestrictions.isEmpty()) {
                alarmQueryResponse = getHistoryAlarms(historicalQueryData, query, restrictionBuilder, compositeRestriction, nodeRestrictions);
            } else {
                alarmQueryResponse = new AlarmQueryResponse();
                alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>());
                LOGGER.debug("Restriction set from the user input was Null. Input query data {}", eventTimeList, nodes, alarmAttributes);
            }
        }

        catch (final InputFormatConstraintViolationException inputFormatConstraintViolationException) {

            LOGGER.error(inputFormatConstraintViolationException.getMessage());
            alarmQueryResponse.setResponse(inputFormatConstraintViolationException.getMessage());
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>());
        } catch (final AttributeConstraintViolationException constrainViolationException) {

            LOGGER.error(constrainViolationException.getMessage());
            alarmQueryResponse.setResponse(constrainViolationException.getMessage());
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>());
        } catch (final Exception exception) {
            LOGGER.error(exception.getMessage());
            alarmQueryResponse.setResponse(exception.getMessage());
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>());
        }

        return alarmQueryResponse;
    }

    private AlarmQueryResponse getHistoryAlarms(final AlarmLogData historicalQueryData, final Query query,
                                                final RestrictionBuilder restrictionBuilder,
                                                final Restriction compositeRestriction, final List<Restriction> nodeRestrictions) {
        List<AlarmRecord> alarmObjectList = new ArrayList<AlarmRecord>(5000);

        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        if (!isMaxCountExceeded(historicalQueryData, query, restrictionBuilder, compositeRestriction, nodeRestrictions)) {
            /*
             * Attributes and Page filter to be set from GUI. At present Attributes set to all attributes, PageFilter start row 0 and number of rows
             * fetched is 5000.
             */
            query.setAttributes(new String[] { "*" });
            // Setting page filter to 5000 as only 5000 records are displayed in UI.
            query.setPageFilter(0, 5000);
            query.orderBy("insertTime", SortOrder.ASCENDING);
            if (nodeRestrictions != null && !nodeRestrictions.isEmpty()) {
                for (final Restriction nodesRestriction : nodeRestrictions) {
                    alarmObjectList = getAlarmForBatch(query, compositeRestriction, nodesRestriction, alarmObjectList);
                }
            } else {
                alarmObjectList = getAlarmForBatch(query, compositeRestriction, null, alarmObjectList);
            }
            Collections.sort(alarmObjectList, insertTimeComparator);
            alarmQueryResponse.setAlarmRecordList(alarmObjectList);
            alarmQueryResponse.setResponse(SUCCESS);

        } else {
            alarmQueryResponse.setAlarmRecordList(alarmObjectList);
            alarmQueryResponse.setResponse(LIMIT_EXCEEDED);
            LOGGER.debug(LIMIT_EXCEEDED);
        }

        return alarmQueryResponse;
    }

    private List<AlarmRecord> getAlarmForBatch(final Query query, Restriction compositeRestriction, final Restriction nodesRestriction,
                                               final List<AlarmRecord> alarmObjectList) {
        compositeRestriction = setAllofRestriction(query.getRestrictionBuilder(), compositeRestriction, nodesRestriction);
        if (compositeRestriction != null) {
            query.setRestriction(compositeRestriction);
            final List<Map<String, Object>> historyAlarmData = historicalQueryService.execute(query);
            for (final Map<String, Object> historyAlarmMap : historyAlarmData) {
                alarmObjectList.add(AlarmObjectConverter.convertHistoricalDataToAlarmObject(historyAlarmMap));
            }
            historyAlarmData.clear();
        }
        return alarmObjectList;
    }

    private boolean isMaxCountExceeded(final AlarmLogData historicalQueryData, final Query query, final RestrictionBuilder restrictionBuilder,
                                       final Restriction compositeRestriction, final List<Restriction> nodeRestrictions) {

        boolean limitExceeded = false;
        Long numberOfAlarms = 0L;
        if (nodeRestrictions != null && !nodeRestrictions.isEmpty()) {
            for (final Restriction nodesRestriction : nodeRestrictions) {
                numberOfAlarms = numberOfAlarms + getCount(query, compositeRestriction, nodesRestriction);
                if (numberOfAlarms > 5000) {
                    limitExceeded = true;
                    break;
                }
            }
        } else {
            numberOfAlarms = numberOfAlarms + getCount(query, compositeRestriction, null);

            if (numberOfAlarms > 5000) {
                limitExceeded = true;
            }
        }
        LOGGER.debug("The number of records retrieved from HQS: {}", numberOfAlarms);
        return limitExceeded;
    }

    private Long getCount(final Query query, Restriction compositeRestriction, final Restriction nodesRestriction) {
        compositeRestriction = setAllofRestriction(query.getRestrictionBuilder(), compositeRestriction, nodesRestriction);
        if (compositeRestriction != null) {
            query.setRestriction(compositeRestriction);
        }
        return historicalQueryService.executeCount(query);
    }

    private Restriction setAllofRestriction(final RestrictionBuilder restrictionBuilder, Restriction compositeRestriction,
                                            final Restriction restriction) {
        if (restriction != null) {
            if (compositeRestriction != null) {
                compositeRestriction = restrictionBuilder.allOf(compositeRestriction, restriction);
            } else {
                compositeRestriction = restriction;
            }
        }
        return compositeRestriction;
    }

}