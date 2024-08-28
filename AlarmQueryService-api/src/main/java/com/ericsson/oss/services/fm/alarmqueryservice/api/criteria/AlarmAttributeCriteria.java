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

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.LogicalCondition;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;

/**
 * A criteria class that encapsulates a single attribute of an Alarm. The class consists of an attribute name, its value and the {@link Operator} that
 * defines the association between the name and the value.
 *
 *
 **/

public class AlarmAttributeCriteria implements Serializable {

    private static final long serialVersionUID = -8432177276951408595L;
    private String attributeName;
    private Object attributeValue;
    private Operator operator;
    private LogicalCondition logicalCondition;


    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(final String attributeName) {
        this.attributeName = attributeName;
    }

    public Object getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(final Object attributeValue) {
        this.attributeValue = attributeValue;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    public LogicalCondition getLogicalCondition() {
        return logicalCondition;
    }

    public void setLogicalCondition(final LogicalCondition logicalCondition) {
        this.logicalCondition = logicalCondition;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AlarmAttributeCriteria [attributeName=").append(attributeName).append(", attributeValue=").append(attributeValue)
                .append(", operator=").append(operator).append(",logicalCondition=").append(logicalCondition).append("]");
        return builder.toString();
    }

}
