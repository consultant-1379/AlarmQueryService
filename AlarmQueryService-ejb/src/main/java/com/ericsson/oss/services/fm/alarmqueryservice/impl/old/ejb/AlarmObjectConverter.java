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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.HASH_DELIMITER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ACKOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ACKTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMING_OBJECT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMNUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARMSTATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.BACKUPOBJECTINSTANCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.BACKUPSTATUS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CEASEOPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CEASETIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CI_GROUP_1;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CI_GROUP_2;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CLEARED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENTS;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.COMMENTTEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CORRELATED_VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.CRITICAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENTTYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.EVENT_POID;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FMX_GENERATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.INDETERMINATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.INSERTTIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LASTUPDATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.LAST_ALARM_OPERATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MAJOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MANUAL_CEASE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.MINOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.NOT_APPLICABLE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OBJECTOFREFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OSCILLATIONCOUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PRESENTSEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PREVIOUSSEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PRIMARY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROBABLECAUSE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROBLEMDETAIL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROBLEMTEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROCESSING_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.PROPOSEDREPAIRACTION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.RECORDTYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.REPEATCOUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ROOT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SECONDARY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SPECIFICPROBLEM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.SYNC_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.TRENDINDICATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.UNDEFINED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.WARNING;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord.AlarmRecordType;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord.EventSeverity;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord.EventState;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord.EventTrendIndication;
import com.ericsson.oss.services.alarm.query.service.models.AlarmRecord.LastAlarmOperation;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants;
import com.ericsson.oss.services.fm.common.addinfo.CorrelationType;
import com.ericsson.oss.services.fm.common.addinfo.TargetAdditionalInformationHandler;

/**
 *
 * This class map of Open Alarm attributes to Alarm Record Object.
 *
 *
 **/
public class AlarmObjectConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmObjectConverter.class);
    private static final Map<String, EventSeverity> EVENT_SEVERITY_MAP;
    private static final Map<String, CorrelationType> CORRELATION_TYPE_MAP;
    private static final String ESCAPE_CHARACTERS_FOR_HASH = "¡¿§";

    static {
        final Map<String, EventSeverity> severityMap = new HashMap<String, EventSeverity>(8);
        severityMap.put(UNDEFINED, EventSeverity.UNDEFINED);
        severityMap.put(INDETERMINATE, EventSeverity.INDETERMINATE);
        severityMap.put(CLEARED, EventSeverity.CLEARED);
        severityMap.put(MAJOR, EventSeverity.MAJOR);
        severityMap.put(CRITICAL, EventSeverity.CRITICAL);
        severityMap.put(MINOR, EventSeverity.MINOR);
        severityMap.put(WARNING, EventSeverity.WARNING);
        EVENT_SEVERITY_MAP = Collections.unmodifiableMap(severityMap);
        final Map<String, CorrelationType> correlationTypeMap = new HashMap<>(3);
        correlationTypeMap.put(PRIMARY, CorrelationType.PRIMARY);
        correlationTypeMap.put(SECONDARY, CorrelationType.SECONDARY);
        correlationTypeMap.put(NOT_APPLICABLE, CorrelationType.NOT_APPLICABLE);
        CORRELATION_TYPE_MAP = Collections.unmodifiableMap(correlationTypeMap);
    }

    public static AlarmRecord convertToAlarmObject(final Object[] persistenceObject, final List<String> headers) {
        final AlarmRecord alarmRecord = new AlarmRecord();

        for (int i = 0; i < headers.size(); i++) {
            switch (headers.get(i)) {
                case OBJECTOFREFERENCE:
                    final String oor = (String) persistenceObject[i];
                    alarmRecord.setObjectOfReference(oor);
                    alarmRecord.setManagedObject(getManagedObject(oor));
                    break;
                case EVENT_POID:
                    alarmRecord.setEventPoId((Long) persistenceObject[i]);
                    break;
                case REPEATCOUNT:
                    alarmRecord.setRepeatCount((Integer) persistenceObject[i]);
                    break;
                case OSCILLATIONCOUNT:
                    alarmRecord.setOscillationCount((Integer) persistenceObject[i]);
                    break;
                case FDN:
                    alarmRecord.setFdn((String) persistenceObject[i]);
                    break;
                case EVENTTYPE:
                    alarmRecord.setEventType((String) persistenceObject[i]);
                    break;
                case EVENTTIME:
                    alarmRecord.setEventTime((Date) persistenceObject[i]);
                    break;
                case INSERTTIME:
                    alarmRecord.setInsertTime((Date) persistenceObject[i]);
                    break;
                case LASTUPDATED:
                    alarmRecord.setLastUpdated((Date) persistenceObject[i]);
                    break;
                case PROBABLECAUSE:
                    alarmRecord.setProbableCause((String) persistenceObject[i]);
                    break;
                case SPECIFICPROBLEM:
                    alarmRecord.setSpecificProblem((String) persistenceObject[i]);
                    break;
                case BACKUPSTATUS:
                    alarmRecord.setBackupStatus((Boolean) persistenceObject[i]);
                    break;
                case BACKUPOBJECTINSTANCE:
                    alarmRecord.setBackupObjectInstance((String) persistenceObject[i]);
                    break;
                case PROPOSEDREPAIRACTION:
                    alarmRecord.setProposedRepairAction((String) persistenceObject[i]);
                    break;
                case ALARMNUMBER:
                    alarmRecord.setAlarmNumber((Long) persistenceObject[i]);
                    break;
                case ALARMID:
                    alarmRecord.setAlarmId((Long) persistenceObject[i]);
                    break;
                case CEASETIME:
                    alarmRecord.setCeaseTime((Date) persistenceObject[i]);
                    break;
                case CEASEOPERATOR:
                    alarmRecord.setCeaseOperator((String) persistenceObject[i]);
                    break;
                case ACKTIME:
                    alarmRecord.setAckTime((Date) persistenceObject[i]);
                    break;
                case ACKOPERATOR:
                    alarmRecord.setAckOperator((String) persistenceObject[i]);
                    break;
                case PROBLEMTEXT:
                    alarmRecord.setProblemText((String) persistenceObject[i]);
                    break;
                case PROBLEMDETAIL:
                    alarmRecord.setProblemDetail((String) persistenceObject[i]);
                    break;
                case ADDITIONAL_INFORMATION:
                    final String additionalInformation = (String) persistenceObject[i];
                    alarmRecord.setAdditionalInformationMap(convertAdditionalInfoToMap(additionalInformation));
                    if (additionalInformation != null) {
                        // Replace any escaped character for hash with value "#"
                        alarmRecord.setAdditionalInformation(replaceEscapeSequenceWithHashValue(additionalInformation));
                    }
                    break;
                case COMMENTTEXT:
                    alarmRecord.setCommentText((String) persistenceObject[i]);
                    break;
                case COMMENTS:
                    break;

                case PRESENTSEVERITY:
                    alarmRecord.setPresentSeverity(setSeverity((String) persistenceObject[i]));
                    break;

                case PREVIOUSSEVERITY:
                    alarmRecord.setPreviousSeverity(setSeverity((String) persistenceObject[i]));
                    break;

                case RECORDTYPE:
                    setRecordType((String) persistenceObject[i], alarmRecord);
                    break;
                case ALARMSTATE:
                    setAlarmState((String) persistenceObject[i], alarmRecord);
                    break;
                case TRENDINDICATION:
                    setTrendIndication((String) persistenceObject[i], alarmRecord);
                    break;
                case LAST_ALARM_OPERATION:
                    setLastAlarmOpetartion((String) persistenceObject[i], alarmRecord);
                    break;
                case MANUAL_CEASE:
                    alarmRecord.setManualCease((Boolean) persistenceObject[i]);
                    break;
                case VISIBILITY:
                    alarmRecord.setVisibility((Boolean) persistenceObject[i]);
                    break;
                case CORRELATED_VISIBILITY:
                    alarmRecord.setCorrelatedVisibility((Boolean) persistenceObject[i]);
                    break;
                case FMX_GENERATED:
                    alarmRecord.setFmxGenerated((String) persistenceObject[i]);
                    break;
                case PROCESSING_TYPE:
                    alarmRecord.setProcessingType((String) persistenceObject[i]);
                    break;
                case SYNC_STATE:
                    alarmRecord.setSyncState((Boolean) persistenceObject[i]);
                    break;
                case ALARMING_OBJECT:
                    alarmRecord.setAlarmingObject((String) persistenceObject[i]);
                    break;
                case ROOT:
                    alarmRecord.setRoot(setCorrelationType((String) persistenceObject[i]));
                    break;
                case CI_GROUP_1:
                    alarmRecord.setCiFirstGroup((String) persistenceObject[i]);
                    break;
                case CI_GROUP_2:
                    alarmRecord.setCiSecondGroup((String) persistenceObject[i]);
                    break;
                default:
                    break;
            }
        }
        return alarmRecord;
    }

    private static Map<String, String> convertAdditionalInfoToMap(final String additionalInfo) {
        final Map<String, String> additionalAttributeMap = new HashMap<String, String>();
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            final String[] additionalAttributes = additionalInfo.split(QueryConstants.DELIMETER);
            if (additionalAttributes != null && additionalAttributes.length > 0) {
                for (final String additionalAttribute : additionalAttributes) {
                    if (additionalAttribute != null) {
                        final String[] additionalAttributeKeyValue = additionalAttribute.split(QueryConstants.SEMICOLON, 2);
                        if (additionalAttributeKeyValue != null && additionalAttributeKeyValue.length == 2) {
                            final String additionalAttributeValue = replaceEscapeSequenceWithHashValue(additionalAttributeKeyValue[1]);
                            additionalAttributeMap.put(additionalAttributeKeyValue[0], additionalAttributeValue);
                        }
                    }
                }
            }
        }
        return additionalAttributeMap;
    }

    public static AlarmRecord convertToAlarmObject(final Map<String, Object> openAlarmAttributesMap) {
        final AlarmRecord alarmRecord = new AlarmRecord();
        final Set<String> attributeSet = openAlarmAttributesMap.keySet();
        for (final String attribute : attributeSet) {
            final Object attributeValue = openAlarmAttributesMap.get(attribute);
            if (attributeValue != null) {
                switch (attribute) {
                    case OBJECTOFREFERENCE:
                        final String oor = (String) attributeValue;
                        alarmRecord.setObjectOfReference(oor);
                        alarmRecord.setManagedObject(getManagedObject(oor));
                        break;
                    case EVENT_POID:
                        alarmRecord.setEventPoId((Long) attributeValue);
                        break;
                    case REPEATCOUNT:
                        alarmRecord.setRepeatCount((Integer) attributeValue);
                        break;
                    case OSCILLATIONCOUNT:
                        alarmRecord.setRepeatCount((Integer) attributeValue);
                        break;
                    case FDN:
                        alarmRecord.setFdn((String) attributeValue);
                        break;
                    case EVENTTYPE:
                        alarmRecord.setEventType((String) attributeValue);
                        break;
                    case EVENTTIME:
                        alarmRecord.setEventTime((Date) attributeValue);
                        break;
                    case INSERTTIME:
                        alarmRecord.setInsertTime((Date) attributeValue);
                        break;
                    case LASTUPDATED:
                        alarmRecord.setLastUpdated((Date) attributeValue);
                        break;
                    case PROBABLECAUSE:
                        alarmRecord.setProbableCause((String) attributeValue);
                        break;
                    case SPECIFICPROBLEM:
                        alarmRecord.setSpecificProblem((String) attributeValue);
                        break;
                    case BACKUPSTATUS:
                        alarmRecord.setBackupStatus((Boolean) attributeValue);
                        break;
                    case BACKUPOBJECTINSTANCE:
                        alarmRecord.setBackupObjectInstance((String) attributeValue);
                        break;
                    case PROPOSEDREPAIRACTION:
                        alarmRecord.setProposedRepairAction((String) attributeValue);
                        break;
                    case ALARMNUMBER:
                        alarmRecord.setAlarmNumber((Long) attributeValue);
                        break;
                    case ALARMID:
                        alarmRecord.setAlarmId((Long) attributeValue);
                        break;
                    case CEASETIME:
                        alarmRecord.setCeaseTime((Date) attributeValue);
                        break;
                    case CEASEOPERATOR:
                        alarmRecord.setCeaseOperator((String) attributeValue);
                        break;
                    case ACKTIME:
                        alarmRecord.setAckTime((Date) attributeValue);
                        break;
                    case ACKOPERATOR:
                        alarmRecord.setAckOperator((String) attributeValue);
                        break;
                    case PROBLEMTEXT:
                        alarmRecord.setProblemText((String) attributeValue);
                        break;
                    case PROBLEMDETAIL:
                        alarmRecord.setProblemDetail((String) attributeValue);
                        break;
                    case ADDITIONAL_INFORMATION:
                        final String additionalInformation = (String) attributeValue;
                        final Map<String, String> additionalInfoMap = convertAdditionalInfoToMap(additionalInformation);
                        alarmRecord.setAdditionalInformationMap(additionalInfoMap);
                        if (additionalInformation != null) {
                            final String enrichedAdditionalInformation = enrichTargetAdditionalInformation(openAlarmAttributesMap, additionalInfoMap);
                            alarmRecord.setAdditionalInformation(replaceEscapeSequenceWithHashValue(
                                    enrichedAdditionalInformation != null ? enrichedAdditionalInformation : additionalInformation));
                        }
                        break;
                    case COMMENTTEXT:
                        alarmRecord.setCommentText((String) attributeValue);
                        break;
                    case COMMENTS:
                        alarmRecord.setComments((List<Map<Object, Object>>) attributeValue);
                        break;
                    case PRESENTSEVERITY:
                        alarmRecord.setPresentSeverity(setSeverity((String) attributeValue));
                        break;
                    case PREVIOUSSEVERITY:
                        alarmRecord.setPreviousSeverity(setSeverity((String) attributeValue));
                        break;
                    case RECORDTYPE:
                        setRecordType((String) attributeValue, alarmRecord);
                        break;
                    case ALARMSTATE:
                        setAlarmState((String) attributeValue, alarmRecord);
                        break;
                    case TRENDINDICATION:
                        setTrendIndication((String) attributeValue, alarmRecord);
                        break;
                    case LAST_ALARM_OPERATION:
                        setLastAlarmOpetartion((String) attributeValue, alarmRecord);
                        break;
                    case MANUAL_CEASE:
                        alarmRecord.setManualCease((Boolean) attributeValue);
                        break;
                    case VISIBILITY:
                        alarmRecord.setVisibility((Boolean) attributeValue);
                        break;
                    case CORRELATED_VISIBILITY:
                        alarmRecord.setCorrelatedVisibility((Boolean) attributeValue);
                        break;
                    case FMX_GENERATED:
                        alarmRecord.setFmxGenerated((String) attributeValue);
                        break;
                    case PROCESSING_TYPE:
                        alarmRecord.setProcessingType((String) attributeValue);
                        break;
                    case SYNC_STATE:
                        alarmRecord.setSyncState((Boolean) attributeValue);
                        break;
                    case ALARMING_OBJECT:
                        alarmRecord.setAlarmingObject((String) attributeValue);
                        break;
                    case ROOT:
                        alarmRecord.setRoot(setCorrelationType((String) attributeValue));
                        break;
                    case CI_GROUP_1:
                        alarmRecord.setCiFirstGroup((String) attributeValue);
                        break;
                    case CI_GROUP_2:
                        alarmRecord.setCiSecondGroup((String) attributeValue);
                        break;
                    default:
                        break;
                }
            }
        }
        return alarmRecord;
    }
    
    private static String enrichTargetAdditionalInformation(final Map<String, Object> openAlarmAttributesMap, final Map<String, String> additionalInfoMap){
        String enrichedAdditionalInformation = null;
        if (openAlarmAttributesMap.get(ROOT) != null) {
            try {
                final com.ericsson.oss.services.fm.common.addinfo.CorrelationType root = com.ericsson.oss.services.fm.common.addinfo.CorrelationType
                        .valueOf(String.valueOf(openAlarmAttributesMap.get(ROOT)));

                final TargetAdditionalInformationHandler targetAdditionalInformationHandler = new TargetAdditionalInformationHandler();
                enrichedAdditionalInformation = targetAdditionalInformationHandler.enrichTargetAdditionalInfoInformation(
                        additionalInfoMap,
                        (openAlarmAttributesMap.get(CI_GROUP_1)) == null ? null
                                : openAlarmAttributesMap.get(CI_GROUP_1).toString(),
                        (openAlarmAttributesMap.get(CI_GROUP_2)) == null ? null
                                : openAlarmAttributesMap.get(CI_GROUP_2).toString(),
                        root);
                LOGGER.debug("enrichedAdditionalInformation {}", enrichedAdditionalInformation);
            } catch (final IllegalArgumentException e) {
                LOGGER.error("Invalid value for CorrelationType {}", openAlarmAttributesMap.get(ROOT), e);
            }
        }
        return enrichedAdditionalInformation;
    }

    private static void setLastAlarmOpetartion(final String value, final AlarmRecord alarmRecord) {
        switch (value) {
            case "UNDEFINED":
                alarmRecord.setLastAlarmOperation(LastAlarmOperation.UNDEFINED);
                break;
            case "NEW":
                alarmRecord.setLastAlarmOperation(LastAlarmOperation.NEW);
                break;
            case "CLEAR":
                alarmRecord.setLastAlarmOperation(LastAlarmOperation.CLEAR);
                break;
            case "CHANGE":
                alarmRecord.setLastAlarmOperation(LastAlarmOperation.CHANGE);
                break;
            case "ACKSTATE_CHANGE":
                alarmRecord.setLastAlarmOperation(LastAlarmOperation.ACKSTATE_CHANGE);
                break;
            case "COMMENT":
                alarmRecord.setLastAlarmOperation(LastAlarmOperation.COMMENT);
                break;
            default:
                break;
        }
    }

    private static void setTrendIndication(final String value, final AlarmRecord alarmRecord) {
        switch (value) {
            case "UNDEFINED":
                alarmRecord.setTrendIndication(EventTrendIndication.UNDEFINED);
                break;
            case "LESS_SEVERE":
                alarmRecord.setTrendIndication(EventTrendIndication.LESS_SEVERE);
                break;
            case "MORE_SEVERE":
                alarmRecord.setTrendIndication(EventTrendIndication.MORE_SEVERE);
                break;
            case "NO_CHANGE":
                alarmRecord.setTrendIndication(EventTrendIndication.NO_CHANGE);
                break;
            default:
                break;
        }
    }

    private static void setAlarmState(final String value, final AlarmRecord alarmRecord) {
        switch (value) {
            case "ACTIVE_ACKNOWLEDGED":
                alarmRecord.setAlarmState(EventState.ACTIVE_ACKNOWLEDGED);
                break;
            case "ACTIVE_UNACKNOWLEDGED":
                alarmRecord.setAlarmState(EventState.ACTIVE_UNACKNOWLEDGED);
                break;
            case "CLEARED_ACKNOWLEDGED":
                alarmRecord.setAlarmState(EventState.CLEARED_ACKNOWLEDGED);
                break;
            case "CLEARED_UNACKNOWLEDGED":
                alarmRecord.setAlarmState(EventState.CLEARED_UNACKNOWLEDGED);
                break;
            case "CLOSED":
                alarmRecord.setAlarmState(EventState.CLOSED);
                break;
            default:
                break;
        }
    }

    private static void setRecordType(final String value, final AlarmRecord alarmRecord) {
        try {
            alarmRecord.setRecordType(AlarmRecordType.valueOf(value));
        } catch (final Exception e) {
            alarmRecord.setRecordType(AlarmRecordType.UNDEFINED);
        }
    }

    private static EventSeverity setSeverity(final String value) {
        final EventSeverity eventSeverity = EVENT_SEVERITY_MAP.get(value);
        if (eventSeverity != null) {
            return eventSeverity;
        } else {
            return EventSeverity.UNDEFINED;
        }
    }

    private static CorrelationType setCorrelationType(final String value) {
        final CorrelationType correlationType = CORRELATION_TYPE_MAP.get(value);
        return correlationType != null ? correlationType : CorrelationType.NOT_APPLICABLE;
    }

    public static AlarmRecord convertHistoricalDataToAlarmObject(final Map<String, Object> openAlarmAttributesMap) {
        final AlarmRecord alarmRecord = new AlarmRecord();
        final Set<String> attributeSet = openAlarmAttributesMap.keySet();

        for (final String attribute : attributeSet) {
            final Object attributeValue = openAlarmAttributesMap.get(attribute);
            if (attributeValue != null) {
                switch (attribute) {
                    case OBJECTOFREFERENCE:
                        alarmRecord.setObjectOfReference((String) attributeValue);
                        break;
                    case EVENT_POID:
                        alarmRecord.setEventPoId((Long) attributeValue);
                        break;
                    case REPEATCOUNT:
                        final Long intRepeatCount = (Long) attributeValue;
                        alarmRecord.setRepeatCount(intRepeatCount.intValue());
                        break;
                    case OSCILLATIONCOUNT:
                        final Long intOscillationCount = (Long) attributeValue;
                        alarmRecord.setOscillationCount(intOscillationCount.intValue());
                        break;
                    case FDN:
                        alarmRecord.setFdn((String) attributeValue);
                        break;
                    case EVENTTYPE:
                        alarmRecord.setEventType((String) attributeValue);
                        break;
                    case EVENTTIME:
                        alarmRecord.setEventTime((Date) attributeValue);
                        break;
                    case INSERTTIME:
                        alarmRecord.setInsertTime((Date) attributeValue);
                        break;
                    case LASTUPDATED:
                        alarmRecord.setLastUpdated((Date) attributeValue);
                        break;
                    case PROBABLECAUSE:
                        alarmRecord.setProbableCause(attributeValue.toString());
                        break;
                    case SPECIFICPROBLEM:
                        alarmRecord.setSpecificProblem((String) attributeValue);
                        break;
                    case BACKUPSTATUS:
                        alarmRecord.setBackupStatus((Boolean) attributeValue);
                        break;
                    case BACKUPOBJECTINSTANCE:
                        alarmRecord.setBackupObjectInstance((String) attributeValue);
                        break;
                    case PROPOSEDREPAIRACTION:
                        alarmRecord.setProposedRepairAction((String) attributeValue);
                        break;
                    case ALARMNUMBER:
                        alarmRecord.setAlarmNumber((Long) attributeValue);
                        break;
                    case ALARMID:
                        alarmRecord.setAlarmId((Long) attributeValue);
                        break;
                    case CEASETIME:
                        alarmRecord.setCeaseTime((Date) attributeValue);
                        break;
                    case CEASEOPERATOR:
                        alarmRecord.setCeaseOperator((String) attributeValue);
                        break;
                    case ACKTIME:
                        alarmRecord.setAckTime((Date) attributeValue);
                        break;
                    case ACKOPERATOR:
                        alarmRecord.setAckOperator((String) attributeValue);
                        break;
                    case PROBLEMTEXT:
                        alarmRecord.setProblemText((String) attributeValue);
                        break;
                    case PROBLEMDETAIL:
                        alarmRecord.setProblemDetail((String) attributeValue);
                        break;
                    case ADDITIONAL_INFORMATION:
                        alarmRecord.setAdditionalInformation((String) attributeValue);
                        break;
                    case COMMENTTEXT:
                        alarmRecord.setCommentText((String) attributeValue);
                        break;
                    case COMMENTS:
                        alarmRecord.setComments((List<Map<Object, Object>>) attributeValue);
                        break;
                    case PRESENTSEVERITY:
                        alarmRecord.setPresentSeverity(setSeverity((String) attributeValue));
                        break;
                    case PREVIOUSSEVERITY:
                        alarmRecord.setPreviousSeverity(setSeverity((String) attributeValue));
                        break;
                    case RECORDTYPE:
                        setRecordType((String) attributeValue, alarmRecord);
                        break;
                    case ALARMSTATE:
                        setAlarmState((String) attributeValue, alarmRecord);
                        break;
                    case TRENDINDICATION:
                        setTrendIndication((String) attributeValue, alarmRecord);
                        break;
                    case LAST_ALARM_OPERATION:
                        setLastAlarmOpetartion((String) attributeValue, alarmRecord);
                        break;
                    case ALARMING_OBJECT:
                        alarmRecord.setAlarmingObject((String) attributeValue);
                        break;
                    case ROOT:
                        alarmRecord.setRoot(setCorrelationType((String) attributeValue));
                        break;
                    case CI_GROUP_1:
                        alarmRecord.setCiFirstGroup((String) attributeValue);
                        break;
                    case CI_GROUP_2:
                        alarmRecord.setCiSecondGroup((String) attributeValue);
                        break;
                    default:
                        break;
                }
            }
        }
        return alarmRecord;
    }

    private static String getManagedObject(final String managedObjectReference) {
        String result = "";
        try {
            final int lastIndex = managedObjectReference.lastIndexOf(",") + 1;
            final String lastAttribute = managedObjectReference.substring(lastIndex, managedObjectReference.length());
            result = lastAttribute.split("=")[0];
        } catch (final Exception exception) {
            LOGGER.warn("Exception in getManagedObject for {} is  {}", managedObjectReference, exception);
        }
        return result;
    }

    private static String replaceEscapeSequenceWithHashValue(final String value) {
        String result = value;
        if (result.contains(ESCAPE_CHARACTERS_FOR_HASH)) {
            result = result.replace(ESCAPE_CHARACTERS_FOR_HASH, HASH_DELIMITER);
        }
        return result;
    }
}
