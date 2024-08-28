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
 * A class that encapsulates the output attributes of an alarm. Along with alarm attributes, it also allows to set whether the node PoId and comments
 * history should be part of the query response.
 *
 * <p>
 * When alarm attributes are not set, ALL the attributes of the alarm will be retrieved.
 * <p>
 * Usage examples:
 * <p>
 * Example #1
 * <p>
 * ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();<br>
 * expectedOutputAttributes.setNodeIdRequired(true); <br>
 * Retrieves the alarms with all the alarm attributes and node Id.
 * <p>
 * Example #2
 * <p>
 * ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();<br>
 * expectedOutputAttributes.setCommentHistoryRequired(true);<br>
 * Retrieves the alarms with all the alarm attributes along with comment history.
 * <p>
 *
 * Example #3
 * <p>
 * ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();<br>
 * expectedOutputAttributes.setNodeIdRequired(true);<br>
 * List<String> attributes = new ArrayList<String>();<br>
 * attributes.set("specificProblem");<br>
 * attributes.set("alarmNumber");<br>
 * expectedOutputAttributes.setOutputAttributes(attributes);<br>
 * <br>
 * Retrieves the specific problem and alarm number along with node PoId.
 * <p>
 *
 *
 **/

public class ExpectedOutputAttributes implements Serializable {
    private static final long serialVersionUID = -1151917915950635826L;
    private List<String> outputAttributes;

    private boolean nodeIdRequired;

    private boolean commentHistoryRequired;

    public List<String> getOutputAttributes() {
        return outputAttributes;
    }

    public void setOutputAttributes(final List<String> outputAttributes) {
        this.outputAttributes = outputAttributes;
    }

    public boolean isNodeIdRequired() {
        return nodeIdRequired;
    }

    public void setNodeIdRequired(final boolean nodeIdRequired) {
        this.nodeIdRequired = nodeIdRequired;
    }

    public boolean isCommentHistoryRequired() {
        return commentHistoryRequired;
    }

    public void setCommentHistoryRequired(final boolean commentHistoryRequired) {
        this.commentHistoryRequired = commentHistoryRequired;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ExpectedOutputAttributes [outputAttributes=");
        builder.append(outputAttributes);
        builder.append(", nodeIdRequired=");
        builder.append(nodeIdRequired);
        builder.append(", commentHistoryRequired=");
        builder.append(commentHistoryRequired);
        builder.append("]");
        return builder.toString();
    }

}
