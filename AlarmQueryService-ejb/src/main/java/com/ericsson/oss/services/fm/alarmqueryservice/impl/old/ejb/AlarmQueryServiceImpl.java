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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.old.ejb;

/**
 *
 * This class checks weather information is enough to make any query to DPS Also based on the inputs received, calls respective methods.
 *
 *
 */

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OPEN;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EPredefinedRole;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.alarm.query.service.api.AlarmQueryService;
import com.ericsson.oss.services.alarm.query.service.models.AlarmLogData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryResponse;
import com.ericsson.oss.services.alarm.query.service.models.NodeMatchType;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.hqs.ejb.HQSHistoricalAlarmHandler;

@Stateless
public class AlarmQueryServiceImpl implements AlarmQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmQueryServiceImpl.class);

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private ActiveAlarmHandler activeAlarmHandler;

    @Inject
    private DPSAlarmSearchHandler dpsAlarmSearchHandler;

    @Inject
    private HQSHistoricalAlarmHandler hqsHistoricalAlarmHandler;

    @Override
    @Authorize(resource = "alarms_search", action = "query", role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public List<Map<String, Object>> getAlarmListForCLI(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                                        final List<String> outputAttribute) {
        return activeAlarmHandler.getAlarmsForCLI(nodes, oors, alarmAttributes, outputAttribute);
    }

    @Override
    public AlarmQueryResponse getAlarmList(final AlarmQueryData alarmQueryData) {
        final List<String> nodes = alarmQueryData.getNodes();
        final List<String> oors = alarmQueryData.getObjectOfReferences();
        final List<String> outputAttributes = alarmQueryData.getOutputAttributes();
        final boolean nodeIdRequired = alarmQueryData.isNodeIdRequired();
        AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        LOGGER.debug("Request hits AlarmQueryService with QueryData {} ", alarmQueryData);
        final boolean previousCommentsRequired = alarmQueryData.isPreviousCommentsRequired();

        if (alarmQueryData.isAllAlarms()) {
            alarmQueryResponse = activeAlarmHandler.fetchAllAlarmsFromdb();
        } else {
            if (nodes != null && nodes.size() > 0) {
                LOGGER.debug(" Request recieved in AlarmQueryService with FDNs : {}", nodes);
                if (systemRecorder != null) {
                    systemRecorder.recordEvent("Request recived to Alarm Query Service ", EventLevel.DETAILED,
                            " With FDN as " + alarmQueryData.getNodes(), " ", " ");
                }

                if (!alarmQueryData.isAck() && !alarmQueryData.isUnAck()) {
                    alarmQueryResponse = activeAlarmHandler.fetchAllAlarmsUnderfdn(nodes, previousCommentsRequired);
                } else if (alarmQueryData.isAck() && !alarmQueryData.isUnAck()) {
                    alarmQueryResponse = activeAlarmHandler.fetchAllAcknowledgedAlarms(nodes, oors, previousCommentsRequired);
                } else if (!alarmQueryData.isAck() && alarmQueryData.isUnAck()) {
                    alarmQueryResponse = activeAlarmHandler.fetchAllUnAcknowledgedAlarms(nodes, oors, previousCommentsRequired);
                } else // if(alarmQueryData.isAck() && alarmQueryData.isUnAck())
                {
                    alarmQueryResponse = activeAlarmHandler.fetchBothAcknowledgedAndUnAcknowledgedAlarms(nodes, oors, previousCommentsRequired);
                }
            } else {
                LOGGER.debug(" fetching alarms based on poids ");
                if (nodeIdRequired) {
                    alarmQueryResponse = activeAlarmHandler.fetchAlarmsWithEventPoIdsWithNodeIds(alarmQueryData.getEventPoIds(),
                            previousCommentsRequired);
                } else if (outputAttributes != null && !outputAttributes.isEmpty()) {
                    alarmQueryResponse = activeAlarmHandler.fetchAlarmsWithEventPoIds(alarmQueryData.getEventPoIds(), previousCommentsRequired,
                            outputAttributes);
                } else {
                    alarmQueryResponse = activeAlarmHandler.fetchAlarmsWithEventPoIds(alarmQueryData.getEventPoIds(), previousCommentsRequired);
                }
            }
        }
        return alarmQueryResponse;
    }

    @Override
    public AlarmQueryResponse getAlarmList(final List<String> nodeList, final List<String> oors, final List<String> alarmAttributes,
                                           final boolean previousCommentsRequired) {
        final AlarmQueryResponse alarmQueryResponse = activeAlarmHandler.fetchAlarms(nodeList, oors, alarmAttributes, previousCommentsRequired);
        return alarmQueryResponse;
    }

    @Override
    @Authorize(resource = "alarms_search", action = "query", role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    // allowed for both roles
    public AlarmQueryResponse getAlarmList(final AlarmQueryData alarmQueryData, final boolean authorized) {
        return getAlarmList(alarmQueryData);
    }

    @Override
    @Authorize(resource = "alarms_search", action = "query", role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public Map<String, Long> getAlarmsCount(final List<String> nodeList) {
        LOGGER.trace("Request hits AlarmQueryService for count  with query String  ");
        Map<String, Long> count = new HashMap<String, Long>();
        if (nodeList != null) {
            if (nodeList.isEmpty() || nodeList.size() <= 1000) {
                count = activeAlarmHandler.fetchAlarmsCount(nodeList);
            } else {
                count = activeAlarmHandler.fetchAlarmCountForBulkNodes(nodeList, null);
            }
        }
        return count;
    }

    @Override
    public List<Long> getPoIdsList(final String node, final String objectOfReference, final NodeMatchType matchType) {
        LOGGER.debug("Request hits AlarmQueryService with fdn {}  and match type {}", node, matchType);
        List<Long> poIdList = new ArrayList<Long>();

        if ((node == null || node.isEmpty()) && matchType.equals(NodeMatchType.ALL)) {
            poIdList = activeAlarmHandler.fetchAllPoIdsFromdb();
        } else {
            poIdList = activeAlarmHandler.fetchPoIds(node, objectOfReference, matchType);
        }
        return poIdList;
    }

    @Override
    public AlarmQueryResponse getRecentlyUpdatedAlarms(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                                       final List<Date> dates, final List<String> outputAttributes) {
        final AlarmQueryResponse alarmQueryResponse = activeAlarmHandler.fetchRecentlyUpdatedAlarms(nodes, oors, alarmAttributes, dates,
                outputAttributes);
        return alarmQueryResponse;
    }

    @Override
    @Authorize(resource = "alarms_search", action = "query", role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public AlarmQueryResponse getHistoryAlarmList(final AlarmLogData alarmLogData) {
        LOGGER.debug("Request hits AlarmQueryService with query log data {} ", alarmLogData);
        AlarmQueryResponse alarmQueryResponse = null;
        final String searchType = alarmLogData.getSearchType();
        if (searchType != null && OPEN.equals(searchType)) {
            alarmQueryResponse = dpsAlarmSearchHandler.fetchHistoryAlarms(alarmLogData);
        } else {
            alarmQueryResponse = hqsHistoricalAlarmHandler.fetchHistoryAlarms(alarmLogData);
        }
        return alarmQueryResponse;
    }

    @Override
    public AlarmQueryResponse getHistoryAlarmList(final AlarmLogData alarmLogData, final boolean isAuthorized) {
        return getHistoryAlarmList(alarmLogData);
    }

    @Override
    public List<Long> fetchPoIdsBasedFilters(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes) {
        List<Long> poIdList = new ArrayList<Long>();
        if (nodes == null || nodes.isEmpty() || nodes.size() <= 0) {
            poIdList = activeAlarmHandler.fetchPoIdsBasedOnFilters(nodes, oors, alarmAttributes);
        } else {
            poIdList = activeAlarmHandler.fetchPoIdsForLargeData(nodes, oors, alarmAttributes, null);
        }
        return poIdList;
    }

    @Override
    public List<Object[]> fetchPoIdsBasedFilters(final List<String> fdns, final List<String> oors, final List<String> alarmAttributes,
                                                 final String sortAttribute, final String sortMode) {
        return activeAlarmHandler.fetchPoIdsBasedOnFilters(fdns, oors, alarmAttributes, sortAttribute, sortMode);
    }

    @Override
    public List<Long> fetchPoIdsBasedFilters(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                             final String sortAttribute, final String sortMode, final Long lastUpdatedTime) {
        return new ArrayList<Long>(
                activeAlarmHandler.fetchPoIdsBasedOnFilters(nodes, oors, alarmAttributes, sortAttribute, sortMode, lastUpdatedTime));
    }

    @Override
    public List<Object[]> getAlarmNumbersAndObjectOfRefrences(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes) {
        return activeAlarmHandler.fetchAlarmNumbersAndObjectOfRefrences(nodes, oors, alarmAttributes);
    }

    @Override
    public Map<String, Long> getFilterCount(final List<String> nodeList, final Long lastUpdatedTime) {
        Map<String, Long> count = new HashMap<String, Long>();
        if (nodeList != null) {
            LOGGER.debug("Quering to get the severity counts for {} nodes with timestamp {}", nodeList.size(), lastUpdatedTime);
            count = activeAlarmHandler.fetchAlarmCountForBulkNodes(nodeList, lastUpdatedTime);
        }
        return count;
    }
}
