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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AlarmLogData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String attributeName;
    private List<Date> date;
    private DateOperator dateOperator;
    private List<String> alarmAttributes;
    private List<String> nodeList;
    private String searchType;
    private String sortAttribute;
    private SortingOrder sortMode;
    private String dateFormat;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(final String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(final String attributeName) {
        this.attributeName = attributeName;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(final String searchType) {
        this.searchType = searchType;
    }

    public String getDateAttribute() {
        return attributeName;
    }

    public void setDateAttribute(final String dateAttribute) {
        this.attributeName = dateAttribute;
    }

    public List<Date> getDate() {
        return date;
    }

    public void setDate(final List<Date> date) {
        this.date = date;
    }

    public DateOperator getDateOperator() {
        return dateOperator;
    }

    public void setDateOperator(final DateOperator dateOperator) {
        this.dateOperator = dateOperator;
    }

    public List<String> getAlarmAttributes() {
        return alarmAttributes;
    }

    public void setAlarmAttributes(final List<String> alarmAttributes) {
        this.alarmAttributes = alarmAttributes;
    }

    public List<String> getNodeList() {
        return nodeList;
    }

    public void setNodeList(final List<String> nodeList) {
        this.nodeList = nodeList;
    }

    public String getSortAttribute() {
        return sortAttribute;
    }

    public void setSortAttribute(final String sortAttribute) {
        this.sortAttribute = sortAttribute;
    }

    public SortingOrder getSortMode() {
        return sortMode;
    }

    public void setSortMode(final SortingOrder sortMode) {
        this.sortMode = sortMode;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();
        builder.append("AlarmLogData [attributeName=").append(attributeName).append(", date=").append(date).append(", dateOperator=")
                .append(dateOperator).append(", alarmAttributes=").append(alarmAttributes).append(", nodeList=").append(nodeList)
                .append(", sortMode=").append(sortMode).append(",sortAttribute=").append(sortAttribute).append(",dateFormat=").append(dateFormat)
                .append(",searchType=").append(searchType);
        return builder.toString();
    }

}