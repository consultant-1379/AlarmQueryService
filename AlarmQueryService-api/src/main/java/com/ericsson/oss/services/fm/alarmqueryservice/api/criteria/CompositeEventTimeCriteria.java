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

package com.ericsson.oss.services.fm.alarmqueryservice.api.criteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;

/**
 * A Composite Criteria class that encapsulates the event time of an alarm, along with other alarm attributes and list of nodes. <br>
 * It can be used to query alarms based on following inputs:
 * <p>
 * event time - the time the alarm is generated on a node <br>
 * nodes - the list of FDNs of the nodes <br>
 * other alarm attributes - the other attributes of an alarm like SP, PC, ET etc.
 * <p>
 * The criteria also allows to set a sorting order on a specific alarm attribute.
 **/
public class CompositeEventTimeCriteria implements Serializable {

    private static final long serialVersionUID = 3596843702769225686L;
    private Date fromTime;
    private Date toTime;
    private Operator operator;
    private List<String> nodes;
    private List<AlarmAttributeCriteria> alarmAttributeCriteria;
    private String sortAttribute;
    private int maxNumberOfAlarmsToBeRetrieved;
    private List<String> sortAttributes = new ArrayList<String>();
    private SortingOrder sortDirection;
    private List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();

    public List<AlarmSortCriterion> getAlarmSortCriteria() {
        return alarmSortCriteria;
    }

    public void setAlarmSortCriteria(final List<AlarmSortCriterion> alarmSortCriteria) {
        this.alarmSortCriteria = alarmSortCriteria;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(final Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(final Date toTime) {
        this.toTime = toTime;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(final List<String> nodes) {
        this.nodes = nodes;
    }

    public List<AlarmAttributeCriteria> getAlarmAttributeCriteria() {
        return alarmAttributeCriteria;
    }

    public void setAlarmAttributeCriteria(final List<AlarmAttributeCriteria> alarmAttributeCriteria) {
        this.alarmAttributeCriteria = alarmAttributeCriteria;
    }

    public String getSortAttribute() {
        return sortAttribute;
    }

    public List<String> getSortAttributes() {
        return sortAttributes;
    }

    public void setSortAttribute(final String sortAttribute) {
        this.sortAttribute = sortAttribute;
    }

    public void setSortAttributes(final List<String> sortAttributes) {
        this.sortAttributes = sortAttributes;
    }

    public SortingOrder getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(final SortingOrder sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getMaxNumberOfAlarmsToBeRetrieved() {
        return maxNumberOfAlarmsToBeRetrieved;
    }

    public void setMaxNumberOfAlarmsToBeRetrieved(final int maxNumberOfAlarmsToBeRetrieved) {
        this.maxNumberOfAlarmsToBeRetrieved = maxNumberOfAlarmsToBeRetrieved;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CompositeEventTimeCriteria [fromTime=").append(fromTime).append(", toTime=").append(toTime).append(", operator=")
                .append(operator).append(", nodes=").append(nodes).append(", alarmAttributeCriteria=").append(alarmAttributeCriteria)
                .append(", sortAttribute=").append(sortAttribute).append(", maxNumberOfAlarmsToBeRetrieved=").append(maxNumberOfAlarmsToBeRetrieved)
                .append(", sortAttributes=").append(sortAttributes).append(", sortDirection=").append(sortDirection).append(", alarmSortCriteria=")
                .append(alarmSortCriteria).append("]");
        return builder.toString();
    }

}
