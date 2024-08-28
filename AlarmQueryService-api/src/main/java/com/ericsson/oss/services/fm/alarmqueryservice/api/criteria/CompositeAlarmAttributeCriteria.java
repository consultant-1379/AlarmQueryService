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
 * A Composite Criteria class that encapsulates the list of alarm attributes <br>
 * It can be used to query alarms based on following inputs:
 * <p>
 * alarm attributes - the attributes of an alarm like SP, PC, ET etc.
 *
 *
 **/
public class CompositeAlarmAttributeCriteria implements Serializable {

    private static final long serialVersionUID = 3755781176039517961L;
    List<AlarmAttributeCriteria> alarmAttributeCritera;

    public List<AlarmAttributeCriteria> getAlarmAttributeCritera() {
        return alarmAttributeCritera;
    }

    public void setAlarmAttributeCritera(final List<AlarmAttributeCriteria> alarmAttributeCritera) {
        this.alarmAttributeCritera = alarmAttributeCritera;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CompositeAlarmAttributeCriteria [alarmAttributeCritera=").append(alarmAttributeCritera).append("]");
        return builder.toString();
    }

}
