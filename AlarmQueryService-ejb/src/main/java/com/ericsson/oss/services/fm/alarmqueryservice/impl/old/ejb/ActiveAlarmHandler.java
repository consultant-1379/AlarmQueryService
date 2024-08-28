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
 * This class performs DPS related Operations It will fetch the all the open alarms under given FDN based on the inputs. 1. FDN 2. FDN and Acknowledge
 * 3. FDN and UnAcknowledge 4. FDN, Acknowledge and UnAcknowledge
 */
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ACK;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMNUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARM_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ASC;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CLEARED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CLEAREDUNACK;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CRITICAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.DESC;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENT_POID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.INDETERMINATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.INSERTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LASTUPDATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LIMIT_EXCEEDED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MAJOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MINOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OBJECTOFREFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PRESENTSEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SEARCH_LIMIT_EXCEEDED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SUCCESS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.UNACK;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.WARNING;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.NotDefinedInModelException;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.ObjectField;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.SortDirection;
import com.ericsson.oss.itpf.datalayer.dps.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.alarm.query.service.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.exception.InputFormatConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryResponse;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;
import com.ericsson.oss.services.alarm.query.service.models.NodeMatchType;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.FilterConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

@Stateless
public class ActiveAlarmHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveAlarmHandler.class);

    private final static String DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private DPSAlarmSearchHandler dpsAlarmSearchHandler;

    @Inject
    private HistoryCommentsHandler historyCommentsHandler;

    /**
     * Method fetches the all the alarms under fdn.
     *
     * @Method : fetchAllAlarmsUnderfdn
     * @param fdn
     *            Network Element
     * @return AlarmQueryResponse
     **/
    public AlarmQueryResponse fetchAllAlarmsUnderfdn(final List<String> nodes, final boolean previousCommentsRequired) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        LOGGER.debug(" fetchAllAlarmsUnderfdn with FDN {}", nodes);
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final Restriction restrictfdn = getRestrictionForMultipleNode(typeQuery, nodes, FDN, NodeMatchType.EQUALS);
        final List<AlarmRecord> alarmRecords = getListAlarmsForQuery(liveBucket, typeQuery, restrictfdn, previousCommentsRequired, queryBuilder);
        LOGGER.debug("Total Number of Alarms With FDN List {} is ", nodes.size(), alarmRecords.size());
        alarmQueryResponse.setAlarmRecordList(alarmRecords);
        alarmQueryResponse.setResponse(SUCCESS);
        return alarmQueryResponse;
    }

    /**
     * Method fetches the all the acknowledged alarms under fdn.
     *
     * @Method : fetchAllAcknowledgedAlarms
     * @param fdn
     *            Network Element
     * @return AlarmQueryResponse
     **/
    public AlarmQueryResponse fetchAllAcknowledgedAlarms(final List<String> nodes, final List<String> oors, final boolean previousCommentsRequired) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        LOGGER.debug(" fetchAllAcknowledgedAlarms with FDN {}", nodes);
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        final Restriction ackRestriction = getEqualRestriction(typeQuery, ALARM_STATE, ACK);
        final Restriction restrictfdn = getRestrictionForMultipleNode(typeQuery, nodes, FDN, NodeMatchType.EQUALS);
        final Restriction restrictoor = getRestrictionForMultipleNode(typeQuery, oors, OOR, NodeMatchType.CONTAINS);
        Restriction finalRestriction = null;
        if (restrictfdn != null) {
            finalRestriction = restrictionBuilder.allOf(restrictfdn, ackRestriction);
        }
        if (restrictoor != null) {
            finalRestriction = restrictionBuilder.allOf(restrictoor, ackRestriction);
        } else {
            finalRestriction = ackRestriction;
        }
        final List<AlarmRecord> alarmRecords = getListAlarmsForQuery(liveBucket, typeQuery, finalRestriction, previousCommentsRequired, queryBuilder);
        LOGGER.debug("Total Number of records Acknowledged Pos for FDN List {}  is {} ", nodes.size(), alarmRecords.size());
        alarmQueryResponse.setAlarmRecordList(alarmRecords);
        alarmQueryResponse.setResponse(SUCCESS);
        return alarmQueryResponse;
    }

    /**
     * Method fetches the all the un-acknowledged alarms under fdn.
     *
     * @Method : fetchAllUnAcknowledgedAlarms
     * @param fdn
     *            Network Element
     * @return AlarmQueryResponse
     **/

    public AlarmQueryResponse fetchAllUnAcknowledgedAlarms(final List<String> nodes, final List<String> oors, final boolean previousCommentsRequired) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        LOGGER.debug(" fetchAllUnAcknowledgedAlarms with FDN {}", nodes);
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        final Restriction unackRestriction = getEqualRestriction(typeQuery, ALARM_STATE, UNACK);
        final Restriction clearedUnackRestriction = getEqualRestriction(typeQuery, ALARM_STATE, CLEAREDUNACK);
        final Restriction finalUnackrestriction = restrictionBuilder.anyOf(unackRestriction, clearedUnackRestriction);
        final Restriction restrictfdn = getRestrictionForMultipleNode(typeQuery, nodes, FDN, NodeMatchType.EQUALS);
        final Restriction restrictoor = getRestrictionForMultipleNode(typeQuery, oors, OOR, NodeMatchType.CONTAINS);

        Restriction finalRestriction = null;

        if (restrictfdn != null) {
            finalRestriction = restrictionBuilder.allOf(restrictfdn, finalUnackrestriction);
        }
        if (restrictoor != null) {
            finalRestriction = restrictionBuilder.allOf(restrictoor, finalUnackrestriction);
        } else {
            finalRestriction = finalUnackrestriction;
        }

        final List<AlarmRecord> alarmRecords = getListAlarmsForQuery(liveBucket, typeQuery, finalRestriction, previousCommentsRequired, queryBuilder);
        LOGGER.debug("Total Number of records UnAcknowledged Pos for FDN List {} is {} ", nodes.size(), alarmRecords.size());
        alarmQueryResponse.setAlarmRecordList(alarmRecords);
        alarmQueryResponse.setResponse(SUCCESS);
        return alarmQueryResponse;
    }

    /**
     * Method fetches the all the acknowledged and un-acknowledged alarms under fdn.
     *
     * @Method : fetchBothAcknowledgedAndUnAcknowledgedAlarms
     * @param fdn
     *            Network Element
     * @return AlarmQueryResponse
     **/

    public AlarmQueryResponse fetchBothAcknowledgedAndUnAcknowledgedAlarms(final List<String> nodes, final List<String> oors,
                                                                           final boolean previousCommentsRequired) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        LOGGER.debug(" fetchingBothAcknowledgedAndUnAcknowledgedPos with FDN {}", nodes);
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        final Restriction ackRestriction = getEqualRestriction(typeQuery, ALARM_STATE, ACK);

        final Restriction unackRestriction = getEqualRestriction(typeQuery, ALARM_STATE, UNACK);
        final Restriction clearUnackRestriction = getEqualRestriction(typeQuery, ALARM_STATE, CLEAREDUNACK);
        final Restriction alarmStateRestriction = restrictionBuilder.anyOf(ackRestriction, unackRestriction, clearUnackRestriction);

        final Restriction restrictfdn = getRestrictionForMultipleNode(typeQuery, nodes, FDN, NodeMatchType.EQUALS);
        final Restriction restrictoor = getRestrictionForMultipleNode(typeQuery, oors, OOR, NodeMatchType.CONTAINS);

        Restriction finalRestriction = null;

        if (restrictfdn != null) {
            finalRestriction = restrictionBuilder.allOf(restrictfdn, alarmStateRestriction);
        }
        if (restrictoor != null) {
            finalRestriction = restrictionBuilder.allOf(restrictoor, alarmStateRestriction);
        } else {
            finalRestriction = alarmStateRestriction;
        }
        final List<AlarmRecord> alarmRecords = getListAlarmsForQuery(liveBucket, typeQuery, finalRestriction, previousCommentsRequired, queryBuilder);
        LOGGER.debug(" Total Number of Both UnAcknowledged And Acknowledged Pos for FDN List {} is {} ", nodes.size(), alarmRecords.size());
        alarmQueryResponse.setAlarmRecordList(alarmRecords);
        alarmQueryResponse.setResponse(SUCCESS);
        return alarmQueryResponse;
    }

    public AlarmQueryResponse fetchAlarms(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                          final boolean previousCommentsRequired) {
        LOGGER.debug("fetchAlarms for nodes {} with objectOfReferences {} and alarm attributes {} ", nodes, oors, alarmAttributes);
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        List<AlarmRecord> alarmRecords = null;
        try {
            dpsProxy.getService().setWriteAccess(false);
            final DataBucket liveBucket = dpsProxy.getLiveBucket();
            final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
            final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
            Restriction finalRestriction = null;
            Restriction nodeRestriction = null;
            Restriction oorRestriction = null;
            Restriction attributeRestriction = null;
            final List<Restriction> restrictions = new ArrayList<Restriction>();

            if (nodes != null && nodes.size() > 0) {
                nodeRestriction = getRestrictionForMultipleNode(typeQuery, nodes, FDN, NodeMatchType.EQUALS);
            }

            if (oors != null && oors.size() > 0) {
                oorRestriction = getRestrictionForMultipleNode(typeQuery, oors, OOR, NodeMatchType.CONTAINS);
            }

            if (alarmAttributes != null && alarmAttributes.size() > 0) {
                attributeRestriction = dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, DEFAULT_DATE_FORMAT);
            }

            restrictions.add(dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "="));

            restrictions.add(attributeRestriction);
            restrictions.add(nodeRestriction);
            restrictions.add(oorRestriction);
            for (final Restriction restriction : restrictions) {
                if (restriction != null) {
                    if (finalRestriction != null) {
                        finalRestriction = restrictionBuilder.allOf(finalRestriction, restriction);
                    } else {
                        finalRestriction = restriction;
                    }
                }
            }

            if (finalRestriction != null) {
                alarmRecords = getListAlarmsForQuery(liveBucket, typeQuery, finalRestriction, previousCommentsRequired, queryBuilder);
                LOGGER.debug("Total Number of records for given query is {} ", alarmRecords.size());
            }
            alarmQueryResponse.setResponse(SUCCESS);
            alarmQueryResponse.setAlarmRecordList(alarmRecords);
        } catch (InputFormatConstraintViolationException | AttributeConstraintViolationException | ModelConstraintViolationException
                | NotDefinedInModelException e) {
            LOGGER.error("Error detected while querying {} {} ", e, e.getMessage());
            alarmQueryResponse.setResponse(e.getMessage());
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>(0));
        } catch (final Exception exception) {
            LOGGER.error("Could not able to process your request,because of Internal Server Error {} {} ", exception, exception.getMessage());
            alarmQueryResponse.setResponse("Could not able to process your request,because of Internal Server Error");
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>(0));
        }
        return alarmQueryResponse;
    }

    public AlarmQueryResponse fetchRecentlyUpdatedAlarms(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                                         final List<Date> dates, final List<String> outputAttributes) {
        LOGGER.debug(" request hits to Alarm Query Service for fetchRecentlyUpdatedAlarms  with nodes :{}  alarms attributes {}  between the dates ",
                nodes, alarmAttributes, dates);
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        try {
            dpsProxy.getService().setWriteAccess(false);
            final DataBucket liveBucket = dpsProxy.getLiveBucket();
            final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
            typeQuery.addSortingOrder(INSERTTIME, SortDirection.ASCENDING);
            final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
            List<AlarmRecord> alarmRecords = null;
            Restriction finalRestriction = null;
            Restriction nodeRestriction = null;
            Restriction attributeRestriction = null;
            Restriction dateRestriction = null;
            Restriction oorRestriction = null;

            final List<Restriction> restrictions = new ArrayList<Restriction>(4);
            if (nodes != null && nodes.size() > 0) {
                nodeRestriction = getFinalRestrictionForNodeWithIn(typeQuery, nodes);
            }

            if (oors != null && oors.size() > 0) {
                oorRestriction = getRestrictionForMultipleNode(typeQuery, oors, OOR, NodeMatchType.CONTAINS);
            }

            if (dates != null && dates.size() == 2) {
                dateRestriction = dpsAlarmSearchHandler.getDateRestriction(LASTUPDATED, dates, DateOperator.BETWEEN, typeQuery);
            }

            if (alarmAttributes != null && alarmAttributes.size() > 0) {
                attributeRestriction = dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, DEFAULT_DATE_FORMAT);
            }
            restrictions.add(dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "="));
            restrictions.add(nodeRestriction);
            restrictions.add(dateRestriction);
            restrictions.add(attributeRestriction);
            restrictions.add(oorRestriction);

            for (final Restriction restriction : restrictions) {
                if (restriction != null) {
                    if (finalRestriction != null) {
                        finalRestriction = restrictionBuilder.allOf(finalRestriction, restriction);
                    } else {
                        finalRestriction = restriction;
                    }
                }
            }

            if (finalRestriction != null) {
                alarmRecords = getRecentUpdatedAlams(liveBucket, typeQuery, finalRestriction, outputAttributes);
                LOGGER.debug("Total Number of modifed/new alarms given interval is {} ", alarmRecords.size());
            }
            alarmQueryResponse.setResponse(SUCCESS);
            alarmQueryResponse.setAlarmRecordList(alarmRecords);
        } catch (InputFormatConstraintViolationException | AttributeConstraintViolationException | ModelConstraintViolationException
                | NotDefinedInModelException e) {
            LOGGER.error("Error detected while querying {} {} ", e, e.getMessage());
            alarmQueryResponse.setResponse(e.getMessage());
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>(0));
        } catch (final Exception exception) {
            LOGGER.error("Could not able to process your request,because of Internal Server Error {} {} ", exception, exception.getMessage());
            alarmQueryResponse.setResponse("Could not able to process your request,because of Internal Server Error");
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>(0));
        }
        return alarmQueryResponse;
    }

    /**
     * Method fetches the count Alarms which satisfy the resrtictionString and qualifier.
     *
     * @Method : fetchAllAlarms
     * @return Map
     **/
    public Map<String, Long> fetchAlarmsCount(final List<String> nodes) {
        LOGGER.debug("request received for count with nodes {}", nodes);
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final Map<String, Long> severityBasedAlarmsCount = new HashMap<String, Long>();

        Long criticalAlarms = -1L;
        Long majorAlarms = -1L;
        Long minorAlarms = -1L;
        Long indeterminateAlarms = -1L;
        Long clearedAlarms = -1L;
        Long WarningAlarms = -1L;

        Restriction finalRestriction = null;
        Restriction nodeRestriction = null;

        if (nodes != null && nodes.size() > 0) {
            nodeRestriction = getFinalRestrictionForNodeWithIn(typeQuery, nodes);
        }
        finalRestriction = nodeRestriction;

        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();

        criticalAlarms = fetchCount(typeQuery, finalRestriction, CRITICAL, liveBucket);
        severityBasedAlarmsCount.put(FilterConstants.CRITICAL, criticalAlarms);

        majorAlarms = fetchCount(typeQuery, finalRestriction, MAJOR, liveBucket);
        severityBasedAlarmsCount.put(FilterConstants.MAJOR, majorAlarms);

        minorAlarms = fetchCount(typeQuery, finalRestriction, MINOR, liveBucket);
        severityBasedAlarmsCount.put(FilterConstants.MINOR, minorAlarms);

        WarningAlarms = fetchCount(typeQuery, finalRestriction, WARNING, liveBucket);
        severityBasedAlarmsCount.put(FilterConstants.WARNING, WarningAlarms);

        indeterminateAlarms = fetchCount(typeQuery, finalRestriction, INDETERMINATE, liveBucket);
        severityBasedAlarmsCount.put(FilterConstants.INDETERMINATE, indeterminateAlarms);

        clearedAlarms = fetchCount(typeQuery, finalRestriction, CLEARED, liveBucket);
        severityBasedAlarmsCount.put(FilterConstants.CLEARED, clearedAlarms);
        LOGGER.debug(
                " severity counts are  indeterminateAlarms : {} criticalAlarms {} majorAlarms {} minorAlarms {} WarningAlarms {} clearedAlarms {} ",
                indeterminateAlarms, criticalAlarms, majorAlarms, minorAlarms, WarningAlarms, clearedAlarms);

        return severityBasedAlarmsCount;
    }

    /**
     * Method fetches the all the Alarms from DB.
     *
     * @Method : fetchAllAlarmsFromdb
     * @return AlarmQueryResponse
     **/
    // TODO : Decide on initialCapacity of the List.
    public AlarmQueryResponse fetchAllAlarmsFromdb() {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        int i = 0;
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        LOGGER.trace(" fetching All alarms in DPS ");
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();

        final Restriction visibilityRestriction = dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "=");
        typeQuery.setRestriction(visibilityRestriction);

        final Iterator<PersistenceObject> poListIterator = queryExecutor.execute(typeQuery);
        while (poListIterator.hasNext()) {
            final PersistenceObject persistenceObject = poListIterator.next();
            final AlarmRecord alarmRecord = getAlarmRecord(persistenceObject, false);
            alarmRecords.add(alarmRecord);
            i++;
        }
        LOGGER.debug(" Total Number of records found in DPS {} ", i);
        alarmQueryResponse.setAlarmRecordList(alarmRecords);
        alarmQueryResponse.setResponse(SUCCESS);
        return alarmQueryResponse;
    }

    /**
     * Method fetch the PoIds of alarm under the FDN based on Match Type.
     *
     * @Method : fetchPoIds
     * @param node
     *            String
     * @param matchType
     *            NodeMatchType
     * @return List
     **/
    public List<Long> fetchPoIds(final String node, final String objectOfReference, final NodeMatchType matchType) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);

        Restriction fdnRestriction = null;
        Restriction oorRestriction = null;
        if (node != null && !node.isEmpty()) {
            fdnRestriction = getFdnRestrictionBasedOnMatchType(typeQuery, node, matchType, FDN);
        }
        if (objectOfReference != null && !objectOfReference.isEmpty()) {
            oorRestriction = getFdnRestrictionBasedOnMatchType(typeQuery, objectOfReference, matchType, OOR);
        }

        Restriction restriction = fdnRestriction;

        if (restriction != null) {
            restriction = typeQuery.getRestrictionBuilder().allOf(restriction, oorRestriction);
        } else {
            restriction = oorRestriction;
        }

        final Restriction visibilityRestriction = dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "=");

        if (restriction != null) {
            restriction = typeQuery.getRestrictionBuilder().allOf(visibilityRestriction, restriction);
        }

        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        List<Long> poIds = null;

        if (restriction != null) {
            final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
            typeQuery.setRestriction(restriction);
            poIds = queryExecutor.executeProjection(typeQuery, poIdProjection);
            LOGGER.debug("No of Po Ids found with FDN match type is : {} ", poIds.size());
        }
        return new ArrayList<Long>(poIds);
    }

    /**
     * Method fetches the all the Alarms from DB.
     *
     * @Method : fetchAllAlarmsFromdb
     * @return AlarmQueryResponse
     **/

    public List<Long> fetchAllPoIdsFromdb() {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        final Restriction visibilityRestriction = dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "=");
        typeQuery.setRestriction(visibilityRestriction);
        final List<Long> poIds = queryExecutor.executeProjection(typeQuery, poIdProjection);
        LOGGER.debug("Total Po Ids found in Data base  is {}:", poIds.size());
        return new ArrayList<Long>(poIds);
    }

    public AlarmQueryResponse fetchAlarmsWithEventPoIdsWithNodeIds(final List<Long> poIds, final boolean previousCommentsRequired) {
        final AlarmQueryResponse alarmQueryResponse = fetchAlarmsWithEventPoIds(poIds, previousCommentsRequired);
        final List<AlarmRecord> alarmRecords = alarmQueryResponse.getAlarmRecordList();
        for (final AlarmRecord alarmRecord : alarmRecords) {
            final String objectOfReference = alarmRecord.getObjectOfReference();
            final String nodeId = fetchManagedElement(objectOfReference);
            alarmRecord.setNodeId(nodeId);
        }
        alarmQueryResponse.setAlarmRecordList(alarmRecords);
        return alarmQueryResponse;
    }

    /**
     * Method fetches the all the Alarms which are having PoIds.
     *
     * @Method : fetchAlarmsWithEventPoIds
     * @param List
     *            eventPoIdList
     **/
    public AlarmQueryResponse fetchAlarmsWithEventPoIds(final List<Long> poIds, final boolean previousCommentsRequired) {
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();

        if (poIds != null && poIds.size() > 0) {
            final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>(poIds.size());
            final List<PersistenceObject> persistanceObjects = liveBucket.findPosByIds(poIds);
            for (final PersistenceObject persistenceObject : persistanceObjects) {
                if (persistenceObject.getAttribute(VISIBILITY)) {
                    final AlarmRecord alarmRecord = getAlarmRecord(persistenceObject, previousCommentsRequired);
                    alarmRecords.add(alarmRecord);
                }
            }
            alarmQueryResponse.setAlarmRecordList(alarmRecords);

            if (persistanceObjects == null || persistanceObjects.size() == 0) {
                alarmQueryResponse.setResponse("No Records found with the given List of PoIds ");
            } else {
                alarmQueryResponse.setResponse(SUCCESS);
            }
            LOGGER.debug("Total Alarms found with given PO Ids  : {}", alarmRecords.size());
        }
        return alarmQueryResponse;
    }

    public AlarmQueryResponse fetchAlarmsWithEventPoIds(final List<Long> poIds, final boolean previousCommentsRequired,
                                                        final List<String> outputAttributes) {
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        if (poIds != null && poIds.size() > 0) {
            List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>(poIds.size());
            final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(QueryConstants.FM, QueryConstants.OPEN_ALARM);
            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
            final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
            Restriction restriction = restrictionBuilder.in(ObjectField.PO_ID, poIds.toArray());

            final Restriction visibilityRestriction = dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "=");
            if (restriction != null) {
                restriction = typeQuery.getRestrictionBuilder().allOf(restriction, visibilityRestriction);
            }

            typeQuery.setRestriction(restriction);
            typeQuery.addSortingOrder(INSERTTIME, SortDirection.DESCENDING);
            alarmRecords = getAlarmsWithProjections(outputAttributes, alarmRecords, typeQuery, queryExecutor);
            alarmQueryResponse.setAlarmRecordList(alarmRecords);
            if (alarmRecords.size() == 0) {
                alarmQueryResponse.setResponse("No Records forund with the given List of PoIds ");
            } else {
                alarmQueryResponse.setResponse("Success");
            }
            LOGGER.debug("Total Alarms found with given PO Ids {} :", alarmRecords.size());
        }
        return alarmQueryResponse;
    }

    public void logAttributes(final Map<String, Object> alarmAttributeMap) {
        final Set<String> keyList = alarmAttributeMap.keySet();
        for (final String s : keyList) {
            LOGGER.trace("Attribute Key is :  {}  Value is : {} ", s, alarmAttributeMap.get(s));
        }
    }

    public List<Long> fetchPoIdsBasedOnFilters(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        LOGGER.trace(" fetching All alarms in DPS ");
        final Query<TypeRestrictionBuilder> typeQuery = getrestrictionBasedOnAttributes(nodes, oors, alarmAttributes);
        typeQuery.addSortingOrder(INSERTTIME, SortDirection.DESCENDING);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        final List<Long> poIds = queryExecutor.executeProjection(typeQuery, poIdProjection);
        LOGGER.debug("Total Po Ids found in Data base  is : {} ", poIds.size());
        return new ArrayList<Long>(poIds);
    }

    public List<Long> fetchPoIdsBasedOnFilters(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                               final String sortAttribute, final String sortMode, final Long lastUpdatedTime) {
        return fetchPoIdsForLargeData(nodes, oors, alarmAttributes, lastUpdatedTime);
    }

    public List<Object[]> fetchPoIdsBasedOnFilters(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                                   final String sortAttribute, final String sortMode) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        LOGGER.trace(" fetching All alarms in DPS with given sort criteria");
        final Query<TypeRestrictionBuilder> typeQuery = getrestrictionBasedOnAttributes(nodes, oors, alarmAttributes);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        final Projection sortAttributeProjection = ProjectionBuilder.attribute(sortAttribute);
        if (sortMode.equalsIgnoreCase(ASC)) {
            typeQuery.addSortingOrder(sortAttribute, SortDirection.ASCENDING);
        } else if (sortMode.equalsIgnoreCase(DESC)) {
            typeQuery.addSortingOrder(sortAttribute, SortDirection.DESCENDING);
        }
        final List<Object[]> data = queryExecutor.executeProjection(typeQuery, poIdProjection, sortAttributeProjection);
        LOGGER.debug("Total Po Ids found in Data base with given sort criteria  is : {} ", data.size());
        return new ArrayList<Object[]>(data);
    }

    public List<Object[]> fetchAlarmNumbersAndObjectOfRefrences(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        LOGGER.trace(" fetching alarmNumbers And oors in DPS ");
        final Query<TypeRestrictionBuilder> typeQuery = getrestrictionBasedOnAttributes(nodes, oors, alarmAttributes);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Projection alarmNumberProjection = ProjectionBuilder.attribute(ALARMNUMBER);
        final Projection oorProjection = ProjectionBuilder.attribute(OBJECTOFREFERENCE);
        final List<Object[]> alarmNumbersAndoors = queryExecutor.executeProjection(typeQuery, alarmNumberProjection, oorProjection);
        LOGGER.debug("Total alarmNumbersAndoors found in Data base  is : {} ", alarmNumbersAndoors.size());
        return new ArrayList<Object[]>(alarmNumbersAndoors);
    }

    public List<Long> fetchPoIdsForLargeData(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                             final Long lastUpdatedTime) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        Restriction timeStampRestriction = null;
        if (lastUpdatedTime != null) {
            timeStampRestriction = dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, LASTUPDATED, new Date(lastUpdatedTime),
                    DateOperator.GE);
            LOGGER.debug("Obtained time stamp restriction {}", timeStampRestriction);
        }
        final Restriction attributeRestriction = getrestrictionBasedOnAttributesWithoutNodes(typeQuery, oors, alarmAttributes);
        final List<Long> poIds = new ArrayList<Long>();
        if (nodes != null && nodes.size() <= 1500) {
            final List<Long> batchPoIds = getPoIdsForBatchOfNodes(liveBucket, typeQuery, attributeRestriction, timeStampRestriction, nodes);
            poIds.addAll(batchPoIds);
        } else {
            while (nodes != null && nodes.size() > 1500) {
                final List<String> subList = new ArrayList<String>(nodes.subList(0, 1500));
                final List<Long> batchPoIds = getPoIdsForBatchOfNodes(liveBucket, typeQuery, attributeRestriction, timeStampRestriction, subList);
                nodes.removeAll(subList);
                if (batchPoIds != null && batchPoIds.size() > 0) {
                    poIds.addAll(batchPoIds);
                }
            }
            final List<Long> batchPoIds = getPoIdsForBatchOfNodes(liveBucket, typeQuery, attributeRestriction, timeStampRestriction, nodes);

            if (batchPoIds != null && batchPoIds.size() > 0) {
                poIds.addAll(batchPoIds);
            }
        }
        Collections.sort(poIds);
        Collections.reverse(poIds);
        LOGGER.debug("Total Po Ids found in Data base  is : {} ", poIds.size());
        return poIds;
    }

    public Map<String, Long> fetchAlarmCountForBulkNodes(final List<String> nodes, final Long lastUpdatedTime) {
        Long criticalAlarms = 0L;
        Long majorAlarms = 0L;
        Long minorAlarms = 0L;
        Long indeterminateAlarms = 0L;
        Long clearedAlarms = 0L;
        Long warningAlarms = 0L;
        final List<String> severites = fetchSeveritiesForLargeData(nodes, lastUpdatedTime);
        final Map<String, Long> severityCounts = new LinkedHashMap<String, Long>(6);
        for (final String severity : severites) {
            switch (severity) {
                case CRITICAL:
                    criticalAlarms++;
                    break;
                case MAJOR:
                    majorAlarms++;
                    break;
                case MINOR:
                    minorAlarms++;
                    break;
                case WARNING:
                    warningAlarms++;
                    break;
                case INDETERMINATE:
                    indeterminateAlarms++;
                    break;
                case CLEARED:
                    clearedAlarms++;
                    break;
                default:
                    break;
            }
        }

        severityCounts.put(FilterConstants.CRITICAL, criticalAlarms);
        severityCounts.put(FilterConstants.MAJOR, majorAlarms);
        severityCounts.put(FilterConstants.MINOR, minorAlarms);
        severityCounts.put(FilterConstants.WARNING, warningAlarms);
        severityCounts.put(FilterConstants.INDETERMINATE, indeterminateAlarms);
        severityCounts.put(FilterConstants.CLEARED, clearedAlarms);

        LOGGER.debug(
                " severity counts are  indeterminateAlarms : {} criticalAlarms {} majorAlarms {} minorAlarms {} WarningAlarms {} clearedAlarms {} ",
                indeterminateAlarms, criticalAlarms, majorAlarms, minorAlarms, warningAlarms, clearedAlarms);

        return severityCounts;
    }

    public List<String> fetchSeveritiesForLargeData(final List<String> nodes, final Long lastUpdatedTime) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        Restriction timeStampRestriction = null;
        if (lastUpdatedTime != null) {
            timeStampRestriction = dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, LASTUPDATED, new Date(lastUpdatedTime),
                    DateOperator.LE);
            LOGGER.debug("Obtained time stamp restriction {}", timeStampRestriction);
        }
        final List<String> severities = new ArrayList<String>(nodes.size());
        if (nodes.size() <= 1500) {
            final List<String> batchSeverites = getSeverityForBatchOfNodes(liveBucket, typeQuery, nodes, timeStampRestriction);
            severities.addAll(batchSeverites);
        } else {
            while (nodes.size() > 1500) {
                final List<String> subList = new ArrayList<String>(nodes.subList(0, 1500));

                final List<String> batchSeverites = getSeverityForBatchOfNodes(liveBucket, typeQuery, subList, timeStampRestriction);
                nodes.removeAll(subList);
                if (batchSeverites != null && batchSeverites.size() > 0) {
                    severities.addAll(batchSeverites);
                }
            }
            final List<String> batchSeverites = getSeverityForBatchOfNodes(liveBucket, typeQuery, nodes, timeStampRestriction);
            if (batchSeverites != null && batchSeverites.size() > 0) {
                severities.addAll(batchSeverites);
            }
        }
        return severities;
    }

    public List<Map<String, Object>> getAlarmsForCLI(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes,
                                                     final List<String> outputAttribute) {
        final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        Restriction finalRestriction = null;
        Restriction nodeRestriction = null;
        Restriction oorRestriction = null;
        Restriction attributeRestriction = null;
        final List<Restriction> restrictions = new ArrayList<Restriction>(3);
        LOGGER.debug("The request to get the alarm list for CLI is received with outputAttributes {} for FDNs {} , OORs {} and other Attributes {}",
                outputAttribute, nodes, oors, alarmAttributes);
        if (alarmAttributes != null && alarmAttributes.size() > 0) {
            attributeRestriction = dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, DEFAULT_DATE_FORMAT);
            restrictions.add(attributeRestriction);
        }

        if (nodes != null && nodes.size() > 0) {
            for (final String neFdn : nodes) {
                final Restriction restriction = typeQuery.getRestrictionBuilder().equalTo(FDN, neFdn);
                if (restriction != null) {
                    if (nodeRestriction != null) {
                        nodeRestriction = restrictionBuilder.anyOf(nodeRestriction, restriction);
                    } else {
                        nodeRestriction = restriction;
                    }
                }
            }
            restrictions.add(nodeRestriction);
        }

        if (oors != null && oors.size() > 0) {
            for (final String oor : oors) {
                final Restriction restriction = typeQuery.getRestrictionBuilder().matchesString(OOR, oor, StringMatchCondition.CONTAINS);
                if (restriction != null) {
                    if (oorRestriction != null) {
                        oorRestriction = restrictionBuilder.anyOf(oorRestriction, restriction);
                    } else {
                        oorRestriction = restriction;
                    }
                }
            }
            restrictions.add(oorRestriction);
        }
        restrictions.add(dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "="));
        for (final Restriction restriction : restrictions) {
            if (restriction != null) {
                if (finalRestriction != null) {
                    finalRestriction = restrictionBuilder.allOf(finalRestriction, restriction);
                } else {
                    finalRestriction = restriction;
                }
            }
        }
        if (finalRestriction != null) {
            typeQuery.setRestriction(finalRestriction);
        }

        boolean isEventPoidRequired = false;
        if (outputAttribute.contains(EVENT_POID)) {
            isEventPoidRequired = true;
            outputAttribute.remove(EVENT_POID);
        }

        final Projection poidProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        final Projection[] projections = prepareProjectionForCLI(outputAttribute);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Long numberOfRecords = queryExecutor.executeCount(typeQuery);
        LOGGER.debug("The number of alarms matching the given criteria are : {}", numberOfRecords);
        if (numberOfRecords <= 5000) {
            final List<Object[]> alarmObjectList = queryExecutor.executeProjection(typeQuery, poidProjection, projections);
            if (alarmObjectList != null && !alarmObjectList.isEmpty()) {
                for (final Object[] attributes : alarmObjectList) {
                    final Map<String, Object> alarmMap = new HashMap<String, Object>();
                    final long eventPoId = Long.parseLong(attributes[0].toString());
                    if (isEventPoidRequired) {
                        alarmMap.put(EVENT_POID, eventPoId);
                    }
                    for (int i = 1; i <= outputAttribute.size(); i++) {
                        alarmMap.put(outputAttribute.get(i - 1), attributes[i]);
                    }
                    result.add(alarmMap);
                }
            }
        } else {
            final Map<String, Object> alarmMap = new HashMap<String, Object>(1);
            alarmMap.put(SEARCH_LIMIT_EXCEEDED, LIMIT_EXCEEDED);
            result.add(alarmMap);
        }

        return result;
    }

    private List<AlarmRecord> getAlarmsWithProjections(final List<String> outputAttibutes, final List<AlarmRecord> alarmRecords,
                                                       final Query<TypeRestrictionBuilder> typeQuery, final QueryExecutor queryExecutor) {
        Projection initialProjection = null;
        final List<String> projectedAttibutes = new ArrayList<String>();
        projectedAttibutes.addAll(outputAttibutes);
        initialProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        if (projectedAttibutes.contains(EVENT_POID)) {
            projectedAttibutes.remove(EVENT_POID);
        }

        final Projection[] projections = prepareProjection(projectedAttibutes);
        final List<Object[]> attributeArray = queryExecutor.executeProjection(typeQuery, initialProjection, projections);
        if (attributeArray != null && !attributeArray.isEmpty()) {
            projectedAttibutes.add(0, EVENT_POID);
            for (final Object[] attributes : attributeArray) {
                final AlarmRecord alarmRecord = AlarmObjectConverter.convertToAlarmObject(attributes, projectedAttibutes);
                LOGGER.debug("Alarm Record details :{} ", alarmRecord);
                alarmRecords.add(alarmRecord);
            }
        }
        return alarmRecords;
    }

    private Projection[] prepareProjection(final List<String> outputProjections) {
        Projection[] projections = null;
        if (outputProjections != null && !outputProjections.isEmpty()) {
            projections = new Projection[outputProjections.size()];
            for (int i = 0; i < outputProjections.size(); i++) {
                final Projection projection = ProjectionBuilder.attribute(outputProjections.get(i));
                projections[i] = projection;
            }
        }
        return projections;
    }

    private Projection[] prepareProjectionForCLI(final List<String> outputProjections) {
        Projection[] projections = null;
        if (outputProjections != null && !outputProjections.isEmpty()) {
            projections = new Projection[outputProjections.size()];
            for (int i = 0, j = 0; i < outputProjections.size(); i++, j++) {
                final Projection projection = ProjectionBuilder.attribute(outputProjections.get(i));
                projections[j] = projection;
            }
        }
        return projections;
    }

    /**
     * Method returns the AlarmRecord by taking persistenceObject.
     *
     * @Method : getAlarmRecord
     * @param persistenceObject
     *            Persistence object
     * @return AlarmRecord
     **/
    private AlarmRecord getAlarmRecord(final PersistenceObject persistenceObject, final boolean previousCommentsRequired) {
        final Map<String, Object> alarmAttributeMap = persistenceObject.getAllAttributes();
        final Long eventPoId = persistenceObject.getPoId();
        alarmAttributeMap.put(EVENT_POID, eventPoId);
        List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
        LOGGER.debug("previousCommentsRequired : {},  for the alarm with PoId {}", previousCommentsRequired, eventPoId);
        if (previousCommentsRequired) {
            comments = historyCommentsHandler.getAllComments(eventPoId);
            alarmAttributeMap.put("comments", comments);
        }
        final AlarmRecord alarmRecord = AlarmObjectConverter.convertToAlarmObject(alarmAttributeMap);
        if (LOGGER.isTraceEnabled()) {
            logAttributes(alarmAttributeMap);
        }
        return alarmRecord;
    }

    private Restriction getRestrictionForMultipleNode(final Query<TypeRestrictionBuilder> typeQuery, final List<String> nodes, final String type,
                                                      final NodeMatchType nodeMatchType) {
        Restriction restriction = null;
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        final List<Restriction> restrictionList = new ArrayList<Restriction>();
        for (final String fdn : nodes) {
            restrictionList.add(getFdnRestrictionBasedOnMatchType(typeQuery, fdn, nodeMatchType, type));
        }
        if (!restrictionList.isEmpty()) {
            restriction = restrictionBuilder.anyOf(restrictionList.toArray(new Restriction[restrictionList.size()]));
        }
        return restriction;
    }

    private Restriction getRestrictionForMultipleNodeWithInQuery(final Query<TypeRestrictionBuilder> typeQuery, final List<String> nodes) {
        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);
        final Restriction nodeRestriction = typeQuery.getRestrictionBuilder().in(FDN, nodesArray);
        return nodeRestriction;
    }

    private Restriction getFinalRestrictionForNodeWithIn(final Query<TypeRestrictionBuilder> typeQuery, final List<String> nodes) {
        Restriction nodeRestriction = null;
        if (nodes.size() <= 300) {
            nodeRestriction = getRestrictionForMultipleNodeWithInQuery(typeQuery, nodes);
        } else {
            final List<Restriction> restrictionList = new ArrayList<Restriction>();
            while (nodes.size() > 300) {
                final List<String> subList = new ArrayList<String>(nodes.subList(0, 300));
                restrictionList.add(getRestrictionForMultipleNodeWithInQuery(typeQuery, subList));
                nodes.removeAll(subList);
            }
            restrictionList.add(getRestrictionForMultipleNodeWithInQuery(typeQuery, nodes));
            nodeRestriction = typeQuery.getRestrictionBuilder().anyOf(restrictionList.toArray(new Restriction[restrictionList.size()]));
        }
        return nodeRestriction;
    }

    /**
     * Method forms a cumulated restriction for fdn with match condition.
     *
     * @Method : getFdnRestrictionBasedOnMatchType
     * @param typeQuery
     *            Query type
     * @param fdn
     *            Network Element
     * @param matchType
     *            Node match Type
     * @return Restriction
     **/
    private Restriction getFdnRestrictionBasedOnMatchType(final Query<TypeRestrictionBuilder> typeQuery, final String fdn,
                                                          final NodeMatchType matchType, final String type) {
        Restriction restriction = null;
        if (type != null && type == OOR) {
            if (matchType.equals(NodeMatchType.CONTAINS)) {
                restriction = typeQuery.getRestrictionBuilder().matchesString(OOR, fdn, StringMatchCondition.CONTAINS);
            } else if (matchType.equals(NodeMatchType.EQUALS)) {
                restriction = typeQuery.getRestrictionBuilder().equalTo(OOR, fdn);
            }
        } else if (type != null && type == FDN) {
            if (matchType.equals(NodeMatchType.CONTAINS)) {
                restriction = typeQuery.getRestrictionBuilder().matchesString(FDN, fdn, StringMatchCondition.CONTAINS);
            } else if (matchType.equals(NodeMatchType.EQUALS)) {
                restriction = typeQuery.getRestrictionBuilder().equalTo(FDN, fdn);
            }
        }

        return restriction;
    }

    /**
     * Method forms an equal restriction from DPS.
     *
     * @Method : getEqualRestriction
     * @param typeQuery
     *            Query
     * @param attribute
     *            String
     * @param value
     *            Object
     * @return Restriction
     **/
    private Restriction getEqualRestriction(final Query<TypeRestrictionBuilder> typeQuery, final String attribute, final Object value) {
        final Restriction restriction = typeQuery.getRestrictionBuilder().equalTo(attribute, value);
        return restriction;
    }

    private List<AlarmRecord> getListAlarmsForQuery(final DataBucket liveBucket, final Query<TypeRestrictionBuilder> typeQuery,
                                                    final Restriction restriction, final boolean previousCommentsRequired,
                                                    final QueryBuilder aoQueryBuilder) {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        typeQuery.setRestriction(restriction);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Iterator<PersistenceObject> poListIterator = queryExecutor.execute(typeQuery);

        while (poListIterator.hasNext()) {
            final PersistenceObject persistenceObject = poListIterator.next();
            final AlarmRecord alarmRecord = getAlarmRecord(persistenceObject, previousCommentsRequired);
            alarmRecords.add(alarmRecord);
        }

        return alarmRecords;
    }

    private List<AlarmRecord> getRecentUpdatedAlams(final DataBucket liveBucket, final Query<TypeRestrictionBuilder> typeQuery,
                                                    final Restriction restriction, final List<String> outputAttributes) {
        final List<AlarmRecord> alarmRecordList = new ArrayList<AlarmRecord>();
        typeQuery.setRestriction(restriction);
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        return getAlarmsWithProjections(outputAttributes, alarmRecordList, typeQuery, queryExecutor);
    }

    private Query<TypeRestrictionBuilder> getrestrictionBasedOnAttributes(final List<String> nodes, final List<String> oors,
                                                                          final List<String> alarmAttributes) {
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();

        Restriction finalRestriction = null;
        Restriction nodeRestriction = null;
        Restriction attributeRestriction = null;
        Restriction oorRestriction = null;

        if (nodes != null && nodes.size() > 0) {
            nodeRestriction = getFinalRestrictionForNodeWithIn(typeQuery, nodes);
        }
        if (oors != null && oors.size() > 0) {
            oorRestriction = getRestrictionForMultipleNode(typeQuery, oors, OOR, NodeMatchType.CONTAINS);
        }
        if (alarmAttributes != null && alarmAttributes.size() > 0) {
            attributeRestriction = dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, DEFAULT_DATE_FORMAT);
        }
        if (nodeRestriction != null) {
            finalRestriction = nodeRestriction;
        }

        final List<Restriction> restrictions = new ArrayList<Restriction>(3);
        restrictions.add(dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "="));
        restrictions.add(attributeRestriction);
        restrictions.add(oorRestriction);

        for (final Restriction restriction : restrictions) {
            if (restriction != null) {
                if (finalRestriction != null) {
                    finalRestriction = restrictionBuilder.allOf(finalRestriction, restriction);
                } else {
                    finalRestriction = restriction;
                }
            }
        }
        if (finalRestriction != null) {
            typeQuery.setRestriction(finalRestriction);
        }
        return typeQuery;
    }

    private List<Long> getPoIdsForBatchOfNodes(final DataBucket liveBucket, final Query<TypeRestrictionBuilder> typeQuery,
                                               final Restriction attributeRestriction, final Restriction timeStampRestriction,
                                               final List<String> nodeList) {
        Restriction nodeRestriction = null;
        Restriction finalRestriction = null;
        final List<String> nodes = new ArrayList<String>(nodeList.size());
        nodes.addAll(nodeList);

        if (nodes.size() <= 300) {
            nodeRestriction = getRestrictionForMultipleNodeWithInQuery(typeQuery, nodes);
        } else {
            final List<Restriction> restrictionList = new ArrayList<Restriction>();
            while (nodes.size() > 300) {
                final List<String> subList = new ArrayList<String>(nodes.subList(0, 300));
                restrictionList.add(getRestrictionForMultipleNodeWithInQuery(typeQuery, subList));
                nodes.removeAll(subList);
            }
            restrictionList.add(getRestrictionForMultipleNodeWithInQuery(typeQuery, nodes));
            nodeRestriction = typeQuery.getRestrictionBuilder().anyOf(restrictionList.toArray(new Restriction[restrictionList.size()]));
        }

        if (nodeRestriction != null) {
            finalRestriction = nodeRestriction;
        }
        if (attributeRestriction != null && timeStampRestriction != null) {
            finalRestriction = typeQuery.getRestrictionBuilder().allOf(finalRestriction, attributeRestriction, timeStampRestriction);
        } else if (timeStampRestriction == null && attributeRestriction != null) {
            finalRestriction = typeQuery.getRestrictionBuilder().allOf(finalRestriction, attributeRestriction);
        } else if (timeStampRestriction != null) {
            finalRestriction = typeQuery.getRestrictionBuilder().allOf(finalRestriction, timeStampRestriction);
        }
        if (finalRestriction != null) {
            typeQuery.setRestriction(finalRestriction);
        }
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Projection poIdProjection = ProjectionBuilder.field(ObjectField.PO_ID);
        final List<Long> poIds = queryExecutor.executeProjection(typeQuery, poIdProjection);
        return poIds;
    }

    private Restriction getrestrictionBasedOnAttributesWithoutNodes(final Query<TypeRestrictionBuilder> typeQuery, final List<String> oors,
                                                                    final List<String> alarmAttributes) {
        Restriction finalRestriction = null;
        Restriction attributeRestriction = null;
        Restriction oorRestriction = null;

        if (oors != null && oors.size() > 0) {
            oorRestriction = getRestrictionForMultipleNode(typeQuery, oors, OOR, NodeMatchType.CONTAINS);
        }
        if (alarmAttributes != null && alarmAttributes.size() > 0) {
            attributeRestriction = dpsAlarmSearchHandler.sortAlarmAttributes(alarmAttributes, typeQuery, DEFAULT_DATE_FORMAT);
        }

        final List<Restriction> restrictions = new ArrayList<Restriction>(3);
        restrictions.add(dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "="));
        restrictions.add(attributeRestriction);
        restrictions.add(oorRestriction);

        for (final Restriction restriction : restrictions) {
            if (restriction != null) {
                if (finalRestriction != null) {
                    finalRestriction = typeQuery.getRestrictionBuilder().allOf(finalRestriction, restriction);
                } else {
                    finalRestriction = restriction;
                }
            }
        }
        return finalRestriction;
    }

    private List<String> getSeverityForBatchOfNodes(final DataBucket liveBucket, final Query<TypeRestrictionBuilder> typeQuery,
                                                    final List<String> nodeList, final Restriction timeStampRestriction) {
        Restriction nodeRestriction = null;
        final List<String> nodes = new ArrayList<String>(nodeList.size());
        nodes.addAll(nodeList);
        if (nodes.size() <= 300) {
            nodeRestriction = getRestrictionForMultipleNodeWithInQuery(typeQuery, nodes);
        } else {
            final List<Restriction> restrictionList = new ArrayList<Restriction>();
            while (nodes.size() > 300) {
                final List<String> subList = new ArrayList<String>(nodes.subList(0, 300));
                restrictionList.add(getRestrictionForMultipleNodeWithInQuery(typeQuery, subList));
                nodes.removeAll(subList);
            }
            restrictionList.add(getRestrictionForMultipleNodeWithInQuery(typeQuery, nodes));
            nodeRestriction = typeQuery.getRestrictionBuilder().anyOf(restrictionList.toArray(new Restriction[restrictionList.size()]));
        }

        final Restriction visibilityRestriction = dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "=");

        if (nodeRestriction != null) {
            if (timeStampRestriction != null) {
                nodeRestriction = typeQuery.getRestrictionBuilder().allOf(visibilityRestriction, nodeRestriction, timeStampRestriction);
            } else {
                nodeRestriction = typeQuery.getRestrictionBuilder().allOf(visibilityRestriction, nodeRestriction);
            }
            typeQuery.setRestriction(nodeRestriction);
        }
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Projection severityProjection = ProjectionBuilder.attribute(PRESENTSEVERITY);
        final List<String> severities = queryExecutor.executeProjection(typeQuery, severityProjection);
        return severities;
    }

    private Long fetchCount(final Query<TypeRestrictionBuilder> typeQuery, Restriction finalRestriction, final String severity,
                            final DataBucket liveBucket) {
        final Restriction finalRestrictionforSeverity = typeQuery.getRestrictionBuilder().equalTo(PRESENTSEVERITY, severity);
        final Restriction visibilityRestriction = dpsAlarmSearchHandler.getRestrictionBasedOnOperator(typeQuery, VISIBILITY, true, "=");

        if (finalRestriction != null) {
            finalRestriction = typeQuery.getRestrictionBuilder().allOf(finalRestriction, finalRestrictionforSeverity, visibilityRestriction);
        } else {
            finalRestriction = typeQuery.getRestrictionBuilder().allOf(finalRestrictionforSeverity, visibilityRestriction);
        }
        typeQuery.setRestriction(finalRestriction);
        return liveBucket.getQueryExecutor().executeCount(typeQuery);
    }

    private String fetchManagedElement(final String objectOfReference) {
        String result = "";
        ManagedObject moFound = null;
        int firstpart = 1, n = 0;
        Long nodePoId;
        String nodeId = "";
        if (objectOfReference != null) {
            final String[] fdnArray = objectOfReference.split(",");
            while (fdnArray.length - n > 0 && (moFound == null || moFound.toString().isEmpty())) {
                result = "";
                firstpart = 1;
                for (int i = 0; i < fdnArray.length - n; i++) {
                    if (firstpart == 1) {
                        result += fdnArray[i];
                        firstpart = 0;
                    } else {
                        result += "," + fdnArray[i];
                    }
                }
                moFound = getManagedElement(result);
                n++;
            }
            if (moFound != null) {
                LOGGER.debug(" Managed Element is  not found with objectOfReference {} ", result);
                nodePoId = moFound.getPoId();
                nodeId = nodePoId.toString();
            } else {
                LOGGER.debug(" Managed Element is  not found with objectOfReference {} ", objectOfReference);
                nodeId = "";
            }
        }
        return nodeId;
    }

    private ManagedObject getManagedElement(final String oor) {
        return dpsProxy.getLiveBucket().findMoByFdn(oor);
    }

}