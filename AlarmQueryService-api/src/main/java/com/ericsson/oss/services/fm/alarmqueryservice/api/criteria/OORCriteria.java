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
import java.util.List;

/**
 *
 * A criteria class that encapsulates a list of OOR. <br>
 * It can be used to query PoIds based on following inputs: <br>
 * <p>
 * OOR - the list of object of Reference of the nodes <br>
 *
 *
 **/

public class OORCriteria implements Serializable {

    private static final long serialVersionUID = -3607321645996588324L;
    private List<OORExpression> oorExpressions;

    public List<OORExpression> getOorExpressions() {
        return oorExpressions;
    }

    public void setOorExpressions(final List<OORExpression> oorExpressions) {
        this.oorExpressions = oorExpressions;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("OORCriteria [oorConditions=");
        builder.append(oorExpressions);
        builder.append("]");
        return builder.toString();
    }

}
