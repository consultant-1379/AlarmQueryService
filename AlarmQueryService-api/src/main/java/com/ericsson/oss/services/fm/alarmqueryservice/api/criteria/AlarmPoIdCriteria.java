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
 * A criteria class that encapsulates a list of alarm PoIds.<br>
 * It can be used to query alarms based on following inputs:
 * <p>
 * poIds - the list of PoIds(<b> A unique number associated to each Alarm</b>) of alarms<br>
 *
 *
 **/
public class AlarmPoIdCriteria implements Serializable {

    private static final long serialVersionUID = -5740360341011360057L;
    private List<Long> poIds;

    public List<Long> getPoIds() {
        return poIds;
    }

    public void setPoIds(final List<Long> poIds) {
        this.poIds = poIds;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("PoIdCriteria [poIds=");
        builder.append(poIds);
        builder.append("]");
        return builder.toString();
    }

}
