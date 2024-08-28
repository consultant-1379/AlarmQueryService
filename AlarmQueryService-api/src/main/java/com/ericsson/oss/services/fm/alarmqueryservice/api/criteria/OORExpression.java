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

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;

/**
 *
 * A criteria class that encapsulates a single objectOfReference(OOR) of an Alarm.<br>
 * The class consists of an objectOfReference value and the {@link Operator} that defines the association between the OOR and the value.
 *
 *
 *
 **/

public class OORExpression implements Serializable {

    private static final long serialVersionUID = 2529058825473207011L;
    private String objectOfReference;
    private Operator operator;

    public String getObjectOfReference() {
        return objectOfReference;
    }

    public void setObjectOfReference(final String objectOfReference) {
        this.objectOfReference = objectOfReference;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("OORCondition [objectOfReference=");
        builder.append(objectOfReference);
        builder.append(", operator=");
        builder.append(operator);
        builder.append("]");
        return builder.toString();
    }

}
