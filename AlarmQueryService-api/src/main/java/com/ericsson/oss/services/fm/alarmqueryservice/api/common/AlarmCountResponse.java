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

/**
 *
 * A response class that encapsulates both the alarms count and the response status. <br>
 * Alarm count is number of alarms found for the given criteria.
 *
 *
 */

public class AlarmCountResponse implements Serializable {

    private static final long serialVersionUID = -2178642272205716715L;
    private final String response;
    private Long alarmCount = -1L;

    public AlarmCountResponse(final Long alarmCount, final String response) {
        this.alarmCount = alarmCount;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public Long getAlarmCount() {
        return alarmCount;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AlarmCountResponse [response=").append(response).append(", alarmCount=").append(alarmCount).append("]");
        return builder.toString();
    }

}
