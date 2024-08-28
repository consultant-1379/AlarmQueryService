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

/**
 *
 * This class is POJO which is used as output response for Alarm Query Service. Status of the request alarmRecordList : list of open alarms found for
 * respective query.
 *
 */

public class AlarmQueryResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    String response;
    List<AlarmRecord> alarmRecordList;
    Date lastRecordTime;

    public Date getLastRecordTime() {
        return lastRecordTime;
    }

    public void setLastRecordTime(final Date lastRecordTime) {
        this.lastRecordTime = lastRecordTime;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(final String response) {
        this.response = response;
    }

    public List<AlarmRecord> getAlarmRecordList() {
        return alarmRecordList;
    }

    public void setAlarmRecordList(final List<AlarmRecord> alarmRecordList) {
        this.alarmRecordList = alarmRecordList;
    }

    @Override
    public String toString() {
        return "AlarmQueryResponse [response=" + response
                + ", alarmRecordList=" + alarmRecordList + ", lastRecordTime="
                + lastRecordTime + "]";
    }

}
