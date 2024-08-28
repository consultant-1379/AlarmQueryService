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

package com.ericsson.oss.services.alarm.query.service.models;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EQUAL_DELIMITER;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ericsson.oss.services.fm.common.addinfo.CorrelationType;

public class AlarmRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private String objectOfReference;
    private String fdn;
    private String nodeId;
    private Long eventPoId;
    private Integer repeatCount;
    private Integer oscillationCount;
    private Date eventTime;
    private Date insertTime;
    private Date lastUpdated;
    private EventSeverity presentSeverity;
    private String probableCause;
    private String specificProblem;
    private Long alarmNumber;
    private String eventType;
    private String backupObjectInstance;
    private AlarmRecordType recordType;
    private Boolean backupStatus;
    private EventTrendIndication trendIndication;
    private EventSeverity previousSeverity;
    private String proposedRepairAction;
    private Long alarmId;
    private EventState alarmState;
    private Integer correlatedRecordName;
    private Date ceaseTime;
    private String ceaseOperator;
    private Date ackTime;
    private String ackOperator;
    private String problemText;
    private String problemDetail;
    private String additionalInformation;
    private String commentText;
    private List<Map<Object, Object>> comments;
    private LastAlarmOperation lastAlarmOperation;
    private boolean visibility;
    private String processingType;
    private String fmxGenerated;
    private String fmxToken;
    private Map<String, String> additionalInformationMap;
    private String managedObject;
    private boolean manualCease;
    private boolean correlatedVisibility;
    private boolean syncState;
    private String alarmingObject;
    private String root;
    private String ciFirstGroup;
    private String ciSecondGroup;

    public String getAlarmingObject() {
        return alarmingObject;
    }

    public void setAlarmingObject(final String alarmingObject) {
        this.alarmingObject = alarmingObject;
    }

    public boolean isSyncState() {
        return syncState;
    }

    public void setSyncState(final boolean syncState) {
        this.syncState = syncState;
    }

    public boolean isCorrelatedVisibility() {
        return correlatedVisibility;
    }

    public void setCorrelatedVisibility(final boolean correlatedVisibility) {
        this.correlatedVisibility = correlatedVisibility;
    }

    public boolean isManualCease() {
        return manualCease;
    }

    public void setManualCease(final boolean manualCease) {
        this.manualCease = manualCease;
    }

    public String getManagedObject() {
        return managedObject;
    }

    public void setManagedObject(final String managedObject) {
        this.managedObject = managedObject;
    }

    public Long getEventPoId() {
        return eventPoId;
    }

    public List<Map<Object, Object>> getComments() {
        return comments;
    }

    public void setComments(final List<Map<Object, Object>> comments) {
        this.comments = comments;
    }

    public String getObjectOfReference() {
        return objectOfReference;
    }

    public void setObjectOfReference(final String objectOfReference) {
        this.objectOfReference = objectOfReference;
    }

    public String getFdn() {
        return fdn;
    }

    public String getNeName() {
        return getNetworkElementName(fdn);
    }

    public void setFdn(final String fdn) {
        this.fdn = fdn;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public Integer getOscillationCount() {
        return oscillationCount;
    }

    public void setOscillationCount(final Integer oscillationCount) {
        this.oscillationCount = oscillationCount;
    }

    public void setEventTime(final Date eventTime) {
        this.eventTime = eventTime;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(final Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public EventSeverity getPresentSeverity() {
        return presentSeverity;
    }

    public void setPresentSeverity(final EventSeverity presentSeverity) {
        this.presentSeverity = presentSeverity;
    }

    public String getProbableCause() {
        return probableCause;
    }

    public void setProbableCause(final String probableCause) {
        this.probableCause = probableCause;
    }

    public String getSpecificProblem() {
        return specificProblem;
    }

    public void setSpecificProblem(final String specificProblem) {
        this.specificProblem = specificProblem;
    }

    public Long getAlarmNumber() {
        return alarmNumber;
    }

    public void setAlarmNumber(final Long alarmNumber) {
        this.alarmNumber = alarmNumber;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    public String getBackupObjectInstance() {
        return backupObjectInstance;
    }

    public void setBackupObjectInstance(final String backupObjectInstance) {
        this.backupObjectInstance = backupObjectInstance;
    }

    public AlarmRecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(final AlarmRecordType recordType) {
        this.recordType = recordType;
    }

    public Boolean getBackupStatus() {
        return backupStatus;
    }

    public void setBackupStatus(final Boolean backupStatus) {
        this.backupStatus = backupStatus;
    }

    public EventTrendIndication getTrendIndication() {
        return trendIndication;
    }

    public void setTrendIndication(final EventTrendIndication trendIndication) {
        this.trendIndication = trendIndication;
    }

    public EventSeverity getPreviousSeverity() {
        return previousSeverity;
    }

    public void setPreviousSeverity(final EventSeverity previousSeverity) {
        this.previousSeverity = previousSeverity;
    }

    public String getProposedRepairAction() {
        return proposedRepairAction;
    }

    public void setProposedRepairAction(final String proposedRepairAction) {
        this.proposedRepairAction = proposedRepairAction;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(final Long alarmId) {
        this.alarmId = alarmId;
    }

    public EventState getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(final EventState alarmState) {
        this.alarmState = alarmState;
    }

    public Integer getCorrelatedRecordName() {
        return correlatedRecordName;
    }

    public void setCorrelatedRecordName(final Integer correlatedRecordName) {
        this.correlatedRecordName = correlatedRecordName;
    }

    public Date getCeaseTime() {
        return ceaseTime;
    }

    public void setCeaseTime(final Date ceaseTime) {
        this.ceaseTime = ceaseTime;
    }

    public String getCeaseOperator() {
        return ceaseOperator;
    }

    public void setCeaseOperator(final String ceaseOperator) {
        this.ceaseOperator = ceaseOperator;
    }

    public Date getAckTime() {
        return ackTime;
    }

    public void setAckTime(final Date ackTime) {
        this.ackTime = ackTime;
    }

    public String getAckOperator() {
        return ackOperator;
    }

    public void setAckOperator(final String ackOperator) {
        this.ackOperator = ackOperator;
    }

    public String getProblemText() {
        return problemText;
    }

    public void setProblemText(final String problemText) {
        this.problemText = problemText;
    }

    public String getProblemDetail() {
        return problemDetail;
    }

    public void setProblemDetail(final String problemDetail) {
        this.problemDetail = problemDetail;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(final String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(final String commentText) {
        this.commentText = commentText;
    }

    public String getEventPoIdAsString() {
        return eventPoId.toString();
    }

    public Long getEventPoIdAsLong() {
        return eventPoId;
    }

    public void setEventPoId(final Long eventPoId) {
        this.eventPoId = eventPoId;
    }

    public LastAlarmOperation getLastAlarmOperation() {
        return lastAlarmOperation;
    }

    public void setLastAlarmOperation(final LastAlarmOperation lastAlarmOperation) {
        this.lastAlarmOperation = lastAlarmOperation;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(final Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    public String getProcessingType() {
        return processingType;
    }

    public void setProcessingType(final String processingType) {
        this.processingType = processingType;
    }

    public String getFmxGenerated() {
        return fmxGenerated;
    }

    public void setFmxGenerated(final String fmxGenerated) {
        this.fmxGenerated = fmxGenerated;
    }

    public String getFmxToken() {
        return fmxToken;
    }

    public void setFmxToken(final String fmxToken) {
        this.fmxToken = fmxToken;
    }

    public Map<String, String> getAdditionalInformationMap() {
        return additionalInformationMap;
    }

    public void setAdditionalInformationMap(final Map<String, String> map) {
        additionalInformationMap = map;
    }

    private static String getNetworkElementName(final String fdn) {
        String neName = "";
        if (fdn != null && !fdn.isEmpty()) {
            if (fdn.contains(EQUAL_DELIMITER)) {
                neName = fdn.split(EQUAL_DELIMITER)[1];
            } else {
                neName = fdn;
            }
        }
        return neName;
    }

    public String getCiFirstGroup() {
        return ciFirstGroup;
    }

    public void setCiFirstGroup(final String ciFirstGroup) {
        this.ciFirstGroup = ciFirstGroup;
    }

    public String getCiSecondGroup() {
        return ciSecondGroup;
    }

    public void setCiSecondGroup(final String ciSecondGroup) {
        this.ciSecondGroup = ciSecondGroup;
    }

    public CorrelationType getRoot() {
        return CorrelationType.valueOf(root);
    }

    public void setRoot(final CorrelationType root) {
        this.root = root.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AlarmRecord [objectOfReference=");
        builder.append(objectOfReference);
        builder.append(", fdn=");
        builder.append(fdn);
        builder.append(", nodeId=");
        builder.append(nodeId);
        builder.append(", eventPoId=");
        builder.append(eventPoId);
        builder.append(", repeatCount=");
        builder.append(repeatCount);
        builder.append(", oscillationCount=");
        builder.append(oscillationCount);
        builder.append(", eventTime=");
        builder.append(eventTime);
        builder.append(", insertTime=");
        builder.append(insertTime);
        builder.append(", lastUpdated=");
        builder.append(lastUpdated);
        builder.append(", presentSeverity=");
        builder.append(presentSeverity);
        builder.append(", probableCause=");
        builder.append(probableCause);
        builder.append(", specificProblem=");
        builder.append(specificProblem);
        builder.append(", alarmNumber=");
        builder.append(alarmNumber);
        builder.append(", eventType=");
        builder.append(eventType);
        builder.append(", backupObjectInstance=");
        builder.append(backupObjectInstance);
        builder.append(", recordType=");
        builder.append(recordType);
        builder.append(", backupStatus=");
        builder.append(backupStatus);
        builder.append(", trendIndication=");
        builder.append(trendIndication);
        builder.append(", previousSeverity=");
        builder.append(previousSeverity);
        builder.append(", proposedRepairAction=");
        builder.append(proposedRepairAction);
        builder.append(", alarmId=");
        builder.append(alarmId);
        builder.append(", alarmState=");
        builder.append(alarmState);
        builder.append(", correlatedRecordName=");
        builder.append(correlatedRecordName);
        builder.append(", ceaseTime=");
        builder.append(ceaseTime);
        builder.append(", ceaseOperator=");
        builder.append(ceaseOperator);
        builder.append(", ackTime=");
        builder.append(ackTime);
        builder.append(", ackOperator=");
        builder.append(ackOperator);
        builder.append(", problemText=");
        builder.append(problemText);
        builder.append(", problemDetail=");
        builder.append(problemDetail);
        builder.append(", additionalInformation=");
        builder.append(additionalInformation);
        builder.append(", commentText=");
        builder.append(commentText);
        builder.append(", comments=");
        builder.append(comments);
        builder.append(", lastAlarmOperation=");
        builder.append(lastAlarmOperation);
        builder.append(", visibility=");
        builder.append(visibility);
        builder.append(", processingType=");
        builder.append(processingType);
        builder.append(", fmxGenerated=");
        builder.append(fmxGenerated);
        builder.append(", fmxToken=");
        builder.append(fmxToken);
        builder.append(", additionalInformationMap=");
        builder.append(additionalInformationMap);
        builder.append(", managedObject=");
        builder.append(managedObject);
        builder.append(", manualCease=");
        builder.append(manualCease);
        builder.append(", correlatedVisibility=");
        builder.append(correlatedVisibility);
        builder.append(", syncState=");
        builder.append(syncState);
        builder.append(", alarmingObject=");
        builder.append(alarmingObject);
        builder.append(", root=");
        if (root != null) {
            builder.append(root);
        }
        builder.append(", ciFirstGroup=");
        builder.append(ciFirstGroup);
        builder.append(", ciSecondGroup=");
        builder.append(ciSecondGroup);
        builder.append("]");
        return builder.toString();
    }

    public enum EventSeverity {
        UNDEFINED, INDETERMINATE, CRITICAL, MAJOR, MINOR, WARNING, CLEARED;
        private final int severity;

        EventSeverity() {
            severity = 0;
        }

        EventSeverity(final int s) {
            severity = s;
        }

        public int getEventSeverity() {
            return severity;
        }
    }

    public enum AlarmRecordType {
        UNDEFINED, ALARM, ERROR_MESSAGE, NON_SYNCHABLE_ALARM, REPEATED_ALARM, SYNCHRONIZATION_ALARM, HEARTBEAT_ALARM, SYNCHRONIZATION_STARTED, SYNCHRONIZATION_ENDED, SYNCHRONIZATION_ABORTED, SYNCHRONIZATION_IGNORED, CLEAR_LIST, REPEATED_ERROR_MESSAGE, REPEATED_NON_SYNCHABLE, UPDATE, NODE_SUSPENDED, HB_FAILURE_NO_SYNCH, SYNC_NETWORK, TECHNICIAN_PRESENT, ALARM_SUPPRESSED_ALARM, OSCILLATORY_HB_ALARM, UNKNOWN_RECORD_TYPE, OUT_OF_SYNC, CLEARALL;

        private final int recordType;

        AlarmRecordType() {
            recordType = 0;
        }

        AlarmRecordType(final int s) {
            recordType = s;
        }

        public int getAlarmRecordType() {
            return recordType;
        }
    }

    public enum EventTrendIndication {
        UNDEFINED, LESS_SEVERE, NO_CHANGE, MORE_SEVERE;

        private final int trendIndication;

        EventTrendIndication() {
            trendIndication = 0;
        }

        EventTrendIndication(final int s) {
            trendIndication = s;
        }

        public int getEventTrendIndication() {
            return trendIndication;
        }
    }

    public enum EventState {
        ACTIVE_UNACKNOWLEDGED, ACTIVE_ACKNOWLEDGED, CLEARED_UNACKNOWLEDGED, CLEARED_ACKNOWLEDGED, CLOSED;

        private final int eventState;

        EventState() {
            eventState = 0;
        }

        EventState(final int s) {
            eventState = s;
        }

        public int getEventState() {
            return eventState;
        }
    }

    public enum LastAlarmOperation {
        UNDEFINED, NEW, CLEAR, CHANGE, ACKSTATE_CHANGE, COMMENT;

        private final int eventActionState;

        LastAlarmOperation() {
            eventActionState = 0;
        }

        LastAlarmOperation(final int s) {
            eventActionState = s;
        }

        public int getLastAlarmOperation() {
            return eventActionState;
        }
    }

}
