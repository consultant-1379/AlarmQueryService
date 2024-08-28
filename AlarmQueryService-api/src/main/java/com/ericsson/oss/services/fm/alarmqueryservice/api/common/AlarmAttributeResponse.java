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
import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;

/**
 *
 * A response class that encapsulates both the alarms ({@link AlarmRecord}) and the response status. <br>
 * An AlarmRecord may have all attributes of an alarm or only the attributes that are set as part of {@link ExpectedOutputAttributes}.
 *
 *
 */

public class AlarmAttributeResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String response;
    private List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
    private Long alarmCountForSearchCriteria;

    public AlarmAttributeResponse(final List<AlarmRecord> alarmRecords, final String response) {
        this.alarmRecords = alarmRecords;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public List<AlarmRecord> getAlarmRecords() {
        return alarmRecords;
    }

    public Long getAlarmCountForSearchCriteria() {
        return alarmCountForSearchCriteria;
    }

    public void setAlarmCountForSearchCriteria(final Long alarmCountForSearchCriteria) {
        this.alarmCountForSearchCriteria = alarmCountForSearchCriteria;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AlarmQueryResponse [response=");
        stringBuilder.append(response);
        stringBuilder.append(", alarmRecords=");
        stringBuilder.append(alarmRecords);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
