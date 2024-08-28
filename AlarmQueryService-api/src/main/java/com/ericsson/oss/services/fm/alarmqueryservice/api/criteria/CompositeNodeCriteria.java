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
import java.util.List;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;

/**
 *
 * A Composite Criteria class that encapsulates the nodes, along with alarm attributes. It can be used to query alarms based on following inputs: <br>
 * <p>
 * nodes - the list of FDNs of the nodes <br>
 * other alarm attributes - the other attributes of an alarm like SP, PC, ET etc.
 * <p>
 * The criteria also allows to set a sorting order on a specific alarm attribute.
 *
 *
 **/

public class CompositeNodeCriteria implements Serializable {
    private static final long serialVersionUID = 928299570222562257L;
    private List<String> nodes;
    private List<AlarmAttributeCriteria> alarmAttributeCriteria;
    private String sortAttribute;
    private List<String> sortAttributes = new ArrayList<String>();
    private SortingOrder sortDirection;
    private List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<AlarmSortCriterion>();

    public List<AlarmSortCriterion> getAlarmSortCriteria() {
        return alarmSortCriteria;
    }

    public void setAlarmSortCriteria(final List<AlarmSortCriterion> alarmSortCriteria) {
        this.alarmSortCriteria = alarmSortCriteria;
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

    public List<AlarmAttributeCriteria> getAlarmAttributeCriteria() {
        return alarmAttributeCriteria;
    }

    public void setAlarmAttributeCriteria(final List<AlarmAttributeCriteria> alarmAttributeCriteria) {
        this.alarmAttributeCriteria = alarmAttributeCriteria;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(final List<String> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CompositeNodeCriteria [nodes=").append(nodes).append(", alarmAttributeCriteria=").append(alarmAttributeCriteria)
                .append(", sortAttribute=").append(sortAttribute).append(", sortDirection=").append(sortDirection).append(", alarmSortCriteria=")
                .append(alarmSortCriteria).append("]");
        return builder.toString();
    }

}
