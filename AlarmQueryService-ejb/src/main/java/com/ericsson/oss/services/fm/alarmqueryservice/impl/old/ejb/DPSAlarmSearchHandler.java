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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ACKTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMNUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.BACKUPSTATUS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.BETWEEN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CEASETIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CONTAINS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.DELIMETER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ENDSWITH;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EQUALOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENT_POID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.INSERTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LIMIT_EXCEEDED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.NOTEQUALOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OSCILLATIONCOUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.REPEATCOUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.STARTSWITH;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SUCCESS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SYNC_STATE;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.NotDefinedInModelException;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.RestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.SortDirection;
import com.ericsson.oss.itpf.datalayer.dps.query.StringMatchCondition;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.alarm.query.service.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.exception.InputFormatConstraintViolationException;
import com.ericsson.oss.services.alarm.query.service.models.AlarmLogData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryResponse;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord;
import com.ericsson.oss.services.alarm.query.service.models.DateOperator;
import com.ericsson.oss.services.alarm.query.service.models.SortingOrder;

public class DPSAlarmSearchHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DPSAlarmSearchHandler.class);

    @EServiceRef
    private DataPersistenceService service;

    // TODO:Change Method name.Confusing as the method says history but OpenAlarms retrieved
    // 2. Why there is a need to initialize an arraylist in case of Exception.
    public AlarmQueryResponse fetchHistoryAlarms(final AlarmLogData alarmLogData) {
        LOGGER.debug("Fetching all the history alarms with alarm log data {} ", alarmLogData);

        final DataBucket liveBucket = service.getLiveBucket();
        final AlarmQueryResponse alarmQueryResponse = new AlarmQueryResponse();
        final QueryBuilder queryBuilder = service.getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();

        final List<Date> date = alarmLogData.getDate();
        final List<String> alarmAttributes = alarmLogData.getAlarmAttributes();
        final List<String> nodes = alarmLogData.getNodeList();

        Restriction finalRestriction = null;
        Restriction dateRestriction = null;
        Restriction attributeRestriction = null;
        Restriction nodeListRestriction = null;

        try {
            if (date != null && date.size() > 0) {
                dateRestriction = getDateRestriction(alarmLogData.getDateAttribute(), date, alarmLogData.getDateOperator(), typeQuery);
            }

            if (alarmAttributes != null && alarmAttributes.size() != 0) {
                attributeRestriction = sortAlarmAttributes(alarmAttributes, typeQuery, alarmLogData.getDateFormat());
            }

            if (nodes != null && nodes.size() > 0) {
                nodeListRestriction = getNodeRestriction(typeQuery, nodes);
            }

            if (dateRestriction != null && attributeRestriction != null && nodeListRestriction != null) {
                finalRestriction = restrictionBuilder.allOf(dateRestriction, attributeRestriction, nodeListRestriction);
            } else if (attributeRestriction != null && nodeListRestriction != null) {
                finalRestriction = restrictionBuilder.allOf(attributeRestriction, nodeListRestriction);
            } else if (dateRestriction != null && nodeListRestriction != null) {
                finalRestriction = restrictionBuilder.allOf(dateRestriction, nodeListRestriction);
            } else if (dateRestriction != null && attributeRestriction != null) {
                finalRestriction = restrictionBuilder.allOf(dateRestriction, attributeRestriction);
            } else if (dateRestriction != null) {
                finalRestriction = dateRestriction;
            } else if (attributeRestriction != null) {
                finalRestriction = attributeRestriction;
            } else if (nodeListRestriction != null) {
                finalRestriction = nodeListRestriction;
            } else {
                finalRestriction = null;
            }
            if (finalRestriction != null) {
                typeQuery.setRestriction(finalRestriction);
                if (SortingOrder.ASCENDING == alarmLogData.getSortMode()) {
                    typeQuery.addSortingOrder(alarmLogData.getSortAttribute(), SortDirection.ASCENDING);
                } else if (SortingOrder.DESCENDING == alarmLogData.getSortMode()) {
                    typeQuery.addSortingOrder(alarmLogData.getSortAttribute(), SortDirection.DESCENDING);
                }
                final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
                final Long alarmCount = queryExecutor.executeCount(typeQuery);
                final List<AlarmRecord> alarmObjectList = new ArrayList<AlarmRecord>();
                LOGGER.debug("Total Number of Alarms found in DPS {} ", alarmCount);
                if (alarmCount <= 5000) {
                    final Iterator<PersistenceObject> poListIterator = queryExecutor.execute(typeQuery);
                    while (poListIterator.hasNext()) {
                        final PersistenceObject persistenceObject = poListIterator.next();
                        final AlarmRecord alarmRecord = getHistoryRecord(persistenceObject);
                        alarmObjectList.add(alarmRecord);
                    }
                    alarmQueryResponse.setAlarmRecordList(alarmObjectList);
                    alarmQueryResponse.setResponse(SUCCESS);
                } else {
                    alarmQueryResponse.setAlarmRecordList(alarmObjectList);
                    alarmQueryResponse.setResponse(LIMIT_EXCEEDED);
                }
            } else {
                final List<AlarmRecord> alarmObjectList = new ArrayList<AlarmRecord>();
                alarmQueryResponse.setAlarmRecordList(alarmObjectList);
                LOGGER.info("No records found for given query");
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
        } catch (final ModelConstraintViolationException dpsException) {
            LOGGER.error(dpsException.getMessage());
            alarmQueryResponse.setResponse(dpsException.getMessage());
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>());
        } catch (final NotDefinedInModelException modelException) {
            LOGGER.error(modelException.getMessage());
            alarmQueryResponse.setResponse(modelException.getMessage());
            alarmQueryResponse.setAlarmRecordList(new ArrayList<AlarmRecord>());
        }

        return alarmQueryResponse;
    }

    public Restriction getDateRestriction(final String dateAttribute, final List<Date> date, final DateOperator dateOperator,
            final Query<TypeRestrictionBuilder> typeQuery) {
        LOGGER.debug("Fetching all the history alarms with date attribute {}  date operator {} dates {}", dateAttribute, dateOperator, date);

        Restriction dateRestriction = null;
        // DateFormat formatter = null;
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();

        if (dateOperator != null && dateOperator.equals(DateOperator.BETWEEN)) {
            if (date.size() == 2) {
                dateRestriction = typeQuery.getRestrictionBuilder().between(dateAttribute, date.get(0), date.get(1));
                final Restriction fromDateEqualRestriction = typeQuery.getRestrictionBuilder().equalTo(dateAttribute, date.get(0));
                final Restriction toDateEqualRestriction = typeQuery.getRestrictionBuilder().equalTo(dateAttribute, date.get(1));
                dateRestriction = restrictionBuilder.anyOf(dateRestriction, fromDateEqualRestriction, toDateEqualRestriction);
            } else {
                final String reason = "From or(and) To date  not provided" + date + " it is not valid for the operator of this query";
                final InputFormatConstraintViolationException inputFormatConstraintViolationException = new InputFormatConstraintViolationException(
                        reason);
                LOGGER.error(inputFormatConstraintViolationException.getMessage());
                throw inputFormatConstraintViolationException;
            }
        } else {
            dateRestriction = getRestrictionBasedOnOperator(typeQuery, dateAttribute, date.get(0), dateOperator);
        }
        return dateRestriction;
    }

    public Restriction getAlarmAttributeRestriction(final List<String> alarmAttributes, final Query<TypeRestrictionBuilder> typeQuery,
            final String dateFormat) {
        Restriction attributeRestriction = null;
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        for (final String attribute : alarmAttributes) {
            final String[] alarmAttributesArray = attribute.split(DELIMETER);
            if (alarmAttributesArray.length == 3) {
                final Restriction tempRestriction = getRestriction(alarmAttributesArray[0], alarmAttributesArray[1], alarmAttributesArray[2],
                        typeQuery, dateFormat);
                if (tempRestriction != null) {
                    if (attributeRestriction == null) {
                        attributeRestriction = tempRestriction;
                    } else {
                        attributeRestriction = restrictionBuilder.anyOf(tempRestriction, attributeRestriction);
                    }
                }
            } else {
                final String reason = " Input " + attribute + " is not valid input format for this query";
                final InputFormatConstraintViolationException inputFormatConstraintViolationException = new InputFormatConstraintViolationException(
                        reason);
                throw inputFormatConstraintViolationException;
            }
        }
        return attributeRestriction;
    }

    public Restriction sortAlarmAttributes(final List<String> alarmAttributes, final Query<TypeRestrictionBuilder> typeQuery, final String dateFormat) {
        Restriction attributeRestriction = null;
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        final Map<String, List<String>> sortedAlarmAttributesMap = new HashMap<String, List<String>>();
        for (final String attribute : alarmAttributes) {
            final String[] attributeArray = attribute.split("#");
            if (attributeArray.length == 3) {
                final List<String> attributeList = new ArrayList<String>();

                if (sortedAlarmAttributesMap.containsKey(attributeArray[0])) {
                    sortedAlarmAttributesMap.get(attributeArray[0]).add(attribute);
                } else {
                    attributeList.add(attribute);
                    sortedAlarmAttributesMap.put(attributeArray[0], attributeList);
                }
            } else {
                final String reason = " Input " + attribute + " is not valid input format for this query";
                final InputFormatConstraintViolationException inputFormatConstraintViolationException = new InputFormatConstraintViolationException(
                        reason);
                throw inputFormatConstraintViolationException;
            }
        }

        for (final String attributeKey : sortedAlarmAttributesMap.keySet()) {
            final Restriction restriction = getAlarmAttributeRestriction(sortedAlarmAttributesMap.get(attributeKey), typeQuery, dateFormat);
            if (attributeRestriction == null) {
                attributeRestriction = restriction;
            } else {
                attributeRestriction = restrictionBuilder.allOf(attributeRestriction, restriction);
            }
        }
        return attributeRestriction;
    }

    private Restriction getRestrictionForMultipleNodeWitnInQuery(final Query<TypeRestrictionBuilder> typeQuery, final List<String> nodes) {
        final Object[] nodesArray = nodes.toArray(new String[nodes.size()]);
        final Restriction tempNodeRestriction = typeQuery.getRestrictionBuilder().in(FDN, nodesArray);
        final Restriction nodeRestriction = typeQuery.getRestrictionBuilder().anyOf(tempNodeRestriction);
        return nodeRestriction;
    }

    private Restriction getNodeRestriction(final Query<TypeRestrictionBuilder> typeQuery, final List<String> nodes) {
        Restriction nodeRestriction = null;
        Restriction nodeTempRestriction = null;
        if (nodes.size() <= 300) {
            nodeRestriction = getRestrictionForMultipleNodeWitnInQuery(typeQuery, nodes);
        } else {
            while (nodes.size() > 300) {
                final List<String> subList = new ArrayList<String>(nodes.subList(0, 300));
                nodeTempRestriction = getRestrictionForMultipleNodeWitnInQuery(typeQuery, subList);
                nodes.removeAll(subList);
                if (nodeRestriction != null) {
                    nodeRestriction = typeQuery.getRestrictionBuilder().anyOf(nodeRestriction, nodeTempRestriction);
                } else {
                    nodeRestriction = nodeTempRestriction;
                }
            }
            nodeTempRestriction = getRestrictionForMultipleNodeWitnInQuery(typeQuery, nodes);
            nodeRestriction = typeQuery.getRestrictionBuilder().anyOf(nodeRestriction, nodeTempRestriction);
        }
        return nodeRestriction;
    }

    public Restriction getRestriction(final String attribute, final String value, final String operator,
            final Query<TypeRestrictionBuilder> typeQuery, final String dateFormat) {
        LOGGER.debug("Fetching all the history alarms with  attribute: {} operator {} value {} ", attribute, operator, value);
        Restriction restriction = null;
        if (ALARMNUMBER.equalsIgnoreCase(attribute) || ALARMID.equalsIgnoreCase(attribute)) {
            final Long longValue = Long.parseLong(value);
            restriction = getRestrictionBasedOnOperator(typeQuery, attribute, longValue, operator);
        } else if (REPEATCOUNT.equalsIgnoreCase(attribute) || OSCILLATIONCOUNT.equalsIgnoreCase(attribute)) {
            final Integer attributeValue = Integer.parseInt(value);
            restriction = getRestrictionBasedOnOperator(typeQuery, attribute, attributeValue, operator);
        } else if (BACKUPSTATUS.equalsIgnoreCase(attribute) || SYNC_STATE.equalsIgnoreCase(attribute)) {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                final Boolean booleanValue = Boolean.parseBoolean(value);
                restriction = getEqualRestriction(typeQuery, attribute, booleanValue);
            } else {
                final String reson = "Value " + value + " is not valid for " + attribute + " of this query";
                final AttributeConstraintViolationException modelException = new AttributeConstraintViolationException(reson, attribute, value);
                throw modelException;
            }
        } else if (CEASETIME.equalsIgnoreCase(attribute) || EVENTTIME.equalsIgnoreCase(attribute) || ACKTIME.equalsIgnoreCase(attribute)
                || INSERTTIME.equalsIgnoreCase(attribute)) {
            final List<Date> finalDates = new ArrayList<Date>();
            final DateFormat formatter = new SimpleDateFormat(dateFormat);
            String newOperator;
            List<Date> dateList = null;
            newOperator = operator;
            try {
                if (operator != null && BETWEEN.equalsIgnoreCase(operator)) {
                    dateList = new ArrayList<Date>();
                    final String[] values = value.split(",");
                    if (formatter.parse(values[0]).after(formatter.parse(values[1]))) {
                        dateList.add(formatter.parse(values[1]));
                        dateList.add(formatter.parse(values[0]));
                    } else {
                        dateList.add(formatter.parse(values[0]));
                        dateList.add(formatter.parse(values[1]));
                    }
                } else {
                    dateList = new ArrayList<Date>();
                    dateList.add(formatter.parse(value));
                }

                if (operator != null && EQUALOPERATOR.equalsIgnoreCase(operator)) {
                    finalDates.add(new Date(dateList.get(0).getTime()));
                    finalDates.add(new Date(dateList.get(0).getTime() + 999L));
                    restriction = restrictionSetForDate(typeQuery, attribute, finalDates.get(0), finalDates.get(1));
                } else if (operator != null && NOTEQUALOPERATOR.equalsIgnoreCase(operator)) {
                    finalDates.add(new Date(dateList.get(0).getTime()));
                    finalDates.add(new Date(dateList.get(0).getTime() + 999L));
                    restriction = restrictionSetForDate(typeQuery, attribute, finalDates.get(0), finalDates.get(1));
                    restriction = typeQuery.getRestrictionBuilder().not(restriction);
                } else if (operator != null && BETWEEN.equalsIgnoreCase(operator)) {
                    finalDates.add(new Date(dateList.get(0).getTime()));
                    finalDates.add(new Date(dateList.get(1).getTime() + 999L));
                    restriction = restrictionSetForDate(typeQuery, attribute, finalDates.get(0), finalDates.get(1));
                } else {
                    restriction = getRestrictionBasedOnOperator(typeQuery, attribute, dateList.get(0), newOperator);
                }
            } catch (final ParseException e) {
                LOGGER.error("Parse Exception in date {}", e.getMessage());
                final StringBuilder reason = new StringBuilder(" Date " + value + " is not valid for " + attribute + " of this query");
                final AttributeConstraintViolationException modelException = new AttributeConstraintViolationException(reason.toString(), attribute, value);
                throw modelException;
            }
        } else {
            if (operator.equalsIgnoreCase(EQUALOPERATOR)) {
                final Restriction tempRestriction = typeQuery.getRestrictionBuilder().equalTo(attribute, value);
                restriction = tempRestriction;
            } else if (operator.equalsIgnoreCase(CONTAINS)) {
                restriction = typeQuery.getRestrictionBuilder().matchesString(attribute, value, StringMatchCondition.CONTAINS);
            } else if (operator.equalsIgnoreCase(STARTSWITH)) {
                restriction = typeQuery.getRestrictionBuilder().matchesString(attribute, value, StringMatchCondition.STARTS_WITH);
            } else if (operator.equalsIgnoreCase(ENDSWITH)) {
                restriction = typeQuery.getRestrictionBuilder().matchesString(attribute, value, StringMatchCondition.ENDS_WITH);
            } else {
                final Restriction tempRestriction = typeQuery.getRestrictionBuilder().equalTo(attribute, value);
                restriction = typeQuery.getRestrictionBuilder().not(tempRestriction);
            }
        }
        return restriction;
    }

    private Restriction restrictionSetForDate(final Query<TypeRestrictionBuilder> typeQuery, final String attribute, final Date fromDate,
            final Date toDate) {
        Restriction restriction = null;
        restriction = typeQuery.getRestrictionBuilder().between(attribute, fromDate, toDate);
        final Restriction fromDateEqualRestriction = typeQuery.getRestrictionBuilder().equalTo(attribute, fromDate);
        final Restriction toDateEqualRestriction = typeQuery.getRestrictionBuilder().equalTo(attribute, toDate);
        restriction = typeQuery.getRestrictionBuilder().anyOf(restriction, fromDateEqualRestriction, toDateEqualRestriction);
        return restriction;
    }

    private AlarmRecord getHistoryRecord(final PersistenceObject persistenceObject) {
        Map<String, Object> historyAlarmMap = new HashMap<String, Object>();
        historyAlarmMap = persistenceObject.getAllAttributes();
        final Long eventPoId = persistenceObject.getPoId();
        historyAlarmMap.put(EVENT_POID, eventPoId);
        final AlarmRecord alarmRecord = AlarmObjectConverter.convertToAlarmObject(historyAlarmMap);
        LOGGER.trace("AlarmRecord is {}", alarmRecord);
        return alarmRecord;
    }

    public Restriction getRestrictionBasedOnOperator(final Query<TypeRestrictionBuilder> typeQuery, final String attributeName,
            final Object attributeValue, final Object operator) {
        Restriction restriction = null;
        final RestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        if (operator.equals(DateOperator.LE) || operator.equals("<=") || operator.equals("elder")) {
            final Restriction toDateRestriction = typeQuery.getRestrictionBuilder().lessThan(attributeName, attributeValue);
            final Restriction toDateEqualRestriction = getEqualRestriction(typeQuery, attributeName, attributeValue);
            restriction = restrictionBuilder.anyOf(toDateRestriction, toDateEqualRestriction);
        } else if (operator.equals(DateOperator.GE) || operator.equals(">=") || operator.equals("younger")) {
            final Restriction fromDateRestriction = typeQuery.getRestrictionBuilder().greaterThan(attributeName, attributeValue);
            final Restriction fromDateEqualRestriction = getEqualRestriction(typeQuery, attributeName, attributeValue);
            restriction = restrictionBuilder.anyOf(fromDateRestriction, fromDateEqualRestriction);
        } else if (operator.equals(DateOperator.GT) || operator.equals(">")) {
            restriction = typeQuery.getRestrictionBuilder().greaterThan(attributeName, attributeValue);
        } else if (operator.equals(DateOperator.LT) || operator.equals("<")) {
            restriction = typeQuery.getRestrictionBuilder().lessThan(attributeName, attributeValue);
        } else if (operator.equals(DateOperator.EQ) || operator.equals("=")) {
            restriction = getEqualRestriction(typeQuery, attributeName, attributeValue);
        } else {
            final Restriction tempDateRestriction = getEqualRestriction(typeQuery, attributeName, attributeValue);
            restriction = restrictionBuilder.not(tempDateRestriction);
        }
        return restriction;
    }

    private Restriction getEqualRestriction(final Query<TypeRestrictionBuilder> typeQuery, final String attribute, final Object value) {
        final Restriction restriction = typeQuery.getRestrictionBuilder().equalTo(attribute, value);
        return restriction;
    }
}