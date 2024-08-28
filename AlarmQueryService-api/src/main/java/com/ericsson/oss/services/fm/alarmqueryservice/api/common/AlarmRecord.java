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

package com.ericsson.oss.services.fm.alarmqueryservice.api.common;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ACK_OPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ACK_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARMING_OBJECT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.BACKUP_OBJECT_INSTANCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.BACKUP_STATUS;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CEASE_OPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CEASE_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CI_GROUP_1;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CI_GROUP_2;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.COMMENT_OPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.COMMENT_TEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.COMMENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CORRELATED_RECORD_NAME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CORRELATED_VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EMPTY_STRING;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FMX_GENERATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.HISTORY_ALARM_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.INSERT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.LAST_ALARM_OPERATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.LAST_UPDATED;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.MANUAL_CEASE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OSCILLATION_COUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PREVIOUS_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROBLEM_DETAIL;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROBLEM_TEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROCESSING_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROPOSED_REPAIR_ACTION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.RECORD_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.REPEAT_COUNT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ROOT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SYNC_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.TIME_ZONE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.TREND_INDICATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants._VERSION;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.oss.services.fm.common.addinfo.CorrelationType;

/**
 * An encapsulation of attributes of an Alarm.
 **/
public class AlarmRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ESCAPE_SEQUENCE_FOR_HASH = "¡¿§";
    private static final String HASH_DELIMITER = "#";

    private Map<String, Object> attributeMap = new HashMap<String, Object>();

    private final AdditionalAttributes additionalAttributes = new AdditionalAttributes();

    public String getObjectOfReference() {
        return AlarmAttributeConverter.getStringAttributeValue(FDN, attributeMap.get(OBJECT_OF_REFERENCE));
    }

    public String getNeName() {
        return AlarmAttributeConverter.getNeName(attributeMap.get(FDN));
    }

    public String getAlarmingObject() {
        return AlarmAttributeConverter.getStringAttributeValue(FDN, attributeMap.get(ALARMING_OBJECT));
    }

    public String getFdn() {
        return AlarmAttributeConverter.getStringAttributeValue(FDN, attributeMap.get(FDN));
    }

    public String getProbableCause() {
        return AlarmAttributeConverter.getStringAttributeValue(PROBABLE_CAUSE, attributeMap.get(PROBABLE_CAUSE));
    }

    public String getSpecificProblem() {
        return AlarmAttributeConverter.getStringAttributeValue(SPECIFIC_PROBLEM, attributeMap.get(SPECIFIC_PROBLEM));
    }

    public String getEventType() {
        return AlarmAttributeConverter.getStringAttributeValue(EVENT_TYPE, attributeMap.get(EVENT_TYPE));
    }

    public String getBackupObjectInstance() {
        return AlarmAttributeConverter.getStringAttributeValue(BACKUP_OBJECT_INSTANCE, attributeMap.get(BACKUP_OBJECT_INSTANCE));
    }

    public String getProposedRepairAction() {
        return AlarmAttributeConverter.getStringAttributeValue(PROPOSED_REPAIR_ACTION, attributeMap.get(PROPOSED_REPAIR_ACTION));
    }

    public String getCeaseOperator() {
        return AlarmAttributeConverter.getStringAttributeValue(CEASE_OPERATOR, attributeMap.get(CEASE_OPERATOR));
    }

    public String getAckOperator() {
        return AlarmAttributeConverter.getStringAttributeValue(ACK_OPERATOR, attributeMap.get(ACK_OPERATOR));
    }

    public String getCommentOperator() {
        return AlarmAttributeConverter.getStringAttributeValue(COMMENT_OPERATOR, attributeMap.get(COMMENT_OPERATOR));
    }

    public String getProblemText() {
        return AlarmAttributeConverter.getStringAttributeValue(PROBLEM_TEXT, attributeMap.get(PROBLEM_TEXT));
    }

    public String getProblemDetail() {
        return AlarmAttributeConverter.getStringAttributeValue(PROBLEM_DETAIL, attributeMap.get(PROBLEM_DETAIL));
    }

    public String getAdditionalInformationWithSpecialCharacters(final boolean containsSpecialCharacters) {
        String additionalInformation=EMPTY_STRING;
        if(containsSpecialCharacters){
            additionalInformation=AlarmAttributeConverter.getStringAttributeValue(ADDITIONAL_INFORMATION,
            attributeMap.get(ADDITIONAL_INFORMATION));
        }
        return additionalInformation;
    }

    public String getAdditionalInformation() {
        String additionalInformation = AlarmAttributeConverter.getStringAttributeValue(ADDITIONAL_INFORMATION,
                attributeMap.get(ADDITIONAL_INFORMATION));
        // Replace any escaped character for hash with value "#"
        if (additionalInformation != null) {
            additionalInformation = replaceEscapeSequenceWithHashValue(additionalInformation);
        }
        return additionalInformation;
    }

    public String getCommentText() {
        return AlarmAttributeConverter.getStringAttributeValue(COMMENT_TEXT, attributeMap.get(COMMENT_TEXT));
    }

    public String getProcessingType() {
        return AlarmAttributeConverter.getStringAttributeValue(PROCESSING_TYPE, attributeMap.get(PROCESSING_TYPE));
    }

    public String getFmxGenerated() {
        return AlarmAttributeConverter.getStringAttributeValue(FMX_GENERATED, attributeMap.get(FMX_GENERATED));
    }

    public String getType() {
        return AlarmAttributeConverter.getStringAttributeValue(TYPE, attributeMap.get(TYPE));
    }

    public String getTimeZone() {
        return AlarmAttributeConverter.getStringAttributeValue(TIME_ZONE, attributeMap.get(TIME_ZONE));
    }

    public Long getAlarmId() {
        return AlarmAttributeConverter.getLongAttributeValue(ALARM_ID, attributeMap.get(ALARM_ID));
    }

    public String getEventPoIdAsString() {
        final Long eventPoId = AlarmAttributeConverter.getLongAttributeValue(EVENT_PO_ID, attributeMap.get(EVENT_PO_ID));
        if (eventPoId != null) {
            return eventPoId.toString();
        } else {
            return null;
        }
    }

    public Long getEventPoIdAsLong() {
        return AlarmAttributeConverter.getLongAttributeValue(EVENT_PO_ID, attributeMap.get(EVENT_PO_ID));
    }

    public Long getHistoryAlarmPOId() {
        return AlarmAttributeConverter.getLongAttributeValue(HISTORY_ALARM_PO_ID, attributeMap.get(HISTORY_ALARM_PO_ID));
    }

    public Long getAlarmNumber() {
        return AlarmAttributeConverter.getLongAttributeValue(ALARM_NUMBER, attributeMap.get(ALARM_NUMBER));
    }

    public Integer getRepeatCount() {
        return AlarmAttributeConverter.getIntegerAttributeValue(REPEAT_COUNT, attributeMap.get(REPEAT_COUNT));
    }

    public Integer getOscillationCount() {
        return AlarmAttributeConverter.getIntegerAttributeValue(OSCILLATION_COUNT, attributeMap.get(OSCILLATION_COUNT));
    }

    public Integer getCorrelatedRecordName() {
        return AlarmAttributeConverter.getIntegerAttributeValue(CORRELATED_RECORD_NAME, attributeMap.get(CORRELATED_RECORD_NAME));
    }

    public Date getEventTime() {
        return AlarmAttributeConverter.getDateAttributeValue(EVENT_TIME, attributeMap.get(EVENT_TIME));
    }

    public Date getCommentTime() {
        return AlarmAttributeConverter.getDateAttributeValue(COMMENT_TIME, attributeMap.get(COMMENT_TIME));
    }

    public Date getInsertTime() {
        return AlarmAttributeConverter.getDateAttributeValue(INSERT_TIME, attributeMap.get(INSERT_TIME));
    }

    public Date getLastUpdated() {
        return AlarmAttributeConverter.getDateAttributeValue(LAST_UPDATED, attributeMap.get(LAST_UPDATED));
    }

    public Date getCeaseTime() {
        return AlarmAttributeConverter.getDateAttributeValue(CEASE_TIME, attributeMap.get(CEASE_TIME));
    }

    public Date getAckTime() {
        return AlarmAttributeConverter.getDateAttributeValue(ACK_TIME, attributeMap.get(ACK_TIME));
    }

    public EventSeverity getPresentSeverity() {
        return EventSeverity.valueOf(AlarmAttributeConverter.getEnumAttributeValue(PRESENT_SEVERITY, attributeMap.get(PRESENT_SEVERITY)).toString());
    }

    public AlarmRecordType getRecordType() {
        return AlarmRecordType.valueOf(AlarmAttributeConverter.getEnumAttributeValue(RECORD_TYPE, attributeMap.get(RECORD_TYPE)).toString());
    }

    public EventTrendIndication getTrendIndication() {
        return EventTrendIndication
                .valueOf(AlarmAttributeConverter.getEnumAttributeValue(TREND_INDICATION, attributeMap.get(TREND_INDICATION)).toString());
    }

    public EventSeverity getPreviousSeverity() {
        return EventSeverity
                .valueOf(AlarmAttributeConverter.getEnumAttributeValue(PREVIOUS_SEVERITY, attributeMap.get(PREVIOUS_SEVERITY)).toString());
    }

    public EventState getAlarmState() {
        return EventState.valueOf(AlarmAttributeConverter.getEnumAttributeValue(ALARM_STATE, attributeMap.get(ALARM_STATE)).toString());
    }

    public LastAlarmOperation getLastAlarmOperation() {
        return LastAlarmOperation
                .valueOf(AlarmAttributeConverter.getEnumAttributeValue(LAST_ALARM_OPERATION, attributeMap.get(LAST_ALARM_OPERATION)).toString());
    }

    public Boolean isBackupStatus() {
        return AlarmAttributeConverter.getBooleanAttributeValue(BACKUP_STATUS, attributeMap.get(BACKUP_STATUS));
    }

    public Boolean isVisibility() {
        return AlarmAttributeConverter.getBooleanAttributeValue(VISIBILITY, attributeMap.get(VISIBILITY));
    }

    public Boolean isManualCease() {
        return AlarmAttributeConverter.getBooleanAttributeValue(MANUAL_CEASE, attributeMap.get(MANUAL_CEASE));
    }

    public Boolean isCorrelatedVisibility() {
        return AlarmAttributeConverter.getBooleanAttributeValue(CORRELATED_VISIBILITY, attributeMap.get(CORRELATED_VISIBILITY));
    }

    public Boolean isSyncState() {
        return AlarmAttributeConverter.getBooleanAttributeValue(SYNC_STATE, attributeMap.get(SYNC_STATE));
    }

    public Map<String, String> getAdditionalAttributeMap() {
        final String additionalInformation = AlarmAttributeConverter.getStringAttributeValue(ADDITIONAL_INFORMATION,
                attributeMap.get(ADDITIONAL_INFORMATION));
        return AlarmAttributeConverter.convertAdditionalInfoToMap(additionalInformation);
    }

    public CorrelationType getRoot() {
        CorrelationType result;
        try {
            result = CorrelationType.valueOf(AlarmAttributeConverter.getEnumAttributeValue(ROOT, attributeMap.get(ROOT)).toString());
        } catch (final IllegalArgumentException e) {
            result = CorrelationType.NOT_APPLICABLE;
        }
        return result;
    }

    public String getCiFirstGroup() {
        return AlarmAttributeConverter.getStringAttributeValue(CI_GROUP_1, attributeMap.get(CI_GROUP_1));
    }

    public String getCiSecondGroup() {
        return AlarmAttributeConverter.getStringAttributeValue(CI_GROUP_2, attributeMap.get(CI_GROUP_2));
    }

    // TODO : Need to remove once dependency removed in GUI.

    public void setFdn(final String fdn) {
        attributeMap.put(FDN, fdn);
    }

    public void setAttribute(final String name, final String value) {
        attributeMap.put(name, value);
    }

    private String replaceEscapeSequenceWithHashValue(final String value) {
        String result = value;
        if (result.contains(ESCAPE_SEQUENCE_FOR_HASH)) {
            result = result.replace(ESCAPE_SEQUENCE_FOR_HASH, HASH_DELIMITER);
        }
        return result;
    }

    public AlarmRecord(final Map<String, Object> attributeMap, final String nodeId, final List<Map<String, Object>> comments) {
        additionalAttributes.nodeId = nodeId;
        additionalAttributes.comments = comments;
        this.attributeMap = attributeMap;
    }

    public String getNodeId() {
        return additionalAttributes.nodeId;
    }

    public List<Map<String, Object>> getComments() {
        return additionalAttributes.comments;
    }

    public Map<String, Object> getProjectedData(final List<String> alarmAttributes) {
        final Map<String, Object> alarmAttributeMap = new HashMap<String, Object>(alarmAttributes.size());

        for (final String attribte : alarmAttributes) {
            alarmAttributeMap.put(attribte, attributeMap.get(attribte));
        }
        return alarmAttributeMap;
    }

    public Object getAttribute(final String attribute) {
        return attributeMap.get(attribute);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final Map<String, Object> attributeMap = new HashMap<String, Object>();
        attributeMap.putAll(this.attributeMap);
        attributeMap.remove(ID);
        attributeMap.remove(_VERSION);

        result = prime * result + additionalAttributes.hashCode();
        result = prime * result + attributeMap.hashCode();
        attributeMap.clear();
        return result;
    }

    @Override
    public boolean equals(final Object firstAlarmRecord) {
        if (this == firstAlarmRecord) {
            return true;
        }
        if (firstAlarmRecord == null) {
            return false;
        }
        if (!(firstAlarmRecord instanceof AlarmRecord)) {
            return false;
        }
        final AlarmRecord secondAlarmRecord = (AlarmRecord) firstAlarmRecord;
        if (!additionalAttributes.equals(secondAlarmRecord.additionalAttributes)) {
            return false;
        }
        // TORF-194340. In order to filter the duplicates out removing the SOLR record ID and version.
        final Map<String, Object> firstAttributeMap = attributeMap == null ? new HashMap<String, Object>()
                : new HashMap<String, Object>(attributeMap);
        final Map<String, Object> secondAttributeMap = secondAlarmRecord.attributeMap == null ? new HashMap<String, Object>()
                : new HashMap<String, Object>(secondAlarmRecord.attributeMap);

        if (firstAttributeMap.isEmpty() && secondAttributeMap.isEmpty() || !firstAttributeMap.isEmpty() && !secondAttributeMap.isEmpty()) {
            firstAttributeMap.remove(ID);
            firstAttributeMap.remove(_VERSION);
            secondAttributeMap.remove(ID);
            secondAttributeMap.remove(_VERSION);
            return firstAttributeMap.equals(secondAttributeMap);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AlarmRecord [attributeMap=").append(attributeMap).append(", additionalAttributes=").append(additionalAttributes).append("]");
        return builder.toString();
    }

    class AdditionalAttributes implements Serializable {
        private static final long serialVersionUID = 1L;
        private String nodeId;
        private List<Map<String, Object>> comments;

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("AdditionalAttributes [nodeId=").append(nodeId).append(", comments=").append(comments).append("]");
            return builder.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (comments == null ? 0 : comments.hashCode());
            result = prime * result + (nodeId == null ? 0 : nodeId.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object firstAdditionalAttributes) {
            if (this == firstAdditionalAttributes) {
                return true;
            }
            if (firstAdditionalAttributes == null) {
                return false;
            }
            if (!(firstAdditionalAttributes instanceof AdditionalAttributes)) {
                return false;
            }
            final AdditionalAttributes secondAdditionalAttributes = (AdditionalAttributes) firstAdditionalAttributes;
            if (comments == null) {
                if (secondAdditionalAttributes.comments != null) {
                    return false;
                }
            } else if (!comments.equals(secondAdditionalAttributes.comments)) {
                return false;
            }
            if (nodeId == null) {
                if (secondAdditionalAttributes.nodeId != null) {
                    return false;
                }
            } else if (!nodeId.equals(secondAdditionalAttributes.nodeId)) {
                return false;
            }
            return true;
        }
    }
}
