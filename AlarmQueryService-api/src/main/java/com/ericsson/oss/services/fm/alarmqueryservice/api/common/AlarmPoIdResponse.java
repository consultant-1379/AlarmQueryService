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

package com.ericsson.oss.services.fm.alarmqueryservice.api.common;

import java.io.Serializable;
import java.util.List;

/**
 *
 * A response class that encapsulates both the alarms poIds and the response status. <br>
 * Response may contain success or exception.
 *
 *
 */
public class AlarmPoIdResponse implements Serializable {

    private static final long serialVersionUID = -5288864939016829079L;

    private final List<Long> poIds;
    private final String response;

    public String getResponse() {
        return response;
    }

    public List<Long> getPoIds() {
        return poIds;
    }

    public AlarmPoIdResponse(final List<Long> poIds, final String response) {
        this.response = response;
        this.poIds = poIds;
    }

    @Override
    public String toString() {
        return "AlarmPoIdResponse [poIds=" + poIds + ", response=" + response + "]";
    }

}
