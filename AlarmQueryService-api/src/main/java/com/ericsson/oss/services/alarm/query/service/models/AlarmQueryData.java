
package com.ericsson.oss.services.alarm.query.service.models;

import java.io.Serializable;
import java.util.List;

public class AlarmQueryData implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> nodes;
    private boolean ack;
    private boolean unAck;
    private List<Long> eventPoIds;
    private boolean allAlarms;
    private boolean previousCommentsRequired;
    private List<String> objectOfReferences;
    private boolean nodeIdRequired;
    private List<String> outputAttributes;

    public boolean isNodeIdRequired() {
        return nodeIdRequired;
    }

    public void setNodeIdRequired(final boolean nodeIdRequired) {
        this.nodeIdRequired = nodeIdRequired;
    }

    public List<String> getObjectOfReferences() {
        return objectOfReferences;
    }

    public void setObjectOfReferences(final List<String> objectOfReferences) {
        this.objectOfReferences = objectOfReferences;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(final List<String> nodes) {
        this.nodes = nodes;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(final boolean ack) {
        this.ack = ack;
    }

    public boolean isUnAck() {
        return unAck;
    }

    public void setUnAck(final boolean unAck) {
        this.unAck = unAck;
    }

    public List<Long> getEventPoIds() {
        return eventPoIds;
    }

    public void setEventPoIds(final List<Long> eventPoIds) {
        this.eventPoIds = eventPoIds;
    }

    public boolean isAllAlarms() {
        return allAlarms;
    }

    public void setAllAlarms(final boolean allAlarms) {
        this.allAlarms = allAlarms;
    }

    public boolean isPreviousCommentsRequired() {
        return previousCommentsRequired;
    }

    public void setPreviousCommentsRequired(final boolean previousCommentsRequired) {
        this.previousCommentsRequired = previousCommentsRequired;
    }

    public List<String> getOutputAttributes() {
        return outputAttributes;
    }

    public void setOutputAttributes(final List<String> outputAttributes) {
        this.outputAttributes = outputAttributes;
    }

    @Override
    public String toString() {
        return "AlarmQueryData [nodes=" + nodes + ", ack=" + ack + ", unAck=" + unAck + ", eventPoIds=" + eventPoIds + ", allAlarms=" + allAlarms
                + ", previousCommentsRequired=" + previousCommentsRequired + ", objectOfReferences=" + objectOfReferences + ", nodeIdRequired="
                + nodeIdRequired + "]";
    }

}
