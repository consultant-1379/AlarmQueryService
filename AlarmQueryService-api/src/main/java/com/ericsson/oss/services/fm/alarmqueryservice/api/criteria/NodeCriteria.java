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
 * A criteria class that encapsulates a list of nodes. <br>
 * It can be used to query alarms based on following inputs: <br>
 * <p>
 * nodes - the list of FDNs of the nodes <br>
 *
 *
 **/

public class NodeCriteria implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> nodes;

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(final List<String> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("NodeCriteria [nodes=");
        builder.append(nodes);
        builder.append("]");
        return builder.toString();
    }

}
